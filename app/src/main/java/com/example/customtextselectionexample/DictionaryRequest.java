package com.example.customtextselectionexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class DictionaryRequest extends AsyncTask<String, Integer, String> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private TextView mTextView;


    DictionaryRequest(Context context, TextView textView) {
        mContext = context;
        mTextView = textView;
    }

    @Override
    protected String doInBackground(String... params) {

        //TODO: replace with your own app id and app key
        final String app_id = "c4face7c";
        final String app_key = "2d82bc0e6832de4062b5dcc6cff0c25f";
        try {
            URL url = new URL(params[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("app_id", app_id);
            urlConnection.setRequestProperty("app_key", app_key);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @SuppressLint({"SetTextI18n", "RestrictedApi"})
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String def;
        try {
            JSONObject js = new JSONObject(result);
            JSONArray results = js.getJSONArray("results");

            JSONObject lEntries = results.getJSONObject(0);
            JSONArray lArray = lEntries.getJSONArray("lexicalEntries");


            JSONObject entries = lArray.getJSONObject(0);
            JSONArray e = entries.getJSONArray("entries");


            JSONObject lexicalCategory = entries.getJSONObject("lexicalCategory");

            String text = lexicalCategory.getString("text").toLowerCase();


            JSONObject senses = e.getJSONObject(0);
            JSONArray sensesArray = senses.getJSONArray("senses");

            JSONObject de = sensesArray.getJSONObject(0);
            JSONArray d = de.getJSONArray("definitions");

            def = d.getString(0);
            SpannableString ss = new SpannableString(text + " : " + def);

            StyleSpan italicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
            ss.setSpan(italicSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ProcessTextActivity.progressBar.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            ProcessTextActivity.queryword.setVisibility(View.VISIBLE);
            ProcessTextActivity.queryword.setText(ProcessTextActivity.wordToSpeak);
            ProcessTextActivity.pronunciation.setVisibility(View.VISIBLE);
            mTextView.setText(ss);


            ProcessTextActivity.mTTS = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = ProcessTextActivity.mTTS.setLanguage(Locale.US);
                    }
                }
            });

            ProcessTextActivity.pronunciation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speak();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void speak() {
        ProcessTextActivity.mTTS.speak(ProcessTextActivity.wordToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

}
