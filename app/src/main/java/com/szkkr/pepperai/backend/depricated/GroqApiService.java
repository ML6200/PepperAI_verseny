package com.szkkr.pepperai.backend.depricated;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GroqApiService
{
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private final String apiKey;

    public GroqApiService(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public ChatResponse sendMessage(ChatRequest request)
    {
        String jsonPayload = request.toJson();
        try
        {
            URL url = new URL(API_URL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream())
            {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null)
                {
                    response.append(responseLine.trim());
                }
                throw new RuntimeException("HTTP error code: " + code + " Response: " + response);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null)
            {
                response.append(responseLine.trim());
            }

            return new ChatResponse(response.toString());
        } catch (Exception e)
        {
            e.printStackTrace();
            return new ChatResponse("Error during API request: " + e.getMessage());
        }
    }
}
