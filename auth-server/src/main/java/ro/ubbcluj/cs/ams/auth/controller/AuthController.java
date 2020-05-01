package ro.ubbcluj.cs.ams.auth.controller;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import ro.ubbcluj.cs.ams.auth.config.AuthConfiguration;
import ro.ubbcluj.cs.ams.auth.dto.UserDto;
import ro.ubbcluj.cs.ams.auth.service.exception.AuthExceptionType;
import ro.ubbcluj.cs.ams.auth.service.exception.AuthServiceException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthConfiguration authProps;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

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
    public ResponseEntity<OAuth2AccessToken> login(@Valid UserDto userDto, BindingResult result) {

        logger.info("==========login==========");
        logger.info("===========username: {}==========", userDto.getUsername());
        logger.info("===========password: {}==========", userDto.getPassword());

        if (result.hasErrors()) {
            logger.error("==========login failed==========");
            logger.error("Unexpected data!");
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
                .uri("http://auth-service/authentication/oauth/token")
                .header("Authorization", authHeader)
                .body(BodyInserters.fromValue(bodyParams))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(OAuth2AccessToken.class)
                .block();

        logger.info("==========login successful==========");
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @ExceptionHandler({AuthServiceException.class})
    @ResponseBody
    public ResponseEntity<AuthExceptionType> handleException(AuthServiceException exception) {

        return new ResponseEntity<>(exception.getType(), new HttpHeaders(), exception.getHttpStatus());
    }
}
