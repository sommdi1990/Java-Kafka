package com.kafka.schedule.service;

import com.kafka.shared.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThreadManagementService {

    private final ThreadPoolTaskExecutor taskExecutor;

    @Async("taskExecutor")
    @LogExecution
    public CompletableFuture<Void> executeTaskInThreadPool(Runnable task, String taskName) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("Executing task {} in thread: {}", taskName, Thread.currentThread().getName());
                task.run();
                log.info("Task {} completed successfully", taskName);
            } catch (Exception e) {
                log.error("Task {} failed: {}", taskName, e.getMessage());
                throw new RuntimeException("Task execution failed", e);
            }
        }, taskExecutor);
    }

    @LogExecution
    public void executeParallelTasks(List<Runnable> tasks, String batchName) {
        log.info("Executing {} tasks in parallel for batch: {}", tasks.size(), batchName);

        CompletableFuture<?>[] futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(task, taskExecutor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures)
                .thenRun(() -> log.info("All tasks completed for batch: {}", batchName))
                .exceptionally(throwable -> {
                    log.error("Some tasks failed for batch: {}", batchName, throwable);
                    return null;
                });
    }

    @LogExecution
    public void executeWithVirtualThreads(List<Runnable> tasks, String batchName) {
        log.info("Executing {} tasks with virtual threads for batch: {}", tasks.size(), batchName);

        List<Thread> virtualThreads = tasks.stream()
                .map(task -> Thread.ofVirtual()
                        .name("virtual-thread-" + System.currentTimeMillis())
                        .start(task))
                .toList();

        // Wait for all virtual threads to complete
        virtualThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted for batch: {}", batchName);
            }
        });

        log.info("All virtual threads completed for batch: {}", batchName);
    }

    @LogExecution
    public void executeWithThreadPool(List<Runnable> tasks, String batchName) {
        log.info("Executing {} tasks with thread pool for batch: {}", tasks.size(), batchName);

        CompletableFuture<?>[] futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(task, taskExecutor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures)
                .thenRun(() -> log.info("All thread pool tasks completed for batch: {}", batchName))
                .exceptionally(throwable -> {
                    log.error("Some thread pool tasks failed for batch: {}", batchName, throwable);
                    return null;
                });
    }

    @LogExecution
    public void monitorThreadPool() {
        log.info("Thread Pool Status - Active: {}, Pool Size: {}, Core Pool Size: {}, Max Pool Size: {}, Queue Size: {}",
                taskExecutor.getActiveCount(),
                taskExecutor.getPoolSize(),
                taskExecutor.getCorePoolSize(),
                taskExecutor.getMaxPoolSize(),
                taskExecutor.getQueueSize());
    }
}
