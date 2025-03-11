package io.kestra.plugin.fmatest;

import java.time.Instant;
import lombok.Builder;

@Builder
public record Temperature(
    String location,
    Double value,
    Instant time
    ) {}