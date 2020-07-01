package ro.ubbcluj.cs.ams.auth.controller;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ro.ubbcluj.cs.ams.auth.config.AuthConfiguration;
import ro.ubbcluj.cs.ams.auth.dto.AuthResponse;
import ro.ubbcluj.cs.ams.auth.dto.RefreshData;
import ro.ubbcluj.cs.ams.auth.dto.UserDetailsDto;
import ro.ubbcluj.cs.ams.auth.dto.UserDto;
import ro.ubbcluj.cs.ams.auth.health.ContextClosedHandler;
import ro.ubbcluj.cs.ams.auth.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.auth.health.ServicesHealthChecker;
import ro.ubbcluj.cs.ams.auth.model.User;
import ro.ubbcluj.cs.ams.auth.service.UserDetailsServiceImpl;
import ro.ubbcluj.cs.ams.auth.service.exception.AuthExceptionType;
import ro.ubbcluj.cs.ams.auth.service.exception.AuthServiceException;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthConfiguration authProps;

    @Autowired
    @Qualifier("WebClientBuilderBean")
    private WebClient.Builder webClientBuilder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ServicesHealthChecker servicesHealthChecker;

    @Autowired
    private HandleServicesHealthRequests handleServicesHealthRequests;

    @Autowired
    private ContextClosedHandler contextClosedHandler;

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

    @RequestMapping(value = "/shutdown", method = RequestMethod.POST)
    public ResponseEntity shutdown() {

        LOGGER.info("========== Shutdown service ==========");

        contextClosedHandler.shutdownContext();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/current")
    public Principal getUser(Principal principal) {
        return principal;
    }

    @RequestMapping(value = "/login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            params = {"username", "password"}
    )
    public ResponseEntity<AuthResponse> login(@Valid UserDto userDto, BindingResult result) {

        LOGGER.info("==========login==========");
        LOGGER.info("===========username: {}==========", userDto.getUsername());
        LOGGER.info("===========password: {}==========", userDto.getPassword());

        if (result.hasErrors()) {
            LOGGER.error("==========login failed==========");
            LOGGER.error("Unexpected data!");
            throw new AuthServiceException("Invalid credentials!", AuthExceptionType.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
        }

        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("grant_type", "password");
        bodyParams.add("username", userDto.getUsername());
        bodyParams.add("password", userDto.getPassword());

        String auth = authProps.getUser() + ":" + authProps.getPass();
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);

        OAuth2AccessToken accessToken = webClientBuilder
                .build()
                .post()
                .uri("http://auth-service/auth/oauth/token")
                .header("Authorization", authHeader)
                .body(BodyInserters.fromValue(bodyParams))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OAuth2AccessToken.class)
                .block();

        UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getUsername());
        System.out.println(userDetails.getAuthorities());

        AuthResponse authResponse = buildAuthResponse(userDetails, accessToken);
        LOGGER.info("==========login successful==========");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }


    @RequestMapping(value = "/refresh",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AuthResponse> refreshToken(@Valid RefreshData refreshData, BindingResult result) {

        if (result.hasErrors()) {
            LOGGER.error("========== invalid refresh data ==========");
            LOGGER.error("Unexpected data!");
            throw new AuthServiceException("Invalid refresh data!", AuthExceptionType.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
        }
        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("grant_type", refreshData.getGrant_type());
        bodyParams.add("refresh_token", refreshData.getRefresh_token());

        String auth = refreshData.getClientId() + ":" + refreshData.getClientSecret();
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);

        OAuth2AccessToken accessToken = webClientBuilder
                .build()
                .post()
                .uri("http://auth-service/auth/oauth/token")
                .header("Authorization", authHeader)
                .body(BodyInserters.fromValue(bodyParams))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OAuth2AccessToken.class)
                .block();

        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshData.getUsername());
        System.out.println(userDetails.getAuthorities());

        AuthResponse authResponse = buildAuthResponse(userDetails, accessToken);
        LOGGER.info("========== token refreshed successfully ==========");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/users",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            params = {"username"}
    )
    public ResponseEntity<UserDetailsDto> findUserDetailsByUsername(@RequestParam(name = "username") String username) {

        LOGGER.info("========== LOGGING findUserDetailsByUsername ==========");
        LOGGER.info("Username {}", username);

        User user = userDetailsService.findUserByUsername(username);
        UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .build();

        LOGGER.info("========== SUCCESSFULLY LOGGING findUserDetailsByUsername ==========");
        return new ResponseEntity<>(userDetailsDto, HttpStatus.OK);
    }

    private AuthResponse buildAuthResponse(UserDetails userDetails, OAuth2AccessToken accessToken) {

        return AuthResponse.builder()
                .user(userDetails.getUsername())
                .access_token(accessToken.getValue())
                .refresh_token(accessToken.getRefreshToken().getValue())
                .role(userDetails.getAuthorities().iterator().next().getAuthority())
                .build();
    }

    @ExceptionHandler({AuthServiceException.class})
//    @ResponseBody
    public ResponseEntity<AuthExceptionType> handleException(AuthServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getHttpStatus());
    }
}
