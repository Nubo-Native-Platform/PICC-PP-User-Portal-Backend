package com.nnp.dashboard.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClientWrapper {
    
    private final RestClient restClient;
    
    public HttpClientWrapper(RestClient restClient) {
        this.restClient = restClient;
    }
    
    public static HttpClientWrapper create(String baseUrl) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        return new HttpClientWrapper(restClient);
    }
    
    public RequestBuilder get(String uri) {
        return new RequestBuilder(this, "GET", uri);
    }
    
    public RequestBuilder post(String uri) {
        return new RequestBuilder(this, "POST", uri);
    }
    
    public RequestBuilder put(String uri) {
        return new RequestBuilder(this, "PUT", uri);
    }
    
    public RequestBuilder delete(String uri) {
        return new RequestBuilder(this, "DELETE", uri);
    }
    
    public static class RequestBuilder {
        private final HttpClientWrapper wrapper;
        private final String method;
        private final String uri;
        private final Map<String, String> headers = new HashMap<>();
        private final Map<String, String> queryParams = new HashMap<>();
        private Object requestBody;
        
        RequestBuilder(HttpClientWrapper wrapper, String method, String uri) {
            this.wrapper = wrapper;
            this.method = method;
            this.uri = uri;
        }
        
        public RequestBuilder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }
        
        public RequestBuilder authorization(String token) {
            return header("Authorization", "Bearer " + token);
        }
        
        public RequestBuilder basicAuth(String username, String password) {
            String credentials = java.util.Base64.getEncoder()
                    .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            return header("Authorization", "Basic " + credentials);
        }
        
        public RequestBuilder contentType(String contentType) {
            return header("Content-Type", contentType);
        }
        
        public RequestBuilder queryParam(String name, String value) {
            this.queryParams.put(name, value);
            return this;
        }
        
        public RequestBuilder body(Object body) {
            this.requestBody = body;
            return this;
        }
        
        public <T> T execute(Class<T> responseType) throws RestClientException {
            URI safeUri = buildSafeUri();
            
            switch (method) {
                case "GET":
                    return executeGet(safeUri, responseType);
                case "POST":
                    return executePost(safeUri, responseType);
                case "PUT":
                    return executePut(safeUri, responseType);
                case "DELETE":
                    executeDelete(safeUri);
                    return null;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }
        }
        
        public void execute() throws RestClientException {
            execute(Void.class);
        }
        
        private String buildUri() {
            if (queryParams.isEmpty()) {
                return uri;
            }
            
            StringBuilder uriBuilder = new StringBuilder(uri);
            uriBuilder.append("?");
            queryParams.entrySet().forEach(entry -> 
                uriBuilder.append(entry.getKey())
                         .append("=")
                         .append(java.net.URLEncoder.encode(entry.getValue(), java.nio.charset.StandardCharsets.UTF_8))
                         .append("&")
            );
            uriBuilder.deleteCharAt(uriBuilder.length() - 1);
            return uriBuilder.toString();
        }
        
        private URI buildSafeUri() {
            return URI.create(buildUri());
        }
        
        private <T> T executeGet(URI fullUri, Class<T> responseType) {
            RestClient.RequestHeadersSpec<?> request = wrapper.restClient.get().uri(fullUri);
            headers.forEach(request::header);
            
            ResponseEntity<T> response = request.retrieve().toEntity(responseType);
            return response.getBody();
        }
        
        private <T> T executePost(URI fullUri, Class<T> responseType) {
            RestClient.RequestBodySpec request = wrapper.restClient.post().uri(fullUri);
            headers.forEach(request::header);
            
            if (requestBody != null) {
                request.body(requestBody);
            }
            
            ResponseEntity<T> response = request.retrieve().toEntity(responseType);
            return response.getBody();
        }
        
        private <T> T executePut(URI fullUri, Class<T> responseType) {
            RestClient.RequestBodySpec request = wrapper.restClient.put().uri(fullUri);
            headers.forEach(request::header);
            
            if (requestBody != null) {
                request.body(requestBody);
            }
            
            ResponseEntity<T> response = request.retrieve().toEntity(responseType);
            return response.getBody();
        }
        
        private void executeDelete(URI fullUri) {
            RestClient.RequestHeadersSpec<?> request = wrapper.restClient.delete().uri(fullUri);
            headers.forEach(request::header);
            request.retrieve().toBodilessEntity();
        }
    }
}