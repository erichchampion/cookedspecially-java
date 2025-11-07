package com.cookedspecially.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Abhishek on 2/22/2017.
 */
public class AnalyticsReportConstant {

    public final static String COLUMN_DELIMITER = ",";
    public final static String WHITESPACE = " ";
    public static final String ALL_REPORTS = "all";
    public static final String COMMA_SEPARATOR = ",";
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String REPORT_DESIGN_EXTENSION = "rptdesign";

    /* Constants that represent the output formats supported */
    public static final String PARAM_OUTPUT_FORMAT = "outputFormat";
    public static final String HTML_FORMAT = "html";
    public static final String PDF_FORMAT = "pdf";
    public static final String DOC_FORMAT = "doc";
    public static final String XLS_FORMAT = "xls";
    public static final String CSV_FORMAT = "csv";

    /*  Report constants */
    public final static String REPORT_ID = "report.reportId";
    public final static String REPORT_FORMAT = "report.format";

    /* Report parameter constants */
    public final static String REPORT_PARAM_PREFIX = "report.param.";
    public final static String REPORT_PARAM_START_DATE = "report.param.startDate";
    public final static String REPORT_PARAM_END_DATE = "report.param.endDate";

    /* Report period constants */
    public final static String REPORT_PERIOD = "report.period";
    public final static String REPORT_PERIOD_DAYS_FROM_START_DATE = "report.period.daysFromStartDate";
    public final static String REPORT_PERIOD_DAYS_FROM_END_DATE = "report.period.daysFromEndDate";

    /* Report email constants */
    public final static String REPORT_EMAIL_PREFIX = "report.email.";
    public final static String REPORT_EMAIL_HOST = "report.email.host";
    public final static String REPORT_EMAIL_FROM = "report.email.from";
    public final static String REPORT_EMAIL_TO = "report.email.to";
    public final static String REPORT_EMAIL_CC = "report.email.cc";
    public final static String REPORT_EMAIL_BCC = "report.email.bcc";
    public final static String REPORT_EMAIL_SUBJECT = "report.email.subject";
    public final static String REPORT_EMAIL_BODY = "report.email.body";
    public final static String REPORT_EMAIL_BODY_URL = "report.email.body.url";

    /* Used for report output name */
    public static final DateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("dd-MMM-yyyy-HHmmss");
}
