package ro.ubbcluj.cs.ams.assignment.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
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
