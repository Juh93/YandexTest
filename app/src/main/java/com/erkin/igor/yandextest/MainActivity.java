package com.erkin.igor.yandextest;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    LinearLayout containerLV; //контейнер для оcтальных view
    ProgressBar progressBar; //отображает процесс загрузки
    Button errorBt; //при возникновении ошибок генерим кнопку для повторного запроса
    String responce; //ответ сервера в формате JSON
    Handler handler;
    Address address;
    ArrayList<Artist> listOfArtists; //список с объектами класса Artist
    SharedPreferences lastResponce; //храним последний ответ сервера в файле
    SharedPreferences.Editor editPref;
    Context ctx;
    final String RESPONCE = "last_resp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = MainActivity.this;

        lastResponce = getSharedPreferences(RESPONCE, MODE_PRIVATE);
        editPref = lastResponce.edit();

        address = new Address();
        handler = new MyHandler(this);
        listView = (ListView) findViewById(R.id.listView);
        containerLV = (LinearLayout) findViewById(R.id.containerLV);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        connectToYandex();
    }

    private void connectToYandex() {
        progressBar.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    responce = Jsoup.connect(address.ADDRESS).ignoreContentType(true).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //записываем ответ в файл и заполняем listOfArtists
                if(responce!=null) {
                    editPref.putString(RESPONCE, responce);
                    editPref.apply();
                    fillArrayList();
                } else if (lastResponce.getString(RESPONCE,"").length()!=0) {
                    responce = lastResponce.getString(RESPONCE,"");
                    fillArrayList();
                }
                handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void fillArrayList(){
        listOfArtists = new ArrayList<>();
            try {
                JSONArray arrJson = new JSONArray(responce);
                for (int i=0; i<arrJson.length(); i++) {
                    JSONObject objArtist = arrJson.getJSONObject(i);
                    JSONArray arrGenres = objArtist.getJSONArray("genres");
                    String name = objArtist.getString("name");
                    String tracks = getCase(objArtist.getInt("tracks")
                            , getString(R.string.one_song)
                            , getString(R.string.few_song)
                            , getString(R.string.many_song))
                            + ", " + getCase(objArtist.getInt("albums")
                            , getString(R.string.one_alb)
                            , getString(R.string.few_alb)
                            , getString(R.string.many_alb));
                    String urlImage = objArtist.getJSONObject("cover").getString("small");
                    String urlBigImage = objArtist.getJSONObject("cover").getString("big");
                    String description = objArtist.getString("description");
                    String link = " — ";
                    String genres = "";
                    int numGenre = arrGenres.length();
                    for (int j=0;j<numGenre; j++) {
                        if (j==(numGenre-1))genres = genres + arrGenres.getString(j);
                        else genres = genres + arrGenres.getString(j)+", ";
                    }
                    try{
                        link = objArtist.getString("link");
                    } catch(JSONException e) {};
                    listOfArtists.add(new Artist(name, genres, tracks, urlImage, urlBigImage, description, link));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private void fillViews() {
        progressBar.setVisibility(View.GONE);
        if (responce!=null){
            AdapterForList adapter = new AdapterForList(ctx, listOfArtists);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                    Intent intent = new Intent(ctx, ActivityDetail.class);
                    Artist currArtist = listOfArtists.get(pos);
                    intent.putExtra("currArtist", currArtist);
                    startActivity(intent);
                }
            });
        } else {
            generateErrorButton();
        }
    }

    private void generateErrorButton() {
        errorBt = new Button(ctx);
        errorBt.setText(getString(R.string.error_button));
        errorBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToYandex();
                errorBt.setVisibility(View.GONE);
            }
        });
        containerLV.addView(errorBt);
        Toast.makeText(ctx, getString(R.string.error_message), Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            AlertDialog.Builder ad = new AlertDialog.Builder(ctx);
            ad.setTitle(getString(R.string.about));
            ad.setMessage(getString(R.string.about_descr));
            ad.setPositiveButton(getString(R.string.about_close), null);
            ad.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (handler!=null) handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /* метод возвращает слова "песня" и "альбом" в соответствии с их количеством*
    var1 - песня; var2 - песни; var3 - песен */
    public String getCase(int num, String var1, String var2, String var3) {
        int ostatok = num%10;
        if (num>5 & num<21) return num + " " + var3;
        if (ostatok==1) return num + " " +  var1;
        if (ostatok>1 & ostatok<5) return num + " " +  var2;
        if (ostatok>4 & ostatok<10) return num + " " +  var3;
        return num + " " +  var3;
    }

    //используем static handler со слабыми ссылками чтобы исключить утечки памяти.
    static class MyHandler extends Handler {
        WeakReference<MainActivity> wrActivity;

        public MyHandler(MainActivity activity) {
            wrActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = wrActivity.get();
            if(activity!=null) activity.fillViews();
        }
    }

}
