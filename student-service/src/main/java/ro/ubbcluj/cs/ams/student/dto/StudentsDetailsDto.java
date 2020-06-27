package ro.ubbcluj.cs.ams.student.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.student.dto.StudentDetailsDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentsDetailsDto {

    List<StudentDetailsDto> data;
}
