package com.example.admin.firstversionapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReportsList extends AppCompatActivity {

    ArrayList<Report> reports = new ArrayList<Report>();
    private String systemCode;
    private String name;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyAdapter myAdapter = new MyAdapter();
        myAdapter.addElements(reports);
        recyclerView.setAdapter(myAdapter);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        name = intent.getExtras().getString("username");
        final DatabaseReference userAccount = database.getReference(systemCode);
        userAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user.setName(dataSnapshot.getValue(User.class).getName());

                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> children = null;

                    children = dataSnapshot.child("users").child("doctors").child(name).child("report").getChildren();

                    Log.v("come", dataSnapshot.getKey());
                    for (DataSnapshot child : children) {
                        //if (child.getValue(User.class).getActive() == 1) {
                            Report report = child.getValue(Report.class);
                            reports.add(report);
                        //}
                    }

                    myAdapter.addElements(reports);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.back:
                        Intent intent = new Intent(view.getContext(), ViewReport.class);
                        intent.putExtra("systemCode", systemCode);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        ArrayList<Report> elements = new ArrayList<Report>();

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowView = getLayoutInflater().inflate(R.layout.single_report, parent, false);
            return new MyAdapter.MyViewHolder(rowView);
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            holder.textView.setText(elements.get(position).getTitle());
            holder.textView2.setText(elements.get(position).getReportDate());
            //holder.imageView.setImageResource(elements.get(position).getIcon());
        }

        @Override
        public int getItemCount() {
            return elements.size();
        }

        public void addElements(ArrayList<Report> versions) {
            elements.clear();
            elements.addAll(versions);
            notifyDataSetChanged();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            public TextView textView2;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView)itemView.findViewById(R.id.reportTitle);
                textView2 = (TextView)itemView.findViewById(R.id.reportDate);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("systemCode", systemCode);
                        intent.putExtra("name", name);
                        intent.putExtra("reportTitle", elements.get(getAdapterPosition()).getTitle());
                        intent.putExtra("reportDate", elements.get(getAdapterPosition()).getReportDate());
                        intent.putExtra("reportContent", elements.get(getAdapterPosition()).getReport());
                        startActivity(intent.setClass(view.getContext(), SingleReportView.class));
                    }
                });
            }
        }


    }
}
