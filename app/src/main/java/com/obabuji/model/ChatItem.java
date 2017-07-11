package com.obabuji.model;

/**
 * Created by user on 10-06-2017.
 */

public class ChatItem {

    private String message="",senderId="",receiverId="",datetime="";
    private int leftOrRight;// 0 right(send) 1 left(receive)

    public ChatItem(){

    }

    public String getMessage() {
        return message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getLeftOrRight() {
        return leftOrRight;
    }

    public void setLeftOrRight(int leftOrRight) {
        this.leftOrRight = leftOrRight;
    }

}
