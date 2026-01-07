package com.example.course.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;

//講座情報
@Data

public class Course {
    private int course_id;
    private String course_name;
    private int instructor_id;
    private String instructor_name;
    private int capacity;
    private String location;
    private int price;
    private LocalDate start_date;
    private LocalDate end_date;
    private LocalTime start_Time;
    private LocalTime end_Time;
    private String status;
}