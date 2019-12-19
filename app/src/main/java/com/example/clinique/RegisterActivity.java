package com.example.clinique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.graphics.Color.parseColor;

public class RegisterActivity extends AppCompatActivity {

    private EditText ccName, ccAddress, ccEmail, ccPhone, ccPassword;
    private Button submit;
    private ProgressBar progressBar;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(parseColor("#203FA3"));
        }

        ccName = (EditText)findViewById(R.id.clinic_name);
        ccAddress = (EditText)findViewById(R.id.clinic_address);
        ccEmail = (EditText)findViewById(R.id.email);
        ccPhone = (EditText)findViewById(R.id.contact_phone);
        ccPassword = (EditText)findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        submit = (Button)findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = ccName.getText().toString().trim();
                String address = ccAddress.getText().toString().trim();
                String email = ccEmail.getText().toString().trim();
                String phone = ccPhone.getText().toString().trim();
                String password = ccPassword.getText().toString().trim();



                if (TextUtils.isEmpty(name)) {
                    ccName.setError("Enter your clinic name");
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    ccAddress.setError("Enter your address");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    ccEmail.setError("Enter your email address");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    ccPhone.setError("Enter your phone number");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    ccPassword.setError("Enter your password!");
                    return;
                }

                if (password.length() < 6) {
                    ccPassword.setError("Password too short, enter minimum 6 characters!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                register(name, address, email, phone, password);
            }
        });
    }

    public void register(final String name, final String address, String email, final String phone, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Accounts").child(userID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", userID);
                            map.put("name", name);
                            map.put("address", address);
                            map.put("phone", phone);
                            map.put("verified", "0");

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "Your account will be verified within 24 hours", Toast.LENGTH_SHORT).show();
                                        auth.signOut();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "You can't register with this email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

}
