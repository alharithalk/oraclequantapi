package com.oraclequantapi.oraclequantapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Persistent record of a single {@code /convert-measurements} request, stored in Oracle XE.
 */
@Entity
@Table(name = "HISTORY_RECORD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "REQUEST_TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "SOURCE_IP_ADDRESS", length = 64, nullable = false)
    private String sourceIpAddress;

    @Column(name = "INPUT_TEXT", length = 4000, nullable = false)
    private String input;

    @Column(name = "OUTPUT_TEXT", length = 4000, nullable = false)
    private String output;
}
