package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.domain.ReportType;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Service for generating PDF reports using iText.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

    /**
     * Generate PDF report based on report type and data.
     */
    public byte[] generatePdf(ReportType reportType, Object data) throws IOException {
        log.info("Generating PDF report for type: {}", reportType);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Add title
        document.add(new Paragraph(reportType.name().replace("_", " ") + " Report")
            .setFontSize(20)
            .setBold());

        // Add report content based on type
        // Note: This is a simplified implementation
        // In production, you would create detailed tables for each report type
        document.add(new Paragraph("Report data will be displayed here."));

        document.close();
        return outputStream.toByteArray();
    }
}
