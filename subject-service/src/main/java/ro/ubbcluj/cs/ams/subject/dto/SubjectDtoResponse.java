package ro.ubbcluj.cs.ams.subject.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class SubjectDtoResponse {

    private String subjectId;
    private String name;
    private Integer credits;
    private Integer specId;
    private Integer year;
}
