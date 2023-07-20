package com.example.c195.model;

/** This class represents a USER **/
public class User {

    public int userId;
    public String userName;
    public String password;


        public User(int userId, String userName, String password) {
            this.userId = userId; // Ensure this line exists and is correct
            this.userName = userName;
            this.password = password;
        }



    public User(int userId) {
    }


    public int getUserID() {

        return userId;

    }
    @Override
    public String toString() {
        return String.valueOf(this.getUserID());
    }
    public String getUserName() {

        return userName;
    }
    public String getPassword() {

        return password;
    }
}

