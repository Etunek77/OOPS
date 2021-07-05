package com.example.project.activities;

import java.util.Map;

public class UserHelperClass {
    private String username,email,phone,password,profession;
    private Map<String, Category> categories;

    public UserHelperClass() {

    }

    public UserHelperClass(String username, String email, String phone, String password, String profession, Map<String, Category> categories) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.profession = profession;
        this.categories = categories;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Map<String, Category> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Category> categories) {
        this.categories = categories;
    }
}
