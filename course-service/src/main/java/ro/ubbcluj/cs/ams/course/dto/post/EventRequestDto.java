package ro.ubbcluj.cs.ams.course.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestDto {

    private long date;

    private String place;
}
