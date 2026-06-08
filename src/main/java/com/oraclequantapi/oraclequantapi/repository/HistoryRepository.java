package com.oraclequantapi.oraclequantapi.repository;

import com.oraclequantapi.oraclequantapi.model.HistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link HistoryRecord}, backed by Oracle XE.
 */
@Repository
public interface HistoryRepository extends JpaRepository<HistoryRecord, Long> {
}
