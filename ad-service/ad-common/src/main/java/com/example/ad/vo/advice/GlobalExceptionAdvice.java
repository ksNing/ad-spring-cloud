package com.example.ad.vo.advice;

import com.example.ad.vo.exception.AdException;
import com.example.ad.vo.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(value = AdException.class)
    public CommonResponse<String> handlerAdException(HttpServletRequest request, AdException e) {
        CommonResponse<String> response = new CommonResponse<>(-1,"bussiness error");
        response.setData(e.getMessage());
        return response;
    }
}
