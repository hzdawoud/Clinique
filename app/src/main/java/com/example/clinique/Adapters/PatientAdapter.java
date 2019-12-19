package com.example.clinique.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.clinique.Models.Patient;
import com.example.clinique.R;

import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.MyViewHolder> {
    private List<Patient> mPatient;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, id, dop;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            id = (TextView) view.findViewById(R.id.id_number);
            dop = (TextView) view.findViewById(R.id.age);
        }
    }


    public PatientAdapter(List<Patient> patientsList) {
        mPatient = patientsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Patient patient = mPatient.get(position);
        holder.name.setText(patient.getName());
        holder.id.setText(patient.getId());
        holder.dop.setText(patient.getDop());
    }

    @Override
    public int getItemCount() {
        return mPatient.size();
    }
}