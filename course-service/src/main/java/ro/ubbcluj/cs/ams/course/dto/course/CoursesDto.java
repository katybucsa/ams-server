package ro.ubbcluj.cs.ams.course.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CoursesDto {

    List<CourseDtoResponse> data;
}
