package com.example.ticktrade.s3;

import java.io.InputStream;

public record ProductImage(
        InputStream content,
        String contentType,
        String filename,
        long contentLength
) {
}
