package com.example.admin.firstversionapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientActivity extends AppCompatActivity {
    private TextView username;
    private TextView disability;
    private TextView phone;
    private TextView temperature;
    private TextView heartbeat;
    private ImageView image;

    private String systemCode;
    private String userRole;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        username = (TextView)findViewById(R.id.username);
        disability = (TextView)findViewById(R.id.disability);
        phone = (TextView)findViewById(R.id.phone);
        temperature = (TextView)findViewById(R.id.temperature);
        heartbeat = (TextView)findViewById(R.id.heart);
        image = (ImageView)findViewById(R.id.image);

        findViewById(R.id.back).setOnClickListener(buttonClicksListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        username.setText(intent.getExtras().getString("username"));
        phone.setText(intent.getExtras().getString("phone"));
        disability.setText(intent.getExtras().getString("disability"));
        userRole = intent.getExtras().getString("role");

        DatabaseReference patientData = database.getReference(systemCode);
        patientData.addListenerForSingleValueEvent(valueEventListener);
        patientData.removeEventListener(valueEventListener);
    }

    private View.OnClickListener buttonClicksListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent homeActivity = new Intent();
            homeActivity.putExtra("systemCode", systemCode);
            homeActivity.putExtra("username", username.getText().toString());
            switch (view.getId()){
                case R.id.back:
                    if(userRole.equals("patient"))
                        startActivity(homeActivity.setClass(PatientActivity.this, PatientHomeActivity.class));

                    else if(userRole.equals("admin"))
                        startActivity(homeActivity.setClass(PatientActivity.this, AdminHomeActivity.class));

                    else if(userRole.equals("doctor"))
                        startActivity(homeActivity.setClass(PatientActivity.this, DoctorHomeActivity.class));

                    else if(userRole.equals("guardian"))
                        startActivity(homeActivity.setClass(PatientActivity.this, GuardActivity.class));

                    break;
            }
        }
    };

    private ValueEventListener valueEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.child("patient").exists()) {
                if (dataSnapshot.child("patient").child("image").exists()) {
                    Uri imageURI = Uri.parse(dataSnapshot.child("patient").child("image").getValue().toString());
                    image.setImageURI(imageURI);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}


