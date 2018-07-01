package co.etornam.journalapp.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import co.etornam.journalapp.R;
import co.etornam.journalapp.model.Post;
import co.etornam.journalapp.views.EditJournalActivity;
import co.etornam.journalapp.views.ReadJournalActivity;

import static co.etornam.journalapp.common.Constants.JOURNALS;
import static co.etornam.journalapp.utils.MyUtil.convertTime;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder>{
public List<Post> postList;
public List<String> keyList ;

   private DatabaseReference ref,mJournal;
   private FirebaseAuth mAuth;
   private FirebaseUser user;
   private Context context;
   private String TAG = "JournalAdapter";

    public JournalAdapter(List<Post> postList, Context context,List<String> keyList) {
        this.postList = postList;
        this.context = context;
        this.keyList = keyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_content,
               parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final int itemPos = position;
        String journal_title = postList.get(position).getTitle();
        String journal_category = postList.get(position).getCategory();
        long journal_date = postList.get(position).getTimeStamp();
        holder.setTxtTitle(journal_title);
        holder.setTxtDate(journal_date);
        holder.setTxtCategory(journal_category);
        holder.setImgJournal(journal_category);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference();
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                mJournal = ref.child(JOURNALS).child(user.getUid());
                mJournal.child(keyList.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT)
                                    .show();

                        }else {
                            Toast.makeText(context, "Could not delete", Toast.LENGTH_SHORT).show();
                        }
                        notifyItemRemoved(position);
                        // notifyItemRangeChanged(position, postList.size());
                        notifyDataSetChanged();
                    }
                });


            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, EditJournalActivity.class);
                i.putExtra("key",keyList.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ReadJournalActivity.class);
                i.putExtra("mkey",keyList.get(position));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle;
        private  TextView txtDate;
        private  TextView txtCategory;
        private ImageView imgJournal;
        private ImageButton btnDelete;
        private ImageButton btnEdit;
        private View view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            btnDelete = view.findViewById(R.id.btnDelete);
            btnEdit = view.findViewById(R.id.btnEdit);
        }
        public void setTxtTitle(String title){
            txtTitle = view.findViewById(R.id.txtTitle);
            txtTitle.setText(title);
        }

       public void setTxtDate(long date){
            txtDate = view.findViewById(R.id.txtDate);
            txtDate.setText(convertTime(date));
       }

       public void setTxtCategory(String category){
            txtCategory = view.findViewById(R.id.txtCategory);
            txtCategory.setText(category);
       }

    public void setImgJournal(String imgcategory){
            imgJournal = view.findViewById(R.id.category_image);
            if (imgcategory.equals("Life")){
                imgJournal.setImageResource(R.drawable.ic_accessibility_black_24dp);
            }else if (imgcategory.equals("Social")){
                imgJournal.setImageResource(R.drawable.ic_favorite_black_24dp);
            }else if (imgcategory.equals("Business")){
                imgJournal.setImageResource(R.drawable.ic_business_center_black_24dp);
            }else if (imgcategory.equals("General")){
                imgJournal.setImageResource(R.drawable.ic_public_black_24dp);
            }else if (imgcategory.equals("Education")){
                imgJournal.setImageResource(R.drawable.ic_school_black_24dp);
            }else if (imgcategory.equals("Funny")){
                imgJournal.setImageResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
            }else if (imgcategory.equals("Food")){
                imgJournal.setImageResource(R.drawable.ic_free_breakfast_black_24dp);
            }else if (imgcategory.equals("Inspiration")){
                imgJournal.setImageResource(R.drawable.ic_wb_incandescent_black_24dp);
            }else if (imgcategory.equals("Personal")){
                imgJournal.setImageResource(R.drawable.ic_security_black_24dp);
            }else if (imgcategory.equals("Health")){
            imgJournal.setImageResource(R.drawable.ic_local_hospital_black_24dp);
        }else {
                imgJournal.setImageResource(R.drawable.ic_grain_black_24dp);
            }
    }


    }

}
