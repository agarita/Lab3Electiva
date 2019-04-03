package com.example.lab2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> nombres = new ArrayList<>();
    ArrayList<ImageView> imagenes = new ArrayList<>();

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;

            // Agregar permiso en AndroidManifest.xml
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                // Esto es muy estilo C
                // Se lee un caracter a la vez (como cuando se hace gets() en C o C++)
                int data = inputStreamReader.read();
                while (data != -1){
                    char character = (char)data;
                    result += character;
                    data = inputStreamReader.read();
                }

                return result;
            }
            catch (Exception e){
                e.printStackTrace();
                return "Error";
            }
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask downloadTask = new DownloadTask();
        String html = null;
        try {
            //https://culturedvultures.com/best-indie-games-all-time/
            html = downloadTask.execute("https://culturedvultures.com/best-indie-games-all-time/").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Document document = Jsoup.parse(html);
        Elements nombres = document.select("div .entry-content-wrap h2");
        Elements imagenes = document.select("div .entry-content-wrap figure img");

        Log.i("Info", String.format("Nombres: %s", nombres.toString()));
        Log.i("Info", String.format("Imagenes: %s", imagenes.get(0).attr("src").toString()));
    }
}
