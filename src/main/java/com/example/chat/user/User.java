package com.example.chat.user;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.example.chat.chat.Chat;
import com.example.chat.message.Message;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity(name="user_")
public class User implements UserDetails {

	public User() {
	}

	@PrePersist
	@PreUpdate
	private void ensureNonNull() {
		if (this.chats == null) {
			this.chats = new ArrayList<Chat>();
		}

		if (this.ownedChats == null) {
			this.ownedChats = new ArrayList<Chat>();
		}

		if (this.messages == null) {
			this.messages = new ArrayList<Message>();
		}
	}
	
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(nullable=false)
    private Integer userId;

    @Column(nullable=false)
    private String username;

    @Column(nullable=false)
    private String password;

	@Column(nullable=false)
	private String displayname;

    @ManyToMany(mappedBy="members")
    private List<Chat> chats;

    //all the messages send by the user
    //maybe list is better than set here?
    @OneToMany(mappedBy="sender", cascade=CascadeType.ALL)
    private List<Message> messages;

    //do I need cascase here?
    @OneToMany(mappedBy="owner")
    private List<Chat> ownedChats;

    public List<Chat> getOwnedChats() {
        return ownedChats;
    }

    public void setOwnedChats(List<Chat> ownedChats) {
        this.ownedChats = ownedChats;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public Integer getUserId() {
		return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

	public String getDisplayname() {
		return this.displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

}
