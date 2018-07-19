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

public class ViewReport extends AppCompatActivity {

    ArrayList<User> users = new ArrayList<User>();
    private String systemCode;
    private int active;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyAdapter myAdapter = new MyAdapter();
        myAdapter.addElements(users);
        recyclerView.setAdapter(myAdapter);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");

        final DatabaseReference userAccount = database.getReference(systemCode);
        userAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user.setName(dataSnapshot.getValue(User.class).getName());

                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> children = null;

                    children = dataSnapshot.child("users").child("doctors").getChildren();

                    Log.v("come", dataSnapshot.getKey());
                    for (DataSnapshot child : children) {
                        if (child.getValue(User.class).getActive() == 1) {
                            User user = child.getValue(User.class);
                            users.add(user);
                        }
                    }

                    myAdapter.addElements(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        ArrayList<User> elements = new ArrayList<User>();

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowView = getLayoutInflater().inflate(R.layout.users_row, parent, false);
            return new MyAdapter.MyViewHolder(rowView);
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            holder.textView.setText(elements.get(position).getName());
            holder.textView2.setText(elements.get(position).getEmail());
            //holder.imageView.setImageResource(elements.get(position).getIcon());

            String iconUrl = elements.get(position).getImage();
            holder.imageHolder.setImageURI(Uri.parse(iconUrl));


        }

        @Override
        public int getItemCount() {
            return elements.size();
        }

        public void addElements(ArrayList<User> versions) {
            elements.clear();
            elements.addAll(versions);
            notifyDataSetChanged();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;
            public TextView textView2;
            public ImageView imageHolder;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView)itemView.findViewById(R.id.textView);
                textView2 = (TextView)itemView.findViewById(R.id.textView2);
                imageHolder = (ImageView) itemView.findViewById(R.id.imageHolder);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("systemCode", systemCode);
                        intent.putExtra("username", elements.get(getAdapterPosition()).getName());
                        startActivity(intent.setClass(view.getContext(), ReportsList.class));
                    }
                });
            }
        }


    }
}
