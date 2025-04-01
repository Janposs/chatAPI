package com.example.chat.chat;

import java.util.List;

import com.example.chat.message.Message;
import com.example.chat.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity(name="chat_")
public class Chat {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(nullable=false)
    private int chatId;

    @Column(nullable=false)
    private String title;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="chat_user_", joinColumns=@JoinColumn(name="chatId"), inverseJoinColumns=@JoinColumn(name="userId"))
    private List<User> members;

    //I think I need to change this
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userId", nullable=false)
    private User owner;

    @OneToMany(mappedBy="destination", fetch = FetchType.EAGER)
    private List<Message> messages;
	
    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }


    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Chat() {}
}
