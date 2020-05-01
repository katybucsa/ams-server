package ro.ubbcluj.cs.ams.subject.dao.subjectCodeDao;

import ro.ubbcluj.cs.ams.subject.model.tables.pojos.SubjectCode;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SubjectCodeRecord;

public interface SubjectCodeDao {

    SubjectCodeRecord findBySubjectName(String name);

    //return: code (primary key) of SubjectCode
    Integer addSubjectCode(SubjectCode subjectCode);
}
