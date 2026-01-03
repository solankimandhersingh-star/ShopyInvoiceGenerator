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


        // ================== TOP COLORED HEADER BAR ==================
        canvas.saveState()
                .setFillColorRgb(0.95f, 0.55f, 0.12f)   // orange
                .rectangle(area.getLeft(), area.getTop() - 40, area.getWidth(), 40)
                .fill()
                .restoreState();

        layout.showTextAligned(
                new Paragraph("INVOICE")
                        .setBold()
                        .setFontSize(22)
                        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE),
                area.getRight() - 80,
                area.getTop() - 25,
                TextAlignment.RIGHT
        );


        // ================== COMPANY NAME UNDER BAR ==================
        layout.add(
                new Paragraph(safe(invoice.getUser().getOrganisation().getName()))
                        .setBold()
                        .setFontSize(12)
                        .setMarginTop(10)
        );


        // ================== INVOICE INFO RIGHT SIDE ==================
        Table info = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(220)
                .setTextAlignment(TextAlignment.RIGHT);

        info.addCell(noBorder("Invoice #:"));
        info.addCell(noBorder(safe(invoice.getInvoiceNumber())));

        info.addCell(noBorder("Date:"));
        info.addCell(noBorder(safe(invoice.getDate())));

        info.setFixedPosition(area.getRight() - 250, area.getTop() - 110, 220);
        layout.add(info);


        // ================== SECTION BAR: BILL TO ==================
        canvas.saveState()
                .setFillColorRgb(0.95f, 0.55f, 0.12f)
                .rectangle(area.getLeft(), area.getTop() - 150, area.getWidth(), 18)
                .fill()
                .restoreState();

        layout.showTextAligned(
                new Paragraph("Bill To:")
                        .setBold()
                        .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE)
                        .setFontSize(10),
                area.getLeft() + 10,
                area.getTop() - 145,
                TextAlignment.LEFT
        );


        // ================== BILL FROM + BILL TO DETAILS ==================
        Table parties = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        parties.addCell(noBorderBlock(
                "Contact name",
                safe(invoice.getUser().getOrganisation().getName()),
                safe(profile.getAddress()),
                safe(profile.getPhone())
        ));

        parties.addCell(noBorderBlock(
                "Customer",
                safe(invoice.getClientName()),
                safe(invoice.getClientEmail()),
                safe(invoice.getClientPhone())
        ));

        parties.setFixedPosition(area.getLeft() + 10, area.getTop() - 210, area.getWidth() - 20);
        layout.add(parties);


        // ================== FOOTER REMAINS SAME (BANK + QR) ==================
        // (KEEP YOUR FOOTER CODE)




    // ---------- FOOTER ----------
        Table footer = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // Bank table
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

        // QR (UPI)
        Cell qr = new Cell().setBorder(Border.NO_BORDER);

        if (profile.getUpiId() != null && !profile.getUpiId().isBlank()) {

            String upi = "upi://pay?pa=" + profile.getUpiId()
                    + "&pn=" + profile.getAccountHolder()
                    + "&cu=INR";

            BarcodeQRCode code = new BarcodeQRCode(upi);
            Image qimg = new Image(code.createFormXObject(pdfDoc)).scaleToFit(110, 110);

            qr.add(new Paragraph("Scan & Pay (UPI)").setBold());
            qr.add(qimg);
        }

        footer.addCell(qr);

        footer.setFixedPosition(area.getLeft() + 36, area.getBottom() + 20, area.getWidth() - 72);
        layout.add(footer);

        layout.close();
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }

    private Cell noBorder(String t) {
        return new Cell().add(new Paragraph(t)).setBorder(Border.NO_BORDER);
    }

    private Cell noBorderBlock(String title, String a, String b, String c) {
        return new Cell()
                .add(new Paragraph(title).setBold())
                .add(new Paragraph(a))
                .add(new Paragraph(b))
                .add(new Paragraph(c))
                .setBorder(Border.NO_BORDER);
    }

}




