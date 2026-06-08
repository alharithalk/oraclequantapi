package com.oraclequantapi.oraclequantapi.service;

import com.oraclequantapi.oraclequantapi.model.HistoryRecord;
import com.oraclequantapi.oraclequantapi.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public HistoryRecord recordMeasurement(String symbol, double value) {
        HistoryRecord record = new HistoryRecord(null, symbol, value, LocalDateTime.now());
        return historyRepository.save(record);
    }

    public List<HistoryRecord> getAllHistory() {
        return historyRepository.findAll();
    }

    public List<HistoryRecord> getHistoryBySymbol(String symbol) {
        return historyRepository.findBySymbol(symbol);
    }

    public Optional<HistoryRecord> getHistoryById(Long id) {
        return historyRepository.findById(id);
    }
}
