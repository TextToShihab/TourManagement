package com.example.shihab.tourmanagement;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

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

public class UpdateExpenseRecordActivity extends AppCompatActivity {

    private EditText EDetails, ECost, CDate, CTime;
    private Button updateRecord;

    Calendar dateset = Calendar.getInstance();
    private String currentDate, currentTime;

    private DatabaseReference mExpRecordDatabase;
    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseEvents;


    private String mUpdate_key = null;
    private String evnt_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_expense_record);

        mUpdate_key = getIntent().getExtras().getString("record_id");

        mAuth= FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mExpRecordDatabase = FirebaseDatabase.getInstance().getReference().child("Exp_record");
        mDatabaseEvents = FirebaseDatabase.getInstance().getReference().child("TourEvents");

        mDatabaseUsers =  FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mProgress = new ProgressDialog(this);

        EDetails = (EditText) findViewById(R.id.update_EDetail);
        ECost = (EditText) findViewById(R.id.update_ECost);
        CDate = (EditText) findViewById(R.id.update_cDate);
        CTime = (EditText) findViewById(R.id.update_cTime);
        updateRecord = (Button) findViewById(R.id.updateRecord);

        CDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_current_Date();
            }
        });

        CTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_current_time();
            }
        });

        updateRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdatingRecord();
            }
        });

        mExpRecordDatabase.child(mUpdate_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String E_details = (String)dataSnapshot.child("Expense_detail").getValue();
                String cost = (String)dataSnapshot.child("cost").getValue();
                String crnt_date = (String)dataSnapshot.child("current_date").getValue();
                String crnt_time = (String)dataSnapshot.child("current_time").getValue();

                evnt_id = (String)dataSnapshot.child("travel_event_id").getValue();


                EDetails.setText(E_details);
                ECost.setText(cost);
                CDate.setText(crnt_date);
                CTime.setText(crnt_time);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void update_current_Date() {
        new DatePickerDialog(this, from, dateset.get(Calendar.YEAR), dateset.get(Calendar.MONTH), dateset.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener from = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            currentDate = dayOfMonth+"/"+(month+1)+"/"+year;
            dateset.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateset.set(Calendar.MONTH, month);
            dateset.set(Calendar.YEAR, year);

            CDate.setText(currentDate);
        }
    };

    public void update_current_time() {
        new TimePickerDialog(this, t, dateset.get(Calendar.HOUR_OF_DAY), dateset.get(Calendar.MINUTE), true).show();
        //ordertime = dateset.get(Calendar.HOUR_OF_DAY) + " : " + dateset.get(Calendar.MINUTE);
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            currentTime = hourOfDay+" : "+minute;
            dateset.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateset.set(Calendar.MINUTE, minute);

            CTime.setText(currentTime);
        }
    };



    private void startUpdatingRecord(){

        mProgress.setMessage("Updating record...");

        final String Edetails_val = EDetails.getText().toString().trim();
        final String ECost_val = ECost.getText().toString().trim();
        final String CDate_val = CDate.getText().toString().trim();
        final String CTime_val = CTime.getText().toString().trim();

        if(!TextUtils.isEmpty(Edetails_val) && !TextUtils.isEmpty(ECost_val) && !TextUtils.isEmpty(CDate_val) && !TextUtils.isEmpty(CTime_val)) {

            mProgress.show();

            final DatabaseReference newTEvent = mExpRecordDatabase.child(mUpdate_key);//every time create new post rather than overwrite previous


            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newTEvent.child("Expense_detail").setValue(Edetails_val);
                    newTEvent.child("cost").setValue(ECost_val);
                    newTEvent.child("current_date").setValue(CDate_val);
                    newTEvent.child("current_time").setValue(CTime_val);

                    //newTEvent.child("uid").setValue(mCurrentUser.getUid()); //user id who is actually posting this post

                    //event_key = getIntent().getExtras().getString("event_id");

                    //newTEvent.child("travel_event_id").setValue(event_key);

                    newTEvent.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                // startActivity(new Intent(CreateExpenseRecordActivity.this, ExpenseRecordActivity.class));
                                // event_key = getIntent().getExtras().getString("event_id");

                                Intent goto_record = new Intent(UpdateExpenseRecordActivity.this, ExpenseRecordActivity.class);
                                goto_record.putExtra("event_id", evnt_id);
                                startActivity(goto_record);

                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mProgress.dismiss();
            // startActivity(new Intent(CreateExpenseRecordActivity.this, ExpenseRecordActivity.class));

        }

    }
}
