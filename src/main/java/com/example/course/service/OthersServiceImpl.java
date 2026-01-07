package com.example.course.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.course.dao.OthersDao;
import com.example.course.entity.Course;
// import com.example.course.entity.Instructor;
import com.example.course.entity.Student;
import com.example.course.exception.NotDeleteException;
// import com.example.course.exception.NotInsertException;
import com.example.course.form.EnrollmentForm;
import com.example.course.form.UserCheck;

@Service
public class OthersServiceImpl implements OthersService {

    private final OthersDao othersDao;

    OthersServiceImpl(OthersDao othersDao) {
        this.othersDao = othersDao;
    }

    @Override
    public void registerEnrollment1(EnrollmentForm form) {
        // 呼び出しメソッドを変更
        // othersDao.registerEnrollment(enrollment);
        othersDao.registerEnrollment1(form);
    }
    // メンバー分を追加
    // @Override
    // public void registerEnrollment2(List<String> nameList){
    // //呼び出しメソッドを変更
    // // othersDao.registerEnrollment(enrollment);
    // othersDao.registerEnrollment2(nameList);
    // }

    @Override
    public List<Course> getCoursesByStudent(UserCheck userCheck) {
        List<Course> list = othersDao.getCoursesByStudent(userCheck);
        return list;
    }

    @Override
    public List<Student> findAllByGroupId(int courseID, UserCheck userCheck) {
        List<Student> list = othersDao.findAllByGroupId(courseID, userCheck);
        return list;
    }
    // @Override
    // public void deleteStudent(String name,int courseID){
    // int result=othersDao.deleteStudent(name,courseID);
    // if(result==0){
    // throw new NotDeleteException();
    // }

    // }
    @Override
    public List<Student> getAllStudent() {
        List<Student> list = othersDao.getAllStudent();
        return list;

    }

    @Override
    public String getInstructor_name(int instructor_id) {
        String instructor_name = othersDao.instructor_name(instructor_id);
        return instructor_name;
    }

    public boolean existsUser(UserCheck checkForm) {
        return othersDao.countUser(checkForm) > 0;
    }
    // @Override
    // public List<Instructor> getAllInstructor(){
    // List<Instructor>list=othersDao. getAllInstructor();
    // return list;

    // }
    // @Override
    // public void deleteInstructor(int instructorId){
    // int result=othersDao.deleteInstructor(instructorId);
    // if(result==0){

    // }

    // }
}
