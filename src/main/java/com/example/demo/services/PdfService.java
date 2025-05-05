package com.example.demo.services;

import com.example.demo.models.PurchaseInvoice;
import com.example.demo.models.PurchaseInvoiceItem;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

public class PdfService {

    public static byte[] exportInvoiceToPdf(PurchaseInvoice invoice) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Titre principal
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Purchase Invoice", titleFont));
            document.add(new Paragraph(" ")); // Ligne vide pour espacement

            // Informations générales sur la facture
            Font infoFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            document.add(new Paragraph("Name: " + invoice.getName(), infoFont));
            document.add(new Paragraph("Status: " + invoice.getStatus(), infoFont));
            document.add(new Paragraph("Supplier: " + invoice.getSupplier(), infoFont));
            document.add(new Paragraph("Total: " + invoice.getTotal(), infoFont));
            document.add(new Paragraph("Outstanding Amount: " + invoice.getOutstandingAmount(), infoFont));
            document.add(new Paragraph("Posting Date: " + invoice.getPostingDate(), infoFont));
            document.add(new Paragraph("Due Date: " + invoice.getDueDate(), infoFont));
            document.add(new Paragraph(" ")); // Ligne vide pour espacement

            // Tableau des articles
            PdfPTable table = new PdfPTable(5); // 5 colonnes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // En-têtes du tableau
            Font headerFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            table.addCell(new PdfPCell(new Paragraph("Item Code", headerFont)));
            table.addCell(new PdfPCell(new Paragraph("Item Name", headerFont)));
            table.addCell(new PdfPCell(new Paragraph("Quantity", headerFont)));
            table.addCell(new PdfPCell(new Paragraph("Rate", headerFont)));
            table.addCell(new PdfPCell(new Paragraph("Amount", headerFont)));

            // Contenu du tableau
            Font cellFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            for (PurchaseInvoiceItem item : invoice.getItems()) {
                table.addCell(new PdfPCell(new Paragraph(item.getItemCode(), cellFont)));
                table.addCell(new PdfPCell(new Paragraph(item.getItemName(), cellFont)));
                table.addCell(new PdfPCell(new Paragraph(String.valueOf(item.getQty()), cellFont)));
                table.addCell(new PdfPCell(new Paragraph(String.valueOf(item.getRate()), cellFont)));
                table.addCell(new PdfPCell(new Paragraph(String.valueOf(item.getAmount()), cellFont)));
            }

            document.add(table);

            // Remarques
            if (invoice.getRemarks() != null && !invoice.getRemarks().isEmpty()) {
                document.add(new Paragraph("Remarks: " + invoice.getRemarks(), infoFont));
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }
}
