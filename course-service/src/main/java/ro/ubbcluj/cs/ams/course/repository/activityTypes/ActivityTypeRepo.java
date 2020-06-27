package ro.ubbcluj.cs.ams.course.repository.activityTypes;

import ro.ubbcluj.cs.ams.course.model.tables.records.ActivityTypeRecord;

import java.util.List;

public interface ActivityTypeRepo {

    List<ActivityTypeRecord> findActivityTypes();

    ActivityTypeRecord findActivityTypeById(int typeId);
}
