package ro.ubbcluj.cs.ams.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@AllArgsConstructor
@NonNull
@Data
@Builder
public class GradeResponseDto {

    private Integer gradeId;
    private Integer typeId;
    private String teacher;
    private String student;
    private Double value;
    private String subjectId;
    private LocalDateTime date;
}
