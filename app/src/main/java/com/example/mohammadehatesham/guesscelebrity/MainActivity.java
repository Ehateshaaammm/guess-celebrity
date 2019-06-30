package com.example.mohammadehatesham.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb = 0;
    ImageView imageView;
    String[] answers = new String[4];
    int locationOfCorrect = 0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen(View view){
    if(view.getTag().toString().equals(Integer.toString(locationOfCorrect))){
        Toast.makeText(getApplicationContext(), "Correct!!", Toast.LENGTH_SHORT).show();
    }else{
        Toast.makeText(getApplicationContext(), "Incorrect!! it was "+ celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
    }
    newQuestion();
    }
    public  class imageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            }catch (Exception e){
            return null;
            }
        }
    }
    public class downloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url ;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result+=current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return  "failed!!";
            }
        }
    }
    public void newQuestion(){
        try {
            Random rand = new Random();

            chosenCeleb = rand.nextInt(celebURLs.size());
            imageDownloader imageTask = new imageDownloader();
            Bitmap celebImage = imageTask.execute(celebURLs.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);
            locationOfCorrect = rand.nextInt(4);
            int inCorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrect) {
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    inCorrectAnswerLocation = rand.nextInt(celebURLs.size());
                    while (inCorrectAnswerLocation == chosenCeleb) {
                        inCorrectAnswerLocation = rand.nextInt(celebURLs.size());
                    }
                    answers[i] = celebNames.get(inCorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadTask task = new downloadTask();
        String result = null;
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        try{
            result = task.execute("https://www.imdb.com/list/ls074758327/").get();
            String[] splitString = result.split("<div class=\"listedArticles\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitString[0]);
            while (m.find()){
            celebURLs.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitString[0]);
            while (m.find()){
                celebNames.add(m.group(1));
            }
        newQuestion();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
