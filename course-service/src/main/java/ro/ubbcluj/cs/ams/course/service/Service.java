package ro.ubbcluj.cs.ams.course.service;

import ro.ubbcluj.cs.ams.course.dto.ActivityTypes;
import ro.ubbcluj.cs.ams.course.dto.course.CourseNameResponse;
import ro.ubbcluj.cs.ams.course.dto.course.CourseDtoRequest;
import ro.ubbcluj.cs.ams.course.dto.course.CourseResponseDto;
import ro.ubbcluj.cs.ams.course.dto.course.CoursesDto;
import ro.ubbcluj.cs.ams.course.dto.cplink.CpLinkResponseDto;
import ro.ubbcluj.cs.ams.course.dto.participation.ParticipantsResponseDto;
import ro.ubbcluj.cs.ams.course.dto.participation.ParticipationsResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostRequestDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostsResponseDto;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.ActivityType;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Participation;

public interface Service {

    CourseResponseDto addCourse(CourseDtoRequest course);

    CpLinkResponseDto findCpLink(String courseId, int type, String professor);

    CoursesDto findAllCoursesByProfessorUsername(String professorUsername);

    PostResponseDto addPost(PostRequestDto post, String professorUsername);

    PostsResponseDto findPostsByCourseId(String courseId);

    Participation addOrDeleteParticipation(Participation participation, String auth);

    ParticipationsResponseDto findParticipationsByUserId(String userId);

    ActivityTypes findAllActivityTypes();

    CourseNameResponse findCourseById(String courseId);

    ActivityType findActivityTypeById(int typeId);

    ParticipantsResponseDto findEventParticipants(Integer postId, String auth);
}
