package co.etornam.journalapp.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.etornam.journalapp.R;
import co.etornam.journalapp.model.Post;

import static co.etornam.journalapp.common.Constants.JOURNALS;

public class EditJournalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "EditJournalActivity";
    private String mKey;
private DatabaseReference mDatabase,mJournal;
private FirebaseAuth mAuth;
private String mUid;
private FirebaseUser firebaseUser;
private EditText edtUpdateTitle;
private EditText edtUpdateBody;
private Spinner spinnerUpdateCate;
private Button btnUpdate;
private String item;
private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        mUid = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mJournal = mDatabase.child(JOURNALS).child(mUid);
        edtUpdateTitle = findViewById(R.id.edtUpdateTitle);
        edtUpdateBody = findViewById(R.id.edtUpdateText);
        spinnerUpdateCate = findViewById(R.id.spinnerUpdateCategory);
        btnUpdate = findViewById(R.id.btnUpdate);

        spinnerUpdateCate.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("Random");
        categories.add("Social");
        categories.add("Business");
        categories.add("Funny");
        categories.add("Inspiration");
        categories.add("Life");
        categories.add("Personal");
        categories.add("Food");
        categories.add("Health");
        categories.add("Education");
        categories.add("General");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUpdateCate.setAdapter(dataAdapter);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mKey = extras.getString("key");
            mJournal.child(mKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    edtUpdateBody.setText(post.getBody());
                    edtUpdateTitle.setText(post.getTitle());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setMessage("Working...");
                    progressDialog.show();
                    String title = edtUpdateTitle.getText().toString();
                    String body = edtUpdateBody.getText().toString();
                    Map<String,Object> updateJournal = new HashMap<>();
                    updateJournal.put("title",title);
                    updateJournal.put("body",body);
                    updateJournal.put("category",item);
                    updateJournal.put("timeStamp", ServerValue.TIMESTAMP);
                    mJournal.child(mKey).setValue(updateJournal)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(EditJournalActivity.this, "Update Done",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),
                                                MainActivity.class));
                                        finish();
                                    }else{
                                        Toast.makeText(EditJournalActivity.this, "Could not " +
                                                "update", Toast
                                                .LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });

                }
            });
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
