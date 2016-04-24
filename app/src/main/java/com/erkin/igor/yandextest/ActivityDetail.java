package com.erkin.igor.yandextest;

import android.content.res.Configuration;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ActivityDetail extends AppCompatActivity {
    TextView tvGenre, tvTracks, tvLink, tvDescr;
    ImageView ivBigCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        getViews();

        Artist artist = (Artist) getIntent().getSerializableExtra("currArtist");
        ActionBar bar = getSupportActionBar();

        String goodDescription = getUpperCaseText(artist.description);//делаем первую букву описания заглавной
        tvGenre.setText(artist.genre);
        tvTracks.setText(artist.tracks);
        tvLink.setText(artist.link);
        tvDescr.setText(goodDescription);
        ivBigCover.setMinimumHeight(getScreenResolution());//устанавливаем минимальную высоту imageView = ширине экрана

        if (bar!=null) {
            bar.setTitle(artist.name);
            bar.setDisplayHomeAsUpEnabled(true); //включаем кнопку "назад"
            bar.setHomeButtonEnabled(true);
        }

        Glide.with(this)
                .load(artist.urlBigImage)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .crossFade(200)
                .into(ivBigCover);
    }

    private void getViews() {
        tvGenre = (TextView) findViewById(R.id.tvGenre);
        tvTracks = (TextView) findViewById(R.id.tvTracks);
        tvLink = (TextView) findViewById(R.id.tvLink);
        tvDescr = (TextView) findViewById(R.id.tvDescr);
        ivBigCover = (ImageView) findViewById(R.id.ivBigCover);
    }

    private String getUpperCaseText(String lastDescription) {
        String buffer = lastDescription.substring(1);
        String goodDescription = lastDescription.substring(0,1).toUpperCase() + buffer;
        return goodDescription;
    }

    private int getScreenResolution() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ivBigCover.setMinimumHeight(getScreenResolution());
        super.onConfigurationChanged(newConfig);
    }
}
