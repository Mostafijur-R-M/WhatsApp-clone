package com.mostafijur.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mostafijur.whatsapp.model.Conversations;
import com.mostafijur.whatsapp.view.ChatActivity;
import com.mostafijur.whatsapp.model.Contacts;
import com.mostafijur.whatsapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View PrivateChatsView;
    private RecyclerView chatsList;

    private DatabaseReference ChatsRef, UsersRef, RootRef, ConversationsRef;
    private FirebaseAuth mAuth;
    private String currentUserID="", messageSenderID, messageReceiverID, usersIDs, conversationsID;
    private String key, retName;
    private String[] retImage;
    private Intent chatIntent;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        messageSenderID = mAuth.getCurrentUser().getUid();

        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ConversationsRef = FirebaseDatabase.getInstance().getReference().child("Conversations");
        RootRef = FirebaseDatabase.getInstance().getReference();

        chatsList = PrivateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return PrivateChatsView;
    }


    @Override
    public void onStart()
    {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatsRef, Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model)
                    {
                        usersIDs = getRef(position).getKey();
                        retImage = new String[]{"default_image"};

                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    if (dataSnapshot.hasChild("image"))
                                    {
                                        retImage[0] = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                    }

                                    retName = dataSnapshot.child("name").getValue().toString();
                                    messageReceiverID = dataSnapshot.child("uid").getValue().toString();
                                    final String retStatus = dataSnapshot.child("status").getValue().toString();

                                    holder.userName.setText(retName);


                                    if (dataSnapshot.child("userState").hasChild("state"))
                                    {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if (state.equals("online"))
                                        {
                                            holder.userStatus.setText("online");
                                        }
                                        else if (state.equals("offline"))
                                        {
                                            holder.userStatus.setText("Last Seen: " + date + " " + time);
                                        }
                                    }
                                    else
                                    {
                                        holder.userStatus.setText("offline");
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            createConversations();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

        chatsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void createConversations() {

        ConversationsRef.orderByChild("SenderID").equalTo(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()){
                    String senderID = datas.child("SenderID").getValue().toString();
                    String receiverID = datas.child("ReceiverID").getValue().toString();

                    if (senderID.equals(messageSenderID) && receiverID.equals(messageReceiverID)){
                        key = datas.getKey();
                        break;
                    }
                }
                if (key != null){

                    chatIntent = new Intent(getContext(), ChatActivity.class);
                    chatIntent.putExtra("visit_user_id", messageReceiverID);
                    chatIntent.putExtra("visit_user_name", retName);
                    chatIntent.putExtra("conversations_id", key);
                    chatIntent.putExtra("visit_image", retImage[0]);
                    startActivity(chatIntent);
                    Log.e("123456", ""+key);
                }else {
                    Log.e("123456", "No Data Here");
                    DatabaseReference userTaskKeyRef = RootRef.child("Conversations").push();

                    //String taskPushKey = userTaskKeyRef.getKey();
                    conversationsID  = userTaskKeyRef.push().getKey();
                    //conversationsID  = messageSenderID+messageReceiverID;

                    String senderRef = "Conversations/" +conversationsID + "/SenderID";
                    String receiverRef = "Conversations/" +conversationsID + "/ReceiverID";

                    Map map = new HashMap();
                    map.put(senderRef, messageSenderID);
                    map.put(receiverRef, messageReceiverID);

                    RootRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*RootRef.child("Conversations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                String senderID = dataSnapshot.child("SenderID").getValue(String.class);
                String receiverID = dataSnapshot.child("ReceiverID").getValue(String.class);

                if (senderID.equals(messageSenderID) && receiverID.equals(messageReceiverID)){
                    Log.e("123456", key);


                }else {
                    DatabaseReference userTaskKeyRef = RootRef.child("Conversations").push();

                    //String taskPushKey = userTaskKeyRef.getKey();
                    conversationsID  = userTaskKeyRef.push().getKey();
                    //conversationsID  = messageSenderID+messageReceiverID;

                    String senderRef = "Conversations/" +conversationsID + "/SenderID";
                    String receiverRef = "Conversations/" +conversationsID + "/ReceiverID";

                    Map map = new HashMap();
                    map.put(senderRef, messageSenderID);
                    map.put(receiverRef, messageReceiverID);

                    RootRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                        }
                    });
                    Log.e("123456", "no data here");
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        /**/

        /*ConversationsRef.child(conversationsID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //Conversations conversations = dataSnapshot.getValue(Conversations.class);
                    String senderID = dataSnapshot.child("SenderID").getValue().toString();
                    String receiverID = dataSnapshot.child("ReceiverID").getValue().toString();
                    //String receiverID = conversations.getReceiverID();

                    Log.e("123456: ", "sender: "+ senderID);
                    Log.e("123456: ", "receiver: "+ receiverID);
                    *//*if (dataSnapshot.hasChild("SenderID")){
                        Toast.makeText(getContext(), "Already Here", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "NEED CREATE", Toast.LENGTH_SHORT).show();
                    }*//*
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }


    public static class  ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus, userName;


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userStatus = itemView.findViewById(R.id.user_status);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}