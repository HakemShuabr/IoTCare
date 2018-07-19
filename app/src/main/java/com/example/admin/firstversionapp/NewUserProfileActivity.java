package com.example.admin.firstversionapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewUserProfileActivity extends AppCompatActivity {

    private TextView username;
    private TextView email;
    private TextView phoneNumber;
    private ImageButton addImage;
    private ImageView profileImage;
    private String systemCode;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //// Displays the activity layout
        setContentView(R.layout.activity_new_user_profile);

        username = (TextView)findViewById(R.id.username);
        email = (TextView)findViewById(R.id.email);
        phoneNumber = (TextView)findViewById(R.id.phone);
        profileImage = (ImageView) findViewById(R.id.image);

        findViewById(R.id.back).setOnClickListener(buttonsClickListener);
        findViewById(R.id.accept).setOnClickListener(buttonsClickListener);
        findViewById(R.id.delete).setOnClickListener(buttonsClickListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        username.setText(intent.getExtras().getString("username"));
        email.setText(intent.getExtras().getString("email"));
        phoneNumber.setText(intent.getExtras().getString("phone"));
        profileImage.setImageURI(Uri.parse(intent.getExtras().getString("image")));
    }

    private View.OnClickListener buttonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back:
                    Intent homeActivity = new Intent(view.getContext(), ExistUsersActivity.class);
                    homeActivity.putExtra("systemCode", systemCode);
                    homeActivity.putExtra("active", "0");
                    startActivity(homeActivity);
                    break;

                case R.id.accept:
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
                                        if (child.getValue(User.class).getName().toString().equals(username.getText().toString())) {
                                            if(i==0)
                                                userAccount.child("users").child("doctors").child(username.getText().toString()).child("active").setValue(1);
                                            else if(i==1)
                                                userAccount.child("users").child("guardians").child(username.getText().toString()).child("active").setValue(1);
                                            Toast.makeText(NewUserProfileActivity.this, "user account is accepted", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;

                case R.id.delete:
                    final DatabaseReference accountDelete = database.getReference(systemCode);
                    accountDelete.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        if (child.getValue(User.class).getName().toString().equals(username.getText().toString())) {
                                            if(i==0)
                                                accountDelete.child("users").child("doctors").child(username.getText().toString()).removeValue();
                                            else if(i==1)
                                                accountDelete.child("users").child("guardians").child(username.getText().toString()).removeValue();
                                            Toast.makeText(NewUserProfileActivity.this, "user account is deleted", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    break;

            }
        }
    };
}
