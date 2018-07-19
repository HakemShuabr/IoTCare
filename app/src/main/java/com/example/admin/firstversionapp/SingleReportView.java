package com.example.admin.firstversionapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SingleReportView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_report_view);
        Intent intent = getIntent();
        ((TextView)findViewById(R.id.reportTitle)).setText(intent.getExtras().getString("reportTitle"));
        ((TextView)findViewById(R.id.reportDate)).setText(intent.getExtras().getString("reportDate"));
        ((TextView)findViewById(R.id.name)).setText(intent.getExtras().getString("name"));
        ((TextView)findViewById(R.id.content)).setText(intent.getExtras().getString("reportContent"));
    }
}
