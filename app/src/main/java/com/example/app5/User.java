package com.example.app5;

public class User {

    public String title,email,phone,password,address,category;

    public User(){

    }

    public User(String title, String email, String phone,String password,String address,String category){
       this.title = title;
       this.email = email;
       this.phone = phone;
       this.password = password;
       this.address = address;
       this.category = category;
    }

}