package com.example.admin.firstversionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
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

public class PatientProfileManagementActivity extends AppCompatActivity {
    private TextView patientName;
    private TextView age;
    private TextView disability;
    private TextView phone;
    private ImageView profileImage;
    private ImageButton addImage;

    private PDU patient = new PDU();

    private String systemCode;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile_management);
        Log.v("comehereto", "die");
        patientName = (TextView)findViewById(R.id.patientName);
        age = (TextView)findViewById(R.id.age);
        disability = (TextView)findViewById(R.id.disability);
        phone = (TextView)findViewById(R.id.phone);

        profileImage = (ImageView) findViewById(R.id.image);
        addImage = (ImageButton)findViewById(R.id.addImage);
        addImage.setOnClickListener(buttonsClickListener);
        findViewById(R.id.back).setOnClickListener(buttonsClickListener);
        findViewById(R.id.edit).setOnClickListener(buttonsClickListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addImage.getVisibility() == view.INVISIBLE)
                    addImage.setVisibility(view.VISIBLE);
                else if(addImage.getVisibility() == view.VISIBLE)
                    addImage.setVisibility(view.INVISIBLE);
            }
        });
        DatabaseReference patientData = database.getReference(systemCode);
        patientData.addListenerForSingleValueEvent(valueEventListener);
        patientData.removeEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener= new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.child("patient").exists()) {
                patient = dataSnapshot.child("patient").getValue(PDU.class);
                patientName.setText(patient.getName().toString());
                age.setText(""+patient.getAge());
                disability.setText(patient.getDisability().toString());
                phone.setText(patient.getPhone().toString());

                if(dataSnapshot.child("patient").child("image").exists()) {
                    Uri imageURI = Uri.parse(dataSnapshot.child("patient").child("image").getValue().toString());
                    profileImage.setImageURI(imageURI);
                }
                else
                    Toast.makeText(PatientProfileManagementActivity.this, "click on image space to add an image", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(PatientProfileManagementActivity.this, "tap edit to create new patient profile!", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private View.OnClickListener buttonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back:
                    Intent homeActivity = new Intent(view.getContext(), ManagementActivity.class);
                    homeActivity.putExtra("systemCode", systemCode);
                    startActivity(homeActivity);
                    break;
                case R.id.addImage:
                    handleInsertImage(view);
                    break;

                case R.id.edit:
                    Intent editProfileActivity = new Intent(view.getContext(), PatientProfileEditorActivity.class);
                    editProfileActivity.putExtra("systemCode", systemCode);
                    startActivity(editProfileActivity);
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

        else {
            this.profileImage.setImageURI(image);
            final DatabaseReference userData = database.getReference(systemCode);
            userData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userData.child("patient").child("image").setValue(image.toString());
                    } else {
                        Toast.makeText(PatientProfileManagementActivity.this, "error while uploading the image!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
