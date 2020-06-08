package ro.ubbcluj.cs.ams.course.dto.cplink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CpLinkResponseDto {

    private String subjectId;
    private int typeId;
    private String userId;
}
