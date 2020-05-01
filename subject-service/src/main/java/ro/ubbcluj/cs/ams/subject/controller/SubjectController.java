package ro.ubbcluj.cs.ams.subject.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ro.ubbcluj.cs.ams.subject.dto.SpLinkResponseDto;
import ro.ubbcluj.cs.ams.subject.dto.SubjectDtoRequest;
import ro.ubbcluj.cs.ams.subject.dto.SubjectDtoResponse;
import ro.ubbcluj.cs.ams.subject.model.tables.pojos.Subject;
import ro.ubbcluj.cs.ams.subject.service.Service;
import ro.ubbcluj.cs.ams.subject.service.exception.SubjectExceptionType;
import ro.ubbcluj.cs.ams.subject.service.exception.SubjectServiceException;

import javax.validation.Valid;

@RestController
public class SubjectController {

    private final Logger logger = LogManager.getLogger(SubjectController.class);

    @Autowired
    private Service service;

    /**
     * URL : http://localhost:8080/subject/
     *
     * @param subject
     * @param result
     * @return
     */
    @ApiOperation(value = "Add given subject")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = Subject.class),
            @ApiResponse(code = 400, message = "DUPLICATE_SUBJECT", response = SubjectExceptionType.class),
    })
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SubjectDtoResponse> addSubject(@RequestBody @Valid SubjectDtoRequest subject, BindingResult result) {

        logger.info("+++++++++LOGGING addSubject+++++++++");
        loggingSubject(subject);

        if (result.hasErrors())
            throw new SubjectServiceException("Invalid subject " + subject, SubjectExceptionType.INVALID_SUBJECT, HttpStatus.BAD_REQUEST);

        SubjectDtoResponse subjectDtoResponse = service.addSubject(subject);

        logger.info("+++++++++SUCCESSFUL LOGGING addSubject+++++++++");
        return new ResponseEntity<>(subjectDtoResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Get SPLink by id - subject id, activity id, professor username")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = SpLinkResponseDto.class),
            @ApiResponse(code = 404, message = "NOT FOUND", response = SubjectExceptionType.class)
    })
    @RequestMapping(value = "/assign", method = RequestMethod.GET, params = {"subjectId", "activityTypeId", "professorUsername"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpLinkResponseDto> findSpLink(@RequestParam(name = "subjectId") String subjectId, @RequestParam(name = "activityTypeId") Integer activityTypeId, @RequestParam(name = "professorUsername") String professorUsername) {

        logger.info("========== LOGGING findSpLink ==========");
        logger.info("SubjectId {}, ActivityTypeId {}, ProfessorUsername {}", subjectId, activityTypeId, professorUsername);

        SpLinkResponseDto spLinkResponseDto = service.findSpLink(subjectId, activityTypeId, professorUsername);
        if (spLinkResponseDto == null)
            throw new SubjectServiceException("Assignment not found", SubjectExceptionType.ASSIGNMENT_NOT_FOUND, HttpStatus.NOT_FOUND);

        logger.info("========== SUCCESSFUL LOGGING findSpLink ==========");
        return new ResponseEntity<>(spLinkResponseDto, HttpStatus.OK);
    }

    private void loggingSubject(SubjectDtoRequest subject) {

        logger.info("Subject name: {}", subject.getName());
        logger.info("Subject credits: {}", subject.getCredits());
        logger.info("Subject specialization: {}", subject.getSpecId());
        logger.info("Subject year: {}", subject.getYear());
    }

    @ExceptionHandler({SubjectServiceException.class})
    @ResponseBody
    public ResponseEntity<SubjectExceptionType> handleException(SubjectServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getHttpStatus());
    }
}
