package com.example.course.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

//講座情報
@Data

public class Course {
    private int course_id;
    private String course_name;
    private int instructor_id;
    private int capacity;
    private String location;
    private int price;
    private LocalDate start_date;
    private LocalDate end_date;
    private LocalDateTime start_Time;
    private LocalDateTime end_Time;
    private String status;
}
