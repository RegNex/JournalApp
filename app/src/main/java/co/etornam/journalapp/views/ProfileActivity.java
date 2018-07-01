package co.etornam.journalapp.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import co.etornam.journalapp.R;
import co.etornam.journalapp.model.Profile;
import de.hdodenhof.circleimageview.CircleImageView;

import static co.etornam.journalapp.common.Constants.PROFILE_IMAGES;
import static co.etornam.journalapp.common.Constants.USERS;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private CircleImageView mImage;
private EditText mUsername,mUserEmail;
private Uri resultUri = null;
private FirebaseAuth mAuth;
private DatabaseReference mDatabase, mProfile;
private String mUid;
private ProgressDialog mProgress;
private StorageReference mStorageReference,imagesRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Updating...");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProfile = mDatabase.child(USERS);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mImage = findViewById(R.id.profile_image);
        mUsername = findViewById(R.id.editText);
        mUserEmail = findViewById(R.id.editEmail);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                            .PERMISSION_GRANTED){
                        Toast.makeText(ProfileActivity.this, "Permission Denied!", Toast
                                .LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]
                                {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }else{
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(ProfileActivity.this);
                    }
                }else{
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(ProfileActivity.this);
                }
            }
        });

        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            mUserEmail.setText(mUser.getEmail());
            mProfile.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.getValue() != null){
                     Profile profile = dataSnapshot.getValue(Profile.class);
                     mUsername.setText(profile.getUsername());
                     try{
                         imagesRef = mStorageReference.child(profile.getPhotoUrl());
                         imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                             @Override
                             public void onSuccess(Uri uri) {
                                 Picasso.get()
                                         .load(uri)
                                         .placeholder(R.drawable.ic_account_circle_black_24dp)
                                         .error(R.drawable.ic_account_circle_black_24dp)
                                         .into(mImage);
                             }
                         })
                         .addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 mImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));
                             }
                         });

                     }catch (Exception e){
                         e.printStackTrace();
                     }
                 }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                mImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult: "+error);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void updateBtn(View view) {
        //update profile
        if (!TextUtils.isEmpty(mUsername.getText()) && resultUri != null){
            mProgress.show();
            StorageReference filepath = mStorageReference.child(PROFILE_IMAGES).child(mUid + ".jpg");
            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = taskSnapshot.getMetadata().getPath();
                    String username = mUsername.getText().toString();
                    Map<String,Object> mUserProfile = new HashMap<>();
                    mUserProfile.put("username",username);
                    mUserProfile.put("photoUrl",downloadUrl);
                    mUserProfile.put("timeStamp", ServerValue.TIMESTAMP);
                    mProfile.child(mUid).setValue(mUserProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Profile Updated!", Toast
                                        .LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }else{
                                Toast.makeText(ProfileActivity.this, "Something went wrong",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            }
                        }
                    });
                }
            });

        }else if (TextUtils.isEmpty(mUsername.getText())){
            mUsername.setError("This cannot be empty");
        }else if (resultUri == null){
            Toast.makeText(this, "Please a photo", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Something went wrong. Try again...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            mUid = user.getUid();
        }else{
            startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            finish();
        }
    }
}
