package com.example.clinique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinique.Models.Patient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.graphics.Color.parseColor;

public class PatientActivity extends AppCompatActivity {
    private TextView name, dob, idNumber;
    private Button history, sessions;
    private EditText text;
    private boolean isHistoryClicked = false;
    private boolean isSessionsClicked = true;

    Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        name = (TextView)findViewById(R.id.name);
        dob = (TextView)findViewById(R.id.dob);
        idNumber = (TextView)findViewById(R.id.idNumber);

        text = (EditText)findViewById(R.id.text);

        history = (Button)findViewById(R.id.history);
        sessions = (Button)findViewById(R.id.sessions);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(parseColor("#203FA3"));
        }

        if (getIntent().hasExtra("uid")){
            final String uid = getIntent().getStringExtra("uid");
            Toast.makeText(PatientActivity.this, uid, Toast.LENGTH_LONG).show();

            dataDisplay(uid);
        }


    }

    private void dataDisplay(String uid){

        Query query = FirebaseDatabase.getInstance().getReference("Accounts").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).
                child("Patients").orderByKey().equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        patient = snapshot.getValue(Patient.class);
                        name.setText(patient.getName());
                        dob.setText(patient.getDop());
                        idNumber.setText(patient.getId());

                    }
                /*    patientAdapter.notifyDataSetChanged();
                    loadingBar.setVisibility(View.GONE);*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHistoryClicked = true;
                isSessionsClicked = false;
                if (isHistoryClicked) {
                   // toggle the boolean flag
                    setClickedColor(history);
                    setUnclickedColor(sessions);
                }

                text.setText(patient.getHistory());
            }
        });

        sessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHistoryClicked = false;
                isSessionsClicked = true;
                if (isSessionsClicked) {
                    setClickedColor(sessions);
                    setUnclickedColor(history);


                }

                text.setText("");
                /*text.setEnabled(false);
                text.setTextColor(getResources().getColor(R.color.blackColor));*/
            }
        });


    }

    private void setClickedColor(Button button){
        button.setBackgroundResource(R.drawable.clicked_profile_button);
        button.setTextColor(getResources().getColor(R.color.ourColor));
    }

    private void setUnclickedColor(Button button){
        button.setBackgroundResource(R.drawable.unclicked_profile_button);
        button.setTextColor(getResources().getColor(R.color.whiteColor));
    }
}
