package ro.ubbcluj.cs.ams.course.repository.event;

import ro.ubbcluj.cs.ams.course.model.tables.pojos.Event;
import ro.ubbcluj.cs.ams.course.model.tables.records.EventRecord;

public interface EventRepo {

    EventRecord addEvent(Event event);

    EventRecord findById(int id);
}
