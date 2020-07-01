package ro.ubbcluj.cs.ams.assignment.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CpLinkBean implements Serializable {

    private String courseId;

    private Integer typeId;

    private String userId;
}
