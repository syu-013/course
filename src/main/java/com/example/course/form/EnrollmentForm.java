package com.example.course.form;

import java.util.List;

// import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EnrollmentForm {
    // 代表者の氏名
    @NotEmpty(message = "※氏名を入力してください")
    private String representativeName;//リスト型に変更
    
    // 代表者のメールアドレス
    @NotBlank(message = "※代表者のメールアドレスを入力してください")
    private String representativeEmail;

    private Integer courseId;

    private List<String> names;
}