package com.example.admin.firstversionapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class ChairContorolActivity extends AppCompatActivity {
    private String systemCode;
    static String MQTTHOST = "tcp://broker.hivemq.com:1883";
    //    static String USERNAME = "hakemJack";
////    static String PASSWORD = "Family#1";
    String topicMove;
    private MqttAndroidClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chair_contorol);

        findViewById(R.id.back).setOnClickListener(buttonsClickListener);
        findViewById(R.id.up).setOnClickListener(buttonsClickListener);
        findViewById(R.id.stop).setOnClickListener(buttonsClickListener);
        findViewById(R.id.down).setOnClickListener(buttonsClickListener);
        findViewById(R.id.right).setOnClickListener(buttonsClickListener);
        findViewById(R.id.left).setOnClickListener(buttonsClickListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");

        topicMove = systemCode+"/wheelchair/move";

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
                    Toast.makeText(ChairContorolActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(ChairContorolActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener buttonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back:
                    Intent homeActivity = new Intent(view.getContext(), PatientHomeActivity.class);
                    homeActivity.putExtra("systemCode", systemCode);
                    startActivity(homeActivity);
                    break;

                case R.id.up:
                    publish(topicMove, "m4");
                    break;

                case R.id.down:
                    publish(topicMove, "m2");
                    break;

                case R.id.stop:
                    publish(topicMove, "s");
                    break;

                case R.id.right:
                    publish(topicMove, "m1");
                    break;

                case R.id.left:
                    publish(topicMove, "m3");
                    break;
            }
        }
    };

    public void publish(String topic, String message){
        try {
            client.publish(topic, message.getBytes(), 0, false);
            Toast.makeText(ChairContorolActivity.this, topic, Toast.LENGTH_LONG).show();
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(ChairContorolActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
        }

    }
}
