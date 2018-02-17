package com.yyy.server.frontend;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class RedirectOnResourceNotFoundException {

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public Object handleStaticResourceNotFound(final NoHandlerFoundException ex, HttpServletRequest req, RedirectAttributes redirectAttributes) {
        if (req.getRequestURI().startsWith("/api") || req.getRequestURI().contains(".")) {
            req.setAttribute("javax.servlet.error.status_code", 404);
            return "forward:/error";
        } else {
            return "forward:/";
        }
    }

}