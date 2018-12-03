package com.example.shihab.tourmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class PostActivity extends AppCompatActivity {

    private ImageButton moment_image;
    private EditText title, description;
    private Button btnSave;

    private String random;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;

    private static final int GALLERT_REQUEST = 1;
    private static final int MAX_LENGTH = 50;

    String event_key = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth= FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Moments");
        mDatabaseUsers =  FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        moment_image = (ImageButton) findViewById(R.id.moments_image);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        btnSave = (Button) findViewById(R.id.btnSave);

        mProgress = new ProgressDialog(this);

        moment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryImage = new Intent(Intent.ACTION_GET_CONTENT);
                galleryImage.setType("image/*");
                startActivityForResult(galleryImage, GALLERT_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostingintodb();
            }
        });

        event_key = getIntent().getExtras().getString("event_id");
    }

    private void startPostingintodb() {

        mProgress.setMessage("Saving image...");

        final String mTitle = title.getText().toString().trim();
        final String mDescription = description.getText().toString().trim();

        if(!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mDescription) && mImageUri != null){

            mProgress.show();

            // Create a child reference
            // imagesRef now points to "images"
            random = random();
            StorageReference filepath = storageRef.child("moment_images").child(random);

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabase.push();//every time create new post rather than overwrite previous

                    /*mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("title").setValue(mTitle);
                            newPost.child("description").setValue(mDescription);
                            newPost.child("image").setValue(downloadUrl.toString());

                            newPost.child("post_travel_event_id").setValue(event_key); //user id who is actually posting this post

                            newPost.child("uid").setValue(mCurrentUser.getUid()); //user id who is actually posting this post

                            //event_key = getIntent().getExtras().getString("event_id");

                            newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        // startActivity(new Intent(CreateExpenseRecordActivity.this, ExpenseRecordActivity.class));
                                        // event_key = getIntent().getExtras().getString("event_id");

                                        Intent goto_glry = new Intent(PostActivity.this, MomentGalleryActivity.class);
                                        goto_glry.putExtra("event_id", event_key);
                                        startActivity(goto_glry);

                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/

                    //-------------------------------------------------------
                    newPost.child("title").setValue(mTitle);
                    newPost.child("description").setValue(mDescription);
                    newPost.child("image").setValue(downloadUrl.toString());

                    newPost.child("post_travel_event_id").setValue(event_key); //user id who is actually posting this post

                    //after saving image
                    mProgress.dismiss();

                    //startActivity(new Intent(PostActivity.this, MomentGalleryActivity.class));
                    Intent goto_glry = new Intent(PostActivity.this, MomentGalleryActivity.class);
                    goto_glry.putExtra("event_id", event_key);
                    startActivity(goto_glry);
                }
            });



        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERT_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();
            moment_image.setImageURI(mImageUri);
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
