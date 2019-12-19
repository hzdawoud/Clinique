package com.example.clinique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinique.Adapters.PatientAdapter;
import com.example.clinique.Models.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.graphics.Color.parseColor;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PatientAdapter patientAdapter;
    private List<Patient> patientList;

    private Button add, exit, logOut;
    private FloatingActionButton addPatient;
    private EditText name, id, phone, address, searchBar;
    private TextView dob;
    private View view;
    private Dialog dialog;

    private FirebaseAuth auth;
    private DatabaseReference usersRef, patientsRef, mainRef;
    private FirebaseUser firebaseUser;

    private String hashed, userID;

    private ProgressBar bar, loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(parseColor("#203FA3"));
        }


        loadingBar = (ProgressBar)findViewById(R.id.loading_bar);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        searchBar = (EditText) findViewById(R.id.search);

        patientList = new ArrayList<>();
        patientAdapter = new PatientAdapter(patientList);
        recyclerView.setAdapter(patientAdapter);

        readPatients();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchPatients(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addPatient = (FloatingActionButton) findViewById(R.id.add_patient);
        logOut = (Button) findViewById(R.id.log_out);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });

        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.add_patient_dialog, null);*/
                dialog = new Dialog(HomeActivity.this, R.style.Theme_Dialog);
                dialog.setContentView(R.layout.add_patient_dialog);

                add = dialog.findViewById(R.id.addd);
                exit = dialog.findViewById(R.id.cancel);

                name = dialog.findViewById(R.id.inName);
                id = dialog.findViewById(R.id.inId);
                dob = dialog.findViewById(R.id.inDob);
                phone = dialog.findViewById(R.id.inPhone);
                address = dialog.findViewById(R.id.inAddress);

                bar = dialog.findViewById(R.id.progressBar);

                auth = FirebaseAuth.getInstance();

                firebaseUser = auth.getCurrentUser();

            /*    dialog.setContentView(view);*/
                dialog.show();

                dob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar calendar = Calendar.getInstance();
                        int yy = calendar.get(Calendar.YEAR);
                        int mm = calendar.get(Calendar.MONTH);
                        int dd = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePicker = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String date = (dayOfMonth) + "/" + (monthOfYear+1)
                                        + "/" + (year);
                                dob.setText(date);
                            }
                        }, yy, mm, dd);
                        datePicker.show();
                    }
                });

                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String sName = name.getText().toString().trim();
                        final String sId = id.getText().toString().trim();
                        final String sDob = dob.getText().toString().trim();
                        final String sPhone = phone.getText().toString().trim();
                        final String sAddress = address.getText().toString().trim();
                        hashed = md5(sId.concat(sDob));

                        if (TextUtils.isEmpty(sName)) {
                            name.setError("Enter your patient name");
                            return;
                        }

                        if (TextUtils.isEmpty(sId)) {
                            id.setError("Enter id number");
                            return;
                        }

                        if (TextUtils.isEmpty(sDob)) {
                            dob.setError("This filed is required");
                            return;
                        }

                        if (TextUtils.isEmpty(sPhone)) {
                            phone.setError("Enter patient's phone number");
                            return;
                        }

                        if (TextUtils.isEmpty(sAddress)) {
                            address.setError("Fill up this field man!");
                            return;
                        }

                        bar.setVisibility(View.VISIBLE);

                        mainRef = FirebaseDatabase.getInstance().getReference();
                        userID = firebaseUser.getUid();

                        usersRef = mainRef.child("Users");
                        patientsRef = mainRef.child("Accounts").child(userID).child("Patients");

                        Query query = mainRef.child("Accounts").child(userID.toString()).child("Patients").child(hashed);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //here we check if the user is already found in the account
                                if (dataSnapshot.exists()) {
                                    bar.setVisibility(View.GONE);
                                    Toast.makeText(HomeActivity.this, "Patient is already found, please validate your information", Toast.LENGTH_LONG).show();
                                    return;
                                } else {
                                    //the user is not found, so we check in "Users" table
                                    Query qry = mainRef.child("Users").child(hashed);
                                    qry.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                //user is found in "Users" table so we wanna copy it into my account ("patents" node)
                                                moveGame(usersRef, patientsRef, hashed);

                                            } else {
                                                //user not found at all so we wanna add it twice in bot of "all users" and "patients" tables
                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("uid", hashed);
                                                map.put("name", sName);
                                                map.put("id", sId);
                                                map.put("dop", sDob);
                                                map.put("phone", sPhone);
                                                map.put("address", sAddress);
                                                map.put("history", "worked");

                                                usersRef.child(hashed).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                        } else {
                                                            bar.setVisibility(View.GONE);
                                                            Toast.makeText(HomeActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    }
                                                });

                                                patientsRef.child(hashed).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            bar.setVisibility(View.GONE);
                                                            Toast.makeText(HomeActivity.this, "User successfully created", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                        } else {
                                                            bar.setVisibility(View.GONE);
                                                            Toast.makeText(HomeActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
    }

    private void searchPatients(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Accounts").child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("Patients").orderByChild("id")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Patient user = snapshot.getValue(Patient.class);
                    patientList.add(user);
                }
                patientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPatients() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Accounts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Patients");

        reference.orderByChild("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (searchBar.getText().toString().equals("")) {
                    patientList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Patient user = snapshot.getValue(Patient.class);
                        patientList.add(user);
                    }
                    patientAdapter.notifyDataSetChanged();
                    loadingBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void moveGame(final DatabaseReference fromPath, final DatabaseReference toPath, final String kind) {
        fromPath.child(kind).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.child(kind).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            bar.setVisibility(View.GONE);
                            Toast.makeText(HomeActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        } else {
                            bar.setVisibility(View.GONE);
                            Toast.makeText(HomeActivity.this, "User successfully loaded", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
