package ro.ubbcluj.cs.ams.course.repository.courseCodeRepo;

import ro.ubbcluj.cs.ams.course.model.tables.pojos.CourseCode;
import ro.ubbcluj.cs.ams.course.model.tables.records.CourseCodeRecord;

public interface CourseCodeRepo {

    CourseCodeRecord findByCourseName(String name);

    //return: code (primary key) of CourseCode
    Integer addCourseCode(CourseCode courseCode);
}
