package com.teamig.whatswap.models;

import java.util.List;

/**
 * Created by lyk on 27/5/17.
 */

public class Chat {
    private String buyerUid;
    private String sellerUid;
    private String title;
    private List<Message> messages;

    public Chat() {
    }

    public Chat(String buyerUid, String sellerUid, String title, List<Message> messages) {
        this.buyerUid = buyerUid;
        this.sellerUid = sellerUid;
        this.title = title;
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getBuyerUid() {
        return buyerUid;
    }

    public void setBuyerUid(String buyerUid) {
        this.buyerUid = buyerUid;
    }

    public String getSellerUid() {
        return sellerUid;
    }

    public void setSellerUid(String sellerUid) {
        this.sellerUid = sellerUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
