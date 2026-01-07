package com.example.course.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.course.dao.CourseDao;
import com.example.course.entity.Course;
import com.example.course.form.SearchForm;
import com.example.course.form.UserCheck;

@Service
public class CourseServiceImpl implements CourseService {
    // 依存性の注入（DAO）
    private final CourseDao courseDao;

    CourseServiceImpl(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courseList = courseDao.getAllCourses();
        return courseList;
    }

    @Override
    public List<Course> searchCourses(SearchForm form) {
        List<Course> courseList = courseDao.searchCourses(form);
        return courseList;
    }

    @Override
    public List<Course> searchRegisteredCourses(SearchForm form, UserCheck user) {
        return courseDao.searchRegisteredCourses(form, user);
    }

    @Override
    public Course getCourseDetails(int course_id) {
        Course course = courseDao.getCourseDetails(course_id);
        return course;
    }

    @Override
    public boolean isCapacity(int course_id) {
        return courseDao.isCapacity(course_id);
    }

    @Override
    public boolean isPeriodEnded(int course_id) {
        return courseDao.isPeriodEnded(course_id);
    }

    @Override
    public String isAcceptingApplications(int course_id) {
        return courseDao.isAcceptingApplications(course_id);
    }

    @Override
    public int checkCapacity(int course_id) {
        return courseDao.checkCapacity(course_id);
    }

    // ※追加メソッド
    // 登録講座詳細の取得
    public Course getMyCourseDetails(int course_id, UserCheck user) {
        return courseDao.getMyCourseDetails(course_id, user);
    }
}
