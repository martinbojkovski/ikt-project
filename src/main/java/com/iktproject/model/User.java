package com.iktproject.model;

import jakarta.persistence.*;
import lombok.Data;

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

    private Double grade_average;

    private String password;

    @Enumerated(EnumType.STRING)
    private Type type;

    public User(String username, String name, String surname, Double grade_average, String password, Type type) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.grade_average = grade_average;
        this.password = password;
        this.type = type;
    }

    public User() {
    }
}