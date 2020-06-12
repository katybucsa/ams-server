package ro.ubbcluj.cs.ams.course.service;

import ro.ubbcluj.cs.ams.course.dto.course.CourseDtoRequest;
import ro.ubbcluj.cs.ams.course.dto.course.CourseDtoResponse;
import ro.ubbcluj.cs.ams.course.dto.course.CoursesDto;
import ro.ubbcluj.cs.ams.course.dto.cplink.CpLinkResponseDto;
import ro.ubbcluj.cs.ams.course.dto.participation.ParticipationsResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostRequestDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostsResponseDto;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Participation;

public interface Service {

    CourseDtoResponse addCourse(CourseDtoRequest course);

    CpLinkResponseDto findCpLink(String courseId, int type, String professor);

    CoursesDto findAllCoursesByProfessorUsername(String professorUsername);

    PostResponseDto addPost(PostRequestDto post, String professorUsername);

    PostsResponseDto findPostsByCourseId(String courseId);

    Participation addOrDeleteParticipation(Participation participation);

    ParticipationsResponseDto findParticipationsByUserId(String userId);
}
