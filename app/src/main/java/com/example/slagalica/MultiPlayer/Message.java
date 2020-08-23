package com.example.slagalica.MultiPlayer;

import java.util.Date;
import java.util.Calendar;

public class Message {

    public int senderId;
    public String senderName;
    public String senderLastName;
    public String message;

    public Message(int senderId, String senderName, String senderLastName,String message) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderLastName = senderLastName;
        this.message = message;
    }

    public Message()
    {
        senderId = 0;
        senderName = "";
        senderLastName = "";
        message = "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public void setSenderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
    }

}
