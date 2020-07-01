package ro.ubbcluj.cs.ams.notification.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EnrolledStudents {

    private List<String> data;
}
