package co.etornam.journalapp.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.etornam.journalapp.R;
import co.etornam.journalapp.common.Constants;

import static co.etornam.journalapp.common.Constants.JOURNALS;

public class AddNewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "AddNewActivity";
    private EditText edtTitle,edtBody;
private Spinner mSpinner;
private Button mButton;
private DatabaseReference mDatabase,newJournal;
private FirebaseAuth mAuth;
private String mUid;
private String item;
private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        newJournal = mDatabase.child(JOURNALS);
        mAuth = FirebaseAuth.getInstance();
        edtTitle = findViewById(R.id.edtTitle);
        edtBody = findViewById(R.id.edtText);
        mButton = findViewById(R.id.btnAdd);
        mProgress = new ProgressDialog(this);
        mProgress.setCancelable(false);
        mSpinner = findViewById(R.id.spinnerCategory);
        mSpinner.setOnItemSelectedListener(this);

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
        mSpinner.setAdapter(dataAdapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtBody.getText().toString()) && !TextUtils.isEmpty
                        (edtTitle.getText().toString()) && mSpinner != null){
                    mProgress.setMessage("Adding New Journal...");
                    mProgress.show();
                    String title = edtTitle.getText().toString();
                    String body = edtBody.getText().toString();
                    Map<String,Object> mPost = new HashMap<>();
                    mPost.put("title",title);
                    mPost.put("body",body);
                    mPost.put("category",item);
                    mPost.put("timeStamp", ServerValue.TIMESTAMP);
                    newJournal.child(mUid).push().setValue(mPost)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()){
                                      startActivity(new Intent(getApplicationContext(),
                                              MainActivity.class));
                                      finish();
                                      Toast.makeText(AddNewActivity.this, "Journal Added!", Toast
                                              .LENGTH_SHORT).show();
                                  }else if (task.isCanceled()){
                                      Toast.makeText(AddNewActivity.this, "Cancelled", Toast
                                              .LENGTH_SHORT).show();
                                  }else{
                                      Log.d(TAG, "onComplete: "+task.getException().getMessage());
                                      Toast.makeText(AddNewActivity.this, "Something went wrong",
                                              Toast.LENGTH_SHORT).show();
                                  }
                                  mProgress.dismiss();
                                }
                            });
                }else if (TextUtils.isEmpty(edtTitle.getText().toString())){
                    edtTitle.setError("Field cannot be Empty!");
                }else if (TextUtils.isEmpty(edtBody.getText().toString())){
                    edtBody.setError("Field cannot be Empty!");
                }else if (mSpinner == null){
                    Toast.makeText(AddNewActivity.this, "Select a Category", Toast.LENGTH_SHORT)
                            .show();
                }else{
                    Toast.makeText(AddNewActivity.this, "Something went wrong", Toast
                            .LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: Error");
                }
            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null){
            mUid = mAuth.getCurrentUser().getUid();
        }else {
            //return back to login activity
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
