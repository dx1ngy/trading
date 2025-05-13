package com.dx1ngy.core.advice;


import cn.dev33.satoken.exception.SaTokenException;
import com.dx1ngy.core.Result;
import com.dx1ngy.core.ResultCode;
import com.dx1ngy.core.Results;
import com.dx1ngy.core.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result handleBaseException(HttpServletRequest request, BaseException e) {
        log.error("BaseException=[code={},message={},data={}]", e.getCode(), e.getMessage(), e.getData(), e);
        return Results.error(e);
    }

    @ExceptionHandler
    public Result handleMethodArgumentNotValidException(HttpServletRequest request,
                                                        MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException=[code={},message={}]", ResultCode.PARAM_ERROR.getCode(),
                ResultCode.PARAM_ERROR.getMessage(), e);
        return Results.error(ResultCode.PARAM_ERROR);
    }

    @ExceptionHandler
    public Result handleBindException(HttpServletRequest request, BindException e) {
        log.error("BindException=[code={},message={}]", ResultCode.PARAM_ERROR.getCode(),
                ResultCode.PARAM_ERROR.getMessage(), e);
        return Results.error(ResultCode.PARAM_ERROR);
    }

    @ExceptionHandler
    public Result handleConstraintViolationException(HttpServletRequest request, ConstraintViolationException e) {
        log.error("ConstraintViolationException=[code={},message={}]", ResultCode.PARAM_ERROR.getCode(),
                ResultCode.PARAM_ERROR.getMessage(), e);
        return Results.error(ResultCode.PARAM_ERROR);
    }

    @ExceptionHandler
    public Result handleSaTokenException(HttpServletRequest request, SaTokenException e) {
        log.error("SaTokenException=[code={},message={}]", ResultCode.TOKEN_ERROR.getCode(), e.getMessage(), e);
        return Results.error(ResultCode.TOKEN_ERROR.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler
    public Result handleException(HttpServletRequest request, Exception e) {
        log.error("Exception=[code={},message={}]", ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage(), e);
        return Results.error(ResultCode.ERROR);
    }

}
