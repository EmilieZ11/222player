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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Amin extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
        AudioManager.OnAudioFocusChangeListener {
    final IBinder iBinder = new LocalBinder();
    public static MediaPlayer mp;
    public static int audioIndex = -1, resumePosition = 0;
    AudioManager audioManager;
    boolean ongoingCall = false;
    PhoneStateListener phoneStateListener;
    TelephonyManager telephonyManager;
    public static ArrayList<Audio> audioList;
    int criterion = 2;
    public static Audio activeAudio;
    public static final String ACTION_PLAY = Mahdi.PKG + ".ACTION_PLAY", ACTION_PAUSE = Mahdi.PKG + ".ACTION_PAUSE",
            ACTION_PREVIOUS = Mahdi.PKG + ".ACTION_PREVIOUS", ACTION_NEXT = Mahdi.PKG + ".ACTION_NEXT",
            ACTION_DESTROY = Mahdi.PKG + ".ACTION_DESTROY", PLAYER_TAG = "222Player";
    MediaSessionManager mediaSessionManager;
    MediaSessionCompat mediaSession;
    MediaControllerCompat.TransportControls transportControls;
    private static final int NOTIFICATION_ID = 101;
    public static ArrayList<Integer> shuffleHistory;
    public static boolean exists = false, playing = false;
    Context c;
    Bitmap defLI;
    PendingIntent openMahdi;

    BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            shuffleHistory = null;
            try {
                StorageUtil storage = new StorageUtil(c);
                audioList = storage.loadAudio();
                audioIndex = storage.loadAudioIndex();
                if (audioIndex != -1 && audioIndex < audioList.size())
                    activeAudio = audioList.get(audioIndex);//index is in a valid range
                else safeExit();
            } catch (Exception ignored) {
                safeExit();
            }
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
            StorageUtil storage = new StorageUtil(c);
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();
            if (audioIndex != -1 && audioIndex < audioList.size())
                activeAudio = audioList.get(audioIndex);//index is in a valid range
            else safeExit();
        } catch (Exception ignored) {
            safeExit();
        }

        if (!requestAudioFocus()) {
            Toast.makeText(c, getResources().getString(R.string.couldNotGainAudioFocus), Toast.LENGTH_LONG).show();
            safeExit();
        }
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
        c = getApplicationContext();
        exists = true;
        if (Mahdi.dm != null) if (Mahdi.dm.widthPixels != 0) criterion = (int) Mahdi.dm.density;

        // Perform one-time setup procedures
        callStateListener();
        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        registerReceiver(playNewAudio, new IntentFilter(Mahdi.Broadcast_PLAY_NEW_AUDIO));

        //To create a Notification easily
        Bitmap icon = BitmapFactory.decodeResource(c.getResources(), R.drawable.launcher_1_blue),
                bmOverlay = Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), icon.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawColor(ContextCompat.getColor(c, R.color.textColorPrimary));
        Matrix amin = new Matrix();
        amin.setTranslate((float) (-criterion * 2), 0f);
        canvas.drawBitmap(icon, amin, null);
        defLI = bmOverlay;
        openMahdi = PendingIntent.getActivity(c, 0, new Intent(c, Mahdi.class), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exists = false;

        if (mp != null) {
            stopMedia(true);
            try {
                mp.release();
            } catch (Exception ignored) {
            }
            mp = null;
        }
        mediaSessionManager = null;
        mediaSession = null;
        transportControls = null;
        if (Mahdi.AminHandler != null) Mahdi.AminHandler.obtainMessage(0, 0).sendToTarget();

        if (phoneStateListener != null)
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        try {
            removeAudioFocus();
            unregisterReceiver(becomingNoisyReceiver);
        } catch (Exception ignored) {
        }
        try {
            unregisterReceiver(playNewAudio);
        } catch (Exception ignored) {
        }
        new StorageUtil(c).clearCachedAudioPlaylist();
    }


    @Override
    public void onAudioFocusChange(int focusState) {
        if (mp == null) return;
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                /*if (mp == null) initMediaPlayer(true);
                else if (!playing) mp.start();*/
                resumeMedia();
                mp.setVolume(Mahdi.vVolume, Mahdi.vVolume);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                /*if (playing) mp.stop();
                try {
                    mp.release();
                } catch (Exception ignored) {
                }
                mp = null;*/
                if (playing) pauseMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (playing) pauseMedia();//mp.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                float v = 0f;
                if (Mahdi.vVolume > 0f) v = 0.1f;
                if (playing) mp.setVolume(v, v);
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playing = false;
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
        int msg = R.string.unknownErrorOccurred;
        String desc = " (" + what + " : " + extra + ")";
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                String DESC = "";
                switch (extra) {
                    case MediaPlayer.MEDIA_ERROR_IO:
                        msg = R.string.cannotOpenFile;
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        msg = R.string.mpUnreadable;
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        msg = R.string.mpUnsupported;
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        msg = R.string.mpTimedOut;
                        break;
                    case -2147483648:
                        msg = R.string.lowSystemLevel;
                        break;
                    default:
                        DESC = desc;
                        break;
                }
                Toast.makeText(c, getResources().getString(msg) + DESC, Toast.LENGTH_SHORT).show();
                safeExit();
                return true;
            case -38://IT MEANS YOU'VE DONE SOMETHING BEFORE THE PREPARATION OF MEDIAPLAYER.
                Toast.makeText(c, desc, Toast.LENGTH_SHORT).show();
                return true;
            default://MediaPlayer.MEDIA_ERROR_UNKNOWN
                Toast.makeText(c, getResources().getString(msg) + desc, Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Toast.makeText(c, R.string.mpNotSeekable, Toast.LENGTH_SHORT).show();
                return true;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Toast.makeText(c, R.string.badInterleaving, Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }


    void initMediaPlayer(final boolean autoPlay) {
        mp = new MediaPlayer();//setOnBufferingUpdateListener & setOnSeekCompleteListener
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        mp.setOnInfoListener(this);
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
        mp.reset();//Reset so that the MediaPlayer is not pointing to another data source
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        else try {
            mp.setAudioAttributes(new AudioAttributes.Builder()
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
        } catch (Exception ignored) {
        }
        mp.setVolume(Mahdi.vVolume, Mahdi.vVolume);
        try {
            mp.setDataSource(activeAudio.getData());
        } catch (Exception e) {
            String msg = "\n\n";
            if (e.getMessage() != null) msg = msg + e.getMessage();
            else msg = msg + e.getClass().getName();
            Toast.makeText(c, getResources().getString(R.string.cannotOpenFile) + msg, Toast.LENGTH_LONG).show();
            safeExit();
        }
        mp.prepareAsync();
    }

    public void playMedia() {
        if (mp == null) return;
        if (!playing) {
            mp.start();
            playing = true;

            buildNotification(PlaybackStatus.PLAYING);
            playing(1);
            updateAppWidget(1);
        }
    }

    public void stopMedia(boolean removeNtf) {
        if (mp == null) return;
        if (playing) {
            mp.stop();
            playing = false;
        }

        if (removeNtf) removeNotification();
        playing(0);
        updateAppWidget(0);
    }

    public void pauseMedia() {
        if (mp == null) return;
        if (playing) {
            mp.pause();
            playing = false;
            try {
                resumePosition = mp.getCurrentPosition();
            } catch (Exception ignored) {
                resumePosition = 0;
            }
        }
        buildNotification(PlaybackStatus.PAUSED);
        playing(0);
        updateAppWidget(2);
    }

    public void resumeMedia() {
        if (mp == null) return;
        if (!playing) {
            mp.seekTo(resumePosition);
            mp.start();
            playing = true;

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
        mediaSession = new MediaSessionCompat(c, PLAYER_TAG);
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
        try {
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getAlbumArt(c, activeAudio.getAlbumId()))
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                    .build());
        } catch (Exception ignored) {
        }
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
                } else {
                    Toast.makeText(c, R.string.endOfPlaylist, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(c, R.string.startOfPlaylist, Toast.LENGTH_SHORT).show();
                    changed = false;
                }
            } else activeAudio = audioList.get(--audioIndex);
        }
        if (changed) onChange(true, onCompletion);
    }

    void onChange(boolean skip, boolean onCompletion) {
        if (skip) {
            new StorageUtil(c).storeAudioIndex(audioIndex);
            if (Mahdi.changeHandler != null)
                Mahdi.changeHandler.obtainMessage(audioIndex).sendToTarget();
        }

        boolean autoPlay = true;
        if (!onCompletion) try {
            autoPlay = playing;
        } catch (Exception ignored) {
            autoPlay = false;
        }
        stopMedia(false);
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
        int notificationAction = R.drawable.play_3_ntf;
        PendingIntent play_pauseAction = null;
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.pause_3_ntf;
            play_pauseAction = playbackAction(c, 1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) play_pauseAction = playbackAction(c, 0);
        Bitmap LI = getAlbumArt(c, activeAudio.getAlbumId());
        if (LI != null) {
            int WH = (int) (criterion * 72);
            LI = Bitmap.createScaledBitmap(LI, WH, WH, false);
        } else LI = defLI;

        Notification.Builder notificationBuilder;//String ncId = null;
        if (Build.VERSION.SDK_INT >= 26) {
            String ncId = PLAYER_TAG;
            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(ncId, c.getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getResources().getString(R.string.app_name));
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);

            notificationBuilder = new Notification.Builder(c, ncId);
        } else notificationBuilder = new Notification.Builder(c);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setColor(ContextCompat.getColor(c, R.color.ntfColour));
        notificationBuilder.setShowWhen(false)
                .setLargeIcon(LI)
                .setSmallIcon(R.drawable.ntf_2_white)
                .setContentTitle(activeAudio.getName())
                .setContentText(activeAudio.getAlbum())
                .setContentInfo(Mahdi.fixDur(activeAudio.getDur()))
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(false)//can user close the ntf
                .setContentIntent(openMahdi)
                .setDeleteIntent(playbackAction(c, 66));
        if (Mahdi.dirLtr) notificationBuilder
                .addAction(R.drawable.skip_2_ntf_prev, null, playbackAction(c, 3))
                .addAction(notificationAction, null, play_pauseAction)
                .addAction(R.drawable.skip_2_ntf_next, null, playbackAction(c, 2));
        else notificationBuilder
                .addAction(R.drawable.skip_2_ntf_next, null, playbackAction(c, 2))
                .addAction(notificationAction, null, play_pauseAction)
                .addAction(R.drawable.skip_2_ntf_prev, null, playbackAction(c, 3));

        //Notification n = NotificationExtras.buildWithBGResource(c, notificationBuilder, R.color.ntfBG,
        //ContextCompat.getColor(c, R.color.ntfT));
        //NotificationManagerCompat.from(c).notify(NOTIFICATION_ID, n);//for Compat version
        NotificationManager NM = ((NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE));
        if (NM != null) NM.notify(NOTIFICATION_ID, notificationBuilder.build());//n
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
        else if (actionString.equalsIgnoreCase(ACTION_DESTROY)) safeExit();
    }//transportControls.fastForward();

    void updateAppWidget(int playbackStatus) {
        try {
            PendingIntent.getBroadcast(c, UUID.randomUUID().hashCode(),
                    new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                            .putExtra("playbackStatus", playbackStatus), 0).send();
        } catch (Exception ignored) {
        }
    }

    void playing(int msg) {
        if (Mahdi.AminHandler != null) Mahdi.AminHandler.obtainMessage(msg).sendToTarget();
    }

    void safeExit() {
        if (Mahdi.destroyMe != null) Mahdi.destroyMe.obtainMessage().sendToTarget();
        else stopSelf();
    }


    class LocalBinder extends Binder {
        Amin getService() {
            return Amin.this;
        }
    }
}
