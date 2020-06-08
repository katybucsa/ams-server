package ro.ubbcluj.cs.ams.course.dto.course;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CourseDtoRequest {

    @NotNull
    private String name;
    @NotNull
    private Integer credits;
    @NotNull
    private Integer specId;
    @NotNull
    private Integer year;
}

