package com.example.admin.firstversionapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ManagementActivity extends AppCompatActivity {

    private String systemCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        findViewById(R.id.constraintLayout14).setOnClickListener(buttonsClickListener);
        findViewById(R.id.patientProfile).setOnClickListener(buttonsClickListener);
        findViewById(R.id.oldUsers).setOnClickListener(buttonsClickListener);
        findViewById(R.id.newUsers).setOnClickListener(buttonsClickListener);
        findViewById(R.id.back).setOnClickListener(buttonsClickListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
    }

    private View.OnClickListener buttonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent managementActivity = new Intent();
            managementActivity.putExtra("systemCode", systemCode);
            switch (view.getId()){
                case R.id.constraintLayout14:
                    startActivity(managementActivity.setClass(ManagementActivity.this, AdminProfileEditorActivity.class));
                    break;

                case R.id.patientProfile:
                    startActivity(managementActivity.setClass(ManagementActivity.this, PatientProfileManagementActivity.class));
                    break;

                case R.id.oldUsers:
                    managementActivity.putExtra("active", "1");
                    startActivity(managementActivity.setClass(ManagementActivity.this, ExistUsersActivity.class));
                    break;

                case R.id.newUsers:
                    managementActivity.putExtra("active", "0");
                    startActivity(managementActivity.setClass(ManagementActivity.this, ExistUsersActivity.class));
                    break;

                case R.id.back:
                    startActivity(managementActivity.setClass(ManagementActivity.this, AdminHomeActivity.class));
                    break;
            }
        }
    };
}
