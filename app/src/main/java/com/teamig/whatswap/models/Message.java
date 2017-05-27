package com.teamig.whatswap.models;

/**
 * Created by lyk on 27/5/17.
 */

public class Message {
    private String content;
    private String senderUid;

    public Message() {
    }

    public Message(String content, String senderUid) {
        this.content = content;
        this.senderUid = senderUid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
}
