package com.example.shihab.tourmanagement;

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

public class ExpenseRecordActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private RecyclerView mExpenseRecordt;

    private DatabaseReference mDatabase;
    //-----------------------------------------------
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseCurrentUsers;
    private DatabaseReference mDatabaseEvents;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //private Query mQueryCurrentUser;
    private Query mQueryCurrentEvent;

    private String mEvent_key = "EValue";
    private static String budget;
    private static String cost_str;

    private static int budget_int;
    private static int cost_int;
    private static int calculation;

    private static int add = 0;

    String record_key = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_record);
        /*try{
            mEvent_key = getIntent().getExtras().getString("event_id");
            //Toast.makeText(this, mEvent_key, Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }*/
        mEvent_key = getIntent().getExtras().getString("event_id");


        mAuth = FirebaseAuth.getInstance();
        mAuthListener  = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(ExpenseRecordActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Exp_record");
        mDatabaseEvents = FirebaseDatabase.getInstance().getReference().child("TourEvents");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        //single user---------------------------------------

        //String currentUserID = mAuth.getCurrentUser().getUid();
        //mDatabaseCurrentUsers = FirebaseDatabase.getInstance().getReference().child("Exp_record");
        mQueryCurrentEvent = mDatabase.orderByChild("travel_event_id").equalTo(mEvent_key);


        //----------------------------------------------

        mDatabaseUsers.keepSynced(true); //this will make sure it stores the data offline
        mDatabase.keepSynced(true);
        mDatabaseEvents.keepSynced(true);

        mExpenseRecordt = (RecyclerView) findViewById(R.id.expenseRecord);
        mExpenseRecordt.setHasFixedSize(true);
        mExpenseRecordt.setLayoutManager(new LinearLayoutManager(this));


        mDatabaseEvents.child(mEvent_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                budget = (String)dataSnapshot.child("Budget").getValue();
                /*try{
                    //budget_result = Integer.parseInt(totalBudget);

                    int result = Integer.parseInt(budget);
                    Toast.makeText(ExpenseRecordActivity.this, result, Toast.LENGTH_SHORT).show();

                }catch (NumberFormatException e){
                    Toast.makeText(ExpenseRecordActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ExpenseRecordActivity.this, ""+budget_result, Toast.LENGTH_SHORT).show();
                }*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*String record_key = mDatabase.getRef().getKey();

        mDatabase.child(record_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cost = (String) dataSnapshot.child("cost").getValue();
                cost_result = Integer.parseInt(cost);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/

        fab = (FloatingActionButton) findViewById(R.id.floating_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEvent_key = getIntent().getExtras().getString("event_id");

                Intent glryIntent = new Intent(ExpenseRecordActivity.this, MomentGalleryActivity.class);
                glryIntent.putExtra("event_id", mEvent_key);
                startActivity(glryIntent);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        //mEvent_key = getIntent().getExtras().getString("event_id");

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<ExpenseRecord, RecordViewHolder> firebaseRecordRecyclerAdapter = new FirebaseRecyclerAdapter<ExpenseRecord, RecordViewHolder>(
                ExpenseRecord.class, R.layout.expense_record_row, RecordViewHolder.class, mQueryCurrentEvent
        ) {
            @Override
            protected void populateViewHolder(RecordViewHolder viewHolder, ExpenseRecord model, int position) {
                int add = 0;

                record_key = getRef(position).getKey();

                    viewHolder.setExpense_detail(model.getExpense_detail());
                    viewHolder.setCost(model.getCost());
                    viewHolder.setCurrent_date(model.getCurrent_date());
                    viewHolder.setCurrent_time(model.getCurrent_time());

                    viewHolder.setTotal_bdjt();

                    viewHolder.setCost_calculation();


                viewHolder.delete_event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog diaBox = AskOption();
                        diaBox.show();

                    }
                });

                viewHolder.update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(ExpenseRecordActivity.this, UpdateExpenseRecordActivity.class);
                        i.putExtra("record_id", record_key);
                        startActivity(i);


                    }
                });

            }

        };

        mExpenseRecordt.setAdapter(firebaseRecordRecyclerAdapter);

    }

    private AlertDialog AskOption() {

        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete this particular record?")
                .setIcon(R.drawable.ic_action_delete_forever)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        mDatabase.child(record_key).removeValue();
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

    public static class RecordViewHolder extends RecyclerView.ViewHolder{

        View mRecordView;
        Button delete_event, update;

        public RecordViewHolder(View eventItemView) {
            super(eventItemView);

            mRecordView = eventItemView;
            delete_event = (Button) mRecordView.findViewById(R.id.btn_delt_record);
            update = (Button) mRecordView.findViewById(R.id.btn_update_record);
        }


        public void setExpense_detail(String exp_detail){
            TextView post_exp_details = (TextView) mRecordView.findViewById(R.id.exp_details);
            post_exp_details.setText(exp_detail);
        }

        public void setCost(String cost){
            TextView post_cost = (TextView) mRecordView.findViewById(R.id.exp_amount);
            post_cost.setText(cost);

            cost_str = post_cost.getText().toString();
        }

        public void setCurrent_date(String cDate){
            TextView post_cDate = (TextView) mRecordView.findViewById(R.id.crnt_date);
            post_cDate.setText(cDate);
        }

        public void setCurrent_time(String cTime){
            TextView post_cTime = (TextView) mRecordView.findViewById(R.id.crnt_time);
            post_cTime.setText(cTime);
        }

        public void setTotal_bdjt(){
            TextView total_budget = (TextView) mRecordView.findViewById(R.id.total_amount);
            total_budget.setText(budget);
        }

        public void setCost_calculation(){
            TextView total_budget = (TextView) mRecordView.findViewById(R.id.remaining_amount);

            int extra_cost = 0;

            budget_int = Integer.parseInt(budget);
            cost_int = Integer.parseInt(cost_str);

            calculation = budget_int - getTemp();

            if(calculation < 0 ){
                String set_val_extra = Integer.toString(extra_cost);
                total_budget.setText(set_val_extra);
            }

            String set_val = Integer.toString(calculation);
            total_budget.setText(set_val);
        }

        public int getTemp(){
            add += cost_int;
            return add;
        }

        /*public void setExtraCost(){
            TextView extra_cost = (TextView) mRecordView.findViewById(R.id.extra_amount);

            int Absolute_calculation = 0;

             if(calculation < 0 ){
                 Absolute_calculation = calculation * (-1);
                 extra_cost.setText(Absolute_calculation);
             }else {
                 extra_cost.setText(Absolute_calculation);
             }

        }*/


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            mEvent_key = getIntent().getExtras().getString("event_id");
            //String key = mEvent_key;

            Intent i = new Intent(ExpenseRecordActivity.this, CreateExpenseRecordActivity.class);
            i.putExtra("event_id", mEvent_key);
            startActivity(i);
           // startActivity(new Intent(ExpenseRecordActivity.this, CreateExpenseRecordActivity.class));
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
