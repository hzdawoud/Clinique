package com.example.clinique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.graphics.Color.parseColor;

public class LoginActivity extends AppCompatActivity {

    private EditText inEmail, inPassword;
    private TextView forgetPassword, signUp;
    private Button signIn;
    private ProgressBar prgBar;
    private ProgressDialog mProgress;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String isVerified;


    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if (firebaseUser != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(parseColor("#203FA3"));
        }

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        inEmail = (EditText)findViewById(R.id.email);
        inPassword = (EditText)findViewById(R.id.password);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        signUp = (TextView) findViewById(R.id.sign_up);

        prgBar = (ProgressBar) findViewById(R.id.progressBar);

        signIn = (Button) findViewById(R.id.sign_in);

        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inEmail.getText().toString();
                final String password = inPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgress.show();

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                 verification();

                                } else {
                                    prgBar.setVisibility(View.GONE);
                                    if (password.length() < 6) {
                                        mProgress.dismiss();
                                        inPassword.setError("Password too short, enter minimum 6 characters!");
                                    } else {
                                        mProgress.dismiss();
                                        Toast.makeText(LoginActivity.this, "Authentication failed, check your email and password or sign up", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });

            }
        });

    }

    private void verification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Accounts")
                .child(auth.getCurrentUser().getUid()).child("verified");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isVerified = dataSnapshot.getValue(String.class);
                if (isVerified != null && isVerified.equalsIgnoreCase("1")){
                    mProgress.dismiss();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    auth.signOut();
                    mProgress.dismiss();
                    Toast.makeText(LoginActivity.this, "Sorry! Your account doesn't verified", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgress.dismiss();
                Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
