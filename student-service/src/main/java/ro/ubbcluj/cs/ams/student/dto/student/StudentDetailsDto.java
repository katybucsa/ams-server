package ro.ubbcluj.cs.ams.student.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.student.model.tables.pojos.SGroup;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentDetailsDto {

    private String lastName;
    private String firstName;
    private String username;
    private SGroup group;
}
