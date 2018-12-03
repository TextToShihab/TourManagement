package com.example.shihab.tourmanagement;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateTravelEventActivity extends AppCompatActivity {

    private EditText destination, budget, fromDate, toDate;
    private Button updateTravelEvent;

    Calendar dateset = Calendar.getInstance();
    private String fDate, tDate;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private ProgressDialog mProgress;

    private String mUpdate_key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_travel_event);

        mAuth= FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("TourEvents");
        mDatabaseUsers =  FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mUpdate_key = getIntent().getExtras().getString("event_id");

        mProgress = new ProgressDialog(this);

        destination = (EditText) findViewById(R.id.update_TDestination);
        budget = (EditText) findViewById(R.id.update_EBudget);
        fromDate = (EditText) findViewById(R.id.update_frmDate);
        toDate = (EditText) findViewById(R.id.update_toDate);
        updateTravelEvent = (Button) findViewById(R.id.updateTravelEvent);


        updateTravelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdatingEvent();
            }
        });


        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_from_Date();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_to_Date();
            }
        });

       mDatabase.child(mUpdate_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String Destination = (String)dataSnapshot.child("Destination").getValue();
                String Budget = (String)dataSnapshot.child("Budget").getValue();
                String From = (String)dataSnapshot.child("From").getValue();
                String To = (String)dataSnapshot.child("To").getValue();

                destination.setText(Destination);
                budget.setText(Budget);
                fromDate.setText(From);
                toDate.setText(To);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void update_from_Date() {
        new DatePickerDialog(this, from, dateset.get(Calendar.YEAR), dateset.get(Calendar.MONTH), dateset.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener from = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            fDate = dayOfMonth+"/"+(month+1)+"/"+year;
            dateset.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateset.set(Calendar.MONTH, month);
            dateset.set(Calendar.YEAR, year);

            fromDate.setText(fDate);
        }
    };

    public void update_to_Date() {
        new DatePickerDialog(this, to, dateset.get(Calendar.YEAR), dateset.get(Calendar.MONTH), dateset.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener to = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            tDate = dayOfMonth+"/"+(month+1)+"/"+year;

            dateset.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateset.set(Calendar.MONTH, month);
            dateset.set(Calendar.YEAR, year);

            toDate.setText(tDate);
        }
    };

    private void startUpdatingEvent(){

        mProgress.setMessage("Saving event...");

        final String destination_val = destination.getText().toString().trim();
        final String budget_val = budget.getText().toString().trim();
        final String fromDate_val = fromDate.getText().toString().trim();
        final String toDate_val = toDate.getText().toString().trim();

        if(!TextUtils.isEmpty(destination_val) && !TextUtils.isEmpty(budget_val) && !TextUtils.isEmpty(fromDate_val) && !TextUtils.isEmpty(toDate_val)) {

            mProgress.show();

            final DatabaseReference newTEvent = mDatabase.child(mUpdate_key);//every time create new post rather than overwrite previous



            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newTEvent.child("Destination").setValue(destination_val);
                    newTEvent.child("Budget").setValue(budget_val);
                    newTEvent.child("From").setValue(fromDate_val);
                    newTEvent.child("To").setValue(toDate_val);

                    newTEvent.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                startActivity(new Intent(UpdateTravelEventActivity.this, TravelEventActivity.class));

                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mProgress.dismiss();

        }

    }
}
