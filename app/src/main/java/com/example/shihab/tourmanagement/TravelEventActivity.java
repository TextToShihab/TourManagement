package com.example.shihab.tourmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TravelEventActivity extends AppCompatActivity {

    private RecyclerView mTravel_event_list;

    private DatabaseReference mDatabase;
    //-----------------------------------------------
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseCurrentUsers;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Query  mQueryCurrentUser;

    String event_key;
    private String currentUserID;
    // private static String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_event);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener  = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(TravelEventActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("TourEvents");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        if(mAuth.getCurrentUser() != null){

            //single user---------------------------------------
            currentUserID = mAuth.getCurrentUser().getUid();
            //----------------------------------------------

        }

        //single user---------------------------------------
        //currentUserID = mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUsers = FirebaseDatabase.getInstance().getReference().child("TourEvents");
        mQueryCurrentUser = mDatabaseCurrentUsers.orderByChild("uid").equalTo(currentUserID);
        //----------------------------------------------

        mDatabaseUsers.keepSynced(true); //this will make sure it stores the data offline
        mDatabase.keepSynced(true);

        mTravel_event_list = (RecyclerView) findViewById(R.id.travelEvent);
        mTravel_event_list.setHasFixedSize(true);
        mTravel_event_list.setLayoutManager(new LinearLayoutManager(this));

        chekUserExists();
    }



    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<TravelEvents, EventViewHolder> firebaseEventRecyclerAdapter = new FirebaseRecyclerAdapter<TravelEvents, EventViewHolder>(
                TravelEvents.class, R.layout.travel_event_row, EventViewHolder.class, mQueryCurrentUser
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, TravelEvents model, int position) {

                final String event_key = getRef(position).getKey();

                viewHolder.setDestination(model.getDestination());
                viewHolder.setBudget(model.getBudget());
                viewHolder.setFrom(model.getFrom());
                viewHolder.setTo(model.getTo());

                viewHolder.mEventView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(TravelEventActivity.this, ExpenseRecordActivity.class);
                        i.putExtra("event_id", event_key);
                        startActivity(i);
                    }
                });

                viewHolder.update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(TravelEventActivity.this, UpdateTravelEventActivity.class);
                        i.putExtra("event_id", event_key);
                        startActivity(i);


                    }
                });

                viewHolder.delete_event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog diaBox = AskOption();
                        diaBox.show();

                    }
                });



            }

        };
        mTravel_event_list.setAdapter(firebaseEventRecyclerAdapter);

    }


    private void chekUserExists() {

        if(mAuth.getCurrentUser() != null){
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.hasChild(user_id)){

                        Intent setupIntent = new Intent(TravelEventActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private AlertDialog AskOption() {

        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete?")
                .setIcon(R.drawable.ic_action_delete_forever)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        mDatabase.child(event_key).removeValue();
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }


    public static class EventViewHolder extends RecyclerView.ViewHolder{

        View mEventView;
        Button delete_event, update;
        public EventViewHolder(View eventItemView) {
            super(eventItemView);
            mEventView = eventItemView;
            delete_event = (Button) mEventView.findViewById(R.id.btn_delt);
            update = (Button) mEventView.findViewById(R.id.btn_update);
        }


        public void setDestination(String destination){
            TextView post_destination = (TextView) mEventView.findViewById(R.id.travel_destination);
            post_destination.setText(destination);
        }

        public void setBudget(String budget){
            TextView post_budjet = (TextView) mEventView.findViewById(R.id.est_budjt);
            post_budjet.setText(budget);
        }

        public void setFrom(String fromDate){
            TextView post_frmDate = (TextView) mEventView.findViewById(R.id.frm_date);
            post_frmDate.setText("From :\n"+fromDate);
        }

        public void setTo(String toDate){
            TextView post_toDate = (TextView) mEventView.findViewById(R.id.to_date);
            post_toDate.setText("To :\n"+toDate);
        }

        //Button previous = (Button) mEventView.findViewById(R.id.button_moment_gallery);
        //Button next = (Button) mEventView.findViewById(R.id.button_expense);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(TravelEventActivity.this, CreateTravelEventActivity.class));
        }

        if(item.getItemId() == R.id.action_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}


