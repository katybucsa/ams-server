package ro.ubbcluj.cs.ams.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.ActivityType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ActivityTypes {

    private List<ActivityType> data;
}
