package ro.ubbcluj.cs.ams.assignment.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ro.ubbcluj.cs.ams.assignment.dto.GradeDto;
import ro.ubbcluj.cs.ams.assignment.dto.GradeResponseDto;
import ro.ubbcluj.cs.ams.assignment.service.Service;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceException;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceExceptionType;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AssignmentController {

    @Autowired
    private Service service;

    @Autowired
    private MicroserviceCall microserviceCall;

    private final Logger logger = LogManager.getLogger(AssignmentController.class);

    @ApiOperation(value = "Assign given grade to the specified student")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = GradeResponseDto.class),
            @ApiResponse(code = 400, message = "INVALID_GRADE", response = AssignmentServiceExceptionType.class),
    })
    @RequestMapping(value = "/grades", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GradeResponseDto> assignGrade(@RequestBody @Valid GradeDto gradeDto, BindingResult result, Principal principal) {

        logger.info("========== LOGGING assignGrade ==========");
        logger.info("GradeDto {}", gradeDto);
        if (result.hasErrors()) {
            logger.error("Invalid json object!");
            throw new AssignmentServiceException("Invalid grade" + gradeDto, AssignmentServiceExceptionType.INVALID_GRADE, HttpStatus.BAD_REQUEST);
        }

        String teacherUsername = principal.getName();
        microserviceCall.checkIfProfessorTeachesSpecificActivityTypeForASubject(gradeDto.getSubjectId(), gradeDto.getTypeId(), teacherUsername);
//        microserviceCall.checkIfStudentExistsAndIsEnrolledIntoSubject(gradeDto.getStudent(), gradeDto.getSubjectId(), "assignGrade");
        GradeResponseDto gradeResponseDto = service.addGrade(gradeDto, teacherUsername);

        logger.info("========== SUCCESSFUL LOGGING assignGrade ==========");
        return new ResponseEntity<>(gradeResponseDto, HttpStatus.OK);
    }
}
