package com.example.course.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.course.entity.Course;
import com.example.course.form.SearchForm;
import com.example.course.form.UserCheck;

@Service
public interface CourseService {
    // 全ての講座を取得
    List<Course> getAllCourses();

    // 指定期間の講座を取得
    List<Course> searchCourses(SearchForm form);

    // 登録した講座の中から条件に合う講座を取得
    List<Course> searchRegisteredCourses(SearchForm searchForm, UserCheck userCheck);

    // 講座詳細を取得
    Course getCourseDetails(int course_id);

    // 講座が満員かどうかを取得
    boolean isCapacity(int course_id);

    // 講座の期間が終了しているかどうかを取得
    boolean isPeriodEnded(int course_id);

    // isCapacityとisPeriodEndedの両方を確認
    String isAcceptingApplications(int course_id);

    // 残りの席数を取得する
    int checkCapacity(int course_id);

    // ※追加メソッド
    // 登録講座詳細の取得
    Course getMyCourseDetails(int course_id, UserCheck user);
}
