package com.szkkr.pepperai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aldebaran.qi.sdk.QiSDK;
import com.szkkr.pepperai.backend.Controller;
import com.szkkr.pepperai.backend.ExecuteEndedListener;
import com.szkkr.pepperai.backend.balassi_ai.ChatMemory;
import com.szkkr.pepperai.backend.balassi_ai.ChatRequest;
import com.szkkr.pepperai.backend.balassi_ai.ChatResponse;
import com.szkkr.pepperai.backend.balassi_ai.GroqApiService;
import com.szkkr.pepperai.backend.balassi_ai.GroqModels;
import com.szkkr.pepperai.backend.balassi_ai.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends Controller implements ExecuteEndedListener
{
    //private TextToSpeech tts;
    private Button gomb;


    private final String apiKey = "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"; // Replace with actual key
    private final String systemMessage = "Te a Balassagyarmati Balassi Bálint Gimnázium mesterséges intelligencia alapú robotja vagy. \n" +
            "A te neved Pepi.\n" +
            "A feladatod, hogy segíts a diákoknak a tanulásban és egyéb iskolához kötődő dolgokban.\n" +
            "Ha nem kérdezik, hogy hívnak, vagy nem kérdezik, a neved, akkor csak a kérdésre válaszolj" +
            "Ha be kell mutatkoznod, akkor azt röviden tedd meg!\n" +
            "Fontos, hogy röviden és érthetően válaszolj a kérdésekre.\n" +
            "Ha trágár kifejezésekkel kérdeznek akkor figyelmeztetsd őt, hogy illedelmesen beszéljen\n" +
            "(Mogyorósi Attlila: Az iskola igazgatója. Ő biológiát tanít.)" +
            "\n";

    private final GroqApiService apiService = new GroqApiService(apiKey);
    //private final HumanAwarenessController humanAwarenessController = super.getHumanAwarenessController();
    private SpeechManager speechManager = new SpeechManager();
    private volatile ChatMemory memory = new ChatMemory();



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //initBtns____________
        gomb = findViewById(R.id.gomb);

        initListeners();

        QiSDK.register(this, this);
    }

    public void initListeners()
    {
        gomb.setOnClickListener(v ->
        {
            speechManager.getSpeechInput();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null)
        {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String result = results.get(0);

            memory.addSystemMessage(systemMessage);
            memory.addUserMessage(result);

            new Thread(() ->
            {
                ChatRequest request = new ChatRequest();

                request.setModel(GroqModels.LLAMA3_3_70B_VERSATILE.toString());
                request.setMemory(memory);

                ChatResponse response = apiService.sendMessage(request);

                String valasz = response.getContent();
                exec(valasz, this);
            }).start();
        }
    }

    @Override
    public void onExecuteEnded()
    {
        speechManager.getSpeechInput();
    }

    class SpeechManager
    {

        @SuppressLint("QueryPermissionsNeeded")
        public void getSpeechInput()
        {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(intent, 100); // szoveg hozza
                }
            }
        }
    }
