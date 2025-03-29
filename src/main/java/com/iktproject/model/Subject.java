package com.iktproject.model;
import lombok.Data;

@Data
public class Subject {

    private int Id;

    private String subject_name;

    private String subject_description;

    public Subject(int id, String subject_name, String subject_description) {
        this.Id = id;
        this.subject_name = subject_name;
        this.subject_description = subject_description;
    }

    public Subject() {
    }
}
