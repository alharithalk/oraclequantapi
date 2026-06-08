package com.oraclequantapi.oraclequantapi.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MeasurementServiceTest {

    private final MeasurementService measurementService = new MeasurementService();

    static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of("aa", List.of(1)),
                Arguments.of("abbcc", List.of(2, 6)),
                Arguments.of("dz_a_aazzaaa", List.of(28, 53, 1)),
                Arguments.of("a_", List.of(0)),
                Arguments.of("abcdabcdab", List.of(2, 7, 7)),
                Arguments.of("abcdabcdab_", List.of(2, 7, 7, 0)),
                Arguments.of("zdaaaaaaaabaaaaaaaabaaaaaaaabbaa", List.of(34)),
                Arguments.of("zza_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_a_", List.of(26)),
                Arguments.of("za_a_a_a_a_a_a_a_a_a_a_a_a_azaaa", List.of(40, 1)),
                Arguments.of("_", List.of(0)),
                Arguments.of("_ad", List.of(0)),
                Arguments.of("_zzzb", List.of(0)),
                Arguments.of("__", List.of(0, 0)),
                Arguments.of("_ _", List.of(0, 0)),
                Arguments.of(" ", List.of()),
                Arguments.of(" _ _ ", List.of(0, 0)),
                Arguments.of("  _", List.of()),
                Arguments.of("_  _", List.of(0)),
                Arguments.of("z ab_", List.of()),
                Arguments.of("ba za", List.of()),
                Arguments.of("za@bcd", List.of()),
                Arguments.of("za!", List.of()),
                Arguments.of("ab_  ab_", List.of(2, 0)),
                Arguments.of("ab_   ab_", List.of(2, 0))
        );
    }

    @ParameterizedTest(name = "convert(\"{0}\") = {1}")
    @MethodSource("cases")
    void convertProducesExpectedTotals(String input, List<Integer> expected) {
        assertThat(measurementService.convert(input)).isEqualTo(expected);
    }
}
