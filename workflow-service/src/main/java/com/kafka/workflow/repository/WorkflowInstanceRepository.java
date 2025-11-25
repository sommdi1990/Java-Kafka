package com.kafka.workflow.repository;

import com.kafka.workflow.dto.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, Long> {
}


