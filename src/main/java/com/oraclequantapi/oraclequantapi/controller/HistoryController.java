package com.oraclequantapi.oraclequantapi.controller;

import com.oraclequantapi.oraclequantapi.model.HistoryRecord;
import com.oraclequantapi.oraclequantapi.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read/write access to the persisted {@code /convert-measurements} request history.
 */
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public List<HistoryRecord> getAll() {
        return historyService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoryRecord> getById(@PathVariable Long id) {
        return historyService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoryRecord> update(@PathVariable Long id, @RequestBody HistoryRecord record) {
        return historyService.update(id, record)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HistoryRecord> partialUpdate(@PathVariable Long id, @RequestBody HistoryRecord record) {
        return historyService.partialUpdate(id, record)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        historyService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
