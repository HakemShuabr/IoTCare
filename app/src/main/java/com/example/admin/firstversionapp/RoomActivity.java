package com.example.admin.firstversionapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import java.util.Calendar;
import java.util.TimeZone;

public class RoomActivity extends AppCompatActivity {
    private TextView updateTime;
    private TextView tempValue;
    private TextView humidityValue;
    private TextView fireValue;
    private TextView gasValue;
    private TextView lightValue;
    private TextView acValue;
    private TextView fanValue;
    private TextView lockValue;
    private TextView value;

    private ConstraintLayout gas;
    private ConstraintLayout fire;
    private ConstraintLayout humidity;
    private ConstraintLayout temperature;
    private ConstraintLayout light;
    private ConstraintLayout fan;
    private ConstraintLayout ac;
    private ConstraintLayout lock;

    private String systemCode;
    private String username;
    private String userRole;

    static String MQTTHOST = "tcp://broker.hivemq.com:1883";
//    static String USERNAME = "hakemJack";
////    static String PASSWORD = "Family#1";
    String topicFire;
    String topicGas;
    String topicTemp;
    String topicHumidity;
    String topicLight;
    String topicFan;
    String topicAC;
    String topicDoor;
    private  MqttAndroidClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        updateTime = (TextView)findViewById(R.id.updateTime);
        tempValue = (TextView)findViewById(R.id.TempValue);
        humidityValue = (TextView)findViewById(R.id.HumidityValue);
        fireValue = (TextView)findViewById(R.id.FireValue);
        gasValue = (TextView)findViewById(R.id.GasValue);
        lightValue = (TextView)findViewById(R.id.LightValue);
        acValue = (TextView)findViewById(R.id.AcValue);
        fanValue = (TextView)findViewById(R.id.FanValue);
        lockValue = (TextView)findViewById(R.id.LockValue);
        value = (TextView)findViewById(R.id.value);

        gas = (ConstraintLayout)findViewById(R.id.Gas);
        fire = (ConstraintLayout)findViewById(R.id.Fire);
        humidity = (ConstraintLayout)findViewById(R.id.Humidity);
        temperature = (ConstraintLayout)findViewById(R.id.Temperature);
        light = (ConstraintLayout)findViewById(R.id.Light);
        fan = (ConstraintLayout)findViewById(R.id.Fan);
        ac = (ConstraintLayout)findViewById(R.id.AC);
        lock = (ConstraintLayout)findViewById(R.id.Lock);

        gas.setOnClickListener(buttonClick);
        fire.setOnClickListener(buttonClick);
        humidity.setOnClickListener(buttonClick);
        temperature.setOnClickListener(buttonClick);

//        findViewById(R.id.back).setOnClickListener(buttonClickListener);

//        Intent intent = getIntent();
//        systemCode = intent.getExtras().getString("systemCode");
//        username = intent.getExtras().getString("username");
//        userRole = intent.getExtras().getString("role");
        systemCode = "System1";

        topicFire = systemCode+"/room/fire";
        topicGas = systemCode+"/room/gas";
        topicTemp = systemCode+"/room/temperature";
        topicHumidity = systemCode+"/room/humidity";
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
                    Toast.makeText(RoomActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(RoomActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
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
                if(topic.equals(topicFire)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1) {
                        fireValue.setText("NO");
                        fireValue.setTextColor(Color.parseColor("#009100"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        fireValue.setText("YES");
                        fireValue.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
                else if(topic.equals(topicGas)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1) {
                        gasValue.setText("NO");
                        gasValue.setTextColor(Color.parseColor("#009100"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        gasValue.setText("YES");
                        gasValue.setTextColor(Color.parseColor("#FF0000"));
                    }
                }
                else if(topic.equals(topicHumidity)) {
                    humidityValue.setText(new String(message.getPayload())+"%");
                }
                else if(topic.equals(topicTemp)){
                    tempValue.setText(new String(message.getPayload())+"C");
                }
                if(topic.equals(topicLight)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1){
                        lightValue.setText("ON");
                        light.setBackgroundColor(Color.parseColor("#009100"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        lightValue.setText("OFF");
                        light.setBackgroundColor(Color.parseColor("#ffff4444"));
                    }
                }
                else if(topic.equals(topicFan)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1){
                        fanValue.setText("ON");
                        fan.setBackgroundColor(Color.parseColor("#009100"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        fanValue.setText("OFF");
                        fan.setBackgroundColor(Color.parseColor("#ffff4444"));
                    }
                }
                else if(topic.equals(topicAC)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1){
                        acValue.setText("ON");
                        ac.setBackgroundColor(Color.parseColor("#009100"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        acValue.setText("OFF");
                        ac.setBackgroundColor(Color.parseColor("#ffff4444"));
                    }
                }
                else if(topic.equals(topicDoor)) {
                    if(Integer.parseInt(new String(message.getPayload())) == 1){
                        lockValue.setText("OPEN");
                        lock.setBackgroundColor(Color.parseColor("#009100"));
                    }
                    else if(Integer.parseInt(new String(message.getPayload())) == 0) {
                        lockValue.setText("CLOSE");
                        lock.setBackgroundColor(Color.parseColor("#ffff4444"));
                    }
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
                updateTime.setText(hour);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch(view.getId()){
                case R.id.back:
                    Intent homeActivity = new Intent();
                    homeActivity.putExtra("systemCode", systemCode);
                    homeActivity.putExtra("username", username);
                    if(userRole.equals("Doctor"))
                        homeActivity.setClass(view.getContext(), DoctorHomeActivity.class);

                    else if(userRole.equals("admin"))
                        homeActivity.setClass(view.getContext(), AdminHomeActivity.class);

                    else
                        homeActivity.setClass(view.getContext(), GuardActivity.class);

                    startActivity(homeActivity);
                    break;
            }
        }
    };

    public void subscribe(){
        try {
            client.subscribe(topicFire, 0);
            client.subscribe(topicGas, 0);
            client.subscribe(topicTemp, 0);
            client.subscribe(topicHumidity, 0);
            client.subscribe(topicLight, 0);
            client.subscribe(topicFan, 0);
            client.subscribe(topicAC, 0);
            client.subscribe(topicDoor, 0);
        }catch (MqttException e){
            e.printStackTrace();
            Toast.makeText(RoomActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
        }
    }

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.Gas:
                    resetBackground();
                    gas.setBackgroundColor(Color.parseColor("#808080"));
                    if(gasValue.getText().toString().equals("NO")){
                        value.setTextSize(24);
                        value.setText("Gas is not detected");
                        value.setTextColor(Color.parseColor("#009100"));
                    }
                    else if(gasValue.getText().toString().equals("YES")){
                        value.setTextSize(24);
                        value.setText("Gas is detected");
                        value.setTextColor(Color.parseColor("#ffff4444"));
                    }
                    break;
                case R.id.Fire:
                    resetBackground();
                    fire.setBackgroundColor(Color.parseColor("#808080"));
                    if(fireValue.getText().toString().equals("NO")){
                        value.setTextSize(24);
                        value.setText("Fire is not detected");
                        value.setTextColor(Color.parseColor("#009100"));
                    }
                    else if(fireValue.getText().toString().equals("YES")){
                        value.setTextSize(24);
                        value.setText("Fire is detected");
                        value.setTextColor(Color.parseColor("#ffff4444"));
                    }
                    break;
                case R.id.Humidity:
                    resetBackground();
                    humidity.setBackgroundColor(Color.parseColor("#808080"));
                    value.setTextColor(Color.parseColor("#8a000000"));
                    value.setTextSize(80);
                    value.setText(humidityValue.getText().toString()+"%");
                    break;
                case R.id.Temperature:
                    resetBackground();
                    temperature.setBackgroundColor(Color.parseColor("#808080"));
                    value.setTextColor(Color.parseColor("#8a000000"));
                    value.setTextSize(80);
                    value.setText(tempValue.getText().toString()+"c");
                    break;
            }
        }
    };

    private void resetBackground(){
        gas.setBackgroundColor(Color.parseColor("#1f000000"));
        fire.setBackgroundColor(Color.parseColor("#1f000000"));
        humidity.setBackgroundColor(Color.parseColor("#1f000000"));
        temperature.setBackgroundColor(Color.parseColor("#1f000000"));
    }
}
