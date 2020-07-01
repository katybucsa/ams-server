package ro.ubbcluj.cs.ams.student.dto.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.student.model.tables.pojos.SGroup;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SGroups {

    private List<SGroup> data;
}
