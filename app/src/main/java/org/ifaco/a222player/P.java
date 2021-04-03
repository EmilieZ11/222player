package org.ifaco.a222player;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class P {
    static final String PLAYLIST = "playlist", ID = "id", NAME = "name", CREATED = "date_created", MODIFIED = "date_modified",
            DESC = "description", MEDIA = "media", PATH = "path", N = "number", PLID = "playlist_id";

    @Entity
    public static class Playlist {//None of the columns must be private!
        @PrimaryKey(autoGenerate = true)
        long id;

        @ColumnInfo(name = NAME)
        String name;

        @ColumnInfo(name = CREATED)
        long dateCreated;

        @ColumnInfo(name = MODIFIED)
        long dateModified;

        @Nullable
        @ColumnInfo(name = DESC)
        String description;

        Playlist(String name) {
            this.name = name;
            this.dateCreated = Calendar.getInstance().getTimeInMillis();
            this.dateModified = Calendar.getInstance().getTimeInMillis();
        }

        Playlist set(String attr, Object value) {
            switch (attr) {
                case NAME:
                    this.name = (String) value;
                    break;
                case CREATED:
                    this.dateCreated = (long) value;
                    break;
                case MODIFIED:
                    this.dateModified = (long) value;
                    break;
                case DESC:
                    this.description = (String) value;
                    break;
            }
            return this;
        }

        @Ignore
        ArrayList<Media> items;

        ArrayList<Media> getItems() {
            return items;
        }

        void setItems(ArrayList<Media> items) {
            this.items = items;
        }
    }

    @Entity(foreignKeys = @ForeignKey(entity = Playlist.class, parentColumns = ID, childColumns = PLID,
            onDelete = ForeignKey.CASCADE), indices = {@Index(PLID)})
    public static class Media {
        @PrimaryKey(autoGenerate = true)
        long id;

        @ColumnInfo(name = PATH)
        String path;

        @ColumnInfo(name = N)
        long number;

        @ColumnInfo(name = PLID)
        long playlistId;

        Media(String path, long number, long playlistId) {
            this.path = path;
            this.number = number;
            this.playlistId = playlistId;
        }

        Media set(String attr, Object value) {//BE CAREFUL OF USING THE WORD "value"!
            switch (attr) {
                case PATH:
                    this.path = (String) value;
                    break;
                case N:
                    this.number = (long) value;
                    break;
                case PLID:
                    this.playlistId = (long) value;
                    break;
            }
            return this;
        }
    }

    @Dao
    public interface PlaylistDao {
        @Query("SELECT * FROM " + PLAYLIST)
        List<Playlist> getAll();

        @Query("SELECT * FROM " + MEDIA + " WHERE " + PLID + " LIKE :playlistId")
        List<Media> getItems(long playlistId);

        @Query("SELECT * FROM " + PLAYLIST + " WHERE " + ID + " IN (:ids)")
        List<Playlist> loadAllByIds(long[] ids);

        @Query("SELECT * FROM " + MEDIA + " WHERE " + ID + " IN (:ids)")
        List<Media> loadItemsByIds(long[] ids);

        @Query("SELECT * FROM " + PLAYLIST + " WHERE " + NAME + " LIKE :name LIMIT 1")
        Playlist findByName(String name);

        @Query("SELECT * FROM " + MEDIA + " WHERE " + PLID + " LIKE :playlistId AND " + PATH + " LIKE :path LIMIT 1")
        Media findItemByPath(long playlistId, String path);

        @Query("SELECT * FROM " + MEDIA + " WHERE " + PLID + " LIKE :playlistId AND " + N + " LIKE :number LIMIT 1")
        Media findItemByNumber(long playlistId, long number);

        @Query("SELECT * FROM " + PLAYLIST + " WHERE " + ID + " LIKE :id LIMIT 1")
        Playlist get(long id);

        @Query("SELECT * FROM " + MEDIA + " WHERE " + ID + " LIKE :id LIMIT 1")
        Media getItem(long id);

        @Insert
        long insert(Playlist playlist);
        //REPLACE strategy is dangerous for the media the playlist contains!
        //"long" can be replaced by "void" and "long[]".

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long insertItem(Media media);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        long[] insertItems(Media... media);

        //@Insert//(onConflict = OnConflictStrategy.REPLACE)
        //void insertWithItems(Playlist playlist, List<Media> media);

        @Delete
        void delete(Playlist playlist);

        @Delete
        void deleteItem(Media media);

        @Update
        void update(Playlist playlist);

        @Update
        void updateItem(Media media);
    }

    @Database(entities = {Playlist.class, Media.class}, version = 1, exportSchema = false)
    public abstract static class Playlists extends RoomDatabase {
        public abstract PlaylistDao plDAO();
    }
}
