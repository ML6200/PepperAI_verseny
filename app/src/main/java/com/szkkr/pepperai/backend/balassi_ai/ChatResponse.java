package com.szkkr.pepperai.backend.balassi_ai;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatResponse
{
    private String content;

    public ChatResponse(String jsonResponse)
    {
        try
        {
            JSONObject responseObj = new JSONObject(jsonResponse);

            if (responseObj.has("error"))
            {
                JSONObject errorObj = responseObj.getJSONObject("error");
                this.content = "API Error: " + errorObj.getString("message");
            } else if (responseObj.has("choices"))
            {
                JSONArray choices = responseObj.getJSONArray("choices");
                if (choices.length() > 0)
                {
                    JSONObject firstChoice = choices.getJSONObject(0).getJSONObject("message");
                    this.content = firstChoice.getString("content");
                } else
                {
                    this.content = "No response from API.";
                }
            } else
            {
                this.content = "Unexpected API response structure.";
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getContent()
    {
        return content;
    }
}
