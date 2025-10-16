package com.example.course.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import com.example.course.entity.Course;
import com.example.course.form.SearchForm;

public class CourseDaoImpl implements CourseDao {

    //依存性の注入
    private final JdbcTemplate jdbcTemplate;

    public CourseDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    //全ての講座を取得
    @Override
    public List<Course> getAllCourses(){
        String sql = "SELECT * FROM courses";
        //SQLの実行　→　Mapリストへ
        List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql);
        //データリストを用意
        List<Course> list=new ArrayList<>();
        for(Map<String, Object> result : resultList){
            //memberData型のインスタンス
            Course course = new Course();
            //列を1つずつインスタンスに代入
            course.setCourse_id((int)result.get("course_id"));
            course.setCourse_name((String)result.get("course_name"));
            course.setInstructor_id((int)result.get("instructor_id"));
            course.setCapacity((int)result.get("capacity"));
            course.setPrice((int)result.get("price"));
            course.setStart_date((LocalDate)result.get("start_date"));
            course.setEnd_Time((LocalDateTime)result.get("end_date"));
            course.setStatus((String)result.get("status"));

            //リストに追加
            list.add(course);
        }
        return list;
    }

    //指定期間のに講座を取得
    public List<Course> searchCourses(SearchForm form){
        // 日付を文字列からLocalDateに変換
    String start = form.getStartYear() + "-" + form.getStartMonth() + "-" + form.getStartDate();
    String end = form.getEndtYear() + "-" + form.getEndMonth() + "-" + form.getEndDate();

    String sql = "SELECT * FROM courses WHERE start_date >= ? AND end_date <= ?";

    // SQL実行
    List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, start, end);

    // 結果をCourse型リストに変換
    List<Course> list = new ArrayList<>();
    for (Map<String, Object> result : resultList) {
        Course course = new Course();
        course.setCourse_id((int) result.get("course_id"));
        course.setCourse_name((String) result.get("course_name"));
        course.setInstructor_id((int) result.get("instructor_id"));
        course.setCapacity((int) result.get("capacity"));
        course.setPrice((int) result.get("price"));
        course.setStart_date(((java.sql.Date) result.get("start_date")).toLocalDate());
        course.setEnd_Time(((java.sql.Timestamp) result.get("end_date")).toLocalDateTime());
        course.setStatus((String) result.get("status"));

        list.add(course);
    }

    return list;
    }
    
    @Override
    public Course getCourseDetails(int course_id){
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        Map<String,Object> result = jdbcTemplate.queryForMap(sql, course_id);
        Course course = new Course();
        course.setCourse_id((int)result.get("course_id"));
        course.setCourse_name((String)result.get("course_name"));
        course.setInstructor_id((int)result.get("instructor_id"));
        course.setCapacity((int)result.get("capacity"));
        course.setPrice((int)result.get("price"));
        course.setStart_date((LocalDate)result.get("start_date"));
        course.setEnd_Time((LocalDateTime)result.get("end_date"));
        course.setStatus((String)result.get("status"));
        return course;
    }

    @Override
    public boolean isCapacity(int course_id){
        String sql = "SELECT capacity FROM courses WHERE course_id = ?";
        int capacity = jdbcTemplate.queryForObject(sql, Integer.class, course_id);
        String sql2 = """
                    SELECT COUNT(*) AS participant_count
                    FROM grouptb g
                    WHERE g.group_id IN (
                    SELECT e.group_id
                    FROM enrollmentstb e
                    WHERE e.course_id = ?
                    );
                    """;
        int registeredCount = jdbcTemplate.queryForObject(sql2, Integer.class, course_id);
        //残りの席がある場合trueを返す
        return registeredCount < capacity;
    }

    @Override
    public boolean isPeriodEnded(int course_id){
        String sql = "SELECT end_date FROM courses WHERE course_id = ?";
        LocalDateTime endDate = jdbcTemplate.queryForObject(sql, LocalDateTime.class, course_id);
        //過ぎていなかった場合trueを返す
        return !LocalDateTime.now().isAfter(endDate);
    }

    @Override
    public boolean isAcceptingApplications(int course_id){
        return isCapacity(course_id) && !isPeriodEnded(course_id);
    }

    @Override
    public int checkCapacity(int course_id){
        String sql = "SELECT capacity FROM courses WHERE course_id = ?";
        int capacity = jdbcTemplate.queryForObject(sql, Integer.class, course_id);
        String sql2 = """
            SELECT COUNT(*) AS participant_count
            FROM grouptb g
            WHERE g.group_id IN (
            SELECT e.group_id
            FROM enrollmentstb e
            WHERE e.course_id = ?
            );
            """;
        int registeredCount = jdbcTemplate.queryForObject(sql2, Integer.class, course_id);
        return capacity - registeredCount;
    }
}
