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
import java.util.stream.Stream;


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

        // Attach event handler (header + footer)
        pdf.addEventHandler(
                PdfDocumentEvent.END_PAGE,
                new InvoiceHeaderFooter(invoice, profile)
        );

        // Space so content never touches header/footer
        doc.setMargins(260, 36, 163, 36);

        // ================== MAIN TABLE ==================
        Table table = new Table(new float[]{1, 4, 2, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));

        // Header row
        Stream.of("Qty", "Description", "Unit Price", "Discount", "Amount")
                .forEach(text -> table.addHeaderCell(
                        new Cell()
                                .add(new Paragraph(text).setBold())
                                .setBackgroundColor(new DeviceRgb(255, 153, 0))
                ));

        for (InvoiceItem item : invoice.getItems()) {

            double discount = 0; // Placeholder OR your real discount logic

            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(item.getItemName());
            table.addCell(safe(item.getUnitPrice()));
            table.addCell(safe(discount));
            table.addCell(safe(item.getLineTotal()));
        }

        // -------- TOTALS INSIDE SAME TABLE -------
        table.addCell(new Cell(1, 3).setBorder(Border.NO_BORDER));
        table.addCell("Total Amount");
        table.addCell(safe(invoice.getSubtotal()));

        table.addCell(new Cell(1, 3).setBorder(Border.NO_BORDER));
        table.addCell("GST Total");
        table.addCell(safe(invoice.getCgstAmount()
                + invoice.getSgstAmount()
                + invoice.getIgstAmount()));

        table.addCell(new Cell(1, 3).setBorder(Border.NO_BORDER));
        table.addCell("Discount");
        table.addCell(safe(invoice.getDiscountAmount()));

        table.addCell(new Cell(1, 3).setBorder(Border.NO_BORDER));
        table.addCell(new Paragraph("Grand Total").setBold());
        table.addCell(new Paragraph(safe(invoice.getTotalAmount())).setBold());

        doc.add(table);

        doc.close();
        return baos.toByteArray();
    }

    private String safe(Object v) {
        return v == null ? "0.0" : v.toString();
    }


    private double safeDouble(Double d) {
        return d == null ? 0.0 : d;
    }


}

