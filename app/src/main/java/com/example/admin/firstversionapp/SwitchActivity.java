package com.example.admin.firstversionapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.Time;
import java.util.Date;

public class SwitchActivity extends AppCompatActivity {
    private Switch lightSwitch;
    private Switch fanSwitch;
    private Switch acSwitch;
    private Switch doorSwitch;

    private String systemCode;
    private String userRole;

    private TextView light;
    private TextView fan;
    private TextView ac;
    private TextView door;

    static String MQTTHOST = "tcp://broker.hivemq.com:1883";
    //    static String USERNAME = "hakemJack";
////    static String PASSWORD = "Family#1";
    private String topic;
    String topicLight;
    String topicFan;
    String topicAC;
    String topicDoor;
    private MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        light = (TextView) findViewById(R.id.light);
        fan = (TextView) findViewById(R.id.fan);
        ac = (TextView) findViewById(R.id.ac);
        door = (TextView) findViewById(R.id.door);

        lightSwitch = (Switch) findViewById(R.id.lightSwitch);
        fanSwitch = (Switch) findViewById(R.id.fanSwitch);
        acSwitch = (Switch) findViewById(R.id.acSwitch);
        doorSwitch = (Switch) findViewById(R.id.doorSwitch);

        lightSwitch.setOnClickListener(buttonClicksListener);
        fanSwitch.setOnClickListener(buttonClicksListener);
        acSwitch.setOnClickListener(buttonClicksListener);
        doorSwitch.setOnClickListener(buttonClicksListener);
        findViewById(R.id.back).setOnClickListener(buttonClicksListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        userRole = intent.getExtras().getString("role");

        topic = systemCode+"/room/appliances/control";
        topicLight = systemCode+"/room/light/update";
        topicFan = systemCode+"/room/fan/update";
        topicAC = systemCode+"/room/ac/update";
        topicDoor = systemCode+"/room/door/update";

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
                    Toast.makeText(SwitchActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(SwitchActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
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
                if(topic.equals(topicLight)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1){
                        lightSwitch.setChecked(true);
                        light.setTextColor(Color.parseColor("#ff99cc00"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        lightSwitch.setChecked(false);
                        light.setTextColor(Color.parseColor("#ffff4444"));
                    }
                }
                else if(topic.equals(topicFan)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1){
                        fanSwitch.setChecked(true);
                        fan.setTextColor(Color.parseColor("#ff99cc00"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        fanSwitch.setChecked(false);
                        fan.setTextColor(Color.parseColor("#ffff4444"));
                    }
                }
                else if(topic.equals(topicAC)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1){
                        acSwitch.setChecked(true);
                        ac.setTextColor(Color.parseColor("#ff99cc00"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        acSwitch.setChecked(false);
                        ac.setTextColor(Color.parseColor("#ffff4444"));
                    }
                }
                else if(topic.equals(topicDoor)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1){
                        doorSwitch.setChecked(true);
                        door.setTextColor(Color.parseColor("#ff99cc00"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        doorSwitch.setChecked(false);
                        door.setTextColor(Color.parseColor("#ffff4444"));
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void subscribe(){
        try {
            client.subscribe(topicLight, 0);
            client.subscribe(topicFan, 0);
            client.subscribe(topicAC, 0);
            client.subscribe(topicDoor, 0);
        }catch (MqttException e){
            e.printStackTrace();
            Toast.makeText(SwitchActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
        }
    }

    public void publish(String topic, String message){
        try {
            client.publish(topic, message.getBytes(), 0, false);
            //Toast.makeText(ChairContorolActivity.this, "connection!!", Toast.LENGTH_LONG).show();
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(SwitchActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
        }

    }

    private View.OnClickListener buttonClicksListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.lightSwitch:
                    if(lightSwitch.isChecked()) {
                        light.setTextColor(Color.parseColor("#ff99cc00"));
                        publish(topic, "n1");
                    }
                    else {
                        light.setTextColor(Color.parseColor("#ffff4444"));
                        publish(topic, "f1");
                    }
                    break;

                case R.id.fanSwitch:
                    if(fanSwitch.isChecked()) {
                        fan.setTextColor(Color.parseColor("#ff99cc00"));
                        publish(topic, "n2");
                    }
                    else {
                        fan.setTextColor(Color.parseColor("#ffff4444"));
                        publish(topic, "f2");
                    }
                    break;

                case R.id.acSwitch:
                    if(acSwitch.isChecked()) {
                        ac.setTextColor(Color.parseColor("#ff99cc00"));
                        publish(topic, "n3");
                    }
                    else {
                        ac.setTextColor(Color.parseColor("#ffff4444"));
                        publish(topic, "f3");
                    }
                    break;

                case R.id.doorSwitch:
                    if(doorSwitch.isChecked()) {
                        door.setTextColor(Color.parseColor("#ff99cc00"));
                        publish(topic, "n4");
                    }
                    else {
                        door.setTextColor(Color.parseColor("#ffff4444"));
                        publish(topic, "f4");
                    }
                    break;

                case R.id.back:
                    Intent homeActivity = new Intent();
                    homeActivity.putExtra("systemCode", systemCode);

                    if(userRole.equals("admin"))
                        homeActivity.setClass(view.getContext(), AdminHomeActivity.class);

                    else if(userRole.equals("patient"))
                        homeActivity.setClass(view.getContext(), PatientHomeActivity.class);

                    startActivity(homeActivity);
                    break;
            }
        }
    };
}

