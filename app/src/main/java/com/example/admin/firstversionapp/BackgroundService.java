package com.example.admin.firstversionapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
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

import static java.lang.Integer.parseInt;

/**
 * Created by Admin on 03/09/2017.
 */

public class BackgroundService extends Service{
    private static int minHumidity = 100;
    private static int maxHumidity = 0;
    private static int minHeartBeat = 10000;
    private static int maxHeartBeat= 0;
    private int minTemperature = 100;
    private int maxTemperature = -50;
    private int minBTemperature = 100;
    private int maxBTemperature = -127;
    private String bodyTemperature = "";
    private String humidity = "";
    private String HBR = "";
    private String temperature = "";
    private int fire = 1;
    private int gas = 1;
    private String MQTTHOST = "tcp://broker.hivemq.com:1883";
    //    static String USERNAME = "hakemJack";
////    static String PASSWORD = "Family#1";
    private String topicFire;
    private String topicGas;
    private String topicTemp;
    private String topicHumidity;
    private String topicHeartbeat;
    private String topicHelp;
    private String topicBTemp;
    public static String systemCode;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference saveSummary;
    private Calendar c;

    public static boolean isServiceRunning;
    private MqttAndroidClient client;
    private CountDownTimer timer;

    public BackgroundService(){
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        isServiceRunning = true;
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("herei",systemCode);
        c = Calendar.getInstance();

        continuousSaving();

        topicFire = systemCode+"/room/fire";
        topicGas = systemCode+"/room/gas";
        topicTemp = systemCode+"/room/temperature";
        topicHumidity = systemCode+"/room/humidity";
        topicHeartbeat = systemCode+"/body/pulse";
        topicBTemp = systemCode+"/body/temperature";
        topicHelp = systemCode+"/ask/help";

        updateData();

        PendingIntent pi = PendingIntent.getService(this, 0,
                new Intent(this, LoginActivity.class), 0);
        AlarmManager am =
                (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), 60*1000, pi);

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
                    Toast.makeText(BackgroundService.this, "connected", Toast.LENGTH_LONG).show();
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(BackgroundService.this, "Fail to connect to the Internet", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(BackgroundService.this, "Fail to connect to the Internet", Toast.LENGTH_LONG).show();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                final String msg = new String(message.getPayload());
                DatabaseReference data;

                c = Calendar.getInstance();
                String day = c.get(c.DAY_OF_MONTH)+"-"+(c.get(c.MONTH)+1)+"-"+c.get(c.YEAR);

                if(topic.equals(topicFire)) {
                    if (parseInt(msg) != fire) {
                        fire = parseInt(msg);
                        if (fire == 0) {
                            Toast.makeText(BackgroundService.this, "Your patent is on fire! Immediate reacting is necessary", Toast.LENGTH_LONG).show();
                            notification("Your patent is on fire! Immediate reacting is necessary");
                            saveData("fire", "detected");
                        } else if (fire == 1) {
                            Toast.makeText(BackgroundService.this, "Fire is not detected near your patient :)", Toast.LENGTH_LONG).show();
                            notification("Fire is not detected near your patient :)");
                            abnormalSave("fire", "not detected");
                        }

                    }
                }
                    else if(topic.equals(topicGas)) {
                        if(parseInt(msg) != gas){
                            gas = parseInt(msg);
                            if(gas == 0){
                                Toast.makeText(BackgroundService.this, "Your patent is surrounded by harmful Gas! Immediate reacting is necessary", Toast.LENGTH_LONG).show();
                                notification("Your patent is surrounded by harmful Gas! Immediate reacting is necessary");
                                abnormalSave("gas", "detected");
                            }
                            else if(gas == 1){
                                Toast.makeText(BackgroundService.this, "Gas is not detected near your patient :)", Toast.LENGTH_LONG).show();
                                notification("Gas is not detected near your patient :)");
                                abnormalSave("gas", "not detected");
                            }

                        }

//                    if(parseInt(msg) == 2)
//                        saveData("fire", "not detected");
//                    else
//                        saveData("fire", "detected");

                }
                else if(topic.equals(topicHumidity)) {
                    humidity = msg;
                    if(parseInt(msg)>80 || parseInt(msg)<30)
                    {
                        abnormalSave("humidity", msg);
                        Toast.makeText(BackgroundService.this, "The humidity degree around your patient is "+msg+"% ! Immediate reacting is necessary", Toast.LENGTH_LONG).show();
                        notification("The humidity degree around your patient is "+msg+"% ! Immediate reacting is necessary");
                    }
                    if(parseInt(msg) > maxHumidity){
                        maxHumidity = parseInt(msg);
                        saveSummaryData("maximum humidity", msg);
                    }

                    if(parseInt(msg) < minHumidity){
                        minHumidity = parseInt(msg);
                        saveSummaryData("minimum humidity", msg);
                    }
                }

                else if(topic.equals(topicTemp)){
                    //if(parseInt(msg)>)
                    temperature = msg;
                    if(parseInt(msg)>40 || parseInt(msg)<10)
                    {
                        abnormalSave("room temperature", msg);
                        Toast.makeText(BackgroundService.this, "The temperature around your patient is "+msg+" ! Immediate reacting is necessary", Toast.LENGTH_LONG).show();
                        notification("The temperature around your patient is "+msg+" ! Immediate reacting is necessary");
                    }
                    else if(parseInt(msg)>25 || parseInt(msg)<15)
                    {
                        abnormalSave("room temperature", msg);
                        Toast.makeText(BackgroundService.this, "The temperature around your patient is "+msg+" ! Try to make the environment more comfortable", Toast.LENGTH_LONG).show();
                        notification("The temperature around your patient is "+msg+" ! Try to make the environment more comfortable");
                    }
                    if(parseInt(msg) > maxTemperature){
                        maxTemperature = parseInt(msg);
                        saveSummaryData("maximum temperature", msg);
                    }

                    if(parseInt(msg) < minTemperature){
                        minTemperature = parseInt(msg);
                        saveSummaryData("minimum temperature", msg);
                    }

                }
                else if(topic.equals(topicHeartbeat)){
                    //if(parseInt(msg)>)
                    HBR = msg;
                    if(parseInt(msg)>100 || parseInt(msg)<60)
                    {
                        abnormalSave("heartbeat", msg);
                        Toast.makeText(BackgroundService.this, "Your patient's heartbeat rate is "+msg+" ! Immediate reacting is necessary", Toast.LENGTH_LONG).show();
                        notification("Your patient's heartbeat rate is "+msg+" ! Immediate reacting is necessary");
                    }
                    if(parseInt(msg) > maxHeartBeat){
                        maxHeartBeat = parseInt(msg);
                        saveSummaryData("maximum heartbeat", msg);
                    }

                    if(parseInt(msg) < minHeartBeat){
                        minHeartBeat = parseInt(msg);
                        saveSummaryData("minimum heartbeat", msg);
                    }

                }
                    else if(topic.equals(topicBTemp)){
                        //if(parseInt(msg)>)
                        bodyTemperature = msg;
                        if(parseInt(msg)>37 || parseInt(msg)<32)
                        {
                            Toast.makeText(BackgroundService.this, "Your patient's outer body temperature is "+msg+" ! Immediate reacting is necessary", Toast.LENGTH_LONG).show();
                            notification("Your patient's outter body temperature is "+msg+" ! Immediate reacting is necessary");
                            abnormalSave("body temperature", msg);
                        }
                        if(parseInt(msg) > maxBTemperature){
                            maxHeartBeat = parseInt(msg);
                            saveSummaryData("maximum body temperature", msg);
                        }

                        if(parseInt(msg) < minBTemperature) {
                            minHeartBeat = parseInt(msg);
                            saveSummaryData("minimum body temperature", msg);
                        }
                    }
                    else if(topic.equals(topicHelp)) {
                    //if(parseInt(msg)>)
                    if (parseInt(msg) == 1) {
                        Toast.makeText(BackgroundService.this, "Your patient is ask for your help", Toast.LENGTH_LONG).show();
                        notification("Your patient is asking for your help");
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        timer = new CountDownTimer(60000, 20) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                try{
                    if(!client.isConnected())
                        stopSelf();
                    timer.start();
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        }.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopSelf();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartService = new Intent("RestartService");
        restartService.putExtra("systemCode", systemCode);
        sendBroadcast(restartService);
        Log.v("Destroy", "no");
    }

    private void subscribe(){
        try {
            client.subscribe(topicFire, 0);
            client.subscribe(topicGas, 0);
            client.subscribe(topicTemp, 0);
            client.subscribe(topicHumidity, 0);
            client.subscribe(topicBTemp, 0);
            client.subscribe(topicHeartbeat, 0);
            client.subscribe(topicHelp, 0);
        }catch (MqttException e){
            e.printStackTrace();
            Toast.makeText(BackgroundService.this, "connection failed!!", Toast.LENGTH_LONG).show();
        }
    }
    private void saveSummaryData(String root, String value){
        saveSummary = database.getReference(systemCode.toString());
        c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        int AM_PM = c.get(Calendar.AM_PM);
        String day = c.get(c.DAY_OF_MONTH)+"-"+(c.get(c.MONTH)+1)+"-"+c.get(c.YEAR);
        String time = c.get(c.HOUR_OF_DAY)+":"+c.get(c.MINUTE)+":"+c.get(c.SECOND)+" ";
        if(AM_PM == Calendar.AM){
            time+="AM";
        }
        else {
            time+="PM";
        }
        saveSummary.child("summary data").child(day).child(root).child("value").setValue(value);
        saveSummary.child("summary data").child(day).child(root).child("time").setValue(time);
    }

    private void saveData(final String root, final String value){

        c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        String day = c.get(c.DAY_OF_MONTH)+"-"+(c.get(c.MONTH)+1)+"-"+c.get(c.YEAR);
        int AM_PM = c.get(Calendar.AM_PM);

        String hour = c.get(c.HOUR_OF_DAY)+":"+c.get(c.MINUTE)+" ";
        if(AM_PM == Calendar.AM){
            if(c.get(c.HOUR_OF_DAY)<=9)
                hour="0"+hour;
            hour+="AM";
        }
        else {
            hour+="PM";
        }
        saveSummary = database.getReference(systemCode.toString()).child("data").child(day).child(hour);
        saveSummary.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(root).exists()){
                    saveSummary.child(root).setValue(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void abnormalSave(final String root, final String value){

        c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        String day = c.get(c.DAY_OF_MONTH)+"-"+(c.get(c.MONTH)+1)+"-"+c.get(c.YEAR);
        int AM_PM = c.get(Calendar.AM_PM);

        String hour = c.get(c.HOUR_OF_DAY)+":"+c.get(c.MINUTE)+" ";
        if(AM_PM == Calendar.AM){
            hour+="AM";
        }
        else {
            hour+="PM";
        }
        saveSummary = database.getReference(systemCode.toString()).child("abnormal data").child(day).child(hour);
        saveSummary.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(root).exists()){
                    saveSummary.child(root).setValue(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                    if(dataSnapshot.child("maximum humidity").exists())
                        maxHumidity = parseInt(dataSnapshot.child("maximum humidity").child("value").getValue().toString());
                    if(dataSnapshot.child("minimum humidity").exists())
                        minHumidity = parseInt(dataSnapshot.child("minimum humidity").child("value").getValue().toString());

                    if(dataSnapshot.child("maximum temperature").exists())
                        maxTemperature = parseInt(dataSnapshot.child("maximum temperature").child("value").getValue().toString());
                    if(dataSnapshot.child("minimum temperature").exists())
                        minTemperature = parseInt(dataSnapshot.child("minimum temperature").child("value").getValue().toString());

                    if(dataSnapshot.child("maximum heartbeat").exists())
                        maxHeartBeat = parseInt(dataSnapshot.child("maximum heartbeat").child("value").getValue().toString());
                    if(dataSnapshot.child("minimum heartbeat").exists())
                        minHeartBeat = parseInt(dataSnapshot.child("minimum heartbeat").child("value").getValue().toString());

                    if(dataSnapshot.child("maximum body temperature").exists())
                        maxBTemperature = parseInt(dataSnapshot.child("maximum body temperature").child("value").getValue().toString());
                    if(dataSnapshot.child("minimum body temperature").exists())
                        minBTemperature=  parseInt(dataSnapshot.child("minimum body temperature").child("value").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void notification(String note){
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(BackgroundService.this);
        builder.setContentTitle("IoT Care");
        builder.setContentText(note);
        PendingIntent pi1 = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginActivity.class), 0);
        builder.setContentIntent(pi1);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        notificationManager.notify(1, builder.build());
    }

    private void continuousSaving(){
        Utils.delay(600, new Utils.DelayCallback() {
            @Override
            public void afterDelay() {
                if(fire == 0)
                    saveData("fire", "detected");
                else if(fire == 1)
                    saveData("fire", "not detected");
                if(gas == 0)
                    saveData("gas", "detected");
                else if(gas == 1)
                    saveData("gas", "not detected");
                saveData("humidity", humidity);
                saveData("room temperature", temperature);
                saveData("heartbeat", HBR);
                saveData("body temperature", bodyTemperature);
                Log.v("waito", "done");
                continuousSaving();

            }
        });
    }
}
