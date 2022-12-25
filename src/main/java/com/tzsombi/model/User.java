package com.tzsombi.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "ta_users")
@Table(name = "ta_users")
public class User implements Serializable {
    @Id
    @SequenceGenerator(
            name = "ta_users_sequence",
            sequenceName=  "ta_users_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ta_users_sequence"
    )
    @Column(name = "user_id", updatable = false)
    private Integer userId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(name = "profile_picture_url", nullable = false)
    private String profilePictureUrl;
    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String password;

    public User() {}

    public User(String name, String email, String profilePictureUrl, boolean isAdmin, String password) {
        this.name = name;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.isAdmin = isAdmin;
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", isAdmin=" + isAdmin +
                ", password='" + password + '\'' +
                '}';
    }
}
