package com.kafka.shared.aspect;

import com.kafka.shared.dto.SystemLog;
import com.kafka.shared.repository.SystemLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final SystemLogRepository systemLogRepository;

    @Around("@annotation(com.kafka.shared.annotation.LogExecution)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        SystemLog.SystemLogBuilder logBuilder = SystemLog.builder()
                .serviceName(getServiceName())
                .className(className)
                .methodName(methodName)
                .logLevel("INFO")
                .createdAt(LocalDateTime.now());

        try {
            // Extract request information if available
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                logBuilder.ipAddress(getClientIpAddress(request))
                        .userAgent(request.getHeader("User-Agent"))
                        .requestId(request.getHeader("X-Request-ID"));
            }

            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;
            logBuilder.executionTimeMs(executionTime)
                    .message("Method executed successfully");

            systemLogRepository.save(logBuilder.build());

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logBuilder.executionTimeMs(executionTime)
                    .logLevel("ERROR")
                    .message("Method execution failed: " + e.getMessage())
                    .exceptionStack(getStackTrace(e));

            systemLogRepository.save(logBuilder.build());

            throw e;
        }
    }

    private String getServiceName() {
        return System.getProperty("spring.application.name", "unknown-service");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
