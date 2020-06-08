package ro.ubbcluj.cs.ams.course.dto;

import org.mapstruct.Mapper;
import ro.ubbcluj.cs.ams.course.dto.course.CourseDtoResponse;
import ro.ubbcluj.cs.ams.course.dto.cplink.CpLinkResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostRequestDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostResponseDto;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Post;
import ro.ubbcluj.cs.ams.course.model.tables.records.CourseRecord;
import ro.ubbcluj.cs.ams.course.model.tables.records.CpLinkRecord;
import ro.ubbcluj.cs.ams.course.model.tables.records.PostRecord;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Mappers {

    CpLinkResponseDto cpLinkRecordToCpLinkResponseDto(CpLinkRecord spLinkRecord);

    List<CourseDtoResponse> coursesRecordToCourseDtoResponse(List<CourseRecord> courses);

    PostResponseDto postRecordToPostResponseDto(PostRecord postRecord);

    Post postRequestDtoToPost(PostRequestDto post);

    List<PostResponseDto> postsRecordsToPostsResponseDto(List<PostRecord> postRecords);
}
