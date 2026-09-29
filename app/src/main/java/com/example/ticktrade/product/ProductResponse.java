package com.example.ticktrade.product;



import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {


}
