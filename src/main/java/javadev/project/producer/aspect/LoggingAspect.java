package javadev.project.producer.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Pointcut that matches all methods in controller package
     */
    @Pointcut("execution(* javadev.project.producer.controller..*(..))")
    public void controllerPointcut() {
    }

    /**
     * Log before any controller method is executed
     * Captures HTTP method, URI, client IP, method name, and arguments
     * 
     * @param joinPoint contains information about the intercepted method
     */
    @Before("controllerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            String timestamp = LocalDateTime.now().format(formatter);
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String clientIp = getClientIpAddress(request);

            logger.info("[REQUEST] {} | {} {} | IP: {} | Method: {}.{}() | Args: {}",
                    timestamp,
                    method,
                    uri,
                    clientIp,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
    }

    /**
     * Log after successful execution of any controller method
     * Records the timestamp, HTTP method, URI, and success status
     * 
     * @param joinPoint contains information about the intercepted method
     * @param result    the return value of the method
     */
    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            String timestamp = LocalDateTime.now().format(formatter);
            String method = request.getMethod();
            String uri = request.getRequestURI();

            logger.info("[RESPONSE] {} | {} {} | Method: {}.{}() | Status: SUCCESS",
                    timestamp,
                    method,
                    uri,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
        }
    }

    /**
     * Log when an exception is thrown from any controller method
     * For business exceptions (ResponseStatusException), only logs the message
     * For unexpected errors, logs full stack trace for debugging
     * 
     * @param joinPoint contains information about the intercepted method
     * @param exception the thrown exception
     */
    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            String timestamp = LocalDateTime.now().format(formatter);
            String method = request.getMethod();
            String uri = request.getRequestURI();

            // Check if this is a business exception (expected) or unexpected error
            if (exception instanceof ResponseStatusException) {
                // Business exception - just log the message without stack trace
                ResponseStatusException rse = (ResponseStatusException) exception;
                logger.warn("[ERROR] {} | {} {} | Method: {}.{}() | Status: {} | Message: {}",
                        timestamp,
                        method,
                        uri,
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        rse.getStatusCode(),
                        rse.getReason());
            } else {
                // Unexpected exception - log with full stack trace
                logger.error("[ERROR] {} | {} {} | Method: {}.{}() | Exception: {} - {}",
                        timestamp,
                        method,
                        uri,
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        exception.getClass().getSimpleName(),
                        exception.getMessage(),
                        exception);
            }
        }
    }

    /**
     * Retrieves the current HTTP servlet request from the request context
     * 
     * @return HttpServletRequest object or null if not available
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extracts the real client IP address from the HTTP request
     * Checks multiple headers to handle proxies and load balancers
     * 
     * @param request the HTTP servlet request
     * @return the client's IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
