package com.example.admin.firstversionapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ViewData extends AppCompatActivity {

    ArrayList<Data> packets = new ArrayList<Data>();
    private String systemCode;
    private int active;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyAdapter myAdapter = new MyAdapter();
        myAdapter.addElements(packets);
        recyclerView.setAdapter(myAdapter);
        context = this;

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");

        ((Button)findViewById(R.id.search_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c;
                String search = ((EditText)findViewById(R.id.optionText)).getText().toString();
                if(search.equals("body temperature") || search.equals("heartbeat") ||
                        search.equals("room temperature") || search.equals("humidity") ||
                        search.equals("gas") || search.equals("fire"))
                    getDataOf(search, myAdapter);

                else {
                    getDataOn(search, myAdapter);
                }

            }
        });

        final DatabaseReference userAccount = database.getReference(systemCode);
        userAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user.setName(dataSnapshot.getValue(User.class).getName());

                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> day = null;

                    day = dataSnapshot.child("data").getChildren();

                    Log.v("come", dataSnapshot.getKey());
                    for (DataSnapshot child : day) {
                        Iterable<DataSnapshot> time = null;
                        time = child.getChildren();
                        for (DataSnapshot child2 : time) {
                            Log.v("bodiii", child.getKey().toString() + " " + child2.getKey().toString());
                            Log.v("bodiii", child2.child("fire").getValue().toString());
                            String date = child.getKey().toString() + " " + child2.getKey().toString();
                            if (child2.hasChild("body temperature") &&
                                    !child2.child("body temperature").getValue().toString().isEmpty())
                                packets.add(new Data("body temperature", child2.child("body temperature").getValue().toString() + " c", date));
                            if (child2.hasChild("heartbeat") &&
                                    !child2.child("heartbeat").getValue().toString().isEmpty())
                                packets.add(new Data("heartbeat", child2.child("heartbeat").getValue().toString() + " bpm", date));
                            if (child2.hasChild("room temperature") &&
                                    !child2.child("room temperature").getValue().toString().isEmpty())
                                packets.add(new Data("room temperature", child2.child("room temperature").getValue().toString() + " c", date));
                            if (child2.hasChild("humidity") &&
                                    !child2.child("humidity").getValue().toString().isEmpty())
                                packets.add(new Data("humidity", child2.child("humidity").getValue().toString() + " %", date));
                            if (child2.hasChild("gas") &&
                                    !child2.child("gas").getValue().toString().isEmpty())
                                packets.add(new Data("gas", child2.child("gas").getValue().toString(), date));
                            if (child2.hasChild("fire") &&
                                    !child2.child("fire").getValue().toString().isEmpty())
                                packets.add(new Data("fire", child2.child("fire").getValue().toString(), date));
                        }
                    }
                    myAdapter.addElements(packets);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        ArrayList<Data> elements = new ArrayList<Data>();

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowView = getLayoutInflater().inflate(R.layout.data_row, parent, false);
            return new MyAdapter.MyViewHolder(rowView);
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            holder.textView.setText(elements.get(position).getName());
            holder.textView2.setText(elements.get(position).getValue());
            holder.textView3.setText(elements.get(position).getDate());
            if(elements.get(position).getName().equals("body temperature"))
                holder.container.setBackgroundColor(getResources().getColor(R.color.sea_blue));
            else if(elements.get(position).getName().equals("heartbeat"))
                holder.container.setBackgroundColor(getResources().getColor(R.color.aquamarine));
            else if(elements.get(position).getName().equals("room temperature"))
                holder.container.setBackgroundColor(getResources().getColor(R.color.light_green));
            else if(elements.get(position).getName().equals("humidity"))
                holder.container.setBackgroundColor(getResources().getColor(R.color.tea_green));
            else if(elements.get(position).getName().equals("gas"))
                holder.container.setBackgroundColor(getResources().getColor(R.color.vanilla));
            else if(elements.get(position).getName().equals("fire"))
                holder.container.setBackgroundColor(getResources().getColor(R.color.peach));

        }

        @Override
        public int getItemCount() {
            return elements.size();
        }

        public void addElements(ArrayList<Data> versions) {
            elements.clear();
            elements.addAll(versions);
            notifyDataSetChanged();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            public TextView textView2;
            public TextView textView3;
            public LinearLayout container;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView)itemView.findViewById(R.id.name);
                textView2 = (TextView)itemView.findViewById(R.id.dataValue);
                textView3 = (TextView) itemView.findViewById(R.id.date);
                container = (LinearLayout) itemView.findViewById(R.id.container);
            }
        }


    }

    private void getDataOn(final String date, final MyAdapter myAdapter){
        packets.clear();
        final DatabaseReference userAccount = database.getReference(systemCode);
        userAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user.setName(dataSnapshot.getValue(User.class).getName());

                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> day = null;

                    day = dataSnapshot.child("data").getChildren();
                    boolean done = false;
                    Log.v("come", dataSnapshot.getKey());
                    for (DataSnapshot child : day) {
                        Iterable<DataSnapshot> time = null;
                        time = child.getChildren();
                        Log.v("bodiii", "*"+child.getKey().toString() + "* : *"+ date+"*");
                        if(child.getKey().toString().equals(date)) {
                            done = true;
                            for (DataSnapshot child2 : time) {
                                Log.v("bodiii", child.getKey().toString() + " " + child2.getKey().toString());
                                Log.v("bodiii", child2.child("fire").getValue().toString());
                                String date = child.getKey().toString() + " " + child2.getKey().toString();
                                if (child2.hasChild("body temperature") &&
                                        !child2.child("body temperature").getValue().toString().isEmpty())
                                    packets.add(new Data("body temperature", child2.child("body temperature").getValue().toString() + " c", date));
                                if (child2.hasChild("heartbeat") &&
                                        !child2.child("heartbeat").getValue().toString().isEmpty())
                                    packets.add(new Data("heartbeat", child2.child("heartbeat").getValue().toString() + " bpm", date));
                                if (child2.hasChild("room temperature") &&
                                        !child2.child("room temperature").getValue().toString().isEmpty())
                                    packets.add(new Data("room temperature", child2.child("room temperature").getValue().toString() + " c", date));
                                if (child2.hasChild("humidity") &&
                                        !child2.child("humidity").getValue().toString().isEmpty())
                                    packets.add(new Data("humidity", child2.child("humidity").getValue().toString() + " %", date));
                                if (child2.hasChild("gas") &&
                                        !child2.child("gas").getValue().toString().isEmpty())
                                    packets.add(new Data("gas", child2.child("gas").getValue().toString(), date));
                                if (child2.hasChild("fire") &&
                                        !child2.child("fire").getValue().toString().isEmpty())
                                    packets.add(new Data("fire", child2.child("fire").getValue().toString(), date));
                            }
                        }

                    }
                    if(!done)
                        Toast.makeText(context, "Please enter correct date format (dd-mm-yy) or correct data type (humidity)", Toast.LENGTH_LONG).show();

                    myAdapter.addElements(packets);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDataOf(final String DataType, final MyAdapter myAdapter){
        packets.clear();
        final DatabaseReference userAccount = database.getReference(systemCode);
        userAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user.setName(dataSnapshot.getValue(User.class).getName());

                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> day = null;

                    day = dataSnapshot.child("data").getChildren();

                    Log.v("come", dataSnapshot.getKey());
                    for (DataSnapshot child : day) {
                        Iterable<DataSnapshot> time = null;
                        time = child.getChildren();
                        for (DataSnapshot child2 : time) {
                            Log.v("bodiii", child.getKey().toString() + " " + child2.getKey().toString());
                            Log.v("bodiii", child2.child("fire").getValue().toString());
                            String date = child.getKey().toString() + " " + child2.getKey().toString();
                            if (DataType.equals("body temperature") && child2.hasChild("body temperature") &&
                                    !child2.child("body temperature").getValue().toString().isEmpty())
                                packets.add(new Data("body temperature", child2.child("body temperature").getValue().toString() + " c", date));
                            if (DataType.equals("heartbeat") && child2.hasChild("heartbeat") &&
                                    !child2.child("heartbeat").getValue().toString().isEmpty())
                                packets.add(new Data("heartbeat", child2.child("heartbeat").getValue().toString() + " bpm", date));
                            if (DataType.equals("room temperature") && child2.hasChild("room temperature") &&
                                    !child2.child("room temperature").getValue().toString().isEmpty())
                                packets.add(new Data("room temperature", child2.child("room temperature").getValue().toString() + " c", date));
                            if (DataType.equals("humidity") && child2.hasChild("humidity") &&
                                    !child2.child("humidity").getValue().toString().isEmpty())
                                packets.add(new Data("humidity", child2.child("humidity").getValue().toString() + " %", date));
                            if (DataType.equals("gas") && child2.hasChild("gas") &&
                                    !child2.child("gas").getValue().toString().isEmpty())
                                packets.add(new Data("gas", child2.child("gas").getValue().toString(), date));
                            if (DataType.equals("fire") && child2.hasChild("fire") &&
                                    !child2.child("fire").getValue().toString().isEmpty())
                                packets.add(new Data("fire", child2.child("fire").getValue().toString(), date));
                        }
                    }
                    myAdapter.addElements(packets);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
