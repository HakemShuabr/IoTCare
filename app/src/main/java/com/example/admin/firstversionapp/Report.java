package com.example.admin.firstversionapp;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Admin on 07/09/2017.
 */

public class Report {
    public void setTitle(String title) {
        this.title = title;
    }

    public void setReport(String report) {
        this.reportSummary = report;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getTitle() {

        return title;
    }

    public String getReport() {
        return reportSummary;
    }

    public String getReportDate() {
        return reportDate;
    }

    private String title;
    private String reportSummary;
    private String reportDate;
    private String id;

    public Report() {
    }

    public Report(String id, String title, String reportSummary) {
        this.title = title;
        this.reportSummary = reportSummary;
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        reportDate = c.get(c.DAY_OF_MONTH)+"-"+(c.get(c.MONTH)+1)+"-"+c.get(c.YEAR)+" "+c.get(c.HOUR_OF_DAY)+":"+c.get(c.MINUTE);
        int AM_PM = c.get(Calendar.AM_PM);
        if(AM_PM == Calendar.AM){
            reportDate+="AM";
        }
        else {
            reportDate+="PM";
        }

        this.id = id;
    }
}
