package ro.ubbcluj.cs.ams.course.repository.courseRepo;

import ro.ubbcluj.cs.ams.course.model.tables.pojos.Course;
import ro.ubbcluj.cs.ams.course.model.tables.records.CourseRecord;

import java.util.List;

public interface CourseRepo {

    CourseRecord addCourse(Course course);

    List<CourseRecord> findAllCoursesByIds(List<String> coursesIds);

    CourseRecord findById(String id);
}
