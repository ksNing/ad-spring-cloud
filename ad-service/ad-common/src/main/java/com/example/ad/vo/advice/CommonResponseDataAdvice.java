package com.example.ad.vo.advice;

import com.example.ad.vo.annotation.IgnoreResponseAdvice;
import com.example.ad.vo.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice {
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter, Class aClass) {

        if(methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        if(methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)) {
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    @SuppressWarnings("all")
    public Object beforeBodyWrite(@Nullable Object o,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        CommonResponse<Object> response = new CommonResponse<>(0,"");
        if(null == o) {
            return response;
        } else if(o instanceof CommonResponse) {
            response = (CommonResponse<Object>) o;
        } else {
            response.setData(o);
        }
        return response;
    }
}
