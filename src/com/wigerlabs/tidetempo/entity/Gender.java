package com.wigerlabs.tidetempo.entity;

public enum Gender {
    Male(1), Female(2);
    
    private int id;
    
    private Gender(int id){
        this.id = id;
    }
    
    public int getId(){
        return this.id;
    }
}
