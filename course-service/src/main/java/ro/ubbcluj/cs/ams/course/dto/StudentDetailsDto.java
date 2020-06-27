package ro.ubbcluj.cs.ams.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentDetailsDto {

    private String lastName;
    private String firstName;
    private String username;
}
