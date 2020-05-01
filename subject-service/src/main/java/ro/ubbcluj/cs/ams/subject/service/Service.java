package ro.ubbcluj.cs.ams.subject.service;

import ro.ubbcluj.cs.ams.subject.dto.SpLinkResponseDto;
import ro.ubbcluj.cs.ams.subject.dto.SubjectDtoRequest;
import ro.ubbcluj.cs.ams.subject.dto.SubjectDtoResponse;

public interface Service {

    SubjectDtoResponse addSubject(SubjectDtoRequest subject);

    SpLinkResponseDto findSpLink(String subjectId, int type, String professor);
}
