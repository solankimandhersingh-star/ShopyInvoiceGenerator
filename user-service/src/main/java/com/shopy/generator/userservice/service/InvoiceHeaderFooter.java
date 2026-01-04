package com.shopy.generator.userservice.service;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
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

import static com.itextpdf.kernel.colors.DeviceCmyk.CYAN;

public class InvoiceHeaderFooter implements IEventHandler {

    private final Invoice invoice;
    private final UserProfile profile;

    public InvoiceHeaderFooter(Invoice invoice, UserProfile profile) {
        this.invoice = invoice;
        this.profile = profile;
    }

    @Override
    public void handleEvent(Event event) {

        PdfDocumentEvent de = (PdfDocumentEvent) event;
        Rectangle page = de.getPage().getPageSize();
        Canvas canvas = new Canvas(new PdfCanvas(de.getPage()), page);

        // ============= ORANGE HEADER BAND =============
        Table band = new Table(2)
                .setWidth(UnitValue.createPercentValue(100));

        // ---- Logo ----
        Cell logoCell = new Cell().setBorder(Border.NO_BORDER);

        try {
            ImageData logo = ImageDataFactory.create("classpath:/static/logo.png");
            Image img = new Image(logo).scaleToFit(100, 100);
            logoCell.add(img);
        } catch (Exception ignored) {
        }

        // ---- Invoice Label ----
        band.addCell(
                        new Cell()
                                .add(new Paragraph("INVOICE")
                                        .setBold()
                                        .setFontSize(32)
                                        .setFontColor(ColorConstants.WHITE)
                                        .setTextAlignment(TextAlignment.CENTER)))
                .setBackgroundColor(new DeviceRgb(33, 33, 33));

        band.addCell(
                logoCell.add(
                        new Paragraph(safe(invoice.getUser().getOrganisation().getName()))
                                .setBold().setFontSize(22)
                                .setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.RIGHT)
                ).setBackgroundColor(new DeviceRgb(33, 33, 33))
        );



        band.setFixedPosition(page.getLeft(), page.getTop() - 70, page.getWidth());
        canvas.add(band);


        // ============ COMPANY + INVOICE META ============
        Table header = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        header.addCell(
                new Cell()
                        .add(new Paragraph(safe(profile.getAddress())))
                        .add(new Paragraph("Phone: " + safe(profile.getPhone())))
                        .setBorder(Border.NO_BORDER).setBackgroundColor(ColorConstants.LIGHT_GRAY)
        );

        header.addCell(
                new Cell()
                        .add(new Paragraph("Date: " + invoice.getDate()))
                        .add(new Paragraph("Invoice #: " + invoice.getInvoiceNumber()))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorder(Border.NO_BORDER).setBackgroundColor(ColorConstants.LIGHT_GRAY)
        );

        header.setFixedPosition(page.getLeft() + 20,
                page.getTop() - 155,
                page.getWidth() - 40);

        canvas.add(header);


        // ============ CUSTOMER SECTION =============
        Table bill = new Table(1).setWidth(UnitValue.createPercentValue(100));

        bill.addCell(
                new Cell()
                        .add(new Paragraph("Bill To:").setBold())
                        .add(new Paragraph(safe(invoice.getClientName())))
                        .add(new Paragraph(safe(invoice.getClientEmail())))
                        .add(new Paragraph(safe(invoice.getClientPhone())))
                        .setBorder(Border.NO_BORDER).setBackgroundColor(ColorConstants.LIGHT_GRAY)
        );

        bill.setFixedPosition(page.getLeft() + 20,
                page.getTop() - 250,
                page.getWidth() - 40);

        canvas.add(bill);


        // ============ FOOTER (BANK + QR) ============
        Table footer = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // Bank left
        Table bank = new Table(2);
        bank.addCell("Bank");
        bank.addCell(safe(profile.getBankName()));

        bank.addCell("Account Holder");
        bank.addCell(safe(profile.getAccountHolder()));

        bank.addCell("Account Number");
        bank.addCell(safe(profile.getAccountNumber()));

        bank.addCell("IFSC");
        bank.addCell(safe(profile.getIfsc()));

        footer.addCell(new Cell().add(bank).setBorder(Border.NO_BORDER)).setBorder(Border.NO_BORDER);

        // QR right
        Cell qrCell = new Cell().setBorder(Border.NO_BORDER);

        if (profile.getUpiId() != null && !profile.getUpiId().isBlank()) {
            String upi = "upi://pay?pa=" + profile.getUpiId()
                    + "&pn=" + profile.getAccountHolder()
                    + "&cu=INR";

            BarcodeQRCode qr = new BarcodeQRCode(upi);
            Image qrImg = new Image(qr.createFormXObject(de.getDocument()))
                    .scaleToFit(110, 110);

            qrCell.add(new Paragraph("Scan & Pay (UPI)").setBold());
            qrCell.add(qrImg);
        }

        footer.addCell(qrCell);

        footer.setFixedPosition(
                page.getLeft() + 20,
                page.getBottom() + 30,
                page.getWidth() - 40
        );

        canvas.add(footer);

        canvas.close();
    }

    private String safe(Object v) {
        return v == null ? "" : v.toString();
    }
}







