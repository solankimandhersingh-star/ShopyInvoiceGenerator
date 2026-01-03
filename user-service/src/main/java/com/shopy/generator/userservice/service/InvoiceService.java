package com.shopy.generator.userservice.service;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.shopy.generator.userservice.entity.Invoice;
import com.shopy.generator.userservice.entity.InvoiceItem;
import com.shopy.generator.userservice.entity.UserProfile;
import com.shopy.generator.userservice.repository.ClientRepository;
import com.shopy.generator.userservice.repository.InvoiceRepository;
import com.shopy.generator.userservice.repository.UserProfileRepository;
import com.shopy.generator.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository repo;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final UserProfileRepository userProfileRepository;


    public List<Invoice> userInvoices(Long userId) {
        return repo.findByUserId(userId);
    }


    public void saveInvoice(
            Long userId,
            Long clientId,
            Double subtotal,
            Double cgstPercent,
            Double sgstPercent,
            Double igstPercent,
            Double discountPercent,
            Double discountAmount,
            Double totalAmount,

            List<String> itemNames,
            List<Integer> quantities,
            List<Double> unitPrices
    ) {

        var user = userRepository.findById(userId).orElseThrow();
        var client = clientRepository.findById(clientId).orElseThrow();

        double cgstAmt = cgstPercent == null ? 0 : subtotal * (cgstPercent / 100);
        double sgstAmt = sgstPercent == null ? 0 : subtotal * (sgstPercent / 100);
        double igstAmt = igstPercent == null ? 0 : subtotal * (igstPercent / 100);

        long seq = repo.countByUserId(userId) + 1;
        String prefix = user.getOrganisation().getName()
                .replaceAll("\\s+", "")
                .toUpperCase();

        if (prefix.length() > 3) prefix = prefix.substring(0, 3);

        String invoiceNumber = String.format("%s%05d/%d",
                prefix, seq, LocalDate.now().getYear());

        Invoice invoice = Invoice.builder()
                .date(LocalDate.now())
                .invoiceNumber(invoiceNumber)
                .clientName(client.getName())
                .clientEmail(client.getEmail())
                .clientPhone(client.getPhone())
                .subtotal(subtotal)
                .cgstPercent(cgstPercent)
                .sgstPercent(sgstPercent)
                .igstPercent(igstPercent)
                .cgstAmount(cgstAmt)
                .sgstAmount(sgstAmt)
                .igstAmount(igstAmt)
                .discountPercent(discountPercent)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .status("UNPAID")
                .user(user)
                .build();

        List<InvoiceItem> items = new ArrayList<>();

        for (int i = 0; i < itemNames.size(); i++) {

            double line = unitPrices.get(i) * quantities.get(i);

            InvoiceItem item = InvoiceItem.builder()
                    .invoice(invoice)
                    .itemName(itemNames.get(i))
                    .unitPrice(unitPrices.get(i))
                    .quantity(quantities.get(i))
                    .lineTotal(line)
                    .build();

            items.add(item);
        }

        invoice.setItems(items);

        repo.save(invoice);
    }


    public Invoice getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public byte[] generateInvoicePdf(Long id) throws Exception {

        Invoice invoice = repo.findById(id).orElseThrow();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        UserProfile profile = userProfileRepository
                .findByUserId(invoice.getUser().getId())
                .orElse(new UserProfile());

        // Attach header/footer handler
        pdf.addEventHandler(
                PdfDocumentEvent.END_PAGE,
                new InvoiceHeaderFooter(invoice, profile)
        );

        // Leave space for header + footer
        doc.setMargins(190, 36, 150, 36);


        // ================= ITEMS TABLE =================
        Table items = new Table(new float[]{1, 4, 1, 2, 2, 2, 2, 2});
        items.setWidth(UnitValue.createPercentValue(100));

        String[] heads = {
                "Item No", "Description", "Qty",
                "Unit Price", "CGST", "SGST", "IGST", "Amount"
        };

        for (String h : heads) {
            items.addHeaderCell(new Paragraph(h).setBold());
        }

        int i = 1;
        for (InvoiceItem item : invoice.getItems()) {

            items.addCell(String.valueOf(i++));
            items.addCell(safe(item.getItemName()));
            items.addCell(safe(item.getQuantity()));
            items.addCell(safe(item.getUnitPrice()));

            items.addCell(safe(invoice.getCgstPercent()));
            items.addCell(safe(invoice.getSgstPercent()));
            items.addCell(safe(invoice.getIgstPercent()));

            items.addCell(safe(item.getLineTotal()));
        }

        doc.add(items);
        doc.add(new Paragraph("\n"));


        // ================= TOTALS =================
        Table totals = new Table(new float[]{2, 1});
        totals.setWidth(UnitValue.createPercentValue(40));
        totals.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        totals.addCell("Subtotal");
        totals.addCell(safe(invoice.getSubtotal()));

        totals.addCell("GST");
        totals.addCell(
                safe(
                        invoice.getCgstAmount()
                                + invoice.getSgstAmount()
                                + invoice.getIgstAmount()
                )
        );

        totals.addCell("Discount");
        totals.addCell(safe(invoice.getDiscountAmount()));

        totals.addCell(new Paragraph("Grand Total").setBold());
        totals.addCell(new Paragraph(safe(invoice.getTotalAmount())).setBold());

        doc.add(totals);

        doc.close();
        return baos.toByteArray();
    }


    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }


}

