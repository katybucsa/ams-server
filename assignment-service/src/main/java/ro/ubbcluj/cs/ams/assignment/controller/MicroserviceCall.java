package ro.ubbcluj.cs.ams.assignment.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Recover;
//import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ro.ubbcluj.cs.ams.assignment.dto.SpLinkBean;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceException;
import ro.ubbcluj.cs.ams.assignment.service.exception.AssignmentServiceExceptionType;

import java.util.Objects;

@Component
@RibbonClient(name = "subject-service")
public class MicroserviceCall {

    @Autowired
    private WebClient.Builder webClientBuilder;

//    @Autowired
//    private RedisTemplate redisTemplate;

    private final Logger logger = LogManager.getLogger(MicroserviceCall.class);


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

    //        @SentinelResource(value = "linkVerification",  fallback = "linkVerificationFallback")
//    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 3000))
//    @HystrixCommand(fallbackMethod = "linkVerificationFallback")
//    @CachePut(value = "splinkCache", key = "{#subjectId,#activityTypeId,#professorUsername}")
    public SpLinkBean checkIfProfessorTeachesSpecificActivityTypeForASubject(String subjectId, int activityTypeId, String professorUsername) {

        logger.info("Logging check if a professor teaches specific activity type for a subject");
        logger.info("Call ro.ubbcluj.cs.ams.course microservice");
        SpLinkBean response = null;
        try {
            String path = "http://course-service/course/assign?subjectId=" + subjectId + "&activityTypeId=" + activityTypeId + "&professorUsername=" + professorUsername;
            response = webClientBuilder
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .get()
                    .uri(path)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(SpLinkBean.class)
                    .block();
            if (Objects.isNull(response)) {
                throw new AssignmentServiceException("There is no professor with username " + professorUsername + " that teaches activity type with id " + activityTypeId + " for subject with id " + subjectId + "!\n",
                        AssignmentServiceExceptionType.TEACHER_ACTIVITY_TYPE_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } catch (WebClientResponseException e) {
            logger.error("checkIfProfessorTeachesSpecificActivityTypeForASubject: call to subject microservice failed");
            handleWebClientResponseException(e,
                    "There is no teacher with username " + professorUsername + " that teaches activity type with id " + activityTypeId + " for subject with id " + subjectId + "!\n",
                    AssignmentServiceExceptionType.TEACHER_ACTIVITY_TYPE_NOT_FOUND);
        }
        return response;
    }

    private void loggingWebClientResponseException(WebClientResponseException e) {

        logger.error("========== ERROR LOGGING ==========");
        logger.error("Message: {}", e.getMessage());
        logger.error("Status: {} ", e.getStatusCode());
        logger.error("Code: {} ", e.getStatusCode().value());
        logger.error("========== FINAL ERROR LOGGING ==========");
    }

//    @Cacheable(value = "splinkCache", key = "{#subjectId,#activityTypeId,#professorUsername}")
//    public boolean linkVerificationFallback(String subjectId, int activityTypeId, String professorUsername) {
//
//        System.out.println("Fallback subject service call");
//        redisTemplate.opsForHash().
//        return true;
//    }

//    @Recover
//    public boolean recovern(Throwable t) {
//        logger.info("Bar.recover");
//        return false;
//    }


//    @PostConstruct
//    private static void initDegradeRule() {
//        List<DegradeRule> rules = new ArrayList<>();
//        DegradeRule rule = new DegradeRule("linkVerification");
//        // set threshold rt, 10 ms
//        rule.setCount(10);
//        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
//        rule.setTimeWindow(10);
//        rules.add(rule);
//        DegradeRuleManager.loadRules(rules);
//    }
}
