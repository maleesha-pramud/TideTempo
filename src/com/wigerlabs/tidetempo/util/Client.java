package com.wigerlabs.tidetempo.util;

public class Client {
    private int id;
    private String name;
    private String email;
    
    public Client(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
