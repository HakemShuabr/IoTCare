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

public class AdminHomeActivity extends AppCompatActivity {

    private DrawerLayout menuDrawer;
    private ActionBarDrawerToggle menuToggle;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Admin admin;
    private String systemCode;
    private CountDownTimer timer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        bTemp = (TextView)findViewById(R.id.bTemp);
        maxBT = (TextView)findViewById(R.id.amaxBT);
        dateMBT = (TextView)findViewById(R.id.adateMBT);
        minBT = (TextView)findViewById(R.id.aminBT);
        dateNBT = (TextView)findViewById(R.id.adateNBT);
        heart = (TextView)findViewById(R.id.heart);
        maxH = (TextView)findViewById(R.id.amaxHeart);
        dateMH = (TextView)findViewById(R.id.adateMH);
        minH = (TextView)findViewById(R.id.aminHeart);
        dateNH = (TextView)findViewById(R.id.adateNH);
        rTemp = (TextView)findViewById(R.id.rTemp);
        maxRT = (TextView)findViewById(R.id.amaxRT);
        dateMRT = (TextView)findViewById(R.id.adateMRT);
        minRT = (TextView)findViewById(R.id.aminRT);
        dateNRT = (TextView)findViewById(R.id.adateNRT);
        humidity = (TextView)findViewById(R.id.humidity);
        maxHumidity = (TextView)findViewById(R.id.amaxHumidity);
        dateMHumidity = (TextView)findViewById(R.id.adateMHumidity);
        minHumidity = (TextView)findViewById(R.id.aminHumidity);
        dateNHumidity = (TextView)findViewById(R.id.adateNHumidity);
        ///////////////////
        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");

        Intent intentService = new Intent(this, BackgroundService.class);
        BackgroundService.systemCode = systemCode;
        startService(intentService);

        updateData();

        admin = new Admin();

        DatabaseReference guardianData = database.getReference(systemCode).child("admin");
        guardianData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    admin = dataSnapshot.getValue(Admin.class);
                }
                else{
                    Toast.makeText(AdminHomeActivity.this, "error while uploading data!", Toast.LENGTH_LONG).show();
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
                        Intent profileActivity = new Intent(AdminHomeActivity.this, AdminProfileActivity.class);
                        profileActivity.putExtra("systemCode", systemCode);
                        profileActivity.putExtra("username", admin.getName().toString());
                        profileActivity.putExtra("email", admin.getEmail().toString());
                        profileActivity.putExtra("phone", admin.getPhone().toString());
                        startActivity(profileActivity);
                        break;

                    case (R.id.nav_room):
                        Intent roomActivity = new Intent(AdminHomeActivity.this, RoomActivity.class);
                        roomActivity.putExtra("systemCode", systemCode);
                        roomActivity.putExtra("username", admin.getName().toString());
                        roomActivity.putExtra("role", "admin");
                        startActivity(roomActivity);
                        break;

                    case (R.id.nav_health):
                        DatabaseReference patientData = database.getReference(systemCode).child("patient");
                        patientData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    PDU patient = dataSnapshot.getValue(PDU.class);
                                    Intent PatientActivity = new Intent(AdminHomeActivity.this, PatientActivity.class);
                                    PatientActivity.putExtra("systemCode", systemCode);
                                    PatientActivity.putExtra("username", patient.getName().toString());
                                    PatientActivity.putExtra("phone", patient.getPhone().toString());
                                    PatientActivity.putExtra("disability", patient.getDisability().toString());
                                    PatientActivity.putExtra("role", "admin");
                                    startActivity(PatientActivity);
                                }
                                else{
                                    Toast.makeText(AdminHomeActivity.this, "Your patient does not have profile yet!", Toast.LENGTH_LONG).show();
                                }

                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;

                    case (R.id.nav_switch):
                        Intent switchActivity = new Intent(AdminHomeActivity.this, SwitchActivity.class);
                        switchActivity.putExtra("systemCode", systemCode);
                        switchActivity.putExtra("role", "admin");
                        startActivity(switchActivity);
                        break;

                    case (R.id.nav_Report):
                        Intent reportActivity = new Intent(AdminHomeActivity.this, ViewReport.class);
                        reportActivity.putExtra("systemCode", systemCode);
                        startActivity(reportActivity);
                        break;

                    case (R.id.nav_data):
                        Intent dataActivity = new Intent(AdminHomeActivity.this, ViewData.class);
                        dataActivity.putExtra("systemCode", systemCode);
                        startActivity(dataActivity);
                        break;

                    case (R.id.nav_graph):
                        Intent graphActivity = new Intent(AdminHomeActivity.this, LineGraph.class);
                        graphActivity.putExtra("systemCode", systemCode);
                        startActivity(graphActivity);
                        break;

                    case (R.id.nav_manage):
                        Intent managementActivity = new Intent(AdminHomeActivity.this, ManagementActivity.class);
                        managementActivity.putExtra("systemCode", systemCode);
                        startActivity(managementActivity);
                        break;

                    case (R.id.nav_logout):
                        Intent logoutActivity = new Intent(AdminHomeActivity.this, LoginActivity.class);
                        startActivity(logoutActivity);
                        break;

                }
                return false;
            }
        });

        timer = new CountDownTimer(5000, 20) {

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
        if(menuToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateData(){
        Log.v("kaka", "1");

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
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
