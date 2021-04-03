package org.ifaco.a222player;

class Audio {//implements Serializable
    private String data, name, title, album, artist, mime;
    private long albumId;
    private int dur;

    Audio(String data, String name, String title, String album, String artist, long albumId, int dur, String mime) {
        this.data = data;
        this.name = name;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.albumId = albumId;
        this.dur = dur;
        this.mime = mime;
    }

    String getData() {
        return data;
    }

    void setData(String data) {
        this.data = data;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getTitle() {
        return title;
    }

    String getAlbum() {
        return album;
    }

    String getArtist() {
        return artist;
    }

    long getAlbumId() {
        return albumId;
    }

    int getDur() {
        return dur;
    }

    String getMime() {
        return mime;
    }
}
