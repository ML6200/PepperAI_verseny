package com.szkkr.pepperai;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.intellij.lang.annotations.Language;

import java.util.Locale;



import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.azure.ai.inference.ChatCompletionsClient;
import com.azure.ai.inference.ChatCompletionsClientBuilder;
import com.azure.ai.inference.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.Configuration;


public class MainActivity extends AppCompatActivity {
    private TextToSpeech tts;
    private Button gomb;
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        System.setProperty("GITHUB_TOKEN", "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");

        gomb = findViewById(R.id.gomb);
        edit = findViewById(R.id.edit);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                {
                    tts.setLanguage(Locale.getDefault());
                }
            }
        });
        BasicChat basicChat = new BasicChat();

        gomb.setOnClickListener(v -> {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = basicChat.chat(String.valueOf(edit.getText()));
                    System.out.println(result);
                    tts.speak(result, TextToSpeech.QUEUE_FLUSH, null);
                }
            });

            t.start();
        });
    }
}


class BasicChat {
    public String chat(String input)
    {

        String key = Configuration.getGlobalConfiguration().get("GITHUB_TOKEN");
        String endpoint = "https://models.inference.ai.azure.com";
        String model = "gpt-4o";
        try {
            ChatCompletionsClient client = new ChatCompletionsClientBuilder()
                    .credential(new AzureKeyCredential(key))
                    .endpoint(endpoint)
                    .buildClient();

            ChatCompletionsOptions chatCompletionsOptions = getChatCompletionsOptions(model, input);

            ChatCompletions completions = client.complete(chatCompletionsOptions);

            return completions.getChoices().get(0).getMessage().getContent();
        } catch (Exception e)
        {
            Log.e("Exception: ", Objects.requireNonNull(e.getMessage()));
        }
        return null;

    }

    private static ChatCompletionsOptions getChatCompletionsOptions(String model, String input)
    {
        List<ChatRequestMessage> chatMessages = Arrays.asList(
                new ChatRequestSystemMessage("Te a Balassagyarmati Balassi Bálint Gimnázium AI alapú robotja vagy. " +
                        "A te neved Pepi." + "\n" +
                        "A feladatod, hogy segíts a diákoknak a tanulásban és egyéb iskolához kötődő dolgokban." +
                        "Ha be kell mutatkoznod, akkor azt röviden tedd meg!" +
                        "Fontos, hogy röviden és érthetően válaszolj a kérdésekre." +
                        "Ha trágár kifejezésekkel kérdeznek akkor figyelmeztetsd őt, hogy illedelmesen beszéljen" +
                        "(Mogyorósi Attlila: Az iskola igazgatója. Ő biológiát tanít, de nem csa ő.)"
                ),
                new ChatRequestUserMessage(input)
        );

        ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
        chatCompletionsOptions.setModel(model);
        return chatCompletionsOptions;
    }
}