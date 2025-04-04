package com.iktproject.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private String content;

    @ManyToOne
    @JoinColumn(name="subject_id", nullable = false)
    private Subject subject;

    public Material() {}

    public Material(String content, Subject subject) {
        this.content = content;
        this.subject = subject;
    }

}

