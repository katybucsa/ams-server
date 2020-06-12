package ro.ubbcluj.cs.ams.attendance.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceInfoReq;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceInfoResponse;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceRequest;
import ro.ubbcluj.cs.ams.attendance.dto.AttendanceResponse;
import ro.ubbcluj.cs.ams.attendance.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.attendance.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.attendance.service.Service;
import ro.ubbcluj.cs.ams.attendance.service.exception.AttendanceExceptionType;
import ro.ubbcluj.cs.ams.attendance.service.exception.AttendanceServiceException;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AttendanceController {

    @Autowired
    Service service;


    private static final Logger LOGGER = LoggerFactory.getLogger(AttendanceController.class);

    @Autowired
    private ServicesHealthChecker servicesHealthChecker;

    @Autowired
    private HandleServicesHealthRequests handleServicesHealthRequests;


    @RequestMapping(value = "/health", method = RequestMethod.POST, params = {"service-name"})
    public void health(@RequestParam(name = "service-name") String serviceName) {

        LOGGER.info("========== Health check from service: {} ", serviceName);

        handleServicesHealthRequests.sendResponseToService(serviceName);
    }

    @RequestMapping(value = "/present", method = RequestMethod.POST, params = {"service-name"})
    public void present(@RequestParam(name = "service-name") String serviceName) {

        LOGGER.info("========== Service {} is alive", serviceName);
        servicesHealthChecker.addService(serviceName);
    }

    @RequestMapping(value = "/running", method = RequestMethod.GET)
    public ResponseEntity running() {

        LOGGER.info("========== Service running ==========");
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "Add attendance info")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = AttendanceExceptionType.class),
            @ApiResponse(code = 400, message = "DUPLICATE_ATTENDANCE_INFO", response = AttendanceExceptionType.class),
    })
    @RequestMapping(value = "/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AttendanceInfoResponse> addAttendanceInfo(Principal principal, @RequestBody AttendanceInfoReq attendanceInfoReq, BindingResult result) {

        if (result.hasErrors())
            throw new AttendanceServiceException("Invalid attendance_info " + attendanceInfoReq, AttendanceExceptionType.INVALID_ATTENDANCE_INFO, HttpStatus.BAD_REQUEST);

        LOGGER.info(principal.getName());
        AttendanceInfoResponse attendanceInfoResponse = service.addAttendanceInfo(attendanceInfoReq, principal.getName());

        return new ResponseEntity<>(attendanceInfoResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Add attendance")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "SUCCESS", response = AttendanceExceptionType.class),
            @ApiResponse(code = 400, message = "INVALID_TIME", response = AttendanceExceptionType.class),
    })
    @RequestMapping(value = "/student", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AttendanceResponse> addAttendance(Principal principal, @RequestBody @Valid AttendanceRequest attendanceRequest, BindingResult result) {

        if (result.hasErrors())
            throw new AttendanceServiceException("Invalid attendance " + attendanceRequest, AttendanceExceptionType.INVALID_ATTENDANCE, HttpStatus.BAD_REQUEST);

        LOGGER.info("++++++ addAttendace with attendance info id : " + attendanceRequest.getAttendanceInfoId() + "++++++++++++++");

        AttendanceResponse attendanceResponse = service.addAttendance(attendanceRequest, principal.getName());

        return new ResponseEntity<>(attendanceResponse, HttpStatus.OK);
    }

    @ExceptionHandler({AttendanceServiceException.class})
    @ResponseBody
    public ResponseEntity<AttendanceExceptionType> handleException(AttendanceServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getHttpStatus());
    }

}
