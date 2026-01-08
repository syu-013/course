package com.example.course.dao;

import java.util.List;

import com.example.course.entity.Course;
//import com.example.course.entity.Instructor;
import com.example.course.entity.Student;
import com.example.course.form.EnrollmentForm;
import com.example.course.form.UserCheck;

public interface OthersDao {
    // 受講者情報と申込情報を登録する。重複チェックも含む。
    // void registerEnrollment(EnrollmentForm enrollment);

    // 受講者の登録講座をすべて取得する
    List<Course> getCoursesByStudent(UserCheck userCheck);

    // 受講者グループの情報をすべて取得する。
    List<Student> findAllByGroupId(int courseID, UserCheck userCheck);

    // 該当の受講者を削除する。
    int deleteStudent(String name, int courseID);

    // 全ての受講者情報を取得する。
    List<Student> getAllStudent();

    // 講師名のみを取得する。
    String instructor_name(int instructor_id);

    // 該当の受講者情報の件数を取得する。（重複チェック用）
    int countUser(UserCheck checkForm);

    // 新規講座情報を追加する。
    // int addCourse(CourseForm);

    // 新規講師情報を追加する。
    // int addInstructor(InstructorForm);

    // 全ての講師一覧を取得する。
    // List<Instructor> getAllInstructor();

    // 既存講師情報を削除する。
    // int deleteInstructor(int instructorId);

    // 追加メソッド
    int registerEnrollment1(EnrollmentForm form);

}
