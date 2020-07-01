package ro.ubbcluj.cs.ams.student.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CoursesIdsDto implements Serializable {

    private Iterable<String> data;
}
