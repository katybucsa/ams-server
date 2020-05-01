package ro.ubbcluj.cs.ams.assignment.repository;

import ro.ubbcluj.cs.ams.assignment.model.tables.pojos.Grade;
import ro.ubbcluj.cs.ams.assignment.model.tables.records.GradeRecord;

public interface AssignmentRepo {

    GradeRecord addGrade(Grade grade);
}
