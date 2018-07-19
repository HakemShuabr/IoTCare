package com.example.admin.firstversionapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;
import java.util.TimeZone;
public class PatientHomeActivity extends AppCompatActivity {

    private DrawerLayout menuDrawer;
    private ActionBarDrawerToggle menuToggle;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private TextView updateTime1;
    private TextView temperature1;
    private TextView humidity1;
    private TextView bodyTemp1;
    private TextView heart1;

    private PDU patient;

    private String systemCode;

    static String MQTTHOST = "tcp://broker.hivemq.com:1883";
    //    static String USERNAME = "hakemJack";
////    static String PASSWORD = "Family#1";
    String topicTemp;
    String topicHumidity;
    String topicHeartbeat;
    String topicBTEmp;
    private MqttAndroidClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);

        updateTime1 = (TextView)findViewById(R.id.PatientupdateTime1);
        temperature1 = (TextView)findViewById(R.id.PatientTemp1);
        humidity1 = (TextView)findViewById(R.id.Patienthumidity1);
        bodyTemp1 = (TextView)findViewById(R.id.PatientBodyTemp1);
        heart1 = (TextView)findViewById(R.id.Patientheart1);

        temperature1.setText("food");
        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        topicTemp = systemCode+"/room/temperature";
        topicHumidity = systemCode+"/room/humidity";
        topicHeartbeat = systemCode+"/body/pulse";
        topicBTEmp = systemCode+"/body/temperature";
        Intent intentService = new Intent(this, BackgroundService.class);
        BackgroundService.systemCode = systemCode;
        //startService(intentService);

        patient = new PDU();

        DatabaseReference guardianData = database.getReference(systemCode).child("patient");
        guardianData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    patient = dataSnapshot.getValue(PDU.class);
                }
                else{
                    Toast.makeText(PatientHomeActivity.this, "error while uploading data!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        //options.setUserName(USERNAME);
        //options.setPassword(PASSWORD.toCharArray());
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(PatientHomeActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(PatientHomeActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(topicHumidity)) {
                    humidity1.setText(new String(message.getPayload())+"%");
                }
                else if(topic.equals(topicTemp)){
                    temperature1.setText(new String(message.getPayload()));
                }
                else if(topic.equals(topicHeartbeat)){
                    heart1.setText("hello");
                }
                else if(topic.equals(topicBTEmp)){
                    Toast.makeText(PatientHomeActivity.this, new String(message.getPayload()), Toast.LENGTH_LONG).show();
                    bodyTemp1.setText(new String(message.getPayload())+" C");
                }
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                int AM_PM = c.get(Calendar.AM_PM);

                String hour = c.get(c.HOUR_OF_DAY)+":"+c.get(c.MINUTE)+":"+c.get(c.SECOND);
                if(AM_PM == Calendar.AM){
                    hour+="AM";
                }
                else {
                    hour+="PM";
                }
                Toast.makeText(PatientHomeActivity.this, hour, Toast.LENGTH_LONG).show();
                updateTime1.setText(hour);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        ////////////////////////////////
        setContentView(R.layout.activity_patient_home);

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
                        Intent profileActivity = new Intent(PatientHomeActivity.this, PatientActivity.class);
                        profileActivity.putExtra("systemCode", systemCode);
                        profileActivity.putExtra("username", patient.getName().toString());
                        profileActivity.putExtra("phone", patient.getPhone().toString());
                        profileActivity.putExtra("disability", patient.getDisability().toString());
                        profileActivity.putExtra("role", "patient");
                        startActivity(profileActivity);
                        break;

                    case (R.id.nav_switch):
                        Intent switchActivity = new Intent(PatientHomeActivity.this, SwitchActivity.class);
                        switchActivity.putExtra("systemCode", systemCode);
                        switchActivity.putExtra("role", "patient");
                        startActivity(switchActivity);
                        break;

                    case (R.id.nav_chair):
                        Intent chairActivity = new Intent(PatientHomeActivity.this, ChairContorolActivity.class);
                        chairActivity.putExtra("systemCode", systemCode);
                        startActivity(chairActivity);
                        break;

                    case (R.id.nav_Report):
                        Intent reportActivity = new Intent(PatientHomeActivity.this, ViewReport.class);
                        reportActivity.putExtra("systemCode", systemCode);
                        startActivity(reportActivity);
                        break;

                    case (R.id.nav_data):
                        Intent dataActivity = new Intent(PatientHomeActivity.this, ViewData.class);
                        dataActivity.putExtra("systemCode", systemCode);
                        startActivity(dataActivity);
                        break;

                    case (R.id.nav_logout):
                        Intent logoutActivity = new Intent(PatientHomeActivity.this, LoginActivity.class);
                        startActivity(logoutActivity);
                        break;

                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        ((TextView)findViewById(R.id.textView)).setText(item.getItemId());
        if (menuToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void subscribe(){
        try {
            client.subscribe(topicTemp, 0);
            client.subscribe(topicHumidity, 0);
            client.subscribe(topicHeartbeat, 0);
            client.subscribe(topicBTEmp, 0);
        }catch (MqttException e){
            e.printStackTrace();
            Toast.makeText(PatientHomeActivity.this, "subscription failed!!", Toast.LENGTH_LONG).show();
        }
    }
}
