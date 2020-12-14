package ro.ubbcluj.cs.ams.utils.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class MicroserviceResponseProvider {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Resource
    private HttpServletRequest httpServletRequest;

    //Use this for GET requests
    public <T> T getResponseFromPath(String path, Class<T> responseClass) {

        return sendRequestAndReceiveResponse(path, responseClass, buildWebClientForGetAndDelete().get());
    }

    //Use this for DELETE requests
    public <T> T getDeleteResponseFromPath(String path, Class<T> responseClass) {

        return sendRequestAndReceiveResponse(path, responseClass, buildWebClientForGetAndDelete().delete());
    }

    // Use this method for POST and PUT requests
    public <T, S> T getResponseFromPath(String path, HttpMethod method, Class<T> responseClass, S body) {

        switch (method) {
            case POST:
                return getPostResponseFromPath(path, responseClass, body);
            case PUT:
                return getPutResponseFromPath(path, responseClass, body);
            default:
                throw new IllegalArgumentException(method + "are not allowed here!");
        }
    }

    private <T, S> T getPostResponseFromPath(String path, Class<T> responseClass, S body) {

        return sendRequestAndReceiveResponse(path, responseClass, body, buildWebClientForPostAndPut().post());
    }

    private <T, S> T getPutResponseFromPath(String path, Class<T> responseClass, S body) {

        return sendRequestAndReceiveResponse(path, responseClass, body, buildWebClientForPostAndPut().put());
    }

    private <T> T sendRequestAndReceiveResponse(String path, Class<T> responseClass, WebClient.RequestHeadersUriSpec requestHeadersUriSpec) {

        return requestHeadersUriSpec
                .uri("http://" + path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseClass)
                .block();
    }

    private <T, S> T sendRequestAndReceiveResponse(String path, Class<T> responseClass, S body, WebClient.RequestBodyUriSpec requestBodyUriSpec) {

        return requestBodyUriSpec
                .uri("http://" + path)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(responseClass)
                .block();
    }

    private WebClient buildWebClientForGetAndDelete() {

        String authorizationHeaderValue = getAuthorizationHeaderValue();
        if (authorizationHeaderValue == null)
            return webClientBuilder
                    .build();

        return webClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue())
                .build();
    }

    private WebClient buildWebClientForPostAndPut() {

        String authorizationHeaderValue = getAuthorizationHeaderValue();
        if (authorizationHeaderValue == null)
            return webClientBuilder
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

        return webClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorizationHeaderValue)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String getAuthorizationHeaderValue() {

        return httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    }

}
