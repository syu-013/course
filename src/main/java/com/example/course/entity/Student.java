package com.example.course.entity;

import lombok.Data;

//受講者情報
@Data
public class Student {
    private int StudentID;
    private String name;
    private String email;
    private String role;
}