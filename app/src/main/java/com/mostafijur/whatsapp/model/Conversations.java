package com.mostafijur.whatsapp.model;

public class Conversations {
    private String SenderID, ReceiverID;

    public Conversations() {
    }

    public Conversations(String senderID, String receiverID) {
        SenderID = senderID;
        ReceiverID = receiverID;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }
}
