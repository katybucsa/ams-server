package ro.ubbcluj.cs.ams.assignment.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.retry.Retry;
import reactor.retry.RetryContext;
import ro.ubbcluj.cs.ams.assignment.dto.responses.CpLinkBean;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceException;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceExceptionType;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Objects;

//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Recover;
//import org.springframework.retry.annotation.Retryable;

@Component
public class MicroserviceCall {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Resource
    private HttpServletRequest httpServletRequest;

    private final Logger LOGGER = LogManager.getLogger(MicroserviceCall.class);


    private void handleWebClientResponseException(WebClientResponseException e, String message, AssignmentServiceExceptionType type) {

        loggingWebClientResponseException(e);
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new AssignmentServiceException(e.getMessage(), AssignmentServiceExceptionType.ERROR, e.getStatusCode());
        }
        if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
            throw new AssignmentServiceException(e.getMessage(), AssignmentServiceExceptionType.ERROR, HttpStatus.BAD_GATEWAY);
        }
        throw new AssignmentServiceException(message, type, HttpStatus.NOT_FOUND);
    }

    @Cacheable(cacheNames = "cplinkCache", key = "{#courseId,#activityTypeId,#professorUsername}")
    public CpLinkBean checkIfProfessorTeachesSpecificActivityTypeForASubject(String courseId, int activityTypeId, String professorUsername) {

        return saveCpLinkToCache(courseId, activityTypeId, professorUsername);
    }

    private void loggingWebClientResponseException(WebClientResponseException e) {

        LOGGER.error("========== ERROR LOGGING ==========");
        LOGGER.error("Message: {}", e.getMessage());
        LOGGER.error("Status: {} ", e.getStatusCode());
        LOGGER.error("Code: {} ", e.getStatusCode().value());
        LOGGER.error("========== FINAL ERROR LOGGING ==========");
    }


    @Retryable(value = {WebClientResponseException.class}, maxAttempts = 4, backoff = @Backoff(delay = 6000))
    public CpLinkBean saveCpLinkToCache(String courseId, int activityTypeId, String professorUsername) {

        LOGGER.info("Logging check if a professor teaches specific activity type for a course");
        LOGGER.info("Call course microservice");

        CpLinkBean response = null;
        String path = "http://course-service/course/assign?courseId=" + courseId + "&activityTypeId=" + activityTypeId + "&professorUsername=" + professorUsername;
//        try {
            response = webClientBuilder
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .get()
                    .uri(path)
                    .header("Authorization", httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CpLinkBean.class)
                    .block();
//        } catch (WebClientResponseException e) {
//
//        }
        return response;
    }

    @Recover
    public CpLinkBean recover(WebClientResponseException t) {

        LOGGER.info("Recover");
        throw new AssignmentServiceException("Could not assign grade!", AssignmentServiceExceptionType.ERROR, HttpStatus.SERVICE_UNAVAILABLE);
    }

//    private boolean is5xxServerError(RetryContext<Object> retryContext) {
//        return retryContext.exception() instanceof WebClientResponseException &&
//                ((WebClientResponseException) retryContext.exception()).getStatusCode().is5xxServerError();
//    }

//    @Cacheable(cacheNames = "cplinkCache", key = "{#courseId,#activityTypeId,#professorUsername}")
//    public CpLinkBean getCpLinkBeanFromCache(String courseId, int activityTypeId, String professorUsername, CpLinkBean cpLinkBean) {
//
//        LOGGER.info("========== LOGGING getCpLinkBeanFromCache ==========");
//        return cpLinkBean;
//    }
//
//    @CachePut(cacheNames = "cplinkCache", key = "{#courseId,#activityTypeId,#professorUsername}")
//    public CpLinkBean saveCpLinkBeanToCache(String courseId, int activityTypeId, String professorUsername) {
//
//        LOGGER.info("========== LOGGING saveCpLinkBeanToCache ==========");
//        return CpLinkBean.builder().build();
//    }
}
