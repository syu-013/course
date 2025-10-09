package com.example.course.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCheck {
    @NotBlank(message = "※氏名を入力してください")
    private String userName;
    @NotBlank(message = "※メールアドレスを入力してください")
    private String email;
}
