package ro.ubbcluj.cs.ams.subject.dao.subjectDao;

import ro.ubbcluj.cs.ams.subject.model.tables.pojos.Subject;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SubjectRecord;

public interface SubjectDao {

    SubjectRecord addSubject(Subject subject);
}
