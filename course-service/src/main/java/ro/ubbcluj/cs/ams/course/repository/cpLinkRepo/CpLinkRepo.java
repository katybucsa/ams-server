package ro.ubbcluj.cs.ams.course.repository.cpLinkRepo;

import ro.ubbcluj.cs.ams.course.model.tables.records.CpLinkRecord;

import java.util.List;

public interface CpLinkRepo {

    CpLinkRecord findCpLink(String courseId, int typeId, String professor);

    List<CpLinkRecord> findAllLinksByProfessorUsername(String professorUsername);
}
