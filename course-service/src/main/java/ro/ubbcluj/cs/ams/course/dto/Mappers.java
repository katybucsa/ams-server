package ro.ubbcluj.cs.ams.course.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ro.ubbcluj.cs.ams.course.dto.course.CourseNameResponse;
import ro.ubbcluj.cs.ams.course.dto.course.CourseResponseDto;
import ro.ubbcluj.cs.ams.course.dto.cplink.CpLinkResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostRequestDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostResponseDto;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.ActivityType;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Event;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Participation;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Post;
import ro.ubbcluj.cs.ams.course.model.tables.records.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Mappers {

    CpLinkResponseDto cpLinkRecordToCpLinkResponseDto(CpLinkRecord spLinkRecord);

    List<CourseResponseDto> coursesRecordToCourseDtoResponse(List<CourseRecord> courses);

    @Mapping(target = "date", ignore = true)
    PostResponseDto postRecordToPostResponseDto(PostRecord postRecord);

    Post postRequestDtoToPost(PostRequestDto post);

    List<PostResponseDto> postsRecordsToPostsResponseDto(List<PostRecord> postRecords);

    Event eventRecordToEvent(EventRecord eventRecord);

    List<Participation> participationRecordToParticipation(List<ParticipationRecord> participationRecords);

    List<ActivityType> activityTypesRecordToActivityTypes(List<ActivityTypeRecord> activityTypes);

    CourseNameResponse courseRecordToCourseNameResponse(CourseRecord courseRecord);

    ActivityType activityTypeRecordToActivityType(ActivityTypeRecord activityTypeById);
}
