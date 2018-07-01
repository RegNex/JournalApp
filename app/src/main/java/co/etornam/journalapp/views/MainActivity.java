package co.etornam.journalapp.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.etornam.journalapp.R;
import co.etornam.journalapp.common.Constants;
import co.etornam.journalapp.controllers.JournalAdapter;
import co.etornam.journalapp.model.Post;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static co.etornam.journalapp.common.Constants.JOURNALS;

public class MainActivity extends AppCompatActivity {
private FirebaseAuth mAuth;
private  String mUid;
private ProgressDialog progressDialog;
private List<Post> post_list;
private List<String> keyList;
DatabaseReference mDatabase,mPostRef;
private JournalAdapter journalAdapter;
RecyclerView mRecyclerView;
private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("The Journal");
        setSupportActionBar(toolbar);
        post_list = new ArrayList<>();
        keyList = new ArrayList<>();
        Collections.reverse(post_list);
        journalAdapter = new JournalAdapter(post_list,getApplicationContext(),keyList);
        mRecyclerView = findViewById(R.id.recyclerView);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mPostRef = mDatabase.child(JOURNALS);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddNewActivity.class));
                finish();
            }
        });

       if (mUser != null){
           mPostRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if (dataSnapshot.getValue() != null){
                       keyList.clear();
                       post_list.clear();
                       for (DataSnapshot mSnapshot : dataSnapshot.getChildren()){
                           keyList.add(mSnapshot.getKey());
                           Post post = mSnapshot.getValue(Post.class);
                           post_list.add(post);
                           journalAdapter.notifyDataSetChanged();
                       }
                   }else {

                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }else{
           startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
           finish();
       }

        mRecyclerView.setAdapter(journalAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if(id == R.id.action_about){
            startActivity(new Intent(getApplicationContext(),AboutActivity.class));
            finish();
        }else if (id == R.id.action_logout){
            progressDialog.setMessage("Logging you out...");
            progressDialog.show();
            FirebaseAuth.getInstance().signOut();
           startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
           finish();

        }else if(id == R.id.action_profile){
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        if(!Constants.isNetworkAvailable(this)){
            Snackbar.make(findViewById(android.R.id.content), "No Internet Access. Offline mode", Snackbar.LENGTH_LONG)
                    .setAction("ok", null).show();
            return;
        }if (mUser == null){
            startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            finish();
        }else{
            mUid = mUser.getUid();
            journalAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }
}
