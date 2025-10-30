package com.iseeyou.fortunetelling.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

public final class Constants {
    @Getter
    @AllArgsConstructor
    public enum TargetType {
        CONVERSATION("CONVERSATION"),
        BOOKING("BOOKING"),
        PAYMENT("PAYMENT"),
        REPORT("REPORT"),
        ACCOUNT("ACCOUNT"),
        SERVICE_PACKAGES("SERVICE_PACKAGES"),
        SERVICE_REVIEWS("SERVICE_REVIEWS");

        private final String value;

        public static TargetType get(final String name) {
            return Stream.of(TargetType.values())
                    .filter(p -> p.name().equals(name.toUpperCase()) || p.getValue().equals(name.toUpperCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid role name: %s", name)));
        }
    }
}
