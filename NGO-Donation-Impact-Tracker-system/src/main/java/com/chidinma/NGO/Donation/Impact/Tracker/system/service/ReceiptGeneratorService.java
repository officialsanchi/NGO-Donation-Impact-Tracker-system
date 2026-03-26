package com.chidinma.NGO.Donation.Impact.Tracker.system.service;

import org.springframework.stereotype.Service;

@Service
public class ReceiptGeneratorService {

    public byte[] generateTaxReceipt(Donation donation) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Header
            document.add(new Paragraph("OFFICIAL DONATION RECEIPT")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("NGO Organization Name")
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Tax ID: 12-3456789 | Registered Charity")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // Receipt details
            Table details = new Table(2);
            details.addCell("Receipt Number:").addCell(donation.getReceiptNumber());
            details.addCell("Date of Donation:").addCell(donation.getDonationDate().toString());
            details.addCell("Donor Name:").addCell(donation.getDonor().getDisplayName());
            details.addCell("Donor ID:").addCell(donation.getDonor().getDonorNumber());
            document.add(details);

            document.add(new Paragraph("\n"));

            // Donation amount
            document.add(new Paragraph("DONATION DETAILS").setBold());
            Table donationTable = new Table(2);
            donationTable.addCell("Amount:").addCell(
                    donation.getCurrency().getSymbol() + donation.getAmount().toString());
            donationTable.addCell("Transaction ID:").addCell(donation.getTransactionId());
            donationTable.addCell("Payment Method:").addCell(donation.getPaymentMethod().getDescription());

            if (donation.getDesignatedProject() != null) {
                donationTable.addCell("Designation:").addCell(donation.getDesign
}
