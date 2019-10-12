package com.mostafijur.whatsapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mostafijur.whatsapp.R;
import com.mostafijur.whatsapp.model.Tasks;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.MyViewHolder> {
    private Context context;
    List<Tasks> tasksList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    public TaskListAdapter(Context context, List<Tasks> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
    }

    @NonNull
    @Override
    public TaskListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_task_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListAdapter.MyViewHolder holder, int position) {

        String taskSenderId = mAuth.getCurrentUser().getUid();
        Tasks tasks = tasksList.get(position);
        
        holder.senderTaskText.setText(tasksList.get(position).getTaskName());
        Log.e("taskName: ", tasksList.get(position).getTaskName());

        /*String fromTaskId = tasks.get();
        String fromTaskType = tasks.getType();*/

        //userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromTaskId);

        /*userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

       /* holder.receiverTasktext.setVisibility(View.GONE);
        holder.senderTaskText.setVisibility(View.GONE);
        holder.receiverCB.setVisibility(View.GONE);

        if (fromTaskType.equals("text")){
            if (fromTaskId.equals(taskSenderId)){
                holder.senderTaskText.setVisibility(View.VISIBLE);
                holder.senderTaskText.setTextColor(Color.BLACK);
                holder.senderTaskText.setText(tasks.getTaskName());

            }else {
                holder.receiverTasktext.setVisibility(View.VISIBLE);
                holder.receiverCB.setVisibility(View.VISIBLE);
                holder.receiverTasktext.setTextColor(Color.BLACK);
                holder.receiverTasktext.setText(tasks.getTaskName());
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView senderTaskText, receiverTasktext;
        CheckBox receiverCB;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            senderTaskText = itemView.findViewById(R.id.sender_task_tv_id);
            //receiverTasktext = itemView.findViewById(R.id.receiver_task_tv_id);
            //receiverCB = itemView.findViewById(R.id.receiver_cb_id);
        }
    }
}
