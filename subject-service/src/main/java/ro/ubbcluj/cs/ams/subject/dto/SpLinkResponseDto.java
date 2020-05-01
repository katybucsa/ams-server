package ro.ubbcluj.cs.ams.subject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SpLinkResponseDto {

    private String subjectId;
    private int typeId;
    private String userId;
}
