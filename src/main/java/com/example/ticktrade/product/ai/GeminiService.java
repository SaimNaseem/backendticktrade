package com.example.ticktrade.product.ai;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class GeminiService {

    private final Client client;
    private final HttpClient httpClient;

    public GeminiService() {
        this.client = new Client();

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public AiProductResponse generateProduct(String imageUrl) {
        try {

            /*
             * 1. Download image from Cloudinary
             */
            HttpRequest imageRequest = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<byte[]> imageResponse =
                    httpClient.send(
                            imageRequest,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            if (
                    imageResponse.statusCode() < 200 ||
                            imageResponse.statusCode() >= 300
            ) {
                throw new RuntimeException(
                        "Could not download product image"
                );
            }

            byte[] imageBytes = imageResponse.body();

            /*
             * 2. Detect MIME type
             */
            String mimeType = imageResponse
                    .headers()
                    .firstValue("Content-Type")
                    .orElse("image/jpeg")
                    .split(";")[0];

            /*
             * 3. Prompt
             */
            String prompt = """
                    Analyze this product image.

                    Generate:
                    1. A concise professional ecommerce product name.
                    2. A short ecommerce product description with a marketing touch to it.

                    Rules:
                    - Product name maximum 8 words.
                    - Description maximum 2 sentences.
                    - Do not invent technical specifications.
                    - Describe only things reasonably visible in the image.
                    - Do not mention that you analyzed an image.
                    - Return only this format:

                    NAME: <product name>
                    DESCRIPTION: <product description>
                    """;

            /*
             * 4. Build Gemini request
             */
            Content content = Content.fromParts(
                    Part.fromText(prompt),
                    Part.fromBytes(
                            imageBytes,
                            mimeType
                    )
            );

            /*
             * 5. Send request to Gemini with retry
             */
            GenerateContentResponse response =
                    generateWithRetry(content);

            String generatedText = response.text();

            if (
                    generatedText == null ||
                            generatedText.isBlank()
            ) {
                throw new RuntimeException(
                        "Gemini returned an empty response"
                );
            }

            /*
             * 6. Extract name + description
             */
            String name = extractValue(
                    generatedText,
                    "NAME:",
                    "DESCRIPTION:"
            );

            String description = extractValue(
                    generatedText,
                    "DESCRIPTION:",
                    null
            );

            if (
                    name == null ||
                            name.isBlank()
            ) {
                throw new RuntimeException(
                        "Gemini returned an empty product name"
                );
            }

            if (
                    description == null ||
                            description.isBlank()
            ) {
                throw new RuntimeException(
                        "Gemini returned an empty product description"
                );
            }

            /*
             * 7. Return response
             */
            return new AiProductResponse(
                    name.trim(),
                    description.trim()
            );

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to generate product information with Gemini: "
                            + e.getMessage(),
                    e
            );
        }
    }

    /*
     * Retry Gemini request if model temporarily returns 503
     */
    private GenerateContentResponse generateWithRetry(Content content) {

        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            try {

                return client.models.generateContent(
                        "gemini-3.5-flash-lite",
                        content,
                        null
                );

            } catch (Exception e) {

                String message = e.getMessage();

                boolean isTemporary503 =
                        message != null &&
                                message.contains("503 UNAVAILABLE");

                /*
                 * If it's not a temporary Gemini 503,
                 * immediately propagate the error.
                 */
                if (!isTemporary503) {
                    throw e;
                }

                /*
                 * Stop after final attempt.
                 */
                if (attempt == maxAttempts) {
                    throw e;
                }

                long waitMs = attempt * 1500L;

                System.out.println(
                        "Gemini temporarily unavailable. Retry "
                                + (attempt + 1)
                                + "/"
                                + maxAttempts
                                + " in "
                                + waitMs
                                + " ms"
                );

                try {

                    Thread.sleep(waitMs);

                } catch (InterruptedException interruptedException) {

                    Thread.currentThread().interrupt();

                    throw new RuntimeException(
                            "Gemini retry interrupted",
                            interruptedException
                    );
                }
            }
        }

        throw new RuntimeException(
                "Gemini request failed after all retry attempts"
        );
    }

    private String extractValue(
            String text,
            String startMarker,
            String endMarker
    ) {

        int startIndex = text.indexOf(startMarker);

        if (startIndex == -1) {
            return null;
        }

        startIndex += startMarker.length();

        if (endMarker == null) {
            return text
                    .substring(startIndex)
                    .trim();
        }

        int endIndex = text.indexOf(
                endMarker,
                startIndex
        );

        if (endIndex == -1) {
            return text
                    .substring(startIndex)
                    .trim();
        }

        return text
                .substring(
                        startIndex,
                        endIndex
                )
                .trim();
    }
}