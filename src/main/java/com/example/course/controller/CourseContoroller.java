package com.example.course.controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.course.entity.Course;
import com.example.course.entity.Student;
import com.example.course.form.EnrollmentForm;
import com.example.course.form.SearchForm;
import com.example.course.form.UserCheck;
import com.example.course.service.CourseService;
import com.example.course.service.OthersService;

import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/course") // ベースパスを設定
public class CourseContoroller {

    private final CourseService courseService;
    private final OthersService othersService;

    public CourseContoroller(CourseService courseService, OthersService othersService) {
        this.courseService = courseService;
        this.othersService = othersService;
    }

    @Autowired
    HttpSession session;

    @GetMapping("/list")
    public String listDisp(Model model) {
        // ※現在アクセス中かを確認し、講座一覧か登録講座一覧かを判定する
        UserCheck user = (UserCheck) session.getAttribute("user");
        if (user == null) {
            List<Course> courseList = courseService.getAllCourses();
            model.addAttribute("cList", courseList);
            SearchForm searchForm = new SearchForm();
            model.addAttribute("searchForm", searchForm);
            return "CourseList";
        } else {
            List<Course> courseList = othersService.getCoursesByStudent(user);
            model.addAttribute("cList", courseList);
            SearchForm searchForm = new SearchForm();
            model.addAttribute("searchForm", searchForm);
            return "MyCourseList";
        }
    }

    @GetMapping("/search")
    public String searchDisp(@ModelAttribute("searchForm") SearchForm searchForm, Model model) {

        // ログイン状態を確認して戻り先の画面とデフォルトの表示リストを決定
        UserCheck user = (UserCheck) session.getAttribute("user");
        String returnView = (user == null) ? "CourseList" : "MyCourseList";
        List<Course> defaultList;
        if (user == null) {
            defaultList = courseService.getAllCourses();
        } else {
            defaultList = othersService.getCoursesByStudent(user);
        }

        // --- 入力値のnullチェック ---
        if (searchForm.getStartYear() == null ||
                searchForm.getStartMonth() == null ||
                searchForm.getStartDate() == null) {

            model.addAttribute("errorMessage", "開始日の年・月・日をすべて入力してください。");
            model.addAttribute("cList", defaultList);
            model.addAttribute("searchForm", searchForm);
            return returnView;
        }

        if (searchForm.getEndYear() == null ||
                searchForm.getEndMonth() == null ||
                searchForm.getEndDate() == null) {

            model.addAttribute("errorMessage", "終了日の年・月・日をすべて入力してください。");
            model.addAttribute("cList", defaultList);
            model.addAttribute("searchForm", searchForm);
            return returnView;
        }

        LocalDate start;
        LocalDate end;

        try {
            start = LocalDate.of(searchForm.getStartYear(), searchForm.getStartMonth(), searchForm.getStartDate());
        } catch (DateTimeException e) {
            model.addAttribute("errorMessage", "開始日が存在しない日付です。");
            model.addAttribute("cList", defaultList);
            model.addAttribute("searchForm", searchForm);
            return returnView;
        }

        try {
            end = LocalDate.of(searchForm.getEndYear(), searchForm.getEndMonth(), searchForm.getEndDate());
        } catch (DateTimeException e) {
            model.addAttribute("errorMessage", "終了日が存在しない日付です。");
            model.addAttribute("cList", defaultList);
            model.addAttribute("searchForm", searchForm);
            return returnView;
        }

        if (start.isAfter(end)) {
            model.addAttribute("errorMessage", "開始日は終了日より前にしてください。");
            model.addAttribute("cList", defaultList);
            model.addAttribute("searchForm", searchForm);
            return returnView;
        }

        // --- 検索成功 ---
        if (user == null) {
            List<Course> courseList = courseService.searchCourses(searchForm);
            model.addAttribute("cList", courseList);
            model.addAttribute("searchForm", searchForm);
            return "CourseList";
        } else {
            List<Course> courseList = courseService.searchRegisteredCourses(searchForm, user);
            model.addAttribute("cList", courseList);
            model.addAttribute("searchForm", searchForm);
            return "MyCourseList";
        }
    }

    @GetMapping("/detail/{course_id}")
    public String detailDisp(@PathVariable("course_id") int id, Model model, HttpSession session) {
        int remainingSeats = courseService.checkCapacity(id); // 残り席数
        model.addAttribute("remainingSeats", remainingSeats);

        // ※全一覧からの詳細か、登録一覧からの詳細かを分ける必要あり
        UserCheck user = (UserCheck) session.getAttribute("user");
        if (user == null) {
            Course course = courseService.getCourseDetails(id);
            model.addAttribute("course", course);
            session.setAttribute("course_id", id);
            return "CourseDetail";
        } else {
            Course course = courseService.getMyCourseDetails(id, user);
            model.addAttribute("course", course);
            session.setAttribute("course_id", id);
            // ※ここで登録詳細に遷移します
            // ただし、登録講座詳細画面ができていません
            return "MyCourseDetail";
        }
    }

    @GetMapping("/add/form")
    public String userCheck(@RequestParam("count") int count, Model model) {
        // 講座IDをセッションから取得
        Integer courseId = (Integer) session.getAttribute("course_id");

        // 残席数取得
        int remainingSeats = courseService.checkCapacity(courseId);

        // ★ 残席数超過チェック
        if (count > remainingSeats) {
            model.addAttribute("errorMessage", "申し込み人数が残り席数を超えています。");
            model.addAttribute("remainingSeats", remainingSeats);

            // 講座情報を再表示するために必要（例）
            Course course = courseService.getCourseDetails(courseId);
            model.addAttribute("course", course);

            return "CourseDetail";
        }

        // 以下を変えてみた・・・
        // EnrollmentForm enrollmentForm = new EnrollmentForm();
        // model.addAttribute("enrollmentForm", enrollmentForm);

        // // 空の名前リストを人数分作成
        // List<String> names = new ArrayList<>();
        // for (int i = 0; i < count; i++) {
        // names.add("");
        // }
        // model.addAttribute("count", count);
        // model.addAttribute("names", names);
        // session.setAttribute("count", count);

        EnrollmentForm enrollmentForm = new EnrollmentForm();

        // その他のメンバー
        List<String> names = new ArrayList<>();
        for (int i = 1; i < count; i++) {
            names.add(new String());
        }
        enrollmentForm.setNames(names);

        model.addAttribute("enrollmentForm", enrollmentForm);
        model.addAttribute("count", count);
        session.setAttribute("count", count);
        return "participant_registration2";
    }

    // 以下のメソッドを変えます

    // @PostMapping("/add")
    // public String addStudents(@Validated
    // @ModelAttribute("enrollmentForm")EnrollmentForm form,BindingResult
    // result,Model model) {
    // if (result.hasErrors()) {
    // List<String> names = new ArrayList<>();
    // int count = (int) session.getAttribute("count");
    // for (int i = 0; i < count; i++) {
    // names.add("");
    // }
    // model.addAttribute("enrollmentForm", form);
    // model.addAttribute("count", count);
    // model.addAttribute("names", names);
    // return "participant_registration";
    // }
    // form.setCourseId((int) session.getAttribute("course_id"));
    // othersService.registerEnrollment(form);
    // return "redirect:/course/list";
    // }

    @PostMapping("/add")
    public String addStudents(@Validated @ModelAttribute("enrollmentForm") EnrollmentForm form, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("enrollmentForm", form);
            return "participant_registration2";
        }
        form.setCourseId((int) session.getAttribute("course_id"));
        othersService.registerEnrollment1(form);

        // ※ユーザー情報をsessionに確保
        UserCheck user = new UserCheck();
        user.setUserName(form.getRepresentativeName());
        user.setEmail(form.getRepresentativeEmail());
        session.setAttribute("user", user);
        return "redirect:/course/list";
    }

    @GetMapping("/userCheck")
    public String userCheck(Model model) {
        // ※現在ユーザーがアクセス中かを確認
        UserCheck user = (UserCheck) session.getAttribute("user");
        if (user == null) {
            // UserCheck checkForm = new UserCheck();
            model.addAttribute("checkForm", new UserCheck());
            return "email_confirmation";
        } else {
            //
            return "redirect:/course/list";
        }

    }

    // メアド確認からのメソッド
    // 引数はModleAttributeに変更
    @PostMapping("/registerList")
    public String registerParticipant(@ModelAttribute("checkForm") UserCheck checkForm, Model model) {
        if (!othersService.existsUser(checkForm)) {
            model.addAttribute("errorMessage", "登録されていない名前です。");
            UserCheck newCheckForm = new UserCheck();
            model.addAttribute("checkForm", newCheckForm);
            return "email_confirmation";
        }
        // ※ユーザー情報をsessionに確保
        UserCheck user = new UserCheck();
        user.setUserName(checkForm.getUserName());
        user.setEmail(checkForm.getEmail());
        session.setAttribute("user", user);
        return "redirect:/course/list";
        // List<Course> courseList = othersService.getCoursesByStudent(checkForm);
        // model.addAttribute("cList", courseList);
        // SearchForm searchForm = new SearchForm();
        // model.addAttribute("searchForm", searchForm);
        // return "MyCourseList";
    }

    @GetMapping("/registerdetail/{course_id}")
    public String registerDetail(@PathVariable("course_id") int id, Model model) {
        int remainingSeats = courseService.checkCapacity(id); // 残り席数
        model.addAttribute("remainingSeats", remainingSeats);
        Course course = courseService.getCourseDetails(id);
        model.addAttribute("course", course);
        return "MyCourseDetail";
    }

    @GetMapping("/cancelIndividual")
    public String cancelIndividual(@RequestParam("course_id") int courseId, Model model) {
        model.addAttribute("course_id", courseId);
        UserCheck user = (UserCheck) session.getAttribute("user");
        String userName = user.getUserName();
        model.addAttribute("name", userName);
        return "CancelIndividual";
    }

    @PostMapping("/cancelIndividual/doCancel")
    public String doCancelIndividual(@RequestParam("course_id") int courseId, @RequestParam("name") String name,
            Model model) {
        othersService.deleteStudent(name, courseId);
        return "redirect:/course/exit";

    }

    // ★ グループメンバー画面に遷移（GET）
    @GetMapping("/groupCancel")
    public String showCancelGroupMember(@RequestParam("course_id") int courseId, Model model) {

        UserCheck user = (UserCheck) session.getAttribute("user");
        List<Student> members = othersService.findAllByGroupId(courseId, user);

        model.addAttribute("members", members);
        model.addAttribute("course_id", courseId);
        return "CancelGroupMember";
    }

    // ★ グループキャンセル処理（POST）
    @PostMapping("/group/cancel")
    public String cancelGroup(@RequestParam("course_id") int courseId,
            @RequestParam("role") String role,
            @RequestParam("name") String name, RedirectAttributes redirectAttributes) {

        othersService.deleteStudent(name, courseId);

        redirectAttributes.addFlashAttribute("message", role + "：" + name + " をキャンセルしました");

        // redirect to group cancel page to show remaining members
        // URL needs course_id parameter
        return "redirect:/course/groupCancel?course_id=" + courseId;
    }

    // ★ セッションを破棄して講座一覧画面へ
    @GetMapping("/exit")
    public String exit(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/course/list";
    }

}