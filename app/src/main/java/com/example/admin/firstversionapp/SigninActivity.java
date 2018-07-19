package com.example.admin.firstversionapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SigninActivity extends AppCompatActivity {
    private EditText username;
    private EditText email;
    private EditText phone;
    private EditText password;
    private EditText confirmPassword;
    private Spinner relation;
    private EditText systemCode;
    private String relationType = "Family Member";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // initialize UI elements in the activity
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.conPassword);
        systemCode = (EditText) findViewById(R.id.systemCode);
        relation = (Spinner) findViewById(R.id.relationSpinner);

        // adds listener to the buttons
        findViewById(R.id.done).setOnClickListener(buttonsClickListener);
        findViewById(R.id.back).setOnClickListener(buttonsClickListener);

//        signup = database.getReference("user");

        // inserts items into the drop list
        ArrayAdapter<String> relationAdapter = new ArrayAdapter<String>(SigninActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.relationTypes));
        relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relation.setAdapter(relationAdapter);
        // listens for item chosen from the drop down list
        relation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                relationType = adapterView.getItemAtPosition(index).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    // Listens to buttons clicks and preforms action based on button id
    private View.OnClickListener buttonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.back:
                    startActivity(new Intent(view.getContext(), LoginActivity.class));
                    break;
                case R.id.done:
                    if (validateInput()) {

                        signup = database.getReference(systemCode.getText().toString());
                        signup.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //user.setName(dataSnapshot.getValue(User.class).getName());

                                if(dataSnapshot.exists()) {
                                    boolean userExist = false;
                                    for (int i = 0; i < 2; i++) {
                                        Iterable<DataSnapshot> children = null;

                                        if (i == 0) // checks doctor table
                                            children = dataSnapshot.child("users").child("doctors").getChildren();
                                        else if (i == 1) // checks guardian table
                                            children = dataSnapshot.child("users").child("guardians").getChildren();

                                        Log.v("come", dataSnapshot.getKey());
//                                        for (DataSnapshot child : children) {
//                                            Log.v("comehere", child.getValue(User.class).getName().toString());
//                                            Log.v("gothere", username.getText().toString());
//                                            if (child.exists() && username.getText().toString().equals(child.getValue(User.class).getName().toString())) {
//                                                Toast.makeText(SigninActivity.this, "username is already used!", Toast.LENGTH_LONG).show();
//                                                userExist = true;
//                                                break;
//                                            }
//                                        }
                                    }
                                    if (!userExist) {
                                        //DatabaseReference saveNewUser = database.getReference(systemCode.getText().toString());

                                        if (relationType.equals("Doctor")) {
                                            Doctor newDoctor = new Doctor(username.getText().toString(), password.getText().toString(),
                                                    email.getText().toString(), phone.getText().toString(), 0);
                                            signup.child("users").child("doctors").child(newDoctor.getName().toString()).setValue(newDoctor);
                                        } else {

                                            Guardian newGuardian = new Guardian(username.getText().toString(), password.getText().toString(),
                                                    email.getText().toString(), phone.getText().toString(), 0, relationType);
                                            signup.child("users").child("guardians").child(newGuardian.getName().toString()).setValue(newGuardian);
                                            //signup.child("users").child("guardians").child(newGuardian.getName().toString()).setValue(newGuardian.get)
                                        }
                                        startActivity(new Intent(view.getContext(), LoginActivity.class));
                                    }
                                    //nameView.setText(user.getName().toString());
                                    //Toast.makeText(SigninActivity.this, "got it", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(SigninActivity.this, "system code is not correct!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    break;

            }

        }
    };

    /**
     * Validates the input data entered by the user and checks
     * if all the data meets the required format
     *
     * @return true if all data is valid or false otherwise
     */
    private boolean validateInput() {
        boolean valid = true;
        if (username.getText().toString().isEmpty()) {
            username.setHint("username is required");
            username.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }

        if (email.getText().toString().isEmpty()) {
            email.setHint("email is required");
            email.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }

        if (phone.getText().toString().isEmpty()) {
            phone.setHint("phone number is required");
            phone.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }

        if (systemCode.getText().toString().isEmpty()) {
            systemCode.setHint("system code is required");
            systemCode.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }

        if (password.getText().toString().isEmpty()) {
            password.setHint("password is required");
            password.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        } else if (password.getText().toString().length() < 6) {
            password.setText("");
            confirmPassword.setText("");
            confirmPassword.setHint("confirm Password*");
            password.setHint("more than 6 characters");
            password.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        } else if (password.getText().toString().length() > 15) {
            password.setText("");
            confirmPassword.setText("");
            confirmPassword.setHint("confirm Password*");
            password.setHint("less than 15 characters");
            password.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        } else if (password.getText().toString().contains(" ")) {
            password.setText("");
            confirmPassword.setText("");
            confirmPassword.setHint("confirm Password*");
            password.setHint("spaces not allowed");
            password.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }

        if (confirmPassword.getText().toString().isEmpty()) {
            confirmPassword.setHint("confirmation is required");
            confirmPassword.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        } else if (!(confirmPassword.getText().toString().equals(password.getText().toString()))) {
            confirmPassword.setText("");
            confirmPassword.setHint("password does not match");
            confirmPassword.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }
        return valid;
    }
}


