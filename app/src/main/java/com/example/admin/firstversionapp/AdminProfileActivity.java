package com.example.admin.firstversionapp;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AdminProfileActivity extends AppCompatActivity {
    private TextView name;
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
        setContentView(R.layout.activity_admin_profile);

        name = (TextView)findViewById(R.id.username);
        email = (TextView)findViewById(R.id.email);
        phoneNumber = (TextView)findViewById(R.id.phone);
        profileImage = (ImageView) findViewById(R.id.image);

        findViewById(R.id.back).setOnClickListener(buttonsClickListener);
        addImage = (ImageButton)findViewById(R.id.addImage);
        addImage.setOnClickListener(buttonsClickListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");
        name.setText(intent.getExtras().getString("username"));
        email.setText(intent.getExtras().getString("email"));
        phoneNumber.setText(intent.getExtras().getString("phone"));

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addImage.getVisibility() == view.INVISIBLE)
                    addImage.setVisibility(view.VISIBLE);
                else if(addImage.getVisibility() == view.VISIBLE)
                    addImage.setVisibility(view.INVISIBLE);
            }
        });

        final DatabaseReference userData = database.getReference(systemCode).child("admin");
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("image").exists()) {
                    Uri imageURI = Uri.parse(dataSnapshot.child("image").getValue().toString());
                    profileImage.setImageURI(imageURI);
                }
                else {
                    Toast.makeText(AdminProfileActivity.this, "click on image space to add an image", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private View.OnClickListener buttonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back:
                    Intent homeActivity = new Intent(view.getContext(), AdminHomeActivity.class);
                    homeActivity.putExtra("systemCode", systemCode);
                    startActivity(homeActivity);
                    break;
                case R.id.addImage:
                    handleInsertImage(view);
                    break;
            }
        }
    };

    public void handleInsertImage(View view){
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Uri image = data.getData();
        if(requestCode == RESULT_CANCELED){}
        //profileImage.setImageURI(Uri.parse(image.toString()));

        else {
            this.profileImage.setImageURI(image);
            final DatabaseReference userData = database.getReference(systemCode).child("admin");
            userData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userData.child("image").setValue(image.toString());
                    } else {
                        Toast.makeText(AdminProfileActivity.this, "error while uploading the image!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
