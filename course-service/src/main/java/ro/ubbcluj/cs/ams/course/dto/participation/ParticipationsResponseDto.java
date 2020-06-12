package ro.ubbcluj.cs.ams.course.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Participation;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ParticipationsResponseDto {

    private List<Integer> data;
}
