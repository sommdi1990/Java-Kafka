package com.kafka.workflow.service;

import com.kafka.shared.dto.WorkflowDefinition;
import com.kafka.workflow.repository.WorkflowDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowDefinitionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;

    @Transactional(readOnly = true)
    public List<WorkflowDefinition> getAllDefinitions() {
        return workflowDefinitionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public WorkflowDefinition getById(Long id) {
        return workflowDefinitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found: " + id));
    }

    @Transactional(readOnly = true)
    public WorkflowDefinition getByName(String name) {
        return workflowDefinitionRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found: " + name));
    }

    @Transactional
    public WorkflowDefinition create(WorkflowDefinition definition) {
        // Default status if not set
        if (definition.getStatus() == null) {
            definition.setStatus(WorkflowDefinition.WorkflowStatus.DRAFT);
        }
        return workflowDefinitionRepository.save(definition);
    }

    @Transactional
    public WorkflowDefinition update(Long id, WorkflowDefinition updated) {
        WorkflowDefinition existing = getById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setVersion(updated.getVersion());
        existing.setDefinitionJson(updated.getDefinitionJson());
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        return workflowDefinitionRepository.save(existing);
    }

    @Transactional
    public WorkflowDefinition updateStatus(Long id, WorkflowDefinition.WorkflowStatus status) {
        WorkflowDefinition existing = getById(id);
        existing.setStatus(status);
        return workflowDefinitionRepository.save(existing);
    }
}


