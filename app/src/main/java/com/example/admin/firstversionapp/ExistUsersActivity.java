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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.admin.firstversionapp.R.id.systemCode;

public class ExistUsersActivity extends AppCompatActivity {
    ArrayList<User> users = new ArrayList<User>();
    private String systemCode;
    private int active;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_users);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyAdapter myAdapter = new MyAdapter();
        myAdapter.addElements(users);
        recyclerView.setAdapter(myAdapter);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        active = Integer.parseInt(intent.getExtras().getString("active"));

        final DatabaseReference userAccount = database.getReference(systemCode);
        userAccount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //user.setName(dataSnapshot.getValue(User.class).getName());

                if (dataSnapshot.exists()) {
                    boolean userExist = false;
                    for (int i = 0; i < 2; i++) {
                        Iterable<DataSnapshot> children = null;

                        if (i == 0) // checks doctor table
                            children = dataSnapshot.child("users").child("doctors").getChildren();
                        else if (i == 1) // checks guardian table
                            children = dataSnapshot.child("users").child("guardians").getChildren();

                        Log.v("come", dataSnapshot.getKey());
                        for (DataSnapshot child : children) {
                            if (child.getValue(User.class).getActive() == active) {
                                User user = child.getValue(User.class);
                                users.add(user);
                            }
                        }
                    }
                    myAdapter.addElements(users);
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
                        Intent intent = new Intent(view.getContext(), ManagementActivity.class);
                        intent.putExtra("systemCode", systemCode);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        ArrayList<User> elements = new ArrayList<User>();

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rowView = getLayoutInflater().inflate(R.layout.users_row, parent, false);
            return new MyViewHolder(rowView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
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
                        intent.putExtra("email", elements.get(getAdapterPosition()).getEmail());
                        intent.putExtra("phone", elements.get(getAdapterPosition()).getNumber());
                        intent.putExtra("image", elements.get(getAdapterPosition()).getImage());
                        if(active == 0)
                            startActivity(intent.setClass(view.getContext(), NewUserProfileActivity.class));
                        else if(active == 1)
                            startActivity(intent.setClass(view.getContext(), ExistUserProfileActivity.class));
                    }
                });
            }
        }


    }
}