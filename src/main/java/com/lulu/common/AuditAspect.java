package com.lulu.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {
    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @AfterReturning("execution(* com.lulu..service..*(..))")
    public void logServiceAccess(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        logger.info("Acceso a método de servicio: {}", method);
    }
}
