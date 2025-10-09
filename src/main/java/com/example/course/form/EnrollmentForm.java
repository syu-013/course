package com.example.course.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnrollmentForm {
    // 代表者の氏名
    @NotBlank(message = "※代表者の氏名を入力してください")
    private String representativeName;
    
    // 代表者のメールアドレス
    @NotBlank(message = "※代表者のメールアドレスを入力してください")
    private String representativeEmail;
}
