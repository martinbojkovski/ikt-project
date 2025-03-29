package com.iktproject.model;

import lombok.Data;

@Data
public class User {

    private String username;

    private String name;

    private String surname;

    private int grade;

    private String password;

    private Type type;

    public User(String username, String name, String surname, int grade, String password, Type type) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.grade = grade;
        this.password = password;
        this.type = type;
    }

    public User() {
    }
}