package ro.ubbcluj.cs.ams.student.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.student.model.tables.pojos.Student;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentsDto {

    private List<Student> data;
}
