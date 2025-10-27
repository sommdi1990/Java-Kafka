package com.kafka.schedule.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("schedule-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Job dataProcessingJob(JobRepository jobRepository, Step dataProcessingStep) {
        return new JobBuilder("dataProcessingJob", jobRepository)
                .start(dataProcessingStep)
                .build();
    }

    @Bean
    public Step dataProcessingStep(JobRepository jobRepository,
                                   Tasklet dataProcessingTasklet,
                                   PlatformTransactionManager transactionManager) {
        return new StepBuilder("dataProcessingStep", jobRepository)
                .tasklet(dataProcessingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet dataProcessingTasklet() {
        return (contribution, chunkContext) -> {
            String taskName = chunkContext.getStepContext()
                    .getJobParameters()
                    .get("taskName")
                    .toString();

            System.out.println("Processing data for task: " + taskName);

            // Simulate data processing
            Thread.sleep(1000);

            return RepeatStatus.FINISHED;
        };
    }
}
