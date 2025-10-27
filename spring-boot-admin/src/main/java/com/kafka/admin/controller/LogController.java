package com.kafka.admin.controller;

import com.kafka.shared.dto.ApiResponse;
import com.kafka.shared.dto.SystemLog;
import com.kafka.shared.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final SystemLogRepository systemLogRepository;

    @GetMapping
    public ApiResponse<Page<SystemLog>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String logLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SystemLog> logs;

        if (serviceName != null && startDate != null && endDate != null) {
            logs = systemLogRepository.findByServiceNameAndCreatedAtBetween(serviceName, startDate, endDate, pageable);
        } else if (logLevel != null && startDate != null && endDate != null) {
            logs = systemLogRepository.findByLogLevelAndCreatedAtBetween(logLevel, startDate, endDate, pageable);
        } else {
            logs = systemLogRepository.findAll(pageable);
        }

        return ApiResponse.success("Logs retrieved successfully", logs);
    }

    @GetMapping("/slow-executions")
    public ApiResponse<List<SystemLog>> getSlowExecutions(
            @RequestParam(defaultValue = "1000") Long threshold,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<SystemLog> slowExecutions = systemLogRepository.findSlowExecutions(threshold, startDate, endDate);
        return ApiResponse.success("Slow executions retrieved successfully", slowExecutions);
    }

    @GetMapping("/statistics")
    public ApiResponse<Object> getStatistics(
            @RequestParam(required = false) String serviceName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Long totalLogs = serviceName != null ?
                systemLogRepository.countByServiceNameAndDateRange(serviceName, startDate, endDate) :
                systemLogRepository.countByCreatedAtBetween(startDate, endDate);

        return ApiResponse.success("Statistics retrieved successfully",
                new Object() {
                    public final Long totalLogs = totalLogs;
                    public final String serviceName = serviceName;
                    public final LocalDateTime startDate = startDate;
                    public final LocalDateTime endDate = endDate;
                });
    }
}
