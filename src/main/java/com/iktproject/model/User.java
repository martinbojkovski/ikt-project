package com.iktproject.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String name;

    private String surname;

    private int grade;

    private String password;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Subject> subjects;

    public User(String username, String name, String surname, int grade, String password, Type type, List<Subject> subjects) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.grade = grade;
        this.password = password;
        this.type = type;
        this.subjects = subjects;
    }

    public User() {
    }
}