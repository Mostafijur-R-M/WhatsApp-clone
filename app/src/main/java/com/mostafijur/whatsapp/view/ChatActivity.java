package com.mostafijur.whatsapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mostafijur.whatsapp.R;
import com.mostafijur.whatsapp.adapter.TaskListAdapter;
import com.mostafijur.whatsapp.adapter.TasksAdapter;
import com.mostafijur.whatsapp.model.Tasks;
import com.mostafijur.whatsapp.viewholder.TaskViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID, timeStampID, key;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private androidx.appcompat.widget.Toolbar ChatToolBar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, TaskRef;
    private ImageButton newAddTaskButton;

    private LinearLayoutManager linearLayoutManager;
    private TaskListAdapter taskListAdapter;
    private RecyclerView parentRV;
    final List<Tasks> tasksList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    private String saveCurrentTime, saveCurrentDate;
    Dialog dialog;
    private TasksAdapter tasksAdapter;
    private EditText taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        IntializeControllers();

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        TaskRef = FirebaseDatabase.getInstance().getReference().child("Task");

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();
        key = getIntent().getExtras().get("conversations_id").toString();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);
        DisplayLastSeen();
        newAddTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogeBox();
            }
        });

        parentRV = findViewById(R.id.private_messages_list_of_users);
        //parentRV.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //parentRV.setLayoutManager(layoutManager);


        showConversations();

    }

    private void showConversations() {
        Log.e("123456", "task msg");
        TaskRef.orderByChild("senderID").equalTo(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()){
                    String taskName = datas.child("senderID").getValue().toString();
                    Log.e("123456", "task "+taskName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void openDialogeBox() {
        tasksList.clear();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.task_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final RecyclerView taskListRecyler = dialog.findViewById(R.id.task_recyler_id);

//setAdapter
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        tasksAdapter = new TasksAdapter(ChatActivity.this, tasksList);
        taskListRecyler.setLayoutManager(layoutManager);
        taskListRecyler.setAdapter(tasksAdapter);
        tasksAdapter.notifyDataSetChanged();

        taskName = dialog.findViewById(R.id.task_name_et_id);
        Button addNewTask = dialog.findViewById(R.id.add_new_task_btn_id);
        Button sendTask = dialog.findViewById(R.id.dialog_send_task_btn_id);
        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskList();
            }
        });
        taskName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            addTaskList();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        sendTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendTaskToReceiver();

            }
        });
        dialog.show();
    }

    private void addTaskList() {
        String task = taskName.getText().toString();
        if (TextUtils.isEmpty(task)){
            Toast.makeText(this, "type your task...", Toast.LENGTH_SHORT).show();
        }else {
            tasksList.add(new Tasks(saveCurrentDate, timeStampID, task, "pending", "text", saveCurrentTime));
            tasksAdapter.notifyDataSetChanged();
            taskName.getText().clear();
        }
    }

    private void sendTaskToReceiver() {
        int n = tasksList.size();
        for (int i = 0; i < n; i++){
            String taskId = tasksList.get(i).getTaskId();
            String taskName = tasksList.get(i).getTaskName();
            String taskStatus = tasksList.get(i).getTaskStatus();

            long timeStamp = System.currentTimeMillis()/1000;
            timeStampID = String.valueOf(timeStamp);
            String taskSenderRef = "Task/" + key;
            DatabaseReference userMessageKeyRef = RootRef.child("Task").push();
            String taskPushID = userMessageKeyRef.getKey();

            Map taskTextBody = new HashMap();
            taskTextBody.put("senderID", messageSenderID );
            taskTextBody.put("taskName", taskName);
            taskTextBody.put("text", "text");
            taskTextBody.put("taskStatus", taskStatus);
            taskTextBody.put("time", saveCurrentTime);
            taskTextBody.put("date", saveCurrentDate);

            Map taskTextDetails = new HashMap();
            taskTextDetails.put(taskSenderRef + "/" + timeStampID + "/" + taskPushID, taskTextBody);

            RootRef.updateChildren(taskTextDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Task sent success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else {

                    }
                }
            });
        }

    }

    private void IntializeControllers()
    {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);

        //SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        //SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        //MessageInputText = (EditText) findViewById(R.id.input_message);
        newAddTaskButton = findViewById(R.id.send_task_btn);

        //messageAdapter = new MessageAdapter(messagesList);
        taskListAdapter = new TaskListAdapter(this, tasksList);
       // parentRV = findViewById(R.id.private_messages_list_of_users);


        /*linearLayoutManager = new LinearLayoutManager(this);
        parentRV.setLayoutManager(linearLayoutManager);
        parentRV.setAdapter(taskListAdapter);*/

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }

    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Last Seen: " + date + " " + time);
                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


}