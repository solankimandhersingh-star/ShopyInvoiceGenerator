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

        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        Rectangle area = page.getPageSize();

        PdfCanvas canvas = new PdfCanvas(page);
        Canvas layout = new Canvas(canvas, area);

        // ========= HEADER (LOGO + TITLE) =========
        Table top = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        try {
            ImageData logo = ImageDataFactory.create("classpath:/static/logo.png");
            Image img = new Image(logo).scaleToFit(90, 60);
            top.addCell(new Cell().add(img).setBorder(Border.NO_BORDER));
        } catch (Exception e) {
            top.addCell(new Cell().setBorder(Border.NO_BORDER));
        }

        top.addCell(
                new Cell()
                        .add(new Paragraph("INVOICE").setBold().setFontSize(18))
                        .add(new Paragraph("Invoice #: " + invoice.getInvoiceNumber()))
                        .add(new Paragraph("Invoice Date: " + invoice.getDate()))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(Border.NO_BORDER)
        );

        top.setFixedPosition(
                area.getLeft() + 36,
                area.getTop() - 80,
                area.getWidth() - 72
        );

        layout.add(top);


// ========= SENDER / RECEIVER BLOCK =========
        Table parties = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        Cell from = new Cell()
                .add(new Paragraph("Bill From").setBold())
                .add(new Paragraph(safe(invoice.getUser().getOrganisation().getName())))
                .add(new Paragraph(safe(profile.getAddress())))
                .add(new Paragraph(safe(profile.getPhone())))
                .setBorder(Border.NO_BORDER);

        Cell to = new Cell()
                .add(new Paragraph("Bill To").setBold())
                .add(new Paragraph(safe(invoice.getClientName())))
                .add(new Paragraph(safe(invoice.getClientEmail())))
                .add(new Paragraph(safe(invoice.getClientPhone())))
                .setBorder(Border.NO_BORDER);

        parties.addCell(from);
        parties.addCell(to);

        parties.setFixedPosition(
                area.getLeft() + 36,
                area.getTop() - 150,
                area.getWidth() - 72
        );

        layout.add(parties);


        // ========= FOOTER =========
        Table footer = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // left side bank details
        Table bank = new Table(2);
        bank.setWidth(UnitValue.createPercentValue(100));

        bank.addCell("Bank");
        bank.addCell(safe(profile.getBankName()));

        bank.addCell("Account Holder");
        bank.addCell(safe(profile.getAccountHolder()));

        bank.addCell("Account Number");
        bank.addCell(safe(profile.getAccountNumber()));

        bank.addCell("IFSC");
        bank.addCell(safe(profile.getIfsc()));

        footer.addCell(new Cell().add(bank).setBorder(Border.NO_BORDER));

        // right side QR
        Cell qrCell = new Cell().setBorder(Border.NO_BORDER);

        if (profile.getUpiId() != null && !profile.getUpiId().isBlank()) {

            String upi = "upi://pay?pa=" + profile.getUpiId()
                    + "&pn=" + profile.getAccountHolder()
                    + "&cu=INR";

            BarcodeQRCode qr = new BarcodeQRCode(upi);
            Image qrImg = new Image(qr.createFormXObject(pdfDoc)).scaleToFit(110, 110);

            qrCell.add(new Paragraph("Scan & Pay (UPI)").setBold());
            qrCell.add(qrImg);
        }

        footer.addCell(qrCell);

        footer.setFixedPosition(
                area.getLeft() + 36,
                area.getBottom() + 20,
                area.getWidth() - 72
        );

        layout.add(footer);

        layout.close();
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }
}


