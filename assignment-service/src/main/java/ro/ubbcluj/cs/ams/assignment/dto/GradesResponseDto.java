package ro.ubbcluj.cs.ams.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GradesResponseDto {

    private List<GradeResponseDto> data;
}
