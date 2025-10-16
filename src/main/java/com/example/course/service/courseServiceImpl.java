package com.example.course.service;

import java.util.List;
import com.example.course.dao.CourseDao;
import com.example.course.entity.Course;
import com.example.course.form.SearchForm;

public class courseServiceImpl implements courseService {
     //依存性の注入（DAO）
     private final CourseDao courseDao;

     courseServiceImpl(CourseDao courseDao){
         this.courseDao=courseDao;
     }

     @Override
     public List<Course> getAllCourses(){
        List<Course> courseList=courseDao.getAllCourses();
        return courseList;
     }
    
    @Override
    public List<Course> searchCourses(SearchForm form){
        List<Course> courseList=courseDao.searchCourses(form);
        return courseList;
    }

    @Override
    public Course getCourseDetails(int course_id){
        Course course=courseDao.getCourseDetails(course_id);
        return course;
    }

    @Override
    public boolean isCapacity(int course_id){
        return courseDao.isCapacity(course_id);
    }

    @Override
    public boolean isPeriodEnded(int course_id){
        return courseDao.isPeriodEnded(course_id);
    }

    @Override
    public boolean isAcceptingApplications(int course_id){
        return courseDao.isAcceptingApplications(course_id);
    }

    @Override
    public int checkCapacity(int course_id){
        return courseDao.checkCapacity(course_id);
    }
}
