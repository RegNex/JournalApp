package co.etornam.journalapp.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.etornam.journalapp.R;
import co.etornam.journalapp.model.Post;

import static co.etornam.journalapp.common.Constants.JOURNALS;
import static co.etornam.journalapp.utils.MyUtil.convertTime;

public class ReadJournalActivity extends AppCompatActivity {
    private static final String TAG = "ReadJournalActivity";
    public TextView txtTitle,txtBody,txtDate,txtCategory;
private String mKey;
    private DatabaseReference mDatabase,mJournal;
    private FirebaseAuth mAuth;
    private String mUid;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_journal);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null){
            mUid = firebaseUser.getUid();

            mJournal = mDatabase.child(JOURNALS).child(mUid);
        }

        txtBody = findViewById(R.id.txtBody);
        txtTitle = findViewById(R.id.txtTitle);
        txtCategory = findViewById(R.id.txtCategory);
        txtDate = findViewById(R.id.txtDate);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mKey = extras.getString("mkey");
            mJournal.child(mKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    txtTitle.setText(post.getTitle());
                    txtBody.setText(post.getBody());
                    txtCategory.setText(post.getCategory());
                    txtDate.setText(convertTime(post.getTimeStamp()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.edit_menu){
            editJournal();
        }
        if (item.getItemId()==android.R.id.home)
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

    //edit Journal
    private void editJournal() {
        Intent i = new Intent(getApplicationContext(), EditJournalActivity.class);
        i.putExtra("key",mKey);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}
