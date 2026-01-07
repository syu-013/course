package com.example.course.form;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchForm {
    @NotBlank(message="検索する期間を入力してください")
    private Integer startYear;
    @NotBlank(message="検索する期間を入力してください")
    @Min(value=1,message="月は1以上で入力してください")
    @Max(value=12,message="月は12以下で入力してください")
    private Integer startMonth;
    @NotBlank(message="検索する期間を入力してください")
    @Min(value=1,message="日付は1以上で入力してください")
    @Max(value=31,message="日付は31以下で入力してください")
    private Integer startDate;
    @NotBlank(message="検索する期間を入力してください")
    private Integer endYear;
    @NotBlank(message="検索する期間を入力してください")
    @Min(value=1,message="月は1以上で入力してください")
    @Max(value=12,message="月は12以下で入力してください")
    private Integer endMonth;
    @NotBlank(message="検索する期間を入力してください")
    @Min(value=1,message="日付は1以上で入力してください")
    @Max(value=31,message="日付は31以下で入力してください")
    private Integer endDate;

}
