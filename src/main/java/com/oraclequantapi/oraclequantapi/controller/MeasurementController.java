package com.oraclequantapi.oraclequantapi.controller;

import com.oraclequantapi.oraclequantapi.service.HistoryService;
import com.oraclequantapi.oraclequantapi.service.MeasurementService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes the measurement-conversion endpoint. Each request is delegated to
 * {@link MeasurementService} for computation and recorded via {@link HistoryService}.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MeasurementController {

    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";

    private final MeasurementService measurementService;
    private final HistoryService historyService;

    @GetMapping("/convert-measurements")
    public List<Integer> convertMeasurements(@RequestParam("input") String input, HttpServletRequest request) {
        List<Integer> output = measurementService.convert(input);

        String sourceIpAddress = resolveClientIp(request);
        historyService.save(sourceIpAddress, input, output.toString());

        log.info("Converted measurement input='{}' from {} -> {}", input, sourceIpAddress, output);
        return output;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(FORWARDED_FOR_HEADER);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
