package com.example.course.entity;

import java.sql.Date;
import java.sql.Time;

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
    private Date start_date;
    private Date end_date;
    private Time start_Time;
    private Time end_Time;
    private String status;
}
