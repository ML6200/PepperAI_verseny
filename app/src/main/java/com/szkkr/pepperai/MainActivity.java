package com.szkkr.pepperai;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aldebaran.qi.sdk.Qi;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.aldebaran.qi.sdk.object.humanawareness.HumanawarenessConverter;
import com.aldebaran.qi.sdk.util.PhraseSetUtil;
import com.szkkr.pepperai.backend.balassi_ai.ChatRequest;
import com.szkkr.pepperai.backend.balassi_ai.ChatResponse;
import com.szkkr.pepperai.backend.Controller;
import com.szkkr.pepperai.backend.balassi_ai.GroqApiService;
import com.szkkr.pepperai.backend.balassi_ai.GroqModels;
import com.szkkr.pepperai.backend.balassi_ai.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends Controller {
    private TextToSpeech tts;
    private Button gomb;
    private EditText edit;

    private final String apiKey = "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"; // Replace with actual key
    private final GroqApiService apiService = new GroqApiService(apiKey);

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

        gomb = findViewById(R.id.gomb);
        edit = findViewById(R.id.edit);

        gomb.setOnClickListener(v ->
        {
            getSpeechInput();
        });

        QiSDK.register(this, this);
    }

    public void getSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 100); // szoveg hozza
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String eredm = result.get(0);

                new Thread(() ->
                {
                    ChatRequest request = new ChatRequest(
                            GroqModels.LLAMA3_3_70B_VERSATILE.toString(),
                            Arrays.asList(
                                    new Message("system", "Te a Balassagyarmati Balassi Bálint Gimnázium AI alapú robotja vagy. \n" +
                                            "A te neved Pepi.\n" +
                                            "A feladatod, hogy segíts a diákoknak a tanulásban és egyéb iskolához kötődő dolgokban.\n" +
                                            "Ha be kell mutatkoznod, akkor azt röviden tedd meg!\n" +
                                            "Fontos, hogy röviden és érthetően válaszolj a kérdésekre.\n" +
                                            "Ha trágár kifejezésekkel kérdeznek akkor figyelmeztetsd őt, hogy illedelmesen beszéljen\n" +
                                            "(Mogyorósi Attlila: Az iskola igazgatója. Ő biológiát tanít, de nem csa ő.)\n" +
                                            ""),
                                    new Message("user", eredm)
                            )
                    );

                    ChatResponse response = apiService.sendMessage(request);

                    String valasz = response.getContent();
                    exec(valasz);
                }).start();

            }
        }
}
