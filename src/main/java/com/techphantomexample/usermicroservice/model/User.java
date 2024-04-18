package com.techphantomexample.usermicroservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users_info")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int userId;
    @Column(name="FullName")
    private String userFullName;
    @Column(name="Email")
    private  String userEmail;
    @Column(name="Password")
    private String userPassword;
    @Column(name="Role")
    private String userRole;

    public User() {
    }

    public User(String userFullName, String userEmail, String userPassword, String userRole) {
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userRole = userRole;
    }


    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
