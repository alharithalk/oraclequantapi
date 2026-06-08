package com.oraclequantapi.oraclequantapi.service;

import com.oraclequantapi.oraclequantapi.model.HistoryRecord;
import com.oraclequantapi.oraclequantapi.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Owns persistence and CRUD operations for {@link HistoryRecord}s.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    @Transactional
    public HistoryRecord save(String sourceIpAddress, String input, String output) {
        HistoryRecord record = HistoryRecord.builder()
                .timestamp(LocalDateTime.now())
                .sourceIpAddress(sourceIpAddress)
                .input(input)
                .output(output)
                .build();

        HistoryRecord saved = historyRepository.save(record);
        log.debug("Saved history record id={} input='{}'", saved.getId(), input);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<HistoryRecord> getAll() {
        return historyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<HistoryRecord> getById(Long id) {
        return historyRepository.findById(id);
    }

    @Transactional
    public Optional<HistoryRecord> update(Long id, HistoryRecord replacement) {
        return historyRepository.findById(id).map(existing -> {
            existing.setTimestamp(replacement.getTimestamp());
            existing.setSourceIpAddress(replacement.getSourceIpAddress());
            existing.setInput(replacement.getInput());
            existing.setOutput(replacement.getOutput());
            log.debug("Fully updated history record id={}", id);
            return historyRepository.save(existing);
        });
    }

    @Transactional
    public Optional<HistoryRecord> partialUpdate(Long id, HistoryRecord patch) {
        return historyRepository.findById(id).map(existing -> {
            if (patch.getTimestamp() != null) {
                existing.setTimestamp(patch.getTimestamp());
            }
            if (patch.getSourceIpAddress() != null) {
                existing.setSourceIpAddress(patch.getSourceIpAddress());
            }
            if (patch.getInput() != null) {
                existing.setInput(patch.getInput());
            }
            if (patch.getOutput() != null) {
                existing.setOutput(patch.getOutput());
            }
            log.debug("Partially updated history record id={}", id);
            return historyRepository.save(existing);
        });
    }

    @Transactional
    public void deleteAll() {
        long count = historyRepository.count();
        historyRepository.deleteAllInBatch();
        log.info("Deleted {} history record(s)", count);
    }
}
