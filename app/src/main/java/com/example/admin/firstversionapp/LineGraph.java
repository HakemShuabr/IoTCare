package com.example.admin.firstversionapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class LineGraph extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    private LineChart mChart_BT;
    private LineChart mChart_RT;
    private LineChart mChart_H;
    private LineChart mChart_HBR;
    private ArrayList<String> xVals;
    private ArrayList<Entry> yVals;
    private String systemCode;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new Intent(this, AdminHomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // To make full screen layout
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_line_graph);


        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");

        xVals = new ArrayList<String>();
        yVals = new ArrayList<Entry>();

        LimitLine upper_limit = new LimitLine(37f, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        LimitLine lower_limit = new LimitLine(32f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        mChart_BT = (LineChart) findViewById(R.id.linechart_BT);
        mChart_BT.setOnChartGestureListener(this);
        mChart_BT.setOnChartValueSelectedListener(this);
        mChart_BT.setTouchEnabled(true);
        mChart_BT.setDragEnabled(true);
        mChart_BT.setScaleEnabled(true);
        mChart_BT.getAxisLeft().setAxisMaxValue(100f);
        mChart_BT.getAxisLeft().setAxisMinValue(0f);
        mChart_BT.getAxisLeft().addLimitLine(upper_limit);
        mChart_BT.getAxisLeft().addLimitLine(lower_limit);
        mChart_BT.getAxisRight().setAxisMaxValue(100f);
        mChart_BT.getAxisRight().setAxisMinValue(0f);
        mChart_BT.setDescription("Body Temperature");

        LimitLine upper_limit1 = new LimitLine(40f, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        LimitLine lower_limit1 = new LimitLine(10f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        mChart_RT = (LineChart) findViewById(R.id.linechart_RT);
        mChart_RT.setOnChartGestureListener(this);
        mChart_RT.setOnChartValueSelectedListener(this);
        mChart_RT.setTouchEnabled(true);
        mChart_RT.setDragEnabled(true);
        mChart_RT.setScaleEnabled(true);
        mChart_RT.getAxisLeft().setAxisMaxValue(100f);
        mChart_RT.getAxisLeft().setAxisMinValue(0f);
        mChart_RT.getAxisRight().setAxisMaxValue(100f);
        mChart_RT.getAxisRight().setAxisMinValue(0f);
        mChart_RT.getAxisLeft().addLimitLine(upper_limit1);
        mChart_RT.getAxisLeft().addLimitLine(lower_limit1);
        mChart_RT.setDescription("Room Temperature");

        LimitLine upper_limit2 = new LimitLine(80f, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        LimitLine lower_limit2 = new LimitLine(30f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        mChart_H = (LineChart) findViewById(R.id.linechart_H);
        mChart_H.setOnChartGestureListener(this);
        mChart_H.setOnChartValueSelectedListener(this);
        mChart_H.setTouchEnabled(true);
        mChart_H.setDragEnabled(true);
        mChart_H.setScaleEnabled(true);
        mChart_H.getAxisLeft().setAxisMaxValue(100f);
        mChart_H.getAxisLeft().setAxisMinValue(0f);
        mChart_H.getAxisRight().setAxisMaxValue(100f);
        mChart_H.getAxisRight().setAxisMinValue(0f);
        mChart_H.getAxisLeft().addLimitLine(upper_limit2);
        mChart_H.getAxisLeft().addLimitLine(lower_limit2);
        mChart_H.setDescription("Humidity");

        LimitLine upper_limit3 = new LimitLine(100f, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        LimitLine lower_limit3 = new LimitLine(60f, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);


        mChart_HBR = (LineChart) findViewById(R.id.linechart_HBR);
        mChart_HBR.setOnChartGestureListener(this);
        mChart_HBR.setOnChartValueSelectedListener(this);
        mChart_HBR.setTouchEnabled(true);
        mChart_HBR.setDragEnabled(true);
        mChart_HBR.setScaleEnabled(true);
        mChart_HBR.getAxisLeft().setAxisMaxValue(200f);
        mChart_HBR.getAxisLeft().setAxisMinValue(0f);
        mChart_HBR.getAxisRight().setAxisMaxValue(200f);
        mChart_HBR.getAxisRight().setAxisMinValue(0f);
        mChart_HBR.getAxisLeft().addLimitLine(upper_limit3);
        mChart_HBR.getAxisLeft().addLimitLine(lower_limit3);
        mChart_HBR.setDescription("Heart Beat Rate");

        Toast.makeText(this, "Zoom up the line graphs for better viewing", Toast.LENGTH_LONG).show();

        setAxisValues("room temperature");

        // get the legend (only possible after setting data)
        Legend l = mChart_BT.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
    }


    @Override
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
            // or highlightTouch(null) for callback to onNothingSelected(...)
            mChart_BT.highlightValues(null);
            mChart_RT.highlightValues(null);
        }
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    private void setAxisValues(final String DataType){
        xVals = new ArrayList<String>();
        yVals = new ArrayList<Entry>();
        final DatabaseReference userAccount = database.getReference(systemCode);
        userAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user.setName(dataSnapshot.getValue(User.class).getName());

                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> day = null;

                    boolean done = false;

                    day = dataSnapshot.child("data").getChildren();

                    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                    String toDay = c.get(c.DAY_OF_MONTH)+"-"+(c.get(c.MONTH)+1)+"-"+c.get(c.YEAR);

                    Log.v("come", dataSnapshot.getKey());
                    for (DataSnapshot child : day) {
                        Iterable<DataSnapshot> time = null;
                        time = child.getChildren();
                        if(child.getKey().equals(toDay)) {
                            done = true;
                            int i = 0;
                            for (DataSnapshot child2 : time) {

                                Log.v("bodiii", child.getKey().toString() + " " + child2.getKey().toString());
                                String date = child.getKey().toString() + " " + child2.getKey().toString();
                                xVals.add(child2.getKey().toString());
                                if (DataType.equals("body temperature") && child2.hasChild("body temperature") &&
                                        !child2.child("body temperature").getValue().toString().isEmpty()) {
                                    yVals.add(new Entry(Float.parseFloat(child2.child("body temperature")
                                            .getValue().toString()), i++));
                                }
                                else if (DataType.equals("heartbeat") && child2.hasChild("heartbeat") &&
                                        !child2.child("heartbeat").getValue().toString().isEmpty())
                                    yVals.add(new Entry(Float.parseFloat(child2.child("heartbeat")
                                            .getValue().toString()), i++));
                                else if (DataType.equals("room temperature") && child2.hasChild("room temperature") &&
                                        !child2.child("room temperature").getValue().toString().isEmpty())
                                    yVals.add(new Entry(Float.parseFloat(child2.child("room temperature")
                                            .getValue().toString()), i++));
                                else if (DataType.equals("humidity") && child2.hasChild("humidity") &&
                                        !child2.child("humidity").getValue().toString().isEmpty())
                                    yVals.add(new Entry(Float.parseFloat(child2.child("humidity")
                                            .getValue().toString()), i++));
                            }
                        }
                    }

                    if(done){
                        Log.v("gogo", DataType);
                        if(DataType.equals("room temperature")) {
                                mChart_RT.setData(setData("celsius"));
                            setAxisValues("body temperature");
                        }
                        else if(DataType.equals("body temperature")) {
                                mChart_BT.setData(setData("celsius"));
                            setAxisValues("humidity");
                        }
                        else if(DataType.equals("humidity")) {
                                mChart_H.setData(setData("%"));
                            setAxisValues("heartbeat");
                        }
                        else if(DataType.equals("heartbeat")) {
                                mChart_HBR.setData(setData("BPM"));
                            return;
                        }
                        done = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private LineData setData(String unit) {

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, unit);
        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        // set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.YELLOW);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        return data;
    }
}

