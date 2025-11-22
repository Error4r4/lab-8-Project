package com.example.models;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected int userId;
    protected String username;
    protected String email;
    protected String passwordHash;
    protected String role;

    private static int userCounter = 1;

    public User() { /* for Gson */ }

    public User(String username, String email, String passwordHash, String role){
        this.userId = userCounter++;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // getters (note: codebase expects getUserId())
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }

    // setters
    public void setUserId(int id){
        this.userId = id;
        if(id >= userCounter) userCounter = id + 1;
    }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }

    public static void setUserCounter(int value){ userCounter = value; }
}
