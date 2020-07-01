package ro.ubbcluj.cs.ams.student.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentsDetailsDto {

    List<StudentDetailsDto> data;
}