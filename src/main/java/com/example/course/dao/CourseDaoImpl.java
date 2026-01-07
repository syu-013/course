package com.example.course.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.course.entity.Course;
import com.example.course.form.SearchForm;
import com.example.course.form.UserCheck;

@Repository
public class CourseDaoImpl implements CourseDao {

    // 依存性の注入
    private final JdbcTemplate jdbcTemplate;

    public CourseDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 全ての講座を取得
    @Override
    public List<Course> getAllCourses() {
        String sql = "SELECT * FROM coursetb";
        // SQLの実行 → Mapリストへ
        // List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql);
        // データリストを用意
        List<Course> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Course course = new Course();
            course.setCourse_id(rs.getInt("course_id"));
            course.setCourse_name(rs.getString("course_name"));
            course.setLocation(rs.getString("location"));
            course.setInstructor_id(rs.getInt("instructor_id"));
            course.setInstructor_name(getInstructorName(rs.getInt("instructor_id")));
            course.setCapacity(rs.getInt("capacity"));
            course.setPrice(rs.getInt("price"));
            course.setStart_date(rs.getObject("start_date", LocalDate.class));
            course.setStart_Time(rs.getObject("start_time", LocalTime.class));
            course.setEnd_date(rs.getObject("end_date", LocalDate.class));
            course.setEnd_Time(rs.getObject("end_time", LocalTime.class));
            course.setStatus(rs.getString("status"));
            return course;
        });
        return list;
    }

    // 指定期間のに講座を取得
    public List<Course> searchCourses(SearchForm form) {

        String sql = "SELECT * FROM coursetb WHERE start_date >= ? AND end_date < ?";

        LocalDate start = LocalDate.of(form.getStartYear(), form.getStartMonth(), form.getStartDate());
        LocalDate endPlusOne = LocalDate.of(form.getEndYear(), form.getEndMonth(), form.getEndDate()).plusDays(1);
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, start, endPlusOne);

        // 結果をCourse型リストに変換
        List<Course> list = new ArrayList<>();
        for (Map<String, Object> result : resultList) {
            Course course = new Course();
            course.setCourse_id((int) result.get("course_id"));
            course.setCourse_name((String) result.get("course_name"));
            course.setLocation((String) result.get("location"));
            course.setInstructor_id((int) result.get("instructor_id"));
            course.setInstructor_name(getInstructorName((int) result.get("instructor_id")));
            course.setCapacity((int) result.get("capacity"));
            course.setPrice((int) result.get("price"));
            course.setStart_date(((java.sql.Date) result.get("start_date")).toLocalDate());
            course.setStart_Time(((java.sql.Time) result.get("start_time")).toLocalTime());
            course.setEnd_date(((java.sql.Date) result.get("end_date")).toLocalDate());
            course.setEnd_Time(((java.sql.Time) result.get("end_time")).toLocalTime());
            course.setStatus(isAcceptingApplications((int) result.get("course_id")));

            list.add(course);
        }

        return list;
    }

    // 登録した講座の中から条件に合う講座を取得
    @Override
    public List<Course> searchRegisteredCourses(SearchForm form, UserCheck user) {
        String sql = "SELECT * FROM coursetb c JOIN studentstb s ON c.course_id = s.course_id "
                + " WHERE s.full_name = ? AND s.email_address = ? AND c.start_date >= ? AND c.end_date < ?";

        LocalDate start = LocalDate.of(form.getStartYear(), form.getStartMonth(), form.getStartDate());
        LocalDate endPlusOne = LocalDate.of(form.getEndYear(), form.getEndMonth(), form.getEndDate()).plusDays(1);

        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, user.getUserName(), user.getEmail(),
                start,
                endPlusOne);

        List<Course> list = new ArrayList<>();
        for (Map<String, Object> result : resultList) {
            Course course = new Course();
            course.setCourse_id((int) result.get("course_id"));
            course.setCourse_name((String) result.get("course_name"));
            course.setLocation((String) result.get("location"));
            course.setInstructor_id((int) result.get("instructor_id"));
            course.setInstructor_name(getInstructorName((int) result.get("instructor_id")));
            course.setCapacity((int) result.get("capacity"));
            course.setPrice((int) result.get("price"));
            course.setStart_date(((java.sql.Date) result.get("start_date")).toLocalDate());
            course.setStart_Time(((java.sql.Time) result.get("start_time")).toLocalTime());
            course.setEnd_date(((java.sql.Date) result.get("end_date")).toLocalDate());
            course.setEnd_Time(((java.sql.Time) result.get("end_time")).toLocalTime());
            course.setStatus(isAcceptingApplications((int) result.get("course_id")));

            list.add(course);
        }
        return list;
    }

    @Override
    public Course getCourseDetails(int course_id) {
        String sql = "SELECT * FROM coursetb WHERE course_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { course_id }, (rs, rowNum) -> {
            Course course = new Course();
            course.setCourse_id(rs.getInt("course_id"));
            course.setCourse_name(rs.getString("course_name"));
            course.setLocation(rs.getString("location"));
            course.setInstructor_id(rs.getInt("instructor_id"));
            course.setCapacity(rs.getInt("capacity"));
            course.setPrice(rs.getInt("price"));
            course.setStart_date(rs.getObject("start_date", LocalDate.class));
            course.setStart_Time(rs.getObject("start_time", LocalTime.class));
            course.setEnd_date(rs.getObject("end_date", LocalDate.class));
            course.setEnd_Time(rs.getObject("end_time", LocalTime.class));
            course.setStatus(isAcceptingApplications(rs.getInt("course_id")));
            return course;
        });
    }

    @Override
    public boolean isCapacity(int course_id) {
        String sql = "SELECT capacity FROM coursetb WHERE course_id = ?";
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
        // 残りの席がある場合trueを返す
        return registeredCount < capacity;
    }

    @Override
    public boolean isPeriodEnded(int course_id) {
        String sql = "SELECT end_date FROM coursetb WHERE course_id = ?";
        LocalDateTime endDate = jdbcTemplate.queryForObject(sql, LocalDateTime.class, course_id);
        // 過ぎていなかった場合trueを返す
        return !LocalDateTime.now().isAfter(endDate);
    }

    @Override
    public String isAcceptingApplications(int course_id) {
        if (isPeriodEnded(course_id) && isCapacity(course_id)) {
            return "申込受付中";
        } else if (!isPeriodEnded(course_id)) {
            return "期間終了";
        } else if (!isCapacity(course_id)) {
            return "満席";
        }
        return "開催予定";
    }

    @Override
    public int checkCapacity(int course_id) {
        String sql = "SELECT capacity FROM coursetb WHERE course_id = ?";
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

    private String getInstructorName(int instructor_id) {
        String sql = "SELECT instructor_name FROM instructortb WHERE instructor_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, instructor_id);
        } catch (Exception e) {
            return "不明"; // エラー時の対策
        }
    }

    // ※追加メソッドを作ってみました！
    // 登録講座詳細の取得
    public Course getMyCourseDetails(int course_id, UserCheck user) {
        String sql = "SELECT * FROM coursetb c JOIN studentstb s ON c.course_id = s.course_id "
                + " WHERE c.course_id = ? and s.full_name=?";
        Map<String, Object> rs = jdbcTemplate.queryForMap(sql, course_id, user.getUserName());
        Course course = new Course();
        course.setCourse_id((int) rs.get("course_id"));
        course.setCourse_name((String) rs.get("course_name"));
        course.setLocation((String) rs.get("location"));
        course.setInstructor_id((int) rs.get("instructor_id"));
        course.setCapacity((int) rs.get("capacity"));
        course.setPrice((int) rs.get("price"));
        course.setStart_date(((java.sql.Date) rs.get("start_date")).toLocalDate()); // ★修正：直接キャストから変換へ
        course.setStart_Time(((java.sql.Time) rs.get("start_time")).toLocalTime()); // ★修正：直接キャストから変換へ
        course.setEnd_date(((java.sql.Date) rs.get("end_date")).toLocalDate()); // ★修正：直接キャストから変換へ
        course.setEnd_Time(((java.sql.Time) rs.get("end_time")).toLocalTime()); // ★修正：直接キャストから変換へ
        course.setStatus(isAcceptingApplications((int) rs.get("course_id")));
        return course;
    }
}