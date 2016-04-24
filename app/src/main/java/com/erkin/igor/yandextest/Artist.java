package com.erkin.igor.yandextest;

import java.io.Serializable;

public class Artist implements Serializable {
    String name;
    String genre;
    String tracks;
    String urlImage;
    String urlBigImage;
    String description;
    String link;

    Artist(String _name,
           String _genre,
           String _tracks,
           String _urlImage,
           String _urlBigImage,
           String _description,
           String _link)
    {
        name = _name;
        genre = _genre;
        tracks = _tracks;
        urlImage = _urlImage;
        urlBigImage = _urlBigImage;
        description = _description;
        link = _link;
    }
}
