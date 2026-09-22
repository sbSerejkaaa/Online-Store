package com.example.saga.service;

import com.example.saga.persistence.entity.SagaState;
import com.example.saga.persistence.repository.SagaStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaStateService {

    private final SagaStateRepository repository;

    @Transactional
    public SagaState startSaga(UUID orderId, UUID accountId, int totalItems) {
        SagaState state = SagaState.builder()
                .orderId(orderId)
                .accountId(accountId)
                .status(SagaState.SagaStatus.RESERVING_PRODUCTS)
                .totalItems(totalItems)
                .reservedItems(0)
                .failedItems(0)
                .totalAmount(BigDecimal.ZERO)
                .build();
        SagaState saved = repository.save(state);
        log.info("Saga started: orderId={}, totalItems={}", orderId, totalItems);
        return saved;
    }

    @Transactional
    public SagaState incrementReserved(UUID orderId, BigDecimal itemTotal) {
        SagaState state = findOrThrow(orderId);
        state.setReservedItems(state.getReservedItems() + 1);
        state.setTotalAmount(state.getTotalAmount().add(itemTotal));

        if (state.getReservedItems().equals(state.getTotalItems())) {
            state.setStatus(SagaState.SagaStatus.PRODUCTS_RESERVED);
            log.info("All products reserved for orderId={}, totalAmount={}",
                    orderId, state.getTotalAmount());
        }
        return repository.save(state);
    }

    @Transactional
    public SagaState incrementFailed(UUID orderId, String reason) {
        SagaState state = findOrThrow(orderId);
        state.setFailedItems(state.getFailedItems() + 1);
        state.setStatus(SagaState.SagaStatus.FAILED);
        state.setFailureReason(reason);
        log.warn("Saga failed for orderId={}, reason={}", orderId, reason);
        return repository.save(state);
    }

    @Transactional
    public SagaState markPaid(UUID orderId) {
        SagaState state = findOrThrow(orderId);
        state.setStatus(SagaState.SagaStatus.PAYING);
        return repository.save(state);
    }

    @Transactional
    public SagaState markCompleted(UUID orderId) {
        SagaState state = findOrThrow(orderId);
        state.setStatus(SagaState.SagaStatus.COMPLETED);
        log.info("Saga completed: orderId={}", orderId);
        return repository.save(state);
    }

    @Transactional
    public SagaState markCompensating(UUID orderId, String reason) {
        SagaState state = findOrThrow(orderId);
        state.setStatus(SagaState.SagaStatus.COMPENSATING);
        state.setFailureReason(reason);
        log.warn("Saga compensating: orderId={}, reason={}", orderId, reason);
        return repository.save(state);
    }

    public SagaState findOrThrow(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("SagaState not found: " + orderId));
    }
}