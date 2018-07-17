package de.dhbw.karlsruhe.turniere.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if(statusCode == HttpStatus.BAD_REQUEST.value()) {
                return "error400";
            }
            if(statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error403";
            }
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error404";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error500";
            }
            /*if(statusCode == HttpStatus.I_AM_A_TEAPOT.value()) {
                return "teapot";
            }*/
            else {
                return "error";
            }
        }
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
