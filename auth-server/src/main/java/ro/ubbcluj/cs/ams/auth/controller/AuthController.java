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
import ro.ubbcluj.cs.ams.auth.dto.UserDto;
import ro.ubbcluj.cs.ams.auth.health.HandleServicesHealthRequests;
import ro.ubbcluj.cs.ams.auth.health.ServicesHealthChecker;
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

    private AuthResponse buildAuthResponse(UserDetails userDetails, OAuth2AccessToken accessToken) {

        return AuthResponse.builder()
                .access_token(accessToken.getValue())
                .refresh_token(accessToken.getRefreshToken().getValue())
                .role(userDetails.getAuthorities().iterator().next().getAuthority())
                .build();
    }

    @ExceptionHandler({AuthServiceException.class})
    @ResponseBody
    public ResponseEntity<AuthExceptionType> handleException(AuthServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getHttpStatus());
    }
}
