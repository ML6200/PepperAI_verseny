package com.szkkr.pepperai;

import static dev.langchain4j.data.message.UserMessage.userMessage;

import android.app.Activity;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.szkkr.pepperai.backend.GroqModels;
import com.szkkr.pepperai.backend.RobotController;

import java.util.Locale;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;


public class MainActivity extends AppCompatActivity/*RobotController*/
{
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
        //BasicChat basicChat = new BasicChat();

        gomb.setOnClickListener(v -> {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = LangChTest.chat(edit.getText().toString());//basicChat.chat(String.valueOf(edit.getText()));
                    System.out.println(result);
                    tts.speak(result, TextToSpeech.QUEUE_FLUSH, null);
                }
            });

            t.start();
        });
    }
}

class LangChTest
{
    private static Assistant assistant;

    static {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey("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")
                .modelName(GroqModels.LLAMA3_3_70B_VERSATILE)
                .parallelToolCalls(true)
                .build();


       assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new Calculator(), new DataRetriever())
                .build();

    }
    public static String chat(String inp)
    {


        return assistant.chat(inp);
    }

    interface Assistant
    {
        @SystemMessage("Te a Balassagyarmati Balassi Bálint Gimnázium AI alapú robotja vagy. \n" +
                "A te neved Pepi.\n" +
                "A feladatod, hogy segíts a diákoknak a tanulásban és egyéb iskolához kötődő dolgokban.\n" +
                "Ha be kell mutatkoznod, akkor azt röviden tedd meg!\n" +
                "Fontos, hogy röviden és érthetően válaszolj a kérdésekre.\n" +
                "Ha trágár kifejezésekkel kérdeznek akkor figyelmeztetsd őt, hogy illedelmesen beszéljen, de ha nem NE SZÓLJ!\n" +
                "(Mogyorósi Attlila: Az iskola igazgatója. Ő biológiát is tanít, de nem csa ő.)\n" +
                "A Balassi Bálint Gimnázium a Balassagyarmaton, Nógrád megyében található található. \n")
        String chat(String message);
    }

    static class Calculator
    {

        @Tool(name="Kiszámítja a karakterlánc hosszát", value="")
        int stringLength(String s)
        {
            return s.length();
        }

        @Tool("Összead két számot")
        int add(int a, int b)
        {
            return a + b;
        }

        @Tool("Eloszt két számot")
        double divide(double a, double b)
        {
            return a / b;
        }

        @Tool("Kivon két számot")
        int subtract(int a, int b)
        {
            return a - b;
        }

        @Tool("Kiszámítja a négyzetgyökét két számnak")
        double sqrt(int x)
        {
            return Math.sqrt(x);
        }

        @Tool("Kiszámítja a négyzetét két számnak")
        double pow(int x)
        {
            return Math.pow(x, 2);
        }

    }
}


class DataRetriever
{
    //ONLY FOR TEST PURPOSE
    //THIS CLASS SHOULD REPRESENT THE SQL DATA RETRIEVER
    @Tool("Visszaadja az órarend szerinti jelenegi tanórát és a terem nevét vesszővel elválaszva a megadott névből" +
            "Ha nem talál ilyet a függvény, akkor -1 et ad eredményül: Ez esetben azt kell mondanod hogy nincs ilyen név" +
            "Ha viszont nem adott meg semmit a felhasználó akkor a függvény 0-t ad eredményül: Ez esetben kérd, " +
            "hogy adja meg a nevét")
    String getSubjectByName(String name)
    {
        if (name.equals("Mark"))
            return "Fizika, U25";
        if (name.isEmpty())
            return "0";
        else
            return "-1";
    }
}


class SqlData
{

}

/*
class BasicChat {
    public String chat(String input)
    {
        System.setProperty("GITHUB_TOKEN", "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");

        String key = Configuration.getGlobalConfiguration().get("GITHUB_TOKEN");
        String endpoint = "https://models.inference.ai.azure.com";
        String model = "gpt-4o"; //"meta-llama-3-70b-instruct";
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

 */
