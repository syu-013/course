package com.example.course.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.course.CourseApplication;
import com.example.course.entity.Course;
import com.example.course.entity.Student;
import com.example.course.form.EnrollmentForm;
import com.example.course.form.UserCheck;

@Repository
public class OthersDaoImpl implements OthersDao {

    private final CourseApplication courseApplication;

    private final JdbcTemplate jdbcTemplate;

    public OthersDaoImpl(JdbcTemplate jdbcTemplate, CourseApplication courseApplication) {
        this.jdbcTemplate = jdbcTemplate;
        this.courseApplication = courseApplication;
    }

    // 受講者情報と申込情報を登録する。重複チェックも含む。
    @Override
    @Transactional // まとめて処理、失敗でロールバック
    // public void registerEnrollment(EnrollmentForm form) {

    // // 1. グループ作成
    // //String createGroupSql = "INSERT INTO grouptb () VALUES ()";
    // String createGroupSql = "INSERT INTO grouptb DEFAULT VALUES(?,?)";
    // jdbcTemplate.update(createGroupSql);

    // Integer groupId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()",
    // Integer.class);

    // // 2. 申し込み作成
    // String insertApplicationSql = """
    // INSERT INTO applications (group_id, course_id)
    // VALUES (?, ?)
    // """;
    // jdbcTemplate.update(insertApplicationSql, groupId, form.getCourseId());

    // Integer applicationId = jdbcTemplate.queryForObject("SELECT
    // LAST_INSERT_ID()", Integer.class);

    // // 3. 受講者追加
    // String insertParticipantSql = """
    // INSERT INTO studentstb (full_name, email_address, representative_flag)
    // VALUES (?, ?, ?)
    // """;

    // // 代表者
    // jdbcTemplate.update(
    // insertParticipantSql,
    // form.getRepresentativeName().get(0),
    // form.getRepresentativeEmail(),
    // true
    // );

    // Integer representativeId = jdbcTemplate.queryForObject("SELECT
    // LAST_INSERT_ID()", Integer.class);

    // // 中間テーブルへ代表者追加
    // linkParticipantToGroup(groupId, representativeId);

    // // 4. 他の参加者
    // for (int i = 1; i < form.getRepresentativeName().size(); i++) {
    // jdbcTemplate.update(
    // insertParticipantSql,
    // form.getRepresentativeName().get(i),
    // null, // メールなし
    // false
    // );

    // Integer participantId = jdbcTemplate.queryForObject("SELECT
    // LAST_INSERT_ID()", Integer.class);

    // // グループに紐付き
    // linkParticipantToGroup(groupId, participantId);
    // }

    // }

    // private void linkParticipantToGroup(Integer groupId, Integer participantId) {
    // String sql = "INSERT INTO grouptb (group_id, participant_id) VALUES (?, ?)";
    // jdbcTemplate.update(sql, groupId, participantId);
    // }

    // 新たにメソッドを記述
    public int registerEnrollment1(EnrollmentForm form) {
        // 受講者テーブルへの代表者の追加
        String sql = "INSERT INTO studentstb(full_name,email_address,representative_flag,course_id) VALUES(?,?,?,?)";
        int cnt = jdbcTemplate.update(sql, form.getRepresentativeName(), form.getRepresentativeEmail(), 1,
                form.getCourseId());

        // 受講者idの取得
        String sql2 = "SELECT Max(participant_id) as Max_pid FROM studentstb";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql2);
        int no = (int) result.get("Max_pid");

        // グループidの取得
        String sql4 = "SELECT Max(group_id) as Max_id FROM grouptb";
        Map<String, Object> result4 = jdbcTemplate.queryForMap(sql4);
        int noG = (int) result4.get("Max_id");

        // グループテーブルへの追加
        noG += 1;
        String sql3 = "INSERT INTO grouptb(group_id,participant_id) VALUES(?,?)";
        int cnt3 = jdbcTemplate.update(sql3, noG, no);

        // //申し込みテーブルへの追加
        // String sql5 = "INSERT INTO enrollmentstb(group_id,course_id) VALUES(?,?,?)";
        // int cnt5=jdbcTemplate.update(sql5,noG,form.getCourseId(),no);
        // System.out.println("**********************sql5");

        // 受講者テーブルへのメンバーの追加

        // System.out.println("**********************menba::" +form.getNames().size() );
        if (form.getNames() != null) {
            List<String> nameList = form.getNames();
            for (String f2 : nameList) {
                String sql5 = "INSERT INTO studentstb(full_name,representative_flag,course_id) VALUES(?,?,?)";
                jdbcTemplate.update(sql5, f2, 0, form.getCourseId());
                // 各メンバーの受講者idの取得
                String sql6 = "SELECT Max(participant_id) as Max_pid FROM studentstb";
                Map<String, Object> result6 = jdbcTemplate.queryForMap(sql6);
                no = (int) result6.get("Max_pid");
                String sql7 = "INSERT INTO grouptb(group_id,participant_id) VALUES(?,?)";
                int cnt7 = jdbcTemplate.update(sql7, noG, no);
            }
        }
        return cnt3;
    }

    // @Override
    // public int registerEnrollment2(List<String> nameList){
    // //受講者テーブルへのメンバーの追加
    // for(String f2:nameList){
    // String sql = "INSERT INTO studentstb(full_name,representative_flag)
    // VALUES(?,?)";
    // jdbcTemplate.update(sql,f2,0);
    // System.out.println("**********************sql6");

    // }

    // return nameList.size();
    // }

    // 受講者の登録講座をすべて取得する
    @Override
    public List<Course> getCoursesByStudent(UserCheck userCheck) {
        // ※sql文が足りません→変更
        String sql = "SELECT * FROM coursetb c JOIN studentstb s ON c.course_id = s.course_id  WHERE full_name = ? AND email_address = ?";
        // SQLの実行 → Mapリストへ
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, userCheck.getUserName(),
                userCheck.getEmail());
        // データリストを用意
        List<Course> list = new ArrayList<>();
        for (Map<String, Object> result : resultList) {
            // memberData型のインスタンス
            Course course = new Course();
            // 列を1つずつインスタンスに代入
            course.setCourse_id((int) result.get("course_id"));
            course.setCourse_name((String) result.get("course_name"));
            course.setLocation((String) result.get("location"));
            course.setInstructor_id((int) result.get("instructor_id"));
            course.setInstructor_name(this.instructor_name((int) result.get("instructor_id")));
            course.setCapacity((int) result.get("capacity"));
            course.setPrice((int) result.get("price"));
            course.setStart_date(((java.sql.Date) result.get("start_date")).toLocalDate());
            course.setStart_Time(((java.sql.Time) result.get("start_time")).toLocalTime());
            course.setEnd_date(((java.sql.Date) result.get("end_date")).toLocalDate());
            course.setEnd_Time(((java.sql.Time) result.get("end_time")).toLocalTime());
            course.setStatus(isAcceptingApplications((int) result.get("course_id")));

            // リストに追加
            list.add(course);
        }
        return list;
    }

    // 受講者グループの情報をすべて取得する。
    @Override
    public List<Student> findAllByGroupId(int courseID, UserCheck userCheck) {
        String sql = "SELECT\n" + //
                "    P.participant_id,\n" + //
                "    P.full_name,\n" + //
                "    P.email_address,\n" + //
                "    P.representative_flag\n" + //
                "FROM\n" + //
                "    studentstb P\n" + //
                "JOIN\n" + //
                "    grouptb GP ON P.participant_id = GP.participant_id\n" + //
                "WHERE\n" + //
                "    GP.group_id = (\n" + //
                "        SELECT\n" + //
                "            group_id\n" + //
                "        FROM\n" + //
                "            grouptb\n" + //
                "        WHERE\n" + //
                "            participant_id = (\n" + //
                "                SELECT\n" + //
                "                    participant_id\n" + //
                "                FROM\n" + //
                "                    studentstb\n" + //
                "                WHERE\n" + //
                "                    full_name = ? \n" + //
                "            )\n" + //
                "    );";

        // SQLの実行 → Mapリストへ
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, userCheck.getUserName(), courseID);
        // データリストを用意
        List<Student> list = new ArrayList<>();
        for (Map<String, Object> result : resultList) {
            Student student = new Student();
            // 列を1つずつ代入
            student.setStudentID((int) result.get("participant_id"));
            student.setName((String) result.get("full_name"));
            student.setEmail((String) result.get("email_address"));

            // リストに追加
            list.add(student);
        }
        return list;
    }

    // 名前から participant_id を取得
    public Integer findParticipantIdByName(String name) {
        String sql = "SELECT participant_id FROM studentstb WHERE full_name = ?";
        List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, name);
        return result.isEmpty() ? null : result.get(0);
    }

    // participant_id → group_id を取得
    public List<Integer> findGroupIdsByParticipantId(Integer participantId) {
        String sql = "SELECT group_id FROM enrollmentstb WHERE participant_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, participantId);
    }

    // 個人キャンセル（削除処理）
    @Override
    public int deleteStudent(String name, int courseId) {

        // ① 名前と講座IDから participant_id を取得
        String sqlSelectIds = "SELECT participant_id FROM studentstb WHERE full_name = ? AND course_id = ?";
        List<Integer> studentIDs = jdbcTemplate.queryForList(sqlSelectIds, Integer.class, name, courseId);

        if (studentIDs.isEmpty()) {
            throw new RuntimeException("該当する受講者が存在しません: " + name);
        }

        // 名前とコースIDでユニークとは限らないが、ここでは最初の一人を対象とするか、全て対象とするか。
        // 要件では「名前と講座ＩＤからstudentstbに合致するparticipant_idを取得」とある。
        // 複数ヒットした場合は全て処理するロジックにする。

        int deleteCount = 0;

        for (Integer studentID : studentIDs) {
            // ② participant_id が所属している group_id を取得
            String sqlSelectGroups = "SELECT group_id FROM grouptb WHERE participant_id = ?";
            List<Integer> groupIds = jdbcTemplate.queryForList(sqlSelectGroups, Integer.class, studentID);

            // ③ studentstb から削除
            String sqlDeleteStudent = "DELETE FROM studentstb WHERE participant_id = ?";
            deleteCount += jdbcTemplate.update(sqlDeleteStudent, studentID);

            // ④ 取得した group_id を持つレコードを grouptb からすべて削除
            if (!groupIds.isEmpty()) {
                for (Integer groupId : groupIds) {
                    String sqlDeleteGroup = "DELETE FROM grouptb WHERE group_id = ?";
                    jdbcTemplate.update(sqlDeleteGroup, groupId);
                }
            }
        }

        return deleteCount;
    }

    // 全ての受講者情報を取得する。
    @Override
    public List<Student> getAllStudent() {
        String sql = "SELECT * FROM studentstb";

        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

        List<Student> list = new ArrayList<>();

        for (Map<String, Object> result : resultList) {
            Student student = new Student();

            student.setStudentID((int) result.get("participant_id"));
            student.setName((String) result.get("full_name"));
            student.setEmail((String) result.get("email_address"));

            // representative_flag は boolean のはず
            boolean isRep = (boolean) result.get("representative_flag");

            // 画面に渡すための "role" をセット
            student.setRole(isRep ? "代表者" : "同行者");

            list.add(student);
        }
        return list;
    }

    // 講師名のみを取得する。
    @Override
    public String instructor_name(int instructor_id) {
        String sql = "SELECT instructor_name FROM instructortb WHERE instructor_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, instructor_id);
    }

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

    public boolean isPeriodEnded(int course_id) {
        String sql = "SELECT end_date FROM coursetb WHERE course_id = ?";
        LocalDateTime endDate = jdbcTemplate.queryForObject(sql, LocalDateTime.class, course_id);
        // 過ぎていなかった場合trueを返す
        return !LocalDateTime.now().isAfter(endDate);
    }

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
    public int countUser(UserCheck userCheck) {
        // sql文にミスあり
        String sql = "SELECT COUNT(*) FROM studentstb WHERE full_name = ? AND email_address = ?";
        // SQLの実行 → Mapリストへ
        return jdbcTemplate.queryForObject(sql, Integer.class, userCheck.getUserName(), userCheck.getEmail());
    }

}