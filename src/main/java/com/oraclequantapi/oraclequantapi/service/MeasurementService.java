package com.oraclequantapi.oraclequantapi.service;

import com.oraclequantapi.oraclequantapi.model.HistoryRecord;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class MeasurementService {

    private static final double MIN_VALUE = 0.0;
    private static final double MAX_VALUE = 1000.0;

    private final HistoryService historyService;

    public MeasurementService(HistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * Takes a new quantitative measurement for the given symbol and
     * persists it to history.
     */
    public HistoryRecord takeMeasurement(String symbol) {
        double value = ThreadLocalRandom.current().nextDouble(MIN_VALUE, MAX_VALUE);
        return historyService.recordMeasurement(symbol, value);
    }
}
