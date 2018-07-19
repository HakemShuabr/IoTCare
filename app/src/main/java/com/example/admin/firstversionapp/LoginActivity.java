package com.example.admin.firstversionapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText systemCode;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.signin).setOnClickListener(buttonClickListener);
        findViewById(R.id.login).setOnClickListener(buttonClickListener);
        username = (EditText)findViewById(R.id.nameText);
        password = (EditText)findViewById(R.id.passwordText);
        systemCode = (EditText)findViewById(R.id.systemCode);


    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.signin:
                    Intent signActivity = new Intent(view.getContext(), SigninActivity.class);
                    startActivity(signActivity);
                    break;
                case R.id.login:
                    validateInput();
                    break;
            }

        }
    };

    /**
     * Validates the input data entered by the user and checks
     * if username and password are correct
     */
    private void validateInput() {
        final String usernameStr = username.getText().toString();
        final String passwordStr = password.getText().toString();

        if (username.getText().toString().isEmpty()) {
            username.setHint("username is required");
            username.setHintTextColor(Color.parseColor("#FF0000"));

        }

        else if (password.getText().toString().isEmpty()) {
            password.setHint("password is required");
            password.setHintTextColor(Color.parseColor("#FF0000"));
        }
        else if(systemCode.getText().toString().isEmpty()){
            systemCode.setHint("system code is required");
            systemCode.setHintTextColor(Color.parseColor("#FF0000"));
        }


        else{
            final DatabaseReference login = database.getReference(systemCode.getText().toString());
            login.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userRole = "";
                        if (usernameStr.equals(dataSnapshot.child("admin").getValue(Admin.class).getName().toString())) {
                            if (passwordStr.equals(dataSnapshot.child("admin").getValue(Admin.class).getPassword().toString())) {
                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                intent.putExtra("systemCode", systemCode.getText().toString());
                                startActivity(intent);
                            } else {
                                password.setText("");
                                password.setHint("Password");
                                Toast.makeText(LoginActivity.this, "wrong password!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else if (usernameStr.equals(dataSnapshot.child("patient").getValue(PDU.class).getName().toString())) {
                            if (passwordStr.equals(dataSnapshot.child("patient").getValue(PDU.class).getPassword().toString())) {
                                Intent intent = new Intent(LoginActivity.this, PatientHomeActivity.class);
                                intent.putExtra("systemCode", systemCode.getText().toString());
                                startActivity(intent);
                            } else {
                                password.setText("");
                                password.setHint("Password");
                                Toast.makeText(LoginActivity.this, "wrong password!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (dataSnapshot.child("users").child("doctors").child(usernameStr).exists()) {
                                dataSnapshot = dataSnapshot.child("users").child("doctors").child(usernameStr);
                                userRole = "doctors";
                            } else if (dataSnapshot.child("users").child("guardians").child(usernameStr).exists()) {
                                dataSnapshot = dataSnapshot.child("users").child("guardians").child(usernameStr);
                                userRole = "guardians";
                            } else {
                                username.setText("");
                                password.setText("");
                                systemCode.setText("");
                                username.setHint("username");
                                password.setHint("password");
                                systemCode.setHint("system code");
                                Toast.makeText(LoginActivity.this, "wrong username!", Toast.LENGTH_LONG).show();
                            }

                            if (usernameStr.equals(dataSnapshot.getValue(User.class).getName().toString())) {
                                if (dataSnapshot.getValue(User.class).getActive() == 1) {
                                    if (passwordStr.equals(dataSnapshot.getValue(User.class).getPassword().toString())) {
                                        Intent intent;
                                        if (userRole == "doctors") {
                                            intent = new Intent(LoginActivity.this, DoctorHomeActivity.class);
                                            intent.putExtra("systemCode", systemCode.getText().toString());
                                            intent.putExtra("username", usernameStr.toString());
                                            startActivity(intent);
                                            Log.v("done", "yes");
                                        } else if (userRole == "guardians") {
                                            intent = new Intent(LoginActivity.this, GuardActivity.class);
                                            intent.putExtra("systemCode", systemCode.getText().toString());
                                            intent.putExtra("username", usernameStr.toString());
                                            startActivity(intent);
                                            Log.v("done", "yes");
                                        }
                                    } else {
                                        password.setText("");
                                        password.setHint("Password");
                                        Toast.makeText(LoginActivity.this, "wrong password!", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "your account is not activated!", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                username.setText("");
                                password.setText("");
                                username.setHint("username/Email");
                                password.setHint("Password");
                                Toast.makeText(LoginActivity.this, "wrong username or password!", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                    else{
                            systemCode.setText("");
                            systemCode.setHint("system code");
                            Toast.makeText(LoginActivity.this, "wrong system code!", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
