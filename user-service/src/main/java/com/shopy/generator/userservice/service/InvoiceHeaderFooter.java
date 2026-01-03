package com.shopy.generator.userservice.service;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.shopy.generator.userservice.entity.Invoice;
import com.shopy.generator.userservice.entity.UserProfile;

public class InvoiceHeaderFooter implements IEventHandler {

    private final Invoice invoice;
    private final UserProfile profile;

    public InvoiceHeaderFooter(Invoice invoice, UserProfile profile) {
        this.invoice = invoice;
        this.profile = profile;
    }

    @Override
    public void handleEvent(Event event) {

        PdfDocumentEvent e = (PdfDocumentEvent) event;
        PdfDocument pdf = e.getDocument();
        PdfPage page = e.getPage();
        Rectangle area = page.getPageSize();

        PdfCanvas pc = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdf);
        Canvas canvas = new Canvas(pc, area);


        // ================= HEADER =================
        Table header = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        try {
            ImageData logo = ImageDataFactory.create("classpath:/static/logo.png");
            Image img = new Image(logo).scaleToFit(95, 70);
            header.addCell(new Cell().add(img).setBorder(Border.NO_BORDER));
        } catch (Exception ex) {
            header.addCell(new Cell().setBorder(Border.NO_BORDER));
        }

        header.addCell(
                new Cell()
                        .add(new Paragraph("INVOICE").setBold().setFontSize(22))
                        .add(new Paragraph("Invoice #: " + safe(invoice.getInvoiceNumber())))
                        .add(new Paragraph("Invoice Date: " + safe(invoice.getDate())))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(Border.NO_BORDER)
        );

        header.setFixedPosition(
                area.getLeft() + 36,
                area.getTop() - 90,
                area.getWidth() - 72
        );

        canvas.add(header);


        // ================= SENDER / RECEIVER =================
        Table parties = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        parties.addCell(
                new Cell()
                        .add(new Paragraph("Bill From").setBold())
                        .add(new Paragraph(safe(invoice.getUser().getOrganisation().getName())))
                        .add(new Paragraph(safe(profile.getAddress())))
                        .add(new Paragraph(safe(profile.getPhone())))
                        .setBorder(Border.NO_BORDER)
        );

        parties.addCell(
                new Cell()
                        .add(new Paragraph("Bill To").setBold())
                        .add(new Paragraph(safe(invoice.getClientName())))
                        .add(new Paragraph(safe(invoice.getClientEmail())))
                        .add(new Paragraph(safe(invoice.getClientPhone())))
                        .setBorder(Border.NO_BORDER)
        );

        parties.setFixedPosition(
                area.getLeft() + 36,
                area.getTop() - 165,
                area.getWidth() - 72
        );

        canvas.add(parties);


        // ================= FOOTER =================
        Table footer = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // ---- Bank details ----
        Table bank = new Table(2);

        bank.addCell("Bank");
        bank.addCell(safe(profile.getBankName()));

        bank.addCell("Account Holder");
        bank.addCell(safe(profile.getAccountHolder()));

        bank.addCell("Account Number");
        bank.addCell(safe(profile.getAccountNumber()));

        bank.addCell("IFSC");
        bank.addCell(safe(profile.getIfsc()));

        footer.addCell(new Cell().add(bank).setBorder(Border.NO_BORDER));

        // ---- QR ----
        Cell qrCell = new Cell().setBorder(Border.NO_BORDER);

        if (profile.getUpiId() != null && !profile.getUpiId().isBlank()) {

            String upi =
                    "upi://pay?pa=" + profile.getUpiId()
                            + "&pn=" + profile.getAccountHolder()
                            + "&cu=INR";

            BarcodeQRCode qr = new BarcodeQRCode(upi);
            Image qrImg = new Image(qr.createFormXObject(pdf)).scaleToFit(110, 110);

            qrCell.add(new Paragraph("Scan & Pay (UPI)").setBold());
            qrCell.add(qrImg);
        }

        footer.addCell(qrCell);

        footer.setFixedPosition(
                area.getLeft() + 36,
                area.getBottom() + 25,
                area.getWidth() - 72
        );

        canvas.add(footer);

        canvas.close();
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }
}



