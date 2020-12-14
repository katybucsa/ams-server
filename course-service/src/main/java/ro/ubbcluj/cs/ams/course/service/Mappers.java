package ro.ubbcluj.cs.ams.course.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ro.ubbcluj.cs.ams.course.dto.course.CourseNameResponse;
import ro.ubbcluj.cs.ams.course.dto.course.CourseResponseDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostRequestDto;
import ro.ubbcluj.cs.ams.course.dto.post.PostResponseDto;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.ActivityType;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.CpLink;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Event;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Post;
import ro.ubbcluj.cs.ams.course.model.tables.records.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface Mappers {

    CpLink cpLinkRecordToCpLink(CpLinkRecord spLinkRecord);

    List<CourseResponseDto> coursesRecordToCourseDtoResponse(List<CourseRecord> courses);

    @Mapping(target = "date", ignore = true)
    PostResponseDto postRecordToPostResponseDto(PostRecord postRecord);

    Post postRequestDtoToPost(PostRequestDto post);

    Event eventRecordToEvent(EventRecord eventRecord);

    List<ActivityType> activityTypesRecordToActivityTypes(List<ActivityTypeRecord> activityTypes);

    CourseNameResponse courseRecordToCourseNameResponse(CourseRecord courseRecord);

    ActivityType activityTypeRecordToActivityType(ActivityTypeRecord activityTypeById);
}
