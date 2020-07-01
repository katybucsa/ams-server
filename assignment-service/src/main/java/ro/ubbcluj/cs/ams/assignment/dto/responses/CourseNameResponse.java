package ro.ubbcluj.cs.ams.assignment.dto.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CourseNameResponse {

    private String id;
    private String name;
}
