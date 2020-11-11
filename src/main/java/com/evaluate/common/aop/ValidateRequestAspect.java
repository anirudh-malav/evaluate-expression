package com.evaluate.common.aop;

import com.evaluate.common.annotations.ValidateRequest;
import com.evaluate.common.constants.Constants;
import com.evaluate.common.helper.APIResponse;
import com.evaluate.model.UserDetails;
import com.evaluate.service.impl.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Order(0)
@Component
public class ValidateRequestAspect {
    private UserService userService;

    @Autowired
    public ValidateRequestAspect(UserService userService) {
        this.userService = userService;
    }

    @Around("@annotation(setServletRequest)")
    public Object setMDC(ProceedingJoinPoint joinPoint, ValidateRequest setServletRequest) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        String userEmail = request.getHeader(Constants.Header.USER_EMAIL);
        String userPassword = request.getHeader(Constants.Header.USER_PASSWORD);

        if (StringUtils.isEmpty(userEmail) || StringUtils.isEmpty(userPassword)) {
            return APIResponse.renderFailure("Missing request headers", 401, HttpStatus.UNAUTHORIZED);
        }
        validateRequest(userEmail, userPassword);
        return joinPoint.proceed();
    }

    private UserDetails validateRequest(String userEmail, String userPassword) {
        return userService.validateUser(userEmail, userPassword);
    }

}
