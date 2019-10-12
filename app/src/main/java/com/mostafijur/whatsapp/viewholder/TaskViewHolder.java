package com.mostafijur.whatsapp.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mostafijur.whatsapp.R;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    public TextView senderTaskTV;
    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        senderTaskTV = itemView.findViewById(R.id.sender_task_tv_id);
    }
}
