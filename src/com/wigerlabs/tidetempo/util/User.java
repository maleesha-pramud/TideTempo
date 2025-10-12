package com.wigerlabs.tidetempo.util;

public class User {
    public int id;
    public String name;
    public String email;
    public String password;
    
    public User() {
        
    }
    
    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
