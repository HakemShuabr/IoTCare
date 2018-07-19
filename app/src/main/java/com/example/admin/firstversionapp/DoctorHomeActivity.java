package com.example.admin.firstversionapp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

public class DoctorHomeActivity extends AppCompatActivity {

    private DrawerLayout menuDrawer;
    private ActionBarDrawerToggle menuToggle;

    private String systemCode;
    private Doctor doctor;

    private TextView maxH;
    private TextView dateMH;
    private TextView minH;
    private TextView dateNH;

    private TextView maxT;
    private TextView dateMT;
    private TextView minT;
    private TextView dateNT;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Calendar c;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);

        //////
        maxH = (TextView)findViewById(R.id.dmaxHeart);
        dateMH = (TextView)findViewById(R.id.ddateMH);
        minH = (TextView)findViewById(R.id.dminHeart);
        dateNH = (TextView)findViewById(R.id.ddateNH);

        /////

        ///////
        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        String username = intent.getExtras().getString("username");

        Intent intentService = new Intent(this, BackgroundService.class);
        BackgroundService.systemCode = systemCode;
        startService(intentService);

        doctor = new Doctor();
        updateData();

        DatabaseReference doctorData = database.getReference(systemCode).child("users").child("doctors").child(username);
        doctorData.addListenerForSingleValueEvent(valueEventListener);
        doctorData.removeEventListener(valueEventListener);

        ///////////////

        setContentView(R.layout.activity_doctor_home);

        menuDrawer = (DrawerLayout)findViewById(R.id.navMenu);
        menuToggle = new ActionBarDrawerToggle(this, menuDrawer,
                R.string.open, R.string.close);

//        menuDrawer.setOnClickListener(buttonsClickListener);
        menuDrawer.addDrawerListener(menuToggle);
        menuToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case (R.id.nav_myacc):
                        Intent profileActivity = new Intent(DoctorHomeActivity.this, DoctorProfileActivity.class);
                        profileActivity.putExtra("systemCode", systemCode);
                        profileActivity.putExtra("username", doctor.getName().toString());
                        profileActivity.putExtra("email", doctor.getEmail().toString());
                        profileActivity.putExtra("phone", doctor.getNumber().toString());
                        profileActivity.putExtra("specialization", doctor.getSpecialization().toString());
                        startActivity(profileActivity);
                        break;

                    case (R.id.nav_addReport):
                        Intent reportActivity = new Intent(DoctorHomeActivity.this, DoctorReportActivity.class);
                        reportActivity.putExtra("systemCode", systemCode);
                        reportActivity.putExtra("username", doctor.getName().toString());
                        startActivity(reportActivity);
                        break;

                    case (R.id.nav_Report):
                        Intent viewReportActivity = new Intent(DoctorHomeActivity.this, ViewReport.class);
                        viewReportActivity.putExtra("systemCode", systemCode);
                        viewReportActivity.putExtra("username", doctor.getName().toString());
                        startActivity(viewReportActivity);
                        break;

                    case (R.id.nav_health):
                        DatabaseReference patientData = database.getReference(systemCode).child("patient");
                        patientData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    PDU patient = dataSnapshot.getValue(PDU.class);
                                    Intent PatientActivity = new Intent(DoctorHomeActivity.this, PatientActivity.class);
                                    PatientActivity.putExtra("systemCode", systemCode);
                                    PatientActivity.putExtra("username", patient.getName().toString());
                                    PatientActivity.putExtra("phone", patient.getPhone().toString());
                                    PatientActivity.putExtra("disability", patient.getDisability().toString());
                                    PatientActivity.putExtra("role", "doctor");
                                    startActivity(PatientActivity);
                                }
                                else{
                                    Toast.makeText(DoctorHomeActivity.this, "Your patient does not have profile yet!", Toast.LENGTH_LONG).show();
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;

                    case (R.id.nav_room):
                        Intent roomActivity = new Intent(DoctorHomeActivity.this, RoomActivity.class);
                        roomActivity.putExtra("systemCode", systemCode);
                        roomActivity.putExtra("username", doctor.getName().toString());
                        roomActivity.putExtra("role", "Doctor");
                        startActivity(roomActivity);
                        break;

                    case (R.id.nav_data):
                        Intent dataActivity = new Intent(DoctorHomeActivity.this, ViewData.class);
                        dataActivity.putExtra("systemCode", systemCode);
                        startActivity(dataActivity);
                        break;

                    case (R.id.nav_logout):
                        Intent logoutActivity = new Intent(DoctorHomeActivity.this, LoginActivity.class);
                        startActivity(logoutActivity);
                        break;
                }
                return false;
            }
        });

        timer = new CountDownTimer(3000, 20) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try{
                    updateData();
                    timer.start();
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        ((TextView)findViewById(R.id.textView)).setText(item.getItemId());
        if(menuToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ValueEventListener valueEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                doctor = dataSnapshot.getValue(Doctor.class);
            }
            else{
                Toast.makeText(DoctorHomeActivity.this, "error while uploading data!", Toast.LENGTH_LONG).show();
            }

        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void updateData(){
        Log.v("kaka", "1");
        c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        String day = c.get(c.DAY_OF_MONTH)+"-"+(c.get(c.MONTH)+1)+"-"+c.get(c.YEAR);
        Log.v("kaka", "2");
        DatabaseReference data = database.getReference(systemCode).child("summary data").child(day);
        data.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    maxH.setText(dataSnapshot.child("maximum heartbeat").child("value").getValue().toString()+" bpm");
                    dateMH.setText(dataSnapshot.child("maximum heartbeat").child("time").getValue().toString());
                    minH.setText(dataSnapshot.child("minimum heartbeat").child("value").getValue().toString()+" bpm");
                    dateNH.setText(dataSnapshot.child("minimum heartbeat").child("time").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
