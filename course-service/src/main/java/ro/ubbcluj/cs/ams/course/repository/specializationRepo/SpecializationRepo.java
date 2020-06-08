package ro.ubbcluj.cs.ams.course.repository.specializationRepo;


import ro.ubbcluj.cs.ams.course.model.tables.records.SpecializationRecord;

public interface SpecializationRepo {

    SpecializationRecord findById(Integer id);
}
