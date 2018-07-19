package com.example.admin.firstversionapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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

public class GuardActivity extends AppCompatActivity {

    private DrawerLayout menuDrawer;
    private ActionBarDrawerToggle menuToggle;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Guardian guardian;
    private String systemCode;
    private TextView bTemp;
    private TextView maxBT;
    private TextView dateMBT;
    private TextView minBT;
    private TextView dateNBT;
    private TextView heart;
    private TextView maxH;
    private TextView dateMH;
    private TextView minH;
    private TextView dateNH;
    private TextView rTemp;
    private TextView maxRT;
    private TextView dateMRT;
    private TextView minRT;
    private TextView dateNRT;
    private TextView humidity;
    private TextView maxHumidity;
    private TextView dateMHumidity;
    private TextView minHumidity;
    private TextView dateNHumidity;

    private Calendar c;
    private CountDownTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard);
        //////////////////////

        bTemp = (TextView)findViewById(R.id.bTemp);
        maxBT = (TextView)findViewById(R.id.gmaxBT);
        dateMBT = (TextView)findViewById(R.id.gdateMBT);
        minBT = (TextView)findViewById(R.id.gminBT);
        dateNBT = (TextView)findViewById(R.id.gdateNBT);
        heart = (TextView)findViewById(R.id.heart);
        maxH = (TextView)findViewById(R.id.gmaxHeart);
        dateMH = (TextView)findViewById(R.id.gdateMH);
        minH = (TextView)findViewById(R.id.gminHeart);
        dateNH = (TextView)findViewById(R.id.gdateNH);
        rTemp = (TextView)findViewById(R.id.rTemp);
        maxRT = (TextView)findViewById(R.id.gmaxRT);
        dateMRT = (TextView)findViewById(R.id.gdateMRT);
        minRT = (TextView)findViewById(R.id.gminRT);
        dateNRT = (TextView)findViewById(R.id.gdateNRT);
        humidity = (TextView)findViewById(R.id.humidity);
        maxHumidity = (TextView)findViewById(R.id.gmaxHumidity);
        dateMHumidity = (TextView)findViewById(R.id.gdateMHumidity);
        minHumidity = (TextView)findViewById(R.id.gminHumidity);
        dateNHumidity = (TextView)findViewById(R.id.gdateNHumidity);
        ///////////////////

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        String username = intent.getExtras().getString("username");

        Intent intentService = new Intent(this, BackgroundService.class);
        BackgroundService.systemCode = systemCode;
        startService(intentService);

        updateData();

        guardian = new Guardian();

        DatabaseReference guardianData = database.getReference(systemCode).child("users").child("guardians").child(username);
        guardianData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    guardian = dataSnapshot.getValue(Guardian.class);
                }
                else{
                    Toast.makeText(GuardActivity.this, "error while uploading data!", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ////////////////////////////////
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
                        Intent profileActivity = new Intent(GuardActivity.this, ProfileActivity.class);
                        profileActivity.putExtra("systemCode", systemCode);
                        profileActivity.putExtra("username", guardian.getName().toString());
                        profileActivity.putExtra("email", guardian.getEmail().toString());
                        profileActivity.putExtra("phone", guardian.getNumber().toString());
                        profileActivity.putExtra("role", guardian.getRelationship().toString());
                        startActivity(profileActivity);
                        break;

                    case (R.id.nav_room):
                        Intent roomActivity = new Intent(GuardActivity.this, RoomActivity.class);
                        roomActivity.putExtra("systemCode", systemCode);
                        roomActivity.putExtra("username", guardian.getName().toString());
                        roomActivity.putExtra("role", guardian.getRelationship().toString());
                        startActivity(roomActivity);
                        break;

                    case (R.id.nav_health):
                        DatabaseReference patientData = database.getReference(systemCode).child("patient");
                        patientData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    PDU patient = dataSnapshot.getValue(PDU.class);
                                    Intent PatientActivity = new Intent(GuardActivity.this, PatientActivity.class);
                                    PatientActivity.putExtra("systemCode", systemCode);
                                    PatientActivity.putExtra("username", patient.getName().toString());
                                    PatientActivity.putExtra("phone", patient.getPhone().toString());
                                    PatientActivity.putExtra("disability", patient.getDisability().toString());
                                    PatientActivity.putExtra("role", "guardian");
                                    startActivity(PatientActivity);
                                }
                                else{
                                    Toast.makeText(GuardActivity.this, "Your patient does not have profile yet!", Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;

                    case (R.id.nav_data):
                        Intent dataActivity = new Intent(GuardActivity.this, ViewData.class);
                        dataActivity.putExtra("systemCode", systemCode);
                        startActivity(dataActivity);
                        break;

                    case (R.id.nav_Report):
                        Intent reportActivity = new Intent(GuardActivity.this, ViewReport.class);
                        reportActivity.putExtra("systemCode", systemCode);
                        startActivity(reportActivity);
                        break;

                    case (R.id.nav_logout):
                        Intent logoutActivity = new Intent(GuardActivity.this, LoginActivity.class);
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
                    if(dataSnapshot.child("maximum humidity").exists()) {
                        maxHumidity.setText(dataSnapshot.child("maximum humidity").child("value").getValue().toString() + "%");
                        dateMHumidity.setText(dataSnapshot.child("maximum humidity").child("time").getValue().toString());
                    }
                    if(dataSnapshot.child("minimum humidity").exists()) {
                        minHumidity.setText(dataSnapshot.child("minimum humidity").child("value").getValue().toString()+"%");
                        dateNHumidity.setText(dataSnapshot.child("minimum humidity").child("time").getValue().toString());
                    }
                    if(dataSnapshot.child("maximum temperature").exists()) {
                        maxRT.setText(dataSnapshot.child("maximum temperature").child("value").getValue().toString());
                        dateMRT.setText(dataSnapshot.child("maximum temperature").child("time").getValue().toString());
                    }
                    if(dataSnapshot.child("minimum temperature").exists()) {
                        minRT.setText(dataSnapshot.child("minimum temperature").child("value").getValue().toString());
                        dateNRT.setText(dataSnapshot.child("minimum temperature").child("time").getValue().toString());
                    }
                    if(dataSnapshot.child("maximum heartbeat").exists()) {
                        maxH.setText(dataSnapshot.child("maximum heartbeat").child("value").getValue().toString() + " bpm");
                        dateMH.setText(dataSnapshot.child("maximum heartbeat").child("time").getValue().toString());
                    }
                    if(dataSnapshot.child("minimum heartbeat").exists()) {
                        minH.setText(dataSnapshot.child("minimum heartbeat").child("value").getValue().toString() + " bpm");
                        dateNH.setText(dataSnapshot.child("minimum heartbeat").child("time").getValue().toString());
                    }
                    if(dataSnapshot.child("maximum body temperature").exists()) {
                        maxBT.setText(dataSnapshot.child("maximum body temperature").child("value").getValue().toString() + " C");
                        dateMBT.setText(dataSnapshot.child("maximum body temperature").child("time").getValue().toString());
                    }
                    if(dataSnapshot.child("minimum body temperature").exists()) {
                        minBT.setText(dataSnapshot.child("minimum body temperature").child("value").getValue().toString() + " C");
                        dateNBT.setText(dataSnapshot.child("minimum body temperature").child("time").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
