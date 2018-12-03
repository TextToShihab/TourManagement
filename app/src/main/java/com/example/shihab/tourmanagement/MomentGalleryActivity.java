package com.example.shihab.tourmanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class MomentGalleryActivity extends AppCompatActivity {

    private RecyclerView mMoments_list;

    private DatabaseReference mDatabase;
    //-----------------------------------------------
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseEvents;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String mEvent_key = null;
    private Query mQueryCurrentEvent;

     String moment_record_key = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment_gallery);

        try{
            mEvent_key = getIntent().getExtras().getString("event_id");
            //Toast.makeText(this, mEvent_key, Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        //mEvent_key = getIntent().getExtras().getString("event_id");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener  = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(MomentGalleryActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Moments");
        mDatabaseEvents = FirebaseDatabase.getInstance().getReference().child("TourEvents");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        //----------------------------------------------------------------------------
        //single user---------------------------------------
                mQueryCurrentEvent = mDatabase.orderByChild("post_travel_event_id").equalTo(mEvent_key);

        //----------------------------------------------

        mDatabaseUsers.keepSynced(true); //this will make sure it stores the data offline
        mDatabase.keepSynced(true);
        mDatabaseEvents.keepSynced(true);

        mMoments_list = (RecyclerView) findViewById(R.id.moment_galary);
        mMoments_list.setHasFixedSize(true);
        mMoments_list.setLayoutManager(new LinearLayoutManager(this));


       // chekUserExists();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Moments, MomentViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Moments, MomentViewHolder>(
                Moments.class, R.layout.moment_galary_row, MomentViewHolder.class, mQueryCurrentEvent
        ) {
            @Override
            protected void populateViewHolder(MomentViewHolder viewHolder, Moments model, int position) {

                moment_record_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.fab_delt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mEvent_key = getIntent().getExtras().getString("event_id");

                        AlertDialog diaBox = AskOption();
                        diaBox.show();


                    }
                });
            }

        };
        mMoments_list.setAdapter(firebaseRecyclerAdapter);

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
                        mDatabase.child(moment_record_key).removeValue();
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


//-------------------------------------------------------------------------------------
    /*private void chekUserExists() {

        if(mAuth.getCurrentUser() != null){
            final String user_id = mAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(user_id)){

                    Intent setupIntent = new Intent(MomentGalleryActivity.this, SetupActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }

    }*/

    public static class MomentViewHolder extends RecyclerView.ViewHolder{

        View mView;

        FloatingActionButton fab_delt;

        public MomentViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            fab_delt = (FloatingActionButton) mView.findViewById(R.id.dlt_floating_btn);
        }

        public void setTitle(String title){
            TextView post_title = (TextView) mView.findViewById(R.id.moment_title);
            post_title.setText(title);
        }

        public void setDescription(String description){
            TextView post_description = (TextView) mView.findViewById(R.id.moment_description);
            post_description.setText(description);
        }

        public void setImage(Context context, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.moment_image);
            Picasso.with(context).load(image).into(post_image);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            //startActivity(new Intent(MomentGalleryActivity.this, PostActivity.class));
            String mEvent_key = getIntent().getExtras().getString("event_id");
            //String key = mEvent_key;

            Intent i = new Intent(MomentGalleryActivity.this, PostActivity.class);
            i.putExtra("event_id", mEvent_key);
            startActivity(i);
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
