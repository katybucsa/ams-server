package ro.ubbcluj.cs.ams.course.dto.course;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CourseResponseDto {

    private String id;
    private String name;
    private Integer credits;
    private Integer specId;
    private Integer year;
}
