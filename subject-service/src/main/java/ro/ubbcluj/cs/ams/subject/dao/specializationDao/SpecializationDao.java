package ro.ubbcluj.cs.ams.subject.dao.specializationDao;

import ro.ubbcluj.cs.ams.subject.model.tables.records.SpecializationRecord;

public interface SpecializationDao {

    SpecializationRecord findById(Integer id);
}
