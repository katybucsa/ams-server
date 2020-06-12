package ro.ubbcluj.cs.ams.course.repository.participationRepo;

import ro.ubbcluj.cs.ams.course.model.tables.pojos.Participation;
import ro.ubbcluj.cs.ams.course.model.tables.records.ParticipationRecord;

import java.util.List;

public interface ParticipationRepo {

    int addOrDeleteParticipation(Participation participation);

    List<ParticipationRecord> findAllByUserId(String userId);

    List<ParticipationRecord> findAllByEventId(int eventId);
}
