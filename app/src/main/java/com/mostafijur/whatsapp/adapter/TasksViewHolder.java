package com.mostafijur.whatsapp.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mostafijur.whatsapp.R;

public class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView senderTaskName, receiverTaskName;
    public TasksViewHolder(@NonNull View itemView) {
        super(itemView);

        senderTaskName = itemView.findViewById(R.id.sender_task_tv_id);
        receiverTaskName = itemView.findViewById(R.id.receiver_task_tv_id);
    }

    @Override
    public void onClick(View v) {

    }
}
