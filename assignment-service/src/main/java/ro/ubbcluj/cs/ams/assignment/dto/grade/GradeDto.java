package ro.ubbcluj.cs.ams.assignment.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GradeDto {

    @NotNull(message = "Activity type is null!")
    private Integer typeId;

    @NotBlank(message = "Student username is null!")
    private String student;

    @NotNull
    @DecimalMax(value = "10", message = "Grade cannot be greater than 10!")
    @DecimalMin(value = "0", message = "Grade cannot be smaller than 0!")
    private Double value;

    @NotBlank(message = "Course is null!")
    private String courseId;
}
