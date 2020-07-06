package com.example.customtextselectionexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProcessTextActivity extends AppCompatActivity {

    private CharSequence text;
    private TextView mtext;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;
    public static FloatingActionButton pronunciation;
    public static String wordToSpeak = "";
    public static TextToSpeech mTTS;
    public static TextView queryword;


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_text);
        text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        mtext = findViewById(R.id.text_process);
        if (!text.toString().isEmpty()) {
            wordToSpeak = text.toString();
            makerequest();
        }
        progressBar = findViewById(R.id.progress_circular);
        pronunciation = findViewById(R.id.fab_pronun);
        queryword = findViewById(R.id.query_word);

    }

    private void makerequest() {
        DictionaryRequest dictionaryRequest = new DictionaryRequest(this, mtext);
        String url = dictionaryEntries();
        dictionaryRequest.execute(url);
    }

    private String dictionaryEntries() {
        final String language = "en-gb";
        final String word = (String) text;
        final String fields = "definitions";
        final String strictMatch = "false";
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com:443/api/v2/entries/" + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch;
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}
