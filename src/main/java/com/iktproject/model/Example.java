package com.iktproject.model;

import lombok.Data;

@Data

public class Example {

    private int Id;

    private String description;

    public Example() {}

    public Example(int id, String description) {
        Id = id;
        this.description = description;
    }

}
