package com.oraclequantapi.oraclequantapi.repository;

import com.oraclequantapi.oraclequantapi.model.HistoryRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class HistoryRepository {

    private final Map<Long, HistoryRecord> records = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public HistoryRecord save(HistoryRecord record) {
        record.setId(idGenerator.incrementAndGet());
        records.put(record.getId(), record);
        return record;
    }

    public List<HistoryRecord> findAll() {
        return new ArrayList<>(records.values());
    }

    public List<HistoryRecord> findBySymbol(String symbol) {
        List<HistoryRecord> result = new ArrayList<>();
        for (HistoryRecord record : records.values()) {
            if (record.getSymbol().equalsIgnoreCase(symbol)) {
                result.add(record);
            }
        }
        return result;
    }

    public Optional<HistoryRecord> findById(Long id) {
        return Optional.ofNullable(records.get(id));
    }
}
