package ro.ubbcluj.cs.ams.assignment.dto.grade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.assignment.dto.responses.StudentGrade;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GradesResponseDto {

    private List<StudentGrade> data;
}
