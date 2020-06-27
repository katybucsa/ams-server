package ro.ubbcluj.cs.ams.student.repository.groupRepo;

import ro.ubbcluj.cs.ams.student.model.tables.records.SGroupRecord;

import java.util.List;

public interface GroupRepo {

    List<SGroupRecord> findAllGroupsBySpecId(int specId);

    SGroupRecord findById(Integer groupId);
}
