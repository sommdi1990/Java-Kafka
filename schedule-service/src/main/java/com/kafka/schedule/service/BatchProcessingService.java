package com.kafka.schedule.service;

import com.kafka.schedule.dto.ScheduledTask;
import com.kafka.shared.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class BatchProcessingService {

    private final JobLauncher jobLauncher;
    private final Job dataProcessingJob;

    @Scheduled(fixedRate = 60000) // Run every minute
    @LogExecution
    public void processScheduledTasks() {
        log.info("Starting scheduled task processing");
        // Scheduled task processing will be implemented later
    }

    @Async
    @LogExecution
    public CompletableFuture<Void> executeTaskAsync(ScheduledTask task) {
        return CompletableFuture.runAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();

                log.info("Executing task: {}", task.getTaskName());

                JobParameters jobParameters = new JobParametersBuilder()
                        .addString("taskName", task.getTaskName())
                        .addString("executionTime", LocalDateTime.now().toString())
                        .toJobParameters();

                jobLauncher.run(dataProcessingJob, jobParameters);

                long executionTime = System.currentTimeMillis() - startTime;

                log.info("Task {} completed successfully in {} ms", task.getTaskName(), executionTime);

            } catch (Exception e) {
                log.error("Task {} failed: {}", task.getTaskName(), e.getMessage());
            }
        });
    }

    @LogExecution
    public void processDataBatch(String batchName, int batchSize) {
        log.info("Processing data batch: {} with size: {}", batchName, batchSize);

        // Simulate batch processing
        for (int i = 0; i < batchSize; i++) {
            processDataItem(batchName, i);
        }

        log.info("Batch processing completed for: {}", batchName);
    }

    private void processDataItem(String batchName, int itemIndex) {
        // Simulate data processing
        try {
            Thread.sleep(10); // Simulate processing time
            log.debug("Processed item {} for batch {}", itemIndex, batchName);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Processing interrupted for batch: {}", batchName);
        }
    }
}
