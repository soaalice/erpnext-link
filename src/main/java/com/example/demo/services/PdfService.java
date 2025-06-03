package com.example.demo.services;

import com.example.demo.models.PurchaseInvoice;
import com.example.demo.models.PurchaseInvoiceItem;
import com.example.demo.models.hr.SalaryComponent;
import com.example.demo.models.hr.SalarySlip;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
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

    public static byte[] exportSalarySlipToPdf(SalarySlip salarySlip) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Company Header Section
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font subtitleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);

            // Header with company details
            Paragraph header = new Paragraph("SALARY SLIP", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            // Pay Period
            Paragraph payPeriod = new Paragraph(String.format("Pay Period: %s to %s",
                    salarySlip.getStartDate(), salarySlip.getEndDate()), subtitleFont);
            payPeriod.setAlignment(Element.ALIGN_CENTER);
            payPeriod.setSpacingBefore(10f);
            payPeriod.setSpacingAfter(20f);
            document.add(payPeriod);

            // Employee Details Table
            PdfPTable employeeDetails = new PdfPTable(2);
            employeeDetails.setWidthPercentage(100);
            employeeDetails.setSpacingBefore(10f);
            employeeDetails.setSpacingAfter(10f);

            addTableCell(employeeDetails, "Employee Name:", headerFont);
            addTableCell(employeeDetails, salarySlip.getEmployeeName(), normalFont);
            addTableCell(employeeDetails, "Company:", headerFont);
            addTableCell(employeeDetails, salarySlip.getCompany(), normalFont);
            addTableCell(employeeDetails, "Posting Date:", headerFont);
            addTableCell(employeeDetails, salarySlip.getPostingDate(), normalFont);
            addTableCell(employeeDetails, "Total Working Days:", headerFont);
            addTableCell(employeeDetails, String.valueOf(salarySlip.getTotalWorkingDays()), normalFont);
            addTableCell(employeeDetails, "Absent Days:", headerFont);
            addTableCell(employeeDetails, String.valueOf(salarySlip.getAbsentDays()), normalFont);
            document.add(employeeDetails);

            // Earnings & Deductions Section
            document.add(new Paragraph("Earnings & Deductions", subtitleFont));

            // Create table for earnings and deductions
            PdfPTable salaryTable = new PdfPTable(3);
            salaryTable.setWidthPercentage(100);
            salaryTable.setSpacingBefore(10f);
            salaryTable.setSpacingAfter(10f);
            float[] columnWidths = {3f, 2f, 2f};
            salaryTable.setWidths(columnWidths);

            // Table Headers
            addTableHeader(salaryTable, "Component", headerFont);
            addTableHeader(salaryTable, "Type", headerFont);
            addTableHeader(salaryTable, "Amount", headerFont);

            // Add Earnings
            for (SalaryComponent earning : salarySlip.getEarnings()) {
                addTableCell(salaryTable, earning.getSalaryComponent(), normalFont);
                addTableCell(salaryTable, "Earning", normalFont);
                addTableCell(salaryTable, String.format("%.2f", earning.getAmount()), normalFont);
            }

            // Add Deductions
            for (SalaryComponent deduction : salarySlip.getDeductions()) {
                addTableCell(salaryTable, deduction.getSalaryComponent(), normalFont);
                addTableCell(salaryTable, "Deduction", normalFont);
                addTableCell(salaryTable, String.format("%.2f", deduction.getAmount()), normalFont);
            }
            document.add(salaryTable);

            // Summary Section
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(20f);

            addSummaryRow(summaryTable, "Gross Pay:", String.format("%.2f", salarySlip.getGrossPay()), headerFont);
            addSummaryRow(summaryTable, "Total Deductions:", String.format("%.2f", salarySlip.getTotalDeduction()), headerFont);
            addSummaryRow(summaryTable, "Net Pay:", String.format("%.2f", salarySlip.getNetPay()), headerFont);
            addSummaryRow(summaryTable, "Total Income Tax:", String.format("%.2f", salarySlip.getTotalIncomingTax()), headerFont);
            document.add(summaryTable);

            // Amount in Words
            Paragraph amountInWords = new Paragraph("Amount in Words: " + salarySlip.getTotalInWords(),
                    new Font(Font.HELVETICA, 10, Font.ITALIC));
            amountInWords.setSpacingBefore(20f);
            document.add(amountInWords);

        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        return outputStream.toByteArray();
    }

    private static void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBackgroundColor(new java.awt.Color(240, 240, 240));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addSummaryRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Paragraph(label, font));
        labelCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell valueCell = new PdfPCell(new Paragraph(value, font));
        valueCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
