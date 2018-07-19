package com.example.admin.firstversionapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class DoctorReportActivity extends AppCompatActivity {
    private EditText id;
    private EditText title;
    private EditText reportSummary;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private String systemCode;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_report);

        id = (EditText) findViewById(R.id.id);
        title = (EditText) findViewById(R.id.title);
        reportSummary = (EditText) findViewById(R.id.report);

        findViewById(R.id.back).setOnClickListener(buttonsClickListener);
        findViewById(R.id.done).setOnClickListener(buttonsClickListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        username = intent.getExtras().getString("username");
    }

    private View.OnClickListener buttonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.done:
                    if(validateInput()) {
                        final DatabaseReference doctorData = database.getReference(systemCode).child("users").child("doctors").child(username);
                        Report report = new Report(id.getText().toString(), title.getText().toString(),
                                reportSummary.getText().toString());
                        doctorData.child("report").child(id.getText().toString()).setValue(report);
                        id.setText("");
                        title.setText("");
                        reportSummary.setText("");
                        Toast.makeText(DoctorReportActivity.this, "your report is saved successfully", Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.back:
                    Intent homeActivity = new Intent(view.getContext(), DoctorHomeActivity.class);
                    homeActivity.putExtra("systemCode", systemCode);
                    homeActivity.putExtra("username", username);
                    startActivity(homeActivity);
                    break;
            }
        }
    };

    private boolean validateInput() {
        boolean valid = true;
        if (id.getText().toString().isEmpty()) {
            id.setHint("report ID is required");
            id.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }

        if (title.getText().toString().isEmpty()) {
            title.setHint("title is required");
            title.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }

        if (reportSummary.getText().toString().isEmpty()) {
            reportSummary.setHint("summary report is required");
            reportSummary.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }
        return valid;
    }
}
