package com.npcgo.zuul.provider;

import com.netflix.hystrix.exception.HystrixTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ApiFallbackProvider implements FallbackProvider {
    @Override
    public String getRoute() {
        return "spring-snow-eureka-client";//该Provider应用的Route ID，例如：spring-snow-eureka-client，如果设置为 * ，那就对所有路由生效
    }

    //快速回退失败/响应，即处理异常并返回对应输出/响应内容。route：发生异常的RouteID，cause：触发快速回退/失败的异常/错误
    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        log.warn(String.format("route:%s,exceptionType:%s,stackTrace:%s", route, cause.getClass().getName(), cause.getStackTrace()));

        String message = "";
        if (cause instanceof HystrixTimeoutException) {
            message = "Timeout";
        } else {
            message = "Service exception";
        }
        return fallbackResponse(message);
    }

    //Spring提供的HttpResponse接口。可以通过实现该接口自定义Http status、body、header
    public ClientHttpResponse fallbackResponse(String message) {

        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "OK";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                String bodyText = String.format("{\"code\": 999,\"message\": \"Service unavailable:%s\"}", message);
                return new ByteArrayInputStream(bodyText.getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };

    }
}
