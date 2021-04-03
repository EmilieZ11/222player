package org.ifaco.a222player;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Amin extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener {
    final IBinder iBinder = new LocalBinder();
    public static MediaPlayer mp;
    public static int resumePosition;
    AudioManager audioManager;
    boolean ongoingCall = false;
    PhoneStateListener phoneStateListener;
    TelephonyManager telephonyManager;
    ArrayList<Audio> audioList;
    int audioIndex = -1, criterion = 500;
    public static Audio activeAudio;
    public static final String ACTION_PLAY = "org.ifaco.a222player.ACTION_PLAY", ACTION_PAUSE = "org.ifaco.a222player.ACTION_PAUSE",
            ACTION_PREVIOUS = "org.ifaco.a222player.ACTION_PREVIOUS", ACTION_NEXT = "org.ifaco.a222player.ACTION_NEXT",
            ACTION_DESTROY = "org.ifaco.a222player.ACTION_DESTROY", PLAYER_TAG = "222Player";
    MediaSessionManager mediaSessionManager;
    MediaSessionCompat mediaSession;
    MediaControllerCompat.TransportControls transportControls;
    private static final int NOTIFICATION_ID = 101;
    public static ArrayList<Integer> shuffleHistory;
    public static boolean exists = false;
    public static Handler hDestroyer;

    BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            shuffleHistory = null;
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();
            if (audioIndex != -1 && audioIndex < audioList.size())
                activeAudio = audioList.get(audioIndex);//index is in a valid range
            else stopSelf();
            onChange(false, false);
        }
    }, becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size())
                activeAudio = audioList.get(audioIndex);//index is in a valid range
            else stopSelf();
        } catch (NullPointerException e) {
            stopSelf();
        }

        if (!requestAudioFocus()) stopSelf();
        if (mediaSession == null) {//mediaSessionManager == null
            initMediaSession(intent);
            initMediaPlayer(true);
        }
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        exists = true;
        hDestroyer = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                stopMedia();
                if (Mahdi.AminHandler != null) Mahdi.AminHandler.obtainMessage(0, 0).sendToTarget();
                stopSelf();
            }
        };
        if (Mahdi.displayMetrics != null)
            if (Mahdi.displayMetrics.widthPixels != 0) criterion = Mahdi.displayMetrics.widthPixels;

        // Perform one-time setup procedures
        callStateListener();
        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        registerReceiver(playNewAudio, new IntentFilter(Mahdi.Broadcast_PLAY_NEW_AUDIO));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exists = false;

        hDestroyer = null;
        if (mp != null) {
            stopMedia();
            mp.release();
            mp = null;
        }
        mediaSessionManager = null;
        mediaSession = null;
        transportControls = null;
        if (Mahdi.AminHandler != null) Mahdi.AminHandler.obtainMessage(0, 0).sendToTarget();

        removeAudioFocus();
        if (phoneStateListener != null)
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        try {
            unregisterReceiver(becomingNoisyReceiver);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            unregisterReceiver(playNewAudio);
        } catch (IllegalArgumentException ignored) {
        }
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }


    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mp == null) initMediaPlayer(true);
                else if (!mp.isPlaying()) mp.start();
                mp.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mp.isPlaying()) mp.stop();
                mp.release();
                mp = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mp.isPlaying()) mp.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mp.isPlaying()) mp.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (Mahdi.repeat) {
            case 0:
                pauseMedia();
                resumePosition = 0;
                break;
            case 1:
                skipToNext(true);
                break;
            case 2:
                resumePosition = 0;
                resumeMedia();
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        String msg;
        boolean SHOW = true;
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                msg = "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra;
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                msg = "MEDIA ERROR SERVER DIED " + extra;
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                msg = "MEDIA ERROR UNKNOWN " + extra;
                break;
            default:
                msg = "UNKNOWN!!! (" + what + " -:- " + extra + ")";
                SHOW = false;
                break;
        }
        if (SHOW)
            Toast.makeText(Amin.this, "MEDIA PLAYER ERROR: " + msg, Toast.LENGTH_SHORT).show();
        return true;//false; true to avoid noisy onCompletions
    }


    void initMediaPlayer(final boolean autoPlay) {
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (autoPlay) playMedia();
                else {
                    resumePosition = 0;
                    playing(1);
                    playing(0);
                }
            }
        });
        //mp.setOnBufferingUpdateListener(this);//onBufferingUpdate(MediaPlayer mediaPlayer, int i)
        //mp.setOnSeekCompleteListener(this);//onSeekComplete(MediaPlayer mediaPlayer)
        //mp.setOnInfoListener(this);//onInfo(MediaPlayer mediaPlayer, int i, int i1)
        mp.reset();//Reset so that the MediaPlayer is not pointing to another data source
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(activeAudio.getData());
        } catch (IOException | NullPointerException e) {
            Toast.makeText(Amin.this, e.getMessage(), Toast.LENGTH_LONG).show();
            stopSelf();
        }
        mp.prepareAsync();
    }

    public void playMedia() {
        if (mp == null) return;
        if (!mp.isPlaying()) {
            mp.start();

            buildNotification(PlaybackStatus.PLAYING);
            playing(1);
            updateAppWidget(1);
        }
    }

    public void stopMedia() {
        if (mp == null) return;
        if (mp.isPlaying()) mp.stop();

        removeNotification();
        playing(0);
        updateAppWidget(0);
    }

    public void pauseMedia() {
        if (mp == null) return;
        if (mp.isPlaying()) {
            mp.pause();
            try {
                resumePosition = mp.getCurrentPosition();
            } catch (IllegalStateException ignored) {
                resumePosition = 0;
            }// to avoid noisy onCompletions
        }
        buildNotification(PlaybackStatus.PAUSED);
        playing(0);
        updateAppWidget(2);
    }

    public void resumeMedia() {
        if (mp == null) return;
        if (!mp.isPlaying()) {
            mp.seekTo(resumePosition);
            mp.start();

            buildNotification(PlaybackStatus.PLAYING);
            playing(1);
            updateAppWidget(1);
        }
    }

    boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = 0;
        if (audioManager != null)
            result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    void callStateListener() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mp != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mp != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    void initMediaSession(Intent intent) {//throws RemoteException
        if (mediaSession != null) return;//mediaSessionManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mediaSession = new MediaSessionCompat(getApplicationContext(), PLAYER_TAG);
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);//MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext(false);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious(false);
            }//onStop() | onSeekTo(long position)
        });
        updateMetaData();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            MediaButtonReceiver.handleIntent(mediaSession, intent);
    }

    void updateMetaData() {
        if (activeAudio != null) mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getAlbumArt(Amin.this, activeAudio.getAlbumId()))
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                .build());
    }

    public static Bitmap getAlbumArt(Context context, long albumId) {
        String path = FileUtils.getPath(context,
                ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId));
        if (path != null) return BitmapFactory.decodeFile(path);
        else return null;
    }

    public void skipToNext(boolean onCompletion) {
        if (Amin.mp == null) return;
        boolean changed = true;
        if (Mahdi.shuffle) chooseRandom(false);
        else {
            if (audioIndex == audioList.size() - 1) {
                if (Mahdi.repeat == 1) {
                    audioIndex = 0;
                    activeAudio = audioList.get(audioIndex);
                } else {//IK HOU VAN YE!!!
                    Toast.makeText(Amin.this, R.string.endOfPlaylist, Toast.LENGTH_SHORT).show();
                    changed = false;
                }
            } else activeAudio = audioList.get(++audioIndex);
        }
        if (changed) onChange(true, onCompletion);
    }

    public void skipToPrevious(boolean onCompletion) {
        if (Amin.mp == null) return;
        boolean changed = true;
        if (Mahdi.shuffle) chooseRandom(true);
        else {
            if (audioIndex == 0) {
                if (Mahdi.repeat == 1) {
                    audioIndex = audioList.size() - 1;
                    activeAudio = audioList.get(audioIndex);
                } else {
                    Toast.makeText(Amin.this, R.string.startOfPlaylist, Toast.LENGTH_SHORT).show();
                    changed = false;
                }
            } else activeAudio = audioList.get(--audioIndex);
        }
        if (changed) onChange(true, onCompletion);
    }

    void onChange(boolean skip, boolean onCompletion) {
        if (skip) {
            new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);
            if (Mahdi.changeHandler != null)
                Mahdi.changeHandler.obtainMessage(audioIndex).sendToTarget();
        }

        boolean autoPlay = true;
        if (!onCompletion) try {
            autoPlay = mp.isPlaying();
        } catch (IllegalStateException | NullPointerException ignored) {
            autoPlay = false;
        }
        stopMedia();
        mp.reset();
        initMediaPlayer(autoPlay);
        updateMetaData();
    }

    void chooseRandom(boolean skipToPrevious) {
        if (shuffleHistory == null) shuffleHistory = new ArrayList<>();

        if (shuffleHistory.size() > 0 && skipToPrevious && shuffleHistory.get(shuffleHistory.size() - 1) < audioList.size()) {
            audioIndex = shuffleHistory.get(shuffleHistory.size() - 1);
            shuffleHistory.remove(shuffleHistory.size() - 1);
        } else {
            shuffleHistory.add(audioIndex);
            ArrayList<Integer> allNumbers = new ArrayList<>();
            int num = 0;
            for (int n = 0; n < audioList.size(); n++) {
                allNumbers.add(num);
                num += 1;
            }
            allNumbers.remove(audioIndex);
            audioIndex = allNumbers.get(new Random().nextInt(allNumbers.size()));
        }
        activeAudio = audioList.get(audioIndex);
    }

    void buildNotification(PlaybackStatus playbackStatus) {
        Context c = Amin.this;
        int notificationAction = R.drawable.play_3_ntf;
        PendingIntent play_pauseAction = null;
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.pause_3_ntf;
            play_pauseAction = playbackAction(c, 1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) play_pauseAction = playbackAction(c, 0);
        Bitmap LI = getAlbumArt(c, activeAudio.getAlbumId());
        if (LI != null) {
            int WH = (int) (criterion / 5);
            LI = Bitmap.createScaledBitmap(LI, WH, WH, false);
        } else LI = BitmapFactory.decodeResource(c.getResources(), R.mipmap.launcher);
        PendingIntent openMahdi = PendingIntent.getActivity(c, 0, new Intent(c, Mahdi.class), 0);

        Notification.Builder notificationBuilder;//String ncId = null;
        if (Build.VERSION.SDK_INT >= 26) {
            String ncId = PLAYER_TAG;
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(ncId, c.getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("222 Player");
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);

            notificationBuilder = (Notification.Builder) new Notification.Builder(c, ncId);
        } else notificationBuilder = (Notification.Builder) new Notification.Builder(c);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setColor(ContextCompat.getColor(c, R.color.ntfColour));
        notificationBuilder.setShowWhen(false)
                .setLargeIcon(LI)
                .setSmallIcon(R.drawable.ntf_1)
                .setContentTitle(activeAudio.getName())
                .setContentText(activeAudio.getAlbum())
                .setContentInfo(Mahdi.fixDur(activeAudio.getDur()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(false)//can user close the ntf
                .setContentIntent(openMahdi)
                .setDeleteIntent(playbackAction(c, 66));
        if (Mahdi.direction) notificationBuilder
                .addAction(R.drawable.skip_2_ntf_prev, null, playbackAction(c, 3))
                .addAction(notificationAction, null, play_pauseAction)
                .addAction(R.drawable.skip_2_ntf_next, null, playbackAction(c, 2));
        else notificationBuilder
                .addAction(R.drawable.skip_2_ntf_next, null, playbackAction(c, 2))
                .addAction(notificationAction, null, play_pauseAction)
                .addAction(R.drawable.skip_2_ntf_prev, null, playbackAction(c, 3));

        Notification n = NotificationExtras.buildWithBGResource(c, notificationBuilder, R.color.ntfBG, ContextCompat.getColor(c, R.color.ntfT));
        //NotificationManagerCompat.from(c).notify(NOTIFICATION_ID, n);
        NotificationManager NM = ((NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE));
        if (NM != null) NM.notify(NOTIFICATION_ID, n);//notificationBuilder.build()
    }

    void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancel(NOTIFICATION_ID);
    }

    public static PendingIntent playbackAction(Context context, int actionNumber) {
        Intent playbackAction = new Intent(context, Amin.class);
        switch (actionNumber) {
            case 0:
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 66:
                playbackAction.setAction(ACTION_DESTROY);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) transportControls.play();
        else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) transportControls.pause();
        else if (actionString.equalsIgnoreCase(ACTION_NEXT)) transportControls.skipToNext();
        else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) transportControls.skipToPrevious();
        else if (actionString.equalsIgnoreCase(ACTION_DESTROY)) {
            if (Amin.hDestroyer != null) Amin.hDestroyer.obtainMessage().sendToTarget();
        }
    }//transportControls.fastForward();

    void updateAppWidget(int playbackStatus) {
        try {
            PendingIntent.getBroadcast(Amin.this, UUID.randomUUID().hashCode(),
                    new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                            .putExtra("playbackStatus", playbackStatus), 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    void playing(int msg) {
        if (Mahdi.AminHandler != null) Mahdi.AminHandler.obtainMessage(msg).sendToTarget();
    }


    class LocalBinder extends Binder {
        Amin getService() {
            return Amin.this;
        }
    }
}
