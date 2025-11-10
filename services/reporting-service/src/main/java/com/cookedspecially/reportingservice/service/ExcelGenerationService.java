package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.domain.ReportType;
import com.cookedspecially.reportingservice.dto.customer.CustomerReportDTO;
import com.cookedspecially.reportingservice.dto.customer.CustomerSummaryDTO;
import com.cookedspecially.reportingservice.dto.operations.DeliveryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.operations.TillSummaryDTO;
import com.cookedspecially.reportingservice.dto.product.CategoryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.product.TopDishReportDTO;
import com.cookedspecially.reportingservice.dto.sales.DailySalesSummaryDTO;
import com.cookedspecially.reportingservice.dto.sales.DetailedInvoiceReportDTO;
import com.cookedspecially.reportingservice.dto.sales.InvoiceReportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Service for generating Excel reports using Apache POI.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelGenerationService {

    /**
     * Generate Excel report based on report type and data.
     */
    public byte[] generateExcel(ReportType reportType, Object data) throws IOException {
        log.info("Generating Excel report for type: {}", reportType);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(reportType.name());

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Generate report based on type
            switch (reportType) {
                case DAILY_INVOICE -> createInvoiceReport(sheet, headerStyle, (List<InvoiceReportDTO>) data);
                case DAILY_SALES_SUMMARY -> createSalesSummaryReport(sheet, headerStyle, (List<DailySalesSummaryDTO>) data);
                case DETAILED_INVOICE -> createDetailedInvoiceReport(sheet, headerStyle, (List<DetailedInvoiceReportDTO>) data);
                case CUSTOMER_LIST -> createCustomerListReport(sheet, headerStyle, (List<CustomerReportDTO>) data);
                case TOP_DISHES -> createTopDishesReport(sheet, headerStyle, (List<TopDishReportDTO>) data);
                case CATEGORY_PERFORMANCE -> createCategoryPerformanceReport(sheet, headerStyle, (List<CategoryPerformanceDTO>) data);
                case DELIVERY_PERFORMANCE -> createDeliveryPerformanceReport(sheet, headerStyle, (List<DeliveryPerformanceDTO>) data);
                default -> throw new IllegalArgumentException("Unsupported report type: " + reportType);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void createInvoiceReport(Sheet sheet, CellStyle headerStyle, List<InvoiceReportDTO> data) {
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Invoice ID", "Invoice Number", "Date", "Customer", "Order Type",
                           "Payment Method", "Subtotal", "Tax", "Discount", "Grand Total", "Status"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = 1;
        for (InvoiceReportDTO invoice : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(invoice.getInvoiceId());
            row.createCell(1).setCellValue(invoice.getInvoiceNumber());
            row.createCell(2).setCellValue(invoice.getInvoiceDate().toString());
            row.createCell(3).setCellValue(invoice.getCustomerName());
            row.createCell(4).setCellValue(invoice.getOrderType());
            row.createCell(5).setCellValue(invoice.getPaymentMethod());
            row.createCell(6).setCellValue(invoice.getSubtotal().doubleValue());
            row.createCell(7).setCellValue(invoice.getCgst().add(invoice.getSgst()).doubleValue());
            row.createCell(8).setCellValue(invoice.getDiscount().doubleValue());
            row.createCell(9).setCellValue(invoice.getGrandTotal().doubleValue());
            row.createCell(10).setCellValue(invoice.getStatus());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createSalesSummaryReport(Sheet sheet, CellStyle headerStyle, List<DailySalesSummaryDTO> data) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Date", "Fulfillment Center", "Total Orders", "Completed", "Cancelled",
                           "Total Sales", "Cash", "Card", "Online", "Net Sales"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (DailySalesSummaryDTO summary : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(summary.getDate().toString());
            row.createCell(1).setCellValue(summary.getFulfillmentCenterName());
            row.createCell(2).setCellValue(summary.getTotalOrders());
            row.createCell(3).setCellValue(summary.getCompletedOrders());
            row.createCell(4).setCellValue(summary.getCancelledOrders());
            row.createCell(5).setCellValue(summary.getTotalSales().doubleValue());
            row.createCell(6).setCellValue(summary.getCashSales().doubleValue());
            row.createCell(7).setCellValue(summary.getCardSales().doubleValue());
            row.createCell(8).setCellValue(summary.getOnlineSales().doubleValue());
            row.createCell(9).setCellValue(summary.getNetSales().doubleValue());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDetailedInvoiceReport(Sheet sheet, CellStyle headerStyle, List<DetailedInvoiceReportDTO> data) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Invoice Number", "Date", "Customer", "Dish", "Category", "Quantity",
                           "Unit Price", "Line Total", "Discount", "Tax", "Payment Method"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (DetailedInvoiceReportDTO detail : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(detail.getInvoiceNumber());
            row.createCell(1).setCellValue(detail.getInvoiceDate().toString());
            row.createCell(2).setCellValue(detail.getCustomerName());
            row.createCell(3).setCellValue(detail.getDishName());
            row.createCell(4).setCellValue(detail.getCategory());
            row.createCell(5).setCellValue(detail.getQuantity());
            row.createCell(6).setCellValue(detail.getUnitPrice().doubleValue());
            row.createCell(7).setCellValue(detail.getLineTotal().doubleValue());
            row.createCell(8).setCellValue(detail.getDiscount().doubleValue());
            row.createCell(9).setCellValue(detail.getTax().doubleValue());
            row.createCell(10).setCellValue(detail.getPaymentMethod());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createCustomerListReport(Sheet sheet, CellStyle headerStyle, List<CustomerReportDTO> data) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Customer ID", "Name", "Email", "Phone", "Registration Date",
                           "Total Orders", "Total Spent", "Average Order Value", "Last Order Date"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (CustomerReportDTO customer : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(customer.getCustomerId());
            row.createCell(1).setCellValue(customer.getName());
            row.createCell(2).setCellValue(customer.getEmail());
            row.createCell(3).setCellValue(customer.getPhone());
            row.createCell(4).setCellValue(customer.getRegistrationDate().toString());
            row.createCell(5).setCellValue(customer.getTotalOrders());
            row.createCell(6).setCellValue(customer.getTotalSpent().doubleValue());
            row.createCell(7).setCellValue(customer.getAverageOrderValue().doubleValue());
            row.createCell(8).setCellValue(customer.getLastOrderDate().toString());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createTopDishesReport(Sheet sheet, CellStyle headerStyle, List<TopDishReportDTO> data) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Dish Name", "Category", "Quantity Sold", "Total Revenue",
                           "Average Price", "Order Count", "Contribution %"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (TopDishReportDTO dish : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dish.getDishName());
            row.createCell(1).setCellValue(dish.getCategory());
            row.createCell(2).setCellValue(dish.getTotalQuantitySold());
            row.createCell(3).setCellValue(dish.getTotalRevenue().doubleValue());
            row.createCell(4).setCellValue(dish.getAveragePrice().doubleValue());
            row.createCell(5).setCellValue(dish.getOrderCount());
            row.createCell(6).setCellValue(dish.getContributionToRevenue().doubleValue());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createCategoryPerformanceReport(Sheet sheet, CellStyle headerStyle, List<CategoryPerformanceDTO> data) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Category", "Items Sold", "Total Revenue", "Revenue %",
                           "Unique Dishes", "Average Dish Price"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (CategoryPerformanceDTO category : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(category.getCategory());
            row.createCell(1).setCellValue(category.getTotalItemsSold());
            row.createCell(2).setCellValue(category.getTotalRevenue().doubleValue());
            row.createCell(3).setCellValue(category.getPercentageOfTotalRevenue().doubleValue());
            row.createCell(4).setCellValue(category.getUniqueDishesOrdered());
            row.createCell(5).setCellValue(category.getAverageDishPrice().doubleValue());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDeliveryPerformanceReport(Sheet sheet, CellStyle headerStyle, List<DeliveryPerformanceDTO> data) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Delivery Person", "Total Deliveries", "On Time", "Late",
                           "On Time %", "Avg Delivery Time", "Avg Rating"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (DeliveryPerformanceDTO delivery : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(delivery.getDeliveryPersonName());
            row.createCell(1).setCellValue(delivery.getTotalDeliveries());
            row.createCell(2).setCellValue(delivery.getOnTimeDeliveries());
            row.createCell(3).setCellValue(delivery.getLateDeliveries());
            row.createCell(4).setCellValue(delivery.getOnTimePercentage());
            row.createCell(5).setCellValue(delivery.getAverageDeliveryTimeMinutes());
            row.createCell(6).setCellValue(delivery.getAverageRating());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
