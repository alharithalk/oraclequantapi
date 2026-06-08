package com.oraclequantapi.oraclequantapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements the measurement-conversion algorithm used by {@code /convert-measurements}.
 *
 * <p>Parsing rules:
 * <ul>
 *   <li>Letters a-z carry their alphabet position as a value (a=1 ... z=26) and underscore carries 0.</li>
 *   <li>A run of consecutive 'z' characters followed by one terminating letter/underscore forms a single
 *       token whose value is {@code 26 * (number of z's) + value(terminator)} (e.g. za=27, zza=53, z_=26).</li>
 *   <li>Each "package" is read as a COUNT token followed by that many VALUE tokens; the package's total is
 *       the sum of its values. Packages are read back-to-back until the input (or a separator) ends them.</li>
 *   <li>A single space separates packages; two or more consecutive spaces stop processing and the totals
 *       collected so far are returned as-is.</li>
 *   <li>Encountering a space while a token is only partially read, or any character outside a-z/_/space,
 *       invalidates the whole input and an empty list is returned.</li>
 *   <li>A zero-count package (started by '_') immediately followed by a letter stops processing and the
 *       totals collected so far (including that zero) are returned.</li>
 * </ul>
 */
@Slf4j
@Service
public class MeasurementService {

    private static final char SEPARATOR = ' ';
    private static final char ZERO_CHAR = '_';
    private static final char Z = 'z';
    private static final int ALPHABET_SIZE = 26;

    public List<Integer> convert(String input) {
        if (input == null) {
            return Collections.emptyList();
        }

        List<Integer> totals = new ArrayList<>();
        int length = input.length();
        int index = 0;

        while (index < length) {
            char current = input.charAt(index);

            if (current == SEPARATOR) {
                if (index + 1 < length && input.charAt(index + 1) == SEPARATOR) {
                    log.debug("Stopping at consecutive separators for input '{}'", input);
                    return totals;
                }
                index++;
                continue;
            }

            Token countToken = readToken(input, index);
            if (countToken.status() == TokenStatus.INVALID || countToken.status() == TokenStatus.SPACE) {
                log.debug("Invalid input '{}': malformed package count at index {}", input, index);
                return Collections.emptyList();
            }
            if (countToken.status() == TokenStatus.END_OF_INPUT) {
                return totals;
            }

            int count = countToken.value();
            index = countToken.nextIndex();

            int sum = 0;
            boolean incomplete = false;
            for (int valueNumber = 0; valueNumber < count; valueNumber++) {
                Token valueToken = readToken(input, index);
                if (valueToken.status() == TokenStatus.INVALID || valueToken.status() == TokenStatus.SPACE) {
                    log.debug("Invalid input '{}': malformed package value at index {}", input, index);
                    return Collections.emptyList();
                }
                if (valueToken.status() == TokenStatus.END_OF_INPUT) {
                    incomplete = true;
                    break;
                }
                sum += valueToken.value();
                index = valueToken.nextIndex();
            }

            if (incomplete) {
                log.debug("Stopping at incomplete package for input '{}'", input);
                return totals;
            }

            totals.add(sum);

            if (count == 0 && index < length && isLetter(input.charAt(index))) {
                return totals;
            }
        }

        return totals;
    }

    /**
     * Reads a single token starting at {@code index}: zero or more 'z' characters followed by exactly
     * one terminating character (a letter a-y/z or underscore). The token's value combines the leading
     * z-run with the terminator: {@code 26 * zCount + value(terminator)}.
     */
    private Token readToken(String input, int index) {
        int length = input.length();
        int zCount = 0;
        int cursor = index;

        while (cursor < length && input.charAt(cursor) == Z) {
            zCount++;
            cursor++;
        }

        if (cursor >= length) {
            return Token.endOfInput();
        }

        char terminator = input.charAt(cursor);
        if (terminator == SEPARATOR) {
            return Token.space();
        }

        Integer terminatorValue = letterValue(terminator);
        if (terminatorValue == null) {
            return Token.invalid();
        }

        return Token.ok(ALPHABET_SIZE * zCount + terminatorValue, cursor + 1);
    }

    private Integer letterValue(char c) {
        if (c == ZERO_CHAR) {
            return 0;
        }
        if (isLetter(c)) {
            return c - 'a' + 1;
        }
        return null;
    }

    private boolean isLetter(char c) {
        return c >= 'a' && c <= 'z';
    }

    private enum TokenStatus {
        OK, SPACE, INVALID, END_OF_INPUT
    }

    private record Token(TokenStatus status, int value, int nextIndex) {
        static Token ok(int value, int nextIndex) {
            return new Token(TokenStatus.OK, value, nextIndex);
        }

        static Token space() {
            return new Token(TokenStatus.SPACE, 0, -1);
        }

        static Token invalid() {
            return new Token(TokenStatus.INVALID, 0, -1);
        }

        static Token endOfInput() {
            return new Token(TokenStatus.END_OF_INPUT, 0, -1);
        }
    }
}
