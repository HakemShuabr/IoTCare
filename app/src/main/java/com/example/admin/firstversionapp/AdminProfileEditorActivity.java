package com.example.admin.firstversionapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProfileEditorActivity extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText phone;
    private EditText password;
    private EditText confirmPassword;

    private String systemCode;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_editor);

        // initialize UI elements in the activity
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.conPassword);

        // adds listener to the buttons
        findViewById(R.id.done).setOnClickListener(buttonsClickListener);
        findViewById(R.id.back).setOnClickListener(buttonsClickListener);

        Intent intent = getIntent();
        systemCode = intent.getExtras().getString("systemCode");

    }

    // Listens to buttons clicks and preforms action based on button id
    private View.OnClickListener buttonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            Intent intent = new Intent(view.getContext(), ManagementActivity.class);
            intent.putExtra("systemCode", systemCode);
            switch (view.getId()) {
                case R.id.back:
                    startActivity(intent);
                    break;
                case R.id.done:
                    if (validateInput()) {
                        signup = database.getReference(systemCode);
                        signup.addListenerForSingleValueEvent(valueEventListener);
                        startActivity(intent);
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
            phone.setHint("email is required");
            phone.setHintTextColor(Color.parseColor("#FF0000"));
            valid = false;
        }

        if (phone.getText().toString().isEmpty()) {
            phone.setHint("phone number is required");
            phone.setHintTextColor(Color.parseColor("#FF0000"));
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

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //user.setName(dataSnapshot.getValue(User.class).getName());

            if(dataSnapshot.exists()) {
                //signup.child("patient").removeValue();
                Admin admin = new Admin(username.getText().toString(), password.getText().toString(),
                        email.getText().toString(), phone.getText().toString());
                signup.child("admin").setValue(admin);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
