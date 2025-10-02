// package com.example.course.dao;

// import java.util.List;

// public interface CourseDao {
//     //全ての講座を取得
//     List<Course> getAllCourses();
//     //指定期間のに講座を取得
//     List<Course> serchCourses(SerchForm SerchForm);
//     //登録した講座の中から条件に合う講座を取得
//     List<Course> serchRegisteredCourses(SerchForm serchForm,UserCheck userCheck);
//     //講座詳細を取得
//     Course getCourseDetails(int courseId);
//     //講座が満員かどうかを取得
//     boolean isCapacity(int courseID);
//     //講座の期間が終了しているかどうかを取得
//     boolean isPeriodEnded(int courseID);
//     //isCapacityとisPeriodEndedの両方を確認？
//     boolean isAcceptingApplications(int courseID);
//     //残りの席数を取得する
//     int checkCapacity(int courseID);
// }
