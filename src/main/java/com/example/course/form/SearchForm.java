package com.example.course.form;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchForm {
    @NotBlank(message="検索する期間を入力してください")
    private String startYear;
    @NotBlank(message="検索する期間を入力してください")
    private String startMonth;
    @NotBlank(message="検索する期間を入力してください")
    private String startDate;
    @NotBlank(message="検索する期間を入力してください")
    private String endtYear;
    @NotBlank(message="検索する期間を入力してください")
    private String endMonth;
    @NotBlank(message="検索する期間を入力してください")
    private String endDate;

}
