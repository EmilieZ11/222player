package org.ifaco.a222player;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Mahdi extends AppCompatActivity {
    ConstraintLayout mContainer, mVVFrame, mBody, mCrop, mAddSubtitle, mPlayerBody, mPanel, mShuffle, mMoveB, mPlayPause, mMoveF, mRepeat,
            mNavigation, mNavList, mNavLoading, mFileInfo, mLoading;
    View mMotor, mTouchSensor, mPanelBg, mSeekBarReplica, mSideBtnsBorderRestL, mShuffleBG, mMoveBBG, mSideBtnsBorderL, mSideBtnsBorderR,
            mMoveFBG, mRepeatBG, mSideBtnsBorderRestR;
    Toolbar mToolbar;
    ImageView mShwBG, mCropBG, mCropBT, mAddSubtitleBG, mAddSubtitleBT, mAlbumArt, mShuffleBT, mMoveBBT, mPlayPauseBG, mPlayPauseBT,
            mMoveFBT, mRepeatBT, mNLCircle1, mNLCircle2, mToastIV, mLoadingIV;
    SeekBar mSeekBar;
    TextView mDur, mRepeatTV, mToastTV, mLoadingTV, mLoadingVersion;
    ScrollView mFoldersSV, mSoundsSV, mBasicMessages;
    LinearLayout mFolders, mSounds, mToast, mBasicMessagesLL;
    ProgressBar mLoadingPB;

    //file_info.xml
    ConstraintLayout fiBody, fiTitle, fiCL, fiAlbumArtDiv, fiNameDiv;
    ImageView fiTIcon, fiClose, fiAlbumArt, fiSave, fiDelete;
    TextView fiTText, fiName;
    ScrollView fiSV;
    LinearLayout fiLL;
    EditText fiNameEdit;

    public static int loadDur1 = 179, navDur = 78, repeat = 0, adtWPMax = 5;
    final int mtbSwitch = 0, mtbRefresh = 1, mtbLast = 2, controlPlayerDur = 48, mLoadingDur = 480, exitDur = 48, hidePanelDur = 111,
            highestVolume = 15, setupImgDur = 22, fiDur = 222, setupItemDur = 11, highlightCPAlpha = 111, mNLCircleDur = 973,
            doubleTouchDur = 179, loadDur2 = 920, videoEachMove = 5000;
    boolean playing = false, changingTheSeekBar = false, serviceBound = false, checkingFU = false, exiting = false, isNavOpen = false,
            notFirst, reCreation = false, video = false, vPlaying = false, hiddenPanel = false, touchMovedEnough = false,
            keepScreen = false, showingBM = false, bmCancellable = true, fiCanSave = false, fileInfoOpened = false, settingItems = false,
            mNLAnim = false, forceVideo = false, gotIncomingMedia = false, changeColorPrimary = false, changeTCP = false,
            settingFIcons = false, settingMIcons = false, mTSLeft = false, doubleTouch = false, swipeNav = false, vPlayOnResume = false,
            fArrangementAsc = true, mArrangementAsc = true, cancelMediaLoading = false, touchXMovedEnough = false;
    ValueAnimator reproduction;
    Amin player;
    ArrayList<Audio> audioList;
    public static final String PKG = "org.ifaco.a222player", Broadcast_PLAY_NEW_AUDIO = PKG + ".PlayNewAudio", cfuTag = "checkForUpdates",
            cloudFolder = "http://ifaco.org/android/222player/", ver = "last_version.txt", app = "222_Player.apk", adtWP = "adtWP";
    private static final int permWriteExerStor = 630, exitingDelay = 4080;
    public static Handler AminHandler, changeHandler, destroyMe;
    public static ArrayList<Folder> folders;
    ActionBar mToolbarAB;
    File updates;
    ProgressDialog dlDialogue;
    RequestQueue cfuQueue;
    String version = null;
    ScrollView[] navLists;
    public static DisplayMetrics dm = new DisplayMetrics();
    int openedNav = 0, audioIndex = -1, audioListIndex = -1, currentOpenedList = -1, videoIndex = -1, videoListIndex = -1, resumePos = 0,
            crop = 1, colorPrimary = 0, defColorPrimary = 0, textColorPrimary = 0, defTextColorPrimary = 0, swipeToSkipMin = 111,
            swipeNavMin = 48, fArrangement = 0, mArrangement = 0, font = 3;
    ArrayList<Integer> folClItemIds, sndClItemIds;
    int[] backfulLists;
    final float mSBsDisabled = 0.58f, leastOpacity = 0.4f, opacityEachMove = 0.04f, vVolumeEachMove = 0.075f, menuSwitchRatio = 0.7f;
    public static boolean shuffle = false, dirLtr = true;
    SharedPreferences sp;
    TextView mTBTitle;
    MediaPlayer mVVP;
    ArrayList<Video> videoList;
    Video activeVideo = null;
    float y1, yMove, x1, xMove, x2, fiSaveDisabled, touchMSensibility = 22f, fontRatio = 1f;//y2
    float[] touchLMoved, touchRMoved, touchXMoved;
    VideoView mVV;
    ArrayList<Integer> shuffleHistory, mIconIds, fIconIds;
    Handler iconFHandler, iconMHandler, loadedHandler, itemsFinishedHandler, itemLoadedHandler;
    ArrayList<GotBmp> draughtFIcons, draughtMIcons;
    String[] fileInfoList, fonts;
    ArrayList<ConstraintLayout> draughtFolders, draughtMedia;
    public static int[] resWallpapers = {R.drawable.music_bg_1, R.drawable.music_bg_2, R.drawable.music_bg_3};
    boolean[] wpBooleans = new boolean[]{true, true, true, false};
    ArrayList<Bitmap> wallpapers;
    public static final String[] supportedRtlLangs = {"fa"}, aSupport = {"3gp", "mp4", "m4a", "aac", "ts", "flac", "gsm", "mid", "xmf",
            "mxmf", "rtttl", "rtx", "ota", "imy", "mp3", "wav", "ogg", "mkv"}, vSupport = {"3gp", "mp4", "ts", "webm", "mkv"};
    Context c;
    ItemMaker mediaMaker, folderMaker;
    AnimatorSet mNLCircle;
    public static float vVolume = 1f;
    public static final float navBtnRatio = 0.28f;
    Bitmap sdFolder, isFolder;

    public static View.OnClickListener doNothing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Amin.LocalBinder binder = (Amin.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {//USE THIS: savedInstanceState
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        mContainer = findViewById(R.id.mContainer);
        mMotor = findViewById(R.id.mMotor);//translationX, translationY, scaleX, scaleY, rotationX, rotationY
        mVVFrame = findViewById(R.id.mVVFrame);
        mTouchSensor = findViewById(R.id.mTouchSensor);
        mBody = findViewById(R.id.mBody);
        mShwBG = findViewById(R.id.mShwBG);
        mToolbar = findViewById(R.id.mToolbar);
        mCrop = findViewById(R.id.mCrop);
        mCropBG = (ImageView) mCrop.getChildAt(0);
        mCropBT = (ImageView) mCrop.getChildAt(1);
        mAddSubtitle = findViewById(R.id.mAddSubtitle);
        mAddSubtitleBG = (ImageView) mAddSubtitle.getChildAt(0);
        mAddSubtitleBT = (ImageView) mAddSubtitle.getChildAt(1);

        mPlayerBody = findViewById(R.id.mPlayerBody);
        mAlbumArt = findViewById(R.id.mAlbumArt);
        mPanelBg = findViewById(R.id.mPanelBg);
        mSeekBarReplica = findViewById(R.id.mSeekBarReplica);
        mSeekBar = findViewById(R.id.mSeekBar);

        mPanel = findViewById(R.id.mPanel);
        mDur = (TextView) mPanel.getChildAt(0);
        mSideBtnsBorderRestL = mPanel.getChildAt(1);
        mShuffle = findViewById(R.id.mShuffle);
        mShuffleBG = mShuffle.getChildAt(0);
        mShuffleBT = (ImageView) mShuffle.getChildAt(1);
        mMoveB = findViewById(R.id.mMoveB);
        mMoveBBG = mMoveB.getChildAt(0);
        mMoveBBT = (ImageView) mMoveB.getChildAt(1);
        mSideBtnsBorderL = mPanel.getChildAt(4);
        mPlayPause = findViewById(R.id.mPlayPause);
        mPlayPauseBG = (ImageView) mPlayPause.getChildAt(0);
        mPlayPauseBT = (ImageView) mPlayPause.getChildAt(1);
        mSideBtnsBorderR = mPanel.getChildAt(6);
        mMoveF = findViewById(R.id.mMoveF);
        mMoveFBG = mMoveF.getChildAt(0);
        mMoveFBT = (ImageView) mMoveF.getChildAt(1);
        mRepeat = findViewById(R.id.mRepeat);
        mRepeatBG = mRepeat.getChildAt(0);
        mRepeatBT = (ImageView) mRepeat.getChildAt(1);
        mRepeatTV = (TextView) mRepeat.getChildAt(2);
        mSideBtnsBorderRestR = mPanel.getChildAt(9);

        mNavigation = findViewById(R.id.mNavigation);
        mNavList = findViewById(R.id.mNavList);
        mFoldersSV = findViewById(R.id.mFoldersSV);
        mFolders = findViewById(R.id.mFolders);
        mSoundsSV = findViewById(R.id.mSoundsSV);
        mSounds = findViewById(R.id.mSounds);
        mNavLoading = findViewById(R.id.mNavLoading);
        mNLCircle1 = findViewById(R.id.mNLCircle1);
        mNLCircle2 = findViewById(R.id.mNLCircle2);
        mToast = findViewById(R.id.mToast);
        mToastIV = findViewById(R.id.mToastIV);
        mToastTV = findViewById(R.id.mToastTV);
        mFileInfo = findViewById(R.id.mFileInfo);
        mBasicMessages = findViewById(R.id.mBasicMessages);
        mBasicMessagesLL = (LinearLayout) mBasicMessages.getChildAt(0);
        mLoading = findViewById(R.id.mLoading);
        mLoadingIV = findViewById(R.id.mLoadingIV);
        mLoadingTV = findViewById(R.id.mLoadingTV);
        mLoadingPB = findViewById(R.id.mLoadingPB);
        mLoadingVersion = findViewById(R.id.mLoadingVersion);

        c = getApplicationContext();
        notFirst = false;
        for (int rtl = 0; rtl < supportedRtlLangs.length; rtl++)
            if (Locale.getDefault().getLanguage().equals(supportedRtlLangs[rtl])) {
                mContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                dirLtr = false;
            }
        updates = new File(getCacheDir(), app);
        if (updates.exists()) updates.delete();//updates.deleteOnExit();?????
        try {
            version = c.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ignored) {//PackageManager.NameNotFoundException
        }
        mLoadingVersion.setText("v" + version);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        sp = PreferenceManager.getDefaultSharedPreferences(c);
        video = sp.getBoolean("video", false);
        vVolume = sp.getFloat("vVolume", 1f);
        fileInfoList = getResources().getStringArray(R.array.fileInfoList);
        if (sp.contains("fArrangementAsc"))
            fArrangementAsc = sp.getBoolean("fArrangementAsc", true);
        if (sp.contains("mArrangementAsc"))
            mArrangementAsc = sp.getBoolean("mArrangementAsc", true);
        if (sp.contains("fArrangement")) fArrangement = sp.getInt("fArrangement", 0);
        if (sp.contains("mArrangement")) mArrangement = sp.getInt("mArrangement", 0);
        if (sp.contains("font")) font = sp.getInt("font", font);
        if (sp.contains("fontRatio")) fontRatio = sp.getFloat("fontRatio", fontRatio);
        fonts = getResources().getStringArray(R.array.sFonts);

        // Primary Colours
        defColorPrimary = ContextCompat.getColor(c, R.color.colorPrimary);
        colorPrimary = defColorPrimary;
        if (sp.contains("colorPrimary")) {
            colorPrimary = sp.getInt("colorPrimary", colorPrimary);
            changeColorPrimary = true;//if (colorPrimary != defColorPrimary)
        }
        defTextColorPrimary = ContextCompat.getColor(c, R.color.textColorPrimary);
        textColorPrimary = defTextColorPrimary;
        if (sp.contains("textColorPrimary")) {
            textColorPrimary = sp.getInt("textColorPrimary", textColorPrimary);
            changeTCP = true;
        }

        // Incoming Media
        if (getIntent() != null && getIntent().getData() != null) {
            gotIncomingMedia = true;
            try {
                File f = FileUtils.getFile(c, getIntent().getData());
                String extention = MimeTypeMap.getFileExtensionFromUrl(f.getPath());
                if (extention != null) {
                    String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
                    if (mime != null) {
                        if (mime.substring(0, 5).equals("video")) {
                            video = true;
                            forceVideo = true;
                        } else if (mime.substring(0, 5).equals("audio")) video = false;
                    }
                }
            } catch (Exception ignored) {
                gotIncomingMedia = false;
            }
        }

        // Toolbar & Navigation
        if (changeColorPrimary && colorPrimary != defColorPrimary)
            mToolbar.setPopupTheme(R.style.TBPopupTheme1CngCP);
        setSupportActionBar(mToolbar);
        mToolbarAB = getSupportActionBar();
        for (int g = 0; g < mToolbar.getChildCount(); g++) {
            View getTitle = mToolbar.getChildAt(g);
            if (getTitle.getClass().getName().equalsIgnoreCase("androidx.appcompat.widget.AppCompatTextView") &&
                    ((TextView) getTitle).getText().toString().equals(getResources().getString(R.string.app_name))) {
                mTBTitle = ((TextView) getTitle);
                mTBTitle.setTypeface(Typeface.create(fonts[font], Typeface.BOLD));
            }
        }
        Drawable ovIcon = (Drawable) resizeByRatio(c, ContextCompat.getDrawable(c, R.drawable.overflow_3_blue),
                navBtnRatio, true);
        if (ovIcon != null) {
            if (changeColorPrimary)
                ovIcon.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
            mToolbar.setOverflowIcon(ovIcon);
        }
        Drawable navIcon = (Drawable) resizeByRatio(c, mToolbar.getNavigationIcon(), navBtnRatio, true);
        if (navIcon != null) {
            if (changeColorPrimary)
                navIcon.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
            mToolbar.setNavigationIcon(navIcon);
        }
        if (changeColorPrimary) {
            mToolbar.setTitleTextColor(colorPrimary);
            mToolbar.setSubtitleTextColor(colorPrimary);
        }
        if (changeTCP) mToolbar.setBackgroundColor(textColorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNavList(true);
            }
        });
        navLists = new ScrollView[]{mFoldersSV, mSoundsSV};
        backfulLists = new int[]{1};
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNavList(false);
            }
        });
        mNavList.setOnClickListener(doNothing);
        for (int b = 0; b < backfulLists.length; b++) {
            ConstraintLayout clBack = (ConstraintLayout)
                    ((LinearLayout) navLists[backfulLists[b]].getChildAt(0)).getChildAt(0);
            LinearLayout llBack = (LinearLayout) clBack.getChildAt(0);
            ImageView iv = (ImageView) llBack.getChildAt(0);
            TextView tv = (TextView) llBack.getChildAt(1);

            final int B = b;
            clBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelLoadings(false, true);
                    changeNavList(backfulLists[B] - 1, true);
                }
            });
            if (changeColorPrimary) {
                iv.setColorFilter(colorPrimary);
                tv.setTextColor(colorPrimary);
            }
            if (changeTCP)
                llBack.setBackgroundColor(Color.argb(222, Color.red(textColorPrimary), Color.green(textColorPrimary),
                        Color.blue(textColorPrimary)));
            if (!dirLtr) iv.setRotation(180f);
            fixTVFont(tv, Typeface.NORMAL);
        }
        isFolder = folderMark(R.drawable.folder_1_blue, R.drawable.mobile_1_white);
        sdFolder = folderMark(R.drawable.folder_1_blue, R.drawable.sd_card_1_white);
        mFileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileInfo(false, null);
            }
        });

        // Handlers for Amin
        AminHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                playing(inputMessage.what == 1);
                if (inputMessage.obj != null) {
                    mSeekBar.setEnabled(false);
                    audioList = null;
                    audioListIndex = -1;
                    audioIndex = -1;
                    highlightItem(0, -1);
                    highlightItem(1, -1);
                    updateDescription(false);
                }
            }
        };
        changeHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                audioIndex = inputMessage.what;
                if (audioListIndex == currentOpenedList) highlightItem(1, audioIndex);
                updateDescription(true);
            }
        };
        destroyMe = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                destroyAmin();
            }
        };

        // Recreation of Amin
        //launchMode="singleTop" SUFFICES US FROM THIS (reCreation process), although we need reCreation for applying new settings.
        mSeekBar.setEnabled(false);
        if (Amin.exists) {
            reCreation = true;
            video = false;
            if (!serviceBound) {
                Intent playerIntent = new Intent(c, Amin.class);
                bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }

        // Loading Animations
        ValueAnimator anLoad = VA(mMotor, "translationX", loadDur1, 1f, 0f);
        anLoad.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (video || forceVideo) switchMedia(false, false, false);
                if (!reCreation || gotIncomingMedia) {
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        if (ContextCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(Mahdi.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
                                Toast.makeText(c, R.string.accessStorage,
                                        Toast.LENGTH_LONG).show();
                            else ActivityCompat.requestPermissions(Mahdi.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permWriteExerStor);
                        } else start(true);
                    } else start(false);
                }

                ConstraintLayout.LayoutParams mSeekBarReplicaLP = (ConstraintLayout.LayoutParams) mSeekBarReplica.getLayoutParams();
                int sbrMar = (int) (mSeekBar.getHeight() / 2.7);
                mSeekBarReplicaLP.leftMargin = sbrMar;
                mSeekBarReplicaLP.rightMargin = sbrMar;
                mSeekBarReplica.setLayoutParams(mSeekBarReplicaLP);

                swipeToSkipMin = (int) (dm.density * 105);
                swipeNavMin = (int) (dm.density * 30);
                touchMSensibility = dm.density * 15f;
            }
        });
        ValueAnimator anLoad2 = VA(mMotor, "rotationX", loadDur2, 180f, 0f);
        anLoad2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    if (!video) {
                        Drawable newIcon = (Drawable) resizeByRatio(c, R.drawable.video_1_blue, menuSwitchRatio, true);
                        if (newIcon != null) {
                            if (changeColorPrimary)
                                newIcon.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
                            mToolbar.getMenu().getItem(mtbSwitch).setIcon(newIcon);
                        }
                        mToolbar.getMenu().getItem(mtbLast).setEnabled(sp.contains("lastAPlayed"));
                    } else
                        mToolbar.getMenu().getItem(mtbLast).setEnabled(sp.contains("lastVPlayed"));
                } catch (Exception ignored) {
                }
                if (changeColorPrimary) try {
                    Drawable getIcon = mToolbar.getMenu().getItem(mtbRefresh).getIcon();
                    if (getIcon != null) {
                        getIcon.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
                        mToolbar.getMenu().getItem(mtbRefresh).setIcon(getIcon);
                    }
                } catch (Exception ignored) {
                }
            }
        });

        // Media Player Buttons
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (changingTheSeekBar) {
                    if (!video) {
                        if (Amin.exists && Amin.mp != null) {
                            int s = Math.round(((float) Amin.mp.getDuration() / 100f) * (float) seekBar.getProgress());
                            mDur.setText(fixDur(s) + " / " +
                                    fixDur(((Audio) folders.get(audioListIndex).files.get(audioIndex)).getDur()));
                        }
                    } else {
                        int s = Math.round(((float) mVV.getDuration() / 100f) * (float) seekBar.getProgress());
                        mDur.setText(fixDur(s) + " / " + fixDur(mVV.getDuration()));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                changingTheSeekBar = true;
                mSeekBarReplica.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekBarReplica.setVisibility(View.INVISIBLE);
                if (!video) {
                    if (Amin.exists && Amin.mp != null) try {
                        int s = Math.round(((float) Amin.mp.getDuration() / 100f) * (float) seekBar.getProgress());
                        if (playing) Amin.mp.seekTo(s);
                        else {
                            Amin.resumePosition = s;
                            mDur.setText(fixDur(s) + " / " +
                                    fixDur(((Audio) folders.get(audioListIndex).files.get(audioIndex)).getDur()));
                        }
                        heartbeat(mSeekBarReplica, 3.69f, new int[]{666, 179}, 0.55f);
                    } catch (Exception ignored) {
                    }
                } else if (mVV != null) try {
                    int s = Math.round(((float) mVV.getDuration() / 100f) * (float) seekBar.getProgress());
                    mVV.seekTo(s);
                    heartbeat(mSeekBarReplica, 3.69f, new int[]{666, 179}, 0.55f);
                } catch (Exception ignored) {
                }
                changingTheSeekBar = false;
            }
        });
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });
        mMoveF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSkip(true);
            }
        });
        mMoveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSkip(false);
            }
        });
        mShuffleBT.setAlpha(mSBsDisabled);
        if (sp.contains("shuffle")) shuffle(sp.getBoolean("shuffle", false), false);
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffle(!shuffle, true);
            }
        });
        mRepeatBT.setAlpha(mSBsDisabled);
        if (sp.contains("repeat")) repeat(sp.getInt("repeat", 0), false);
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeat(-1, true);
            }
        });
        if (changeColorPrimary) {
            ImageView[] ccpBtns = {mShuffleBT, mMoveBBT, mPlayPauseBT, mMoveFBT, mRepeatBT, mCropBT, mAddSubtitleBT};
            for (ImageView ccpBtn : ccpBtns) ccpBtn.setColorFilter(colorPrimary);
            mRepeatTV.setTextColor(colorPrimary);

            try {
                Drawable getIcon = mSeekBar.getThumb();
                if (getIcon != null) {
                    getIcon.setColorFilter(new PorterDuffColorFilter(textColorPrimary, PorterDuff.Mode.SRC_IN));
                    mSeekBar.setThumb(getIcon);
                }
            } catch (Exception ignored) {
            }
            try {
                Drawable getPD = mSeekBar.getProgressDrawable();
                if (getPD != null) {
                    getPD.setColorFilter(new PorterDuffColorFilter(textColorPrimary, PorterDuff.Mode.SRC_IN));
                    mSeekBar.setProgressDrawable(getPD);
                }
            } catch (Exception ignored) {
            }
        }
        if (changeTCP) {
            mCropBG.setColorFilter(textColorPrimary);
            mAddSubtitleBG.setColorFilter(textColorPrimary);
            mPlayPauseBG.setColorFilter(textColorPrimary);
            mToastIV.setColorFilter(textColorPrimary);
            mShuffleBG.setBackgroundColor(textColorPrimary);
            mMoveBBG.setBackgroundColor(textColorPrimary);
            mMoveFBG.setBackgroundColor(textColorPrimary);
            mRepeatBG.setBackgroundColor(textColorPrimary);
            mSeekBarReplica.setBackgroundColor(Color.argb(33, Color.red(textColorPrimary), Color.green(textColorPrimary),
                    Color.blue(textColorPrimary)));
            View[] fixBGs = {mSideBtnsBorderL, mSideBtnsBorderR, mSideBtnsBorderRestL, mSideBtnsBorderRestR};
            for (View fixBG : fixBGs)
                try {
                    Drawable bg = fixBG.getBackground();
                    bg.setColorFilter(new PorterDuffColorFilter(textColorPrimary, PorterDuff.Mode.SRC_IN));
                    fixBG.setBackground(bg);
                } catch (Exception ignored) {
                }
            mDur.setTextColor(textColorPrimary);
            mToastTV.setTextColor(textColorPrimary);
        }
        fixTVFont(mDur, Typeface.BOLD);

        // Loading the lists
        loadedHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                mLoading(false);
                if (message.arg2 == 1)
                    makeBasicMessage(getResources().getString(R.string.audioPlayer),
                            getResources().getString(R.string.unreadable), null, true, null);

                if (folders.size() > 0) showItems(false, 0);
                else if (message.arg1 == 0) {
                    int mss;
                    if (!video) mss = R.string.noSound;
                    else mss = R.string.noVideo;
                    makeBasicMessage(getResources().getString(R.string.audioPlayer),
                            getResources().getString(mss), null, true, null);
                    mNavLoading(false);
                }

                switch (message.what) {
                    case 0:
                        if ((int) message.obj == 1) {
                            if (!video) playAudio();
                            else playVideo(true, true);
                        }
                        break;
                    case 1:
                        if ((int) message.obj == 1)
                            Toast.makeText(c, R.string.refreshed, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        itemLoadedHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) draughtFolders.add((ConstraintLayout) message.obj);
                else draughtMedia.add((ConstraintLayout) message.obj);
                if (!settingItems) setupItems(message.what);
            }
        };
        itemsFinishedHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) folClItemIds = (ArrayList<Integer>) message.obj;
                else sndClItemIds = (ArrayList<Integer>) message.obj;
            }
        };
        iconFHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                draughtFIcons.add(new GotBmp(message.what, (Bitmap) message.obj));
                if (!settingFIcons) setupIcons(false);
            }
        };//Don't add the raw Messages to the ArrayList; they will corrupt that way.
        iconMHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                draughtMIcons.add(new GotBmp(message.what, (Bitmap) message.obj));
                if (!settingMIcons) setupIcons(true);
            }
        };

        // Improvements for DownloadFileFromURL
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Touching
        touchLMoved = new float[]{0f, 0f};
        touchRMoved = new float[]{0f, 0f};
        touchXMoved = new float[]{0f, 0f};
        mTouchSensor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if (e.getPointerCount() != 2) {
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1 = e.getX();
                            y1 = e.getY();
                            mTSLeft = x1 < (int) (dm.widthPixels / 2);
                            if (dirLtr) swipeNav = x1 < swipeNavMin;
                            else swipeNav = x1 > (dm.widthPixels - swipeNavMin);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!touchXMovedEnough) {
                                yMove = e.getY();
                                float delataY = Math.abs(y1 - yMove);
                                if (delataY > 0f) {
                                    if (yMove < y1) {//UPWARDS
                                        if (mTSLeft) touchLMoved[1] += delataY;
                                        else touchRMoved[1] += delataY;
                                    } else if (yMove > y1) {//DOWNWARDS
                                        if (mTSLeft) touchLMoved[0] -= delataY;
                                        else touchRMoved[0] -= delataY;
                                    }
                                }
                                notifyTouchMoved(mTSLeft);
                                y1 = e.getY();
                            }

                            if (!swipeNav && video && mVV != null && mVVP != null && !touchMovedEnough) {
                                xMove = e.getX();
                                float delataX = Math.abs(x1 - xMove);
                                if (delataX > 0f) {
                                    if (xMove < x1) touchXMoved[0] -= delataX;//LEFTWARDS
                                    else if (xMove > x1) touchXMoved[1] += delataX;//RIGHTWARDS
                                }
                                try {
                                    notifyTouchXMoved();
                                } catch (Exception ignored) {
                                }
                                x1 = e.getX();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            x2 = e.getX();//y2 = e.getY();
                            if (touchMovedEnough) toast(0, false);
                            else if (touchXMovedEnough) touchXMovedEnough = false;
                            else if (swipeNav && x2 > x1) openNavList(true);
                            else if (!video && Math.abs(x1 - x2) > swipeToSkipMin) btnSkip(x2 < x1);
                            else {
                                if (doubleTouch) {
                                    playPause();
                                    doubleTouch = false;
                                } else {
                                    doubleTouch = true;
                                    ValueAnimator DT = VA(mMotor, "rotationY", doubleTouchDur, 180f, 0f);
                                    DT.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            if (doubleTouch) {
                                                doubleTouch = false;
                                                if (video) hidePanel(!hiddenPanel);
                                            }
                                        }
                                    });
                                }
                            }
                            break;
                    }
                }
                return true;
            }
        });
        setVAlpha(sp.getFloat("mBodyAlpha", 1f));

        // Cropping
        if (sp.contains("crop")) crop(sp.getInt("crop", 1));
        mCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crop(-1);
            }
        });

        // Wallpapers
        if (sp.contains("wpBooleans")) {
            String[] split = sp.getString("wpBooleans", "11100000").split("");
            wpBooleans = new boolean[resWallpapers.length + adtWPMax];
            for (int b = 0; b < wpBooleans.length; b++) {
                if (b < split.length - 1) wpBooleans[b] = split[b + 1].equals("1");
            }
        }
        wallpapers = new ArrayList<>();
        for (int b = 0; b < wpBooleans.length; b++)
            if (wpBooleans[b])
                wallpapers.add(BitmapFactory.decodeResource(getResources(), resWallpapers[b]));
        for (int a = 1; a < adtWPMax; a++)
            if (sp.contains(adtWP + a)) {
                Bitmap got = BitmapFactory.decodeFile(sp.getString(adtWP + a, ""));
                if (got != null) wallpapers.add(got);
            }
        if (wallpapers.size() > 0) {
            if (wallpapers.size() > 1)
                mShwBG.setImageBitmap(wallpapers.get(new Random().nextInt(wallpapers.size())));
            else mShwBG.setImageBitmap(wallpapers.get(0));
        } else mShwBG.setImageResource(resWallpapers[0]);

        Drawable drawable = mLoadingPB.getIndeterminateDrawable().mutate();
        int mLDC = ContextCompat.getColor(c, R.color.mLoadingPB);
        if (changeColorPrimary) {
            mLDC = colorPrimary;
            mLoadingIV.setColorFilter(colorPrimary);
            mLoadingTV.setTextColor(colorPrimary);
            mLoadingVersion.setTextColor(colorPrimary);
        }
        drawable.setColorFilter(mLDC, PorterDuff.Mode.SRC_IN);
        mLoadingPB.setIndeterminateDrawable(drawable);
        if (changeTCP) mLoading.setBackgroundColor(textColorPrimary);
        mLoading.setOnClickListener(doNothing);
        fixTVFont(mLoadingTV, Typeface.BOLD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notFirst) {
            if (isNavOpen) openNavList(false);
            mLoading(true);
            loadMedia(1, false);

            if (mVV != null) try {//IT MUST BE NECESSARILY A VIDEOVIEW, NOT A MEDIAPLAYER
                mVV.seekTo(resumePos);
                if (vPlayOnResume) {
                    mVV.start();
                    vPlaying(true);
                }
            } catch (Exception ignored) {
            }
        } else {
            if (reCreation && !gotIncomingMedia) loadMedia(1, false);
            notFirst = true;
        }//startActivity(new Intent(c, Settings.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVV != null) {
            try {
                resumePos = mVV.getCurrentPosition();
            } catch (Exception ignored) {
                resumePos = 0;
            }
            vPlayOnResume = vPlaying;
            if (vPlaying) {
                mVV.pause();
                vPlaying(false);
            }
        }
        reCreation = false;
        gotIncomingMedia = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Destroy(false);
    }

    @Override
    public void onBackPressed() {
        if (!showingBM) {
            if (!fileInfoOpened) {
                if (!isNavOpen) {
                    if (!playing && !vPlaying) {
                        if (exiting) {
                            if (Amin.mp == null) exit();
                            else Destroy(true);
                        } else {
                            exiting = true;
                            Toast.makeText(c, R.string.toExit, Toast.LENGTH_SHORT).show();
                            ValueAnimator anExit = VA(mMotor, "translationY", exitingDelay, 111f, 0f);
                            anExit.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    exiting = false;
                                }
                            });
                        }
                    } else {
                        int msg = R.string.onExitWhilePlaying;
                        if (video) msg = R.string.onExitWhileVPlaying;
                        makeBasicMessage(getResources().getString(R.string.audioPlayer), getResources().getString(msg),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mb1Click();
                                        Destroy(true);
                                    }
                                }, true, null);
                    }
                } else {
                    if (openedNav == 0) openNavList(false);
                    else changeNavList(0, true);
                }
            } else fileInfo(false, null);
        } else if (bmCancellable) mb1Click();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case permWriteExerStor:
                start(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
        }//case 0: {} FUCKS UP!!!
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_tb, menu);
        return super.onCreateOptionsMenu(menu);//DON"T PUT HERE THINGS THAT NEED THE LAYOUT LOADED.
    }//NOTE: "onCreateOptionsMenu" is invoke only one time for every onCreate().

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mtbSwitchMedia:
                switchMedia(video, true, true);
                return true;
            case R.id.mtbRefreshList:
                loadMedia(1, true);
                return true;
            case R.id.mtbLastPlayed:
                String tag = "lastAPlayed";
                if (video) tag = "lastVPlayed";
                FoundFile file = findFile(new File(sp.getString(tag, "")));
                if (file != null) {
                    if (!video) {
                        audioList = file.list;
                        audioListIndex = file.listIndex;
                        audioIndex = file.index;
                        playAudio();
                    } else {
                        videoList = file.list;
                        videoListIndex = file.listIndex;
                        videoIndex = file.index;
                        playVideo(true, false);
                    }
                }
                return true;
            case R.id.mtbSettings:
                startActivity(new Intent(c, Settings.class));
                return true;
            case R.id.mtbUpdate:
                if (!checkingFU) {
                    if (isOnline()) {
                        if (version != null) {
                            cfuQueue = Volley.newRequestQueue(c);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, cloudFolder + ver,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            checkingFU = false;
                                            if (response != null) compareTheVersions(response);
                                            else
                                                Toast.makeText(c,
                                                        R.string.cfuError1, Toast.LENGTH_LONG).show();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    checkingFU = false;
                                    String mess = getResources().getString(R.string.cfuError1);
                                    if (error != null && error.getMessage() != null)
                                        if (!error.getMessage().equals(""))
                                            mess = getResources().getString(R.string.cfuError5) + error.getMessage();
                                    Toast.makeText(c, mess, Toast.LENGTH_LONG).show();
                                }
                            });
                            cfuQueue.add(stringRequest.setTag(cfuTag));
                            checkingFU = true;
                        } else
                            Toast.makeText(c, R.string.cfuError3, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(c, R.string.noInternet, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.mtbAboutUs:
                makeBasicMessage(getResources().getString(R.string.tbAboutUs),
                        getResources().getString(R.string.aboutUs), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mb1Click();
                                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(getResources().getString(R.string.myInsta)));
                                if (webIntent.resolveActivity(getPackageManager()) != null)
                                    startActivity(webIntent);
                            }
                        }, true, new String[]{getResources().getString(R.string.ok1),
                                getResources().getString(R.string.visit)});
                //Linkify.addLinks(body, Linkify.WEB_URLS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ValueAnimator anConfigChanged = VA(mMotor, "rotationX", loadDur1, 180f, 0f);
        anConfigChanged.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mVV != null) crop(-2);
            }
        });
    }


    public static ValueAnimator VA(View v, String prop, int dur, float val1, float val2) {
        final ValueAnimator mValueAnimator1 = ObjectAnimator.ofFloat(v, prop, val1, val2).setDuration(dur);
        mValueAnimator1.start();
        return mValueAnimator1;
    }

    public static ObjectAnimator OA(View v, String prop, float value, int dur) {
        ObjectAnimator mAnim = ObjectAnimator.ofFloat(v, prop, value).setDuration(dur);
        mAnim.start();
        return mAnim;
    }

    void controlPlayer() {
        if (Amin.mp != null && playing) {
            if (!changingTheSeekBar) {
                if (Amin.mp.getCurrentPosition() > -1 && Amin.mp.getDuration() > -1 && Amin.mp.getDuration() >= Amin.mp.getCurrentPosition())
                    try {
                        mSeekBar.setProgress(Math.round((100f / (float) Amin.mp.getDuration()) * (float) Amin.mp.getCurrentPosition()));
                        mDur.setText(fixDur(Amin.mp.getCurrentPosition()) + " / " + fixDur(Amin.mp.getDuration()));
                    } catch (Exception ignored) {
                        mSeekBar.setProgress(0);
                        mDur.setText(R.string.mNormalDur);
                    }
                else mDur.setText(R.string.mNormalDur);
            }
            reproduction = VA(mMotor, "scaleX", controlPlayerDur, 1.8f, 1f);
            reproduction.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    controlPlayer();
                }
            });
        }
    }

    void controlVPlayer() {
        if (mVV != null && vPlaying) {
            if (!changingTheSeekBar) {
                if (mVV.getCurrentPosition() > -1 && mVV.getDuration() > -1 && mVV.getDuration() >= mVV.getCurrentPosition())
                    try {
                        mSeekBar.setProgress(Math.round((100f / (float) mVV.getDuration()) * (float) mVV.getCurrentPosition()));
                        mDur.setText(fixDur(mVV.getCurrentPosition()) + " / " + fixDur(mVV.getDuration()));
                    } catch (Exception ignored) {
                        mSeekBar.setProgress(0);
                        mDur.setText(R.string.mNormalDur);
                    }
            }
            reproduction = VA(mMotor, "scaleX", controlPlayerDur, 1.8f, 1f);
            reproduction.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    controlVPlayer();
                }
            });
        }
    }

    void playing(boolean p) {
        playing = p;
        if (playing) {
            mPlayPauseBT.setImageResource(R.drawable.pause_3);
            controlPlayer();
        } else {
            mPlayPauseBT.setImageResource(R.drawable.play_3);
            if (Amin.mp != null) {
                try {
                    mDur.setText(fixDur(Amin.mp.getCurrentPosition()) + " / " + fixDur(Amin.mp.getDuration()));
                } catch (Exception ignored) {
                    mSeekBar.setProgress(0);
                    mDur.setText(R.string.mNormalDur);
                }
            } else {
                mSeekBar.setProgress(0);
                mDur.setText(R.string.mNormalDur);
            }
        }
    }

    void vPlaying(boolean p) {
        vPlaying = p;
        if (vPlaying) {
            mPlayPauseBT.setImageResource(R.drawable.pause_3);
            controlVPlayer();
        } else {
            mPlayPauseBT.setImageResource(R.drawable.play_3);
            if (mVV != null) {
                try {
                    mDur.setText(fixDur(mVV.getCurrentPosition()) + " / " + fixDur(mVV.getDuration()));
                } catch (Exception ignored) {
                    mSeekBar.setProgress(0);
                    mDur.setText(R.string.mNormalDur);
                }
            } else {
                mSeekBar.setProgress(0);
                mDur.setText(R.string.mNormalDur);
            }
        }
    }

    void playAudio() {
        StorageUtil storage = new StorageUtil(getApplicationContext());
        storage.storeAudio(audioList);
        storage.storeAudioIndex(audioIndex);
        storage.storeAudioListIndex(audioListIndex);
        highlightItem(0, audioListIndex);
        if (audioListIndex == currentOpenedList) highlightItem(1, audioIndex);
        updateDescription(true);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lastAPlayed", ((Audio) folders.get(audioListIndex).files.get(audioIndex)).getData());
        editor.apply();

        mSeekBar.setEnabled(true);
        if (!Amin.exists || !serviceBound) {
            Intent playerIntent = new Intent(c, Amin.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else sendBroadcast(new Intent(Broadcast_PLAY_NEW_AUDIO));
    }

    void makeBasicMessage(String title, String message, View.OnClickListener quesClick, boolean cancellabe, String[] buttonTexts) {
        mBasicMessages.setVisibility(View.VISIBLE);
        mBasicMessagesLL.removeAllViews();
        LayoutInflater.from(c).inflate(R.layout.basic_message_1, mBasicMessagesLL);
        showingBM = true;

        ConstraintLayout m = (ConstraintLayout) mBasicMessagesLL.getChildAt(mBasicMessagesLL.getChildCount() - 1);
        LinearLayout ll = (LinearLayout) m.getChildAt(0);
        TextView Title = (TextView) ((LinearLayout) ((ConstraintLayout) ll.getChildAt(0)).getChildAt(0)).getChildAt(1),
                body = (TextView) ((ConstraintLayout) ll.getChildAt(1)).getChildAt(0);
        Title.setText(title);
        fixTVFont(body, Typeface.BOLD);
        body.setText(message);
        fixTVFont(body, Typeface.NORMAL);
        ImageView ivTitle = (ImageView) ((LinearLayout) ((ConstraintLayout) ll.getChildAt(0)).getChildAt(0))
                .getChildAt(0);
        ivTitle.setColorFilter(ContextCompat.getColor(c, R.color.bm1TitleIV));
        int buttonVGId = View.generateViewId();
        ConstraintLayout buttonVG = (ConstraintLayout) ll.getChildAt(2);
        buttonVG.setId(buttonVGId);
        if (changeColorPrimary) m.setBackgroundColor(colorPrimary);
        if (changeTCP) {
            Title.setTextColor(textColorPrimary);
            ivTitle.setColorFilter(textColorPrimary);
            body.setBackgroundColor(textColorPrimary);
            buttonVG.setBackgroundColor(textColorPrimary);
        }

        TextView button = new TextView(new ContextThemeWrapper(c, R.style.BMButton1), null, 0);
        ConstraintLayout.LayoutParams buttonLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLP.topToTop = buttonVGId;
        if (changeColorPrimary) button.setBackgroundColor(colorPrimary);
        if (changeTCP) button.setTextColor(textColorPrimary);
        fixTVFont(button, Typeface.BOLD);
        final ConstraintLayout M = m;
        if (quesClick == null) {
            buttonLP.endToEnd = buttonVGId;
            buttonLP.startToStart = buttonVGId;
            button.setText(R.string.ok1);
            if (buttonTexts != null) if (buttonTexts[0] != null) button.setText(buttonTexts[0]);
        } else {
            int buttonYesId = View.generateViewId();
            buttonLP.endToStart = buttonYesId;
            buttonLP.setMarginEnd(buttonVG.getPaddingEnd());
            button.setText(R.string.no1);
            if (buttonTexts != null) if (buttonTexts[0] != null) button.setText(buttonTexts[0]);

            TextView buttonYes = new TextView(new ContextThemeWrapper(c, R.style.BMButton1), null, 0);
            ConstraintLayout.LayoutParams buttonYesLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonYesLP.endToEnd = buttonVGId;
            buttonYesLP.topToTop = buttonVGId;
            buttonYes.setText(R.string.yes1);
            if (changeColorPrimary) buttonYes.setBackgroundColor(colorPrimary);
            if (changeTCP) buttonYes.setTextColor(textColorPrimary);
            if (buttonTexts != null) if (buttonTexts[1] != null) {
                buttonYes.setText(buttonTexts[1]);
                buttonYes.setBackgroundColor(ContextCompat.getColor(c, R.color.bm1BtnYesSpecial));
            }
            buttonYes.setId(buttonYesId);
            fixTVFont(buttonYes, Typeface.BOLD);
            buttonVG.addView(buttonYes, buttonYesLP);
            buttonYes.setOnClickListener(quesClick);
        }
        buttonVG.addView(button, buttonLP);

        final int dur = 111;
        OA(mBasicMessages, "alpha", 1f, dur);
        View.OnClickListener cancel = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator a = OA(mBasicMessages, "alpha", 0f, dur);
                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBasicMessagesLL.removeView(M);
                        mBasicMessages.setVisibility(View.GONE);
                        showingBM = false;
                    }
                });
            }
        };
        button.setOnClickListener(cancel);
        if (cancellabe) mBasicMessagesLL.setOnClickListener(cancel);
        else mBasicMessagesLL.setOnClickListener(doNothing);
        bmCancellable = cancellabe;
        m.setOnClickListener(doNothing);
    }

    void mb1Click() {
        mBasicMessagesLL.removeAllViews();
        mBasicMessages.setVisibility(View.GONE);
        showingBM = false;
    }

    boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    void compareTheVersions(String response) {
        String title = getResources().getString(R.string.tbUpdate);
        int curVer = -1, newVer = -1;
        try {
            curVer = Integer.parseInt(version.replace(".", ""));
            newVer = Integer.parseInt(response);
        } catch (NumberFormatException ignored) {
        }
        String sNewVer = response.charAt(0) + "." + response.charAt(1) + "." + response.charAt(2),
                updateVer1 = getResources().getString(R.string.updateSentence1),
                updateVer2 = getResources().getString(R.string.updateSentence2),
                updateVer3 = getResources().getString(R.string.updateSentence3);

        if (curVer != -1 && newVer != -1) {
            if (newVer > curVer) {
                makeBasicMessage("New Update", updateVer1 + version + updateVer2 + sNewVer + updateVer3,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mb1Click();
                                if (updates.exists()) updates.delete();
                                new DownloadFileFromURL().execute(cloudFolder + app);
                            }
                        }, false, null);
            } else if (newVer == curVer)
                makeBasicMessage(title, getResources().getString(R.string.upToDate) + " (" + version + ")", null, true, null);
            else
                makeBasicMessage(title, getResources().getString(R.string.cfuError4) + "\n\n" +
                        getResources().getString(R.string.cfuReceived) + "\n" + response, null, true, null);
        }
    }

    void openNavList(boolean open) {
        isNavOpen = open;
        float fListDest = (float) mNavList.getWidth() + 11f;
        if (dirLtr) fListDest = -fListDest;
        if (open) {
            mNavList.setTranslationX(fListDest);
            mNavigation.setVisibility(View.VISIBLE);
            OA(mNavList, "translationX", 0f, navDur);
            scrollToSelected();
        } else {
            ObjectAnimator an = OA(mNavList, "translationX", fListDest, navDur);
            an.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mNavigation.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    void changeNavList(int n, boolean scroll) {
        openedNav = n;
        for (int l = 0; l < navLists.length; l++)
            if (navLists[l].getVisibility() == View.VISIBLE)
                navLists[l].setVisibility(View.GONE);
        navLists[n].setVisibility(View.VISIBLE);
        if (scroll) scrollToSelected();
    }

    Bitmap bmpRes(int res) {
        return BitmapFactory.decodeResource(getResources(), res);
    }

    void exit() {
        moveTaskToBack(true);//REQUIRED!
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    void start(boolean go) {
        if (go) loadMedia(0, false);
        else {
            Toast.makeText(c, R.string.noPermWES, Toast.LENGTH_LONG).show();
            exit();
        }
    }

    void shuffle(boolean on, boolean toast) {
        shuffle = on;
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("shuffle", shuffle);
        editor.apply();

        int msg = R.string.shuffleOn;
        if (shuffle) mShuffleBT.setAlpha(1f);
        else {
            mShuffleBT.setAlpha(mSBsDisabled);
            Amin.shuffleHistory = null;
            shuffleHistory = null;
            msg = R.string.shuffleOff;
        }
        if (toast) Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    void repeat(int set, boolean toast) {
        if (set == -1) {
            repeat += 1;
            if (repeat == 3) repeat = 0;
        } else if (set >= 0 && set <= 2) repeat = set;
        else return;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("repeat", repeat);
        editor.apply();

        int msg = R.string.noRepeat;
        switch (repeat) {
            case 0:
                mRepeatBT.setAlpha(mSBsDisabled);
                mRepeatTV.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mRepeatBT.setAlpha(1f);
                mRepeatTV.setVisibility(View.INVISIBLE);
                msg = R.string.repeatAll;
                break;
            case 2:
                mRepeatBT.setAlpha(1f);
                mRepeatTV.setVisibility(View.VISIBLE);
                msg = R.string.repeatOnce;
                break;
        }
        if (toast) Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }

    void highlightItem(int list, int item) {
        ArrayList[] idLists = new ArrayList[]{folClItemIds, sndClItemIds};
        ArrayList<Integer> idList = (ArrayList<Integer>) idLists[list];
        if (idList != null) {
            for (int i = 0; i < idList.size(); i++) {
                try {
                    highlight(false, (ConstraintLayout) findViewById(idList.get(i)));
                } catch (Exception ignored) {
                }
            }
            if (item != -1) {
                try {
                    highlight(true, (ConstraintLayout) findViewById(idList.get(item)));
                } catch (Exception ignored) {
                }
            }
        }
    }

    void highlight(boolean Do, ConstraintLayout cl) {
        if (Do) {
            if (!changeColorPrimary) cl.setBackgroundResource(R.drawable.square_1_blue_1_alpha);
            else cl.setBackgroundColor(Color.argb(highlightCPAlpha, Color.red(colorPrimary),
                    Color.green(colorPrimary), Color.blue(colorPrimary)));
        } else cl.setBackgroundResource(R.drawable.square_1_tp_to_white_alpha_xml);
    }

    void updateDescription(boolean New) {
        if (!video) {
            if (New) {
                try {
                    Audio aud = (Audio) folders.get(audioListIndex).files.get(audioIndex);
                    Bitmap bmp = Amin.getAlbumArt(c, aud.getAlbumId());
                    mAlbumArt.setImageBitmap(bmp);
                    mToolbar.setSubtitle(aud.getName());
                    for (int g = 0; g < mToolbar.getChildCount(); g++) {
                        View getTitle = mToolbar.getChildAt(g);
                        if (getTitle.getClass().getName().equalsIgnoreCase("androidx.appcompat.widget.AppCompatTextView") &&
                                ((TextView) getTitle).getText().toString().equals(aud.getName()))
                            ((TextView) getTitle).setTypeface(Typeface.create(fonts[font], Typeface.NORMAL));
                    }
                } catch (Exception ignored) {
                }
            } else {
                mAlbumArt.setImageBitmap(null);
                mToolbar.setSubtitle("");
            }
        } else {
            if (New) {
                try {
                    mToolbar.setSubtitle(activeVideo.name);
                } catch (Exception ignored) {
                }
            } else mToolbar.setSubtitle("");
        }
    }

    String folderPath(String path) {
        try {
            String oldPath;
            String[] split = path.split("/", 4);
            if (split[1].equals("system")) oldPath = path.substring(1);
            else oldPath = split[3];
            if (oldPath.contains("/"))
                return "/" + oldPath.substring(0, oldPath.lastIndexOf("/")) + "/";
            return "/" + oldPath + "/";
        } catch (Exception ignored) {
            return path;
        }
    }

    void scrollToSelected() {
        int listIndex, Index, index;
        ArrayList<Integer> ids;
        Object mmm;
        if (!video) {
            listIndex = audioListIndex;
            Index = audioIndex;
            mmm = Amin.mp;
        } else {
            listIndex = videoListIndex;
            Index = videoIndex;
            mmm = mVV;
        }

        if (mmm == null || (openedNav == 1 && listIndex != currentOpenedList)) return;
        if (openedNav == 0) {
            ids = folClItemIds;
            index = listIndex;
        } else {
            ids = sndClItemIds;
            index = Index;
        }
        if (index == -1 || ids == null) return;

        int distance = 0, backSize = 0;
        boolean backful = false;
        for (int b = 0; b < backfulLists.length; b++)
            if (backfulLists[b] == openedNav) backful = true;
        if (backful) {
            backSize = ((LinearLayout) navLists[openedNav].getChildAt(0)).getChildAt(0).getMeasuredHeight();
            distance += backSize;
        }
        for (int i = 0; i < index; i++)
            if (findViewById(ids.get(i)) != null)
                distance += findViewById(ids.get(i)).getMeasuredHeight();

        if (distance > navLists[openedNav].getHeight() - (int) (backSize * 1.5)) {
            distance -= backSize;
            navLists[openedNav].scrollTo(0, distance);
        }
    }

    public static String fixDur(int dur) {
        StringBuilder DUR = new StringBuilder();
        int number = dur;
        int hours = (int) (number / 3600000);
        if (hours > 0) {
            number -= hours * 3600000;
            if (hours < 10) DUR.append("0");
            DUR.append(hours).append(":");
        }

        int minutes = (int) (number / 60000);
        number -= minutes * 60000;
        if (minutes < 10) DUR.append("0");
        DUR.append(minutes).append(":");

        int seconds = (int) (number / 1000);
        if (seconds < 10) DUR.append("0");
        DUR.append(seconds);

        return DUR.toString();
    }

    void mLoading(boolean b) {
        if (b) {
            mLoading.setTranslationX(0f);
            mLoading.setVisibility(View.VISIBLE);
        } else {
            if (mLoading.getVisibility() == View.VISIBLE) {
                ObjectAnimator l = OA(mLoading, "translationX", -(float) (dm.widthPixels + 22), mLoadingDur);
                l.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoading.setVisibility(View.GONE);
                        mLoading.setTranslationX(0f);
                    }
                });
            }
        }
    }

    void sendToMSession(int act) {
        if (Amin.exists && Amin.mp != null) {
            PendingIntent pi = Amin.playbackAction(c, act);
            if (pi != null) try {
                pi.send();
            } catch (Exception ignored) {
            }
        }
    }

    public static Object changeColor(Context c, int drw, int colour, boolean toDrw) {
        Bitmap bmIcon = BitmapFactory.decodeResource(c.getResources(), drw).copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN));
        Canvas canvas = new Canvas(bmIcon);
        canvas.drawBitmap(bmIcon, 0, 0, paint);
        if (toDrw) return new BitmapDrawable(c.getResources(), bmIcon);
        else return bmIcon;
    }

    void heartbeat(View v, float Scale, int[] durs, float alpha) {
        ConstraintLayout parent = (ConstraintLayout) v.getParent();
        final ImageView effect = new ImageView(c);
        ConstraintLayout.LayoutParams effectLP = new ConstraintLayout.LayoutParams(v.getHeight(), v.getHeight());
        effectLP.leftToLeft = v.getId();
        effectLP.topToTop = v.getId();
        effectLP.rightToRight = v.getId();
        effectLP.bottomToBottom = v.getId();
        effectLP.horizontalBias = ((float) mSeekBar.getProgress()) / 100f;
        effect.setImageResource(R.drawable.solid_white_circle);//round_wave_1 activated_animation_1_white
        effect.setColorFilter(textColorPrimary);
        effect.setAlpha(alpha);
        parent.addView(effect, effectLP);

        ValueAnimator scale1 = ObjectAnimator.ofFloat(effect, "scaleX", Scale).setDuration(durs[0]);
        ValueAnimator scale2 = ObjectAnimator.ofFloat(effect, "scaleY", Scale).setDuration(durs[0]);
        ValueAnimator scale3 = ObjectAnimator.ofFloat(effect, "alpha", 0f).setDuration(durs[1]);
        scale3.setStartDelay(durs[0] - durs[1]);
        final AnimatorSet scale = new AnimatorSet();
        scale.playTogether(scale1, scale2, scale3);
        final ImageView effectGet = effect;
        final ConstraintLayout PARENT = parent;
        scale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                PARENT.removeView(effectGet);
            }
        });
        scale.start();
    }

    void Destroy(boolean delayedExit) {
        if (Amin.exists) destroyAmin();
        playing = false;
        vPlaying = false;
        AminHandler = null;
        changeHandler = null;
        destroyMe = null;
        removeVideoView();
        keepScreen(false);
        cancelLoadings(true, true);

        if (delayedExit) {
            ValueAnimator anExit = VA(mMotor, "translationX", exitDur, 100f, 0f);
            anExit.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    exit();
                }
            });
        }
    }

    void switchMedia(final boolean audio, final boolean Refresh, boolean isUser) {
        if (isUser && ((video && vPlaying) || (!video && playing))) {
            int ask = R.string.askOnSwitchToVideo;
            if (video) ask = R.string.askOnSwitchToAudio;
            makeBasicMessage(getResources().getString(R.string.audioPlayer), getResources().getString(ask),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mb1Click();
                            switchMedia(audio, Refresh, false);
                        }
                    }, false, null);
            return;
        }

        if (draughtFIcons != null) draughtFIcons.clear();
        if (draughtMIcons != null) draughtMIcons.clear();
        if (draughtFolders != null) draughtFolders.clear();
        if (draughtMedia != null) draughtMedia.clear();
        mSeekBar.setEnabled(false);
        updateDescription(false);
        video = !audio;
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("video", video);
        editor.apply();

        if (audio) {
            try {
                mToolbar.getMenu().getItem(mtbSwitch).setTitle(R.string.tbSwitchToVideo);
                Drawable newIcon = (Drawable) resizeByRatio(c, R.drawable.video_1_blue, menuSwitchRatio, true);
                if (newIcon != null) {
                    if (changeColorPrimary)
                        newIcon.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
                    mToolbar.getMenu().getItem(mtbSwitch).setIcon(newIcon);
                }
                mToolbar.getMenu().getItem(mtbLast).setEnabled(sp.contains("lastAPlayed"));
            } catch (Exception ignored) {
            }

            if (vPlaying) vPlaying(false);
            shuffleHistory = null;
            removeVideoView();
            mVVFrame.setVisibility(View.INVISIBLE);
            mCrop.setVisibility(View.GONE);
            mAddSubtitle.setVisibility(View.GONE);
            mPanelBg.setVisibility(View.INVISIBLE);

            if (hiddenPanel) hidePanel(false);
            mShwBG.setVisibility(View.VISIBLE);
            mAlbumArt.setVisibility(View.VISIBLE);
        } else {
            try {
                mToolbar.getMenu().getItem(mtbSwitch).setTitle(R.string.tbSwitchToAudio);
                Drawable newIcon = (Drawable) resizeByRatio(c, R.drawable.sound_2_blue, menuSwitchRatio, true);
                if (newIcon != null) {
                    if (changeColorPrimary)
                        newIcon.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
                    mToolbar.getMenu().getItem(mtbSwitch).setIcon(newIcon);
                }
                mToolbar.getMenu().getItem(mtbLast).setEnabled(sp.contains("lastVPlayed"));
            } catch (Exception ignored) {
            }

            if (Amin.exists) destroyAmin();
            mShwBG.setVisibility(View.INVISIBLE);
            mAlbumArt.setVisibility(View.GONE);

            mVVFrame.setVisibility(View.VISIBLE);
            mPanelBg.setVisibility(View.VISIBLE);
        }//fullScreen(!audio);
        if (Refresh) loadMedia(1, false);
    }

    void playVideo(final boolean doPlay, final boolean autoHide) {
        boolean autoVPlay = vPlaying;
        if (doPlay) autoVPlay = true;
        if (vPlaying) vPlaying(false);

        activeVideo = (Video) folders.get(videoListIndex).files.get(videoIndex);
        highlightItem(0, videoListIndex);
        if (videoListIndex == currentOpenedList) highlightItem(1, videoIndex);
        updateDescription(true);
        mSeekBar.setEnabled(true);

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lastVPlayed", ((Video) folders.get(videoListIndex).files.get(videoIndex)).data);
        editor.apply();

        removeVideoView();
        mVV = new VideoView(new ContextThemeWrapper(c, R.style.mVideoView), null, 0);
        mVV.setBackgroundColor(Color.TRANSPARENT);
        mVV.setVisibility(View.VISIBLE);
        mVV.setAlpha(1f);
        mVVFrame.addView(mVV);
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            mVV.setAudioStreamType(AudioManager.STREAM_MUSIC);
        else try {
            mVV.setAudioAttributes(new AudioAttributes.Builder()
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build());
        } catch (Exception ignored) {
        }
        mVV.setZOrderOnTop(false);
        try {
            mVV.setVideoPath(activeVideo.data);
        } catch (Exception e) {
            String msg = "";
            if (e.getMessage() != null) msg = e.getMessage();
            Toast.makeText(c, getResources().getString(R.string.errorOccurred) + msg, Toast.LENGTH_SHORT).show();
            return;
        }
        final boolean AutoVPlay = autoVPlay;
        mVV.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mVVP = mediaPlayer;
                setVVolume(-1);
                if (AutoVPlay) {//mVV.requestFocus();
                    mVV.start();
                    vPlaying(true);
                }

                if (autoHide) hidePanel(true);
                keepScreen(true);
                crop(-2);
            }
        });
        mVV.setOnErrorListener(new MediaPlayer.OnErrorListener() {
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
                        removeVideoView();
                        return true;
                    default://MediaPlayer.MEDIA_ERROR_UNKNOWN
                        Toast.makeText(c, getResources().getString(msg) + desc, Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
        mVV.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        Toast.makeText(c, R.string.mpNotSeekable, Toast.LENGTH_SHORT).show();
                        return true;
                    case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        Toast.makeText(c, R.string.badInterleaving, Toast.LENGTH_LONG).show();
                        return true;
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        Toast.makeText(c, R.string.vpTrackLagging, Toast.LENGTH_LONG).show();
                        return true;
                    case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        Toast.makeText(c, R.string.vpUnsupportedST, Toast.LENGTH_LONG).show();
                        return true;
                    case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        Toast.makeText(c, R.string.vpSTTimedOut, Toast.LENGTH_LONG).show();
                        return true;
                }
                return false;
            }
        });
        mVV.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                switch (repeat) {
                    case 0:
                        vPlaying(false);
                        mVV.seekTo(0);
                        break;
                    case 1:
                        skipVideo(true);
                        break;
                    case 2:
                        mVV.seekTo(0);
                        mVV.start();
                        break;//FORGETTING "break;" IS A DISASTER!!!
                }
            }
        });
        mCrop.setVisibility(View.VISIBLE);
        mAddSubtitle.setVisibility(View.VISIBLE);
    }

    void skipVideo(boolean next) {
        if (mVV == null) return;
        boolean plays = true;
        if (shuffle) chooseRandom(!next);
        else {
            if (next) {
                if (videoIndex < folders.get(videoListIndex).files.size() - 1) videoIndex += 1;
                else {
                    if (repeat == 1) videoIndex = 0;
                    else {
                        plays = false;
                        Toast.makeText(c, R.string.endOfPlaylist, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (videoIndex > 0) videoIndex -= 1;
                else {
                    if (repeat == 1) videoIndex = 0;
                    else {
                        plays = false;
                        Toast.makeText(c, R.string.startOfPlaylist, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if (plays) playVideo(false, false);
    }

    void chooseRandom(boolean skipToPrevious) {
        if (shuffleHistory == null) shuffleHistory = new ArrayList<>();

        if (shuffleHistory.size() > 0 && skipToPrevious && shuffleHistory.get(shuffleHistory.size() - 1) < videoList.size()) {
            audioIndex = shuffleHistory.get(shuffleHistory.size() - 1);
            shuffleHistory.remove(shuffleHistory.size() - 1);
        } else {
            shuffleHistory.add(videoIndex);
            ArrayList<Integer> allNumbers = new ArrayList<>();
            int num = 0;
            for (int n = 0; n < videoList.size(); n++) {
                allNumbers.add(num);
                num += 1;
            }
            allNumbers.remove(videoIndex);
            videoIndex = allNumbers.get(new Random().nextInt(allNumbers.size()));
        }
    }

    void hidePanel(boolean hide) {
        hiddenPanel = hide;
        float dist = (float) (mPanel.getHeight() + mSeekBar.getHeight() + 22);
        if (hide) {
            if (mPlayerBody.getTranslationY() == 0f) {
                ValueAnimator aHide = VA(mPlayerBody, "translationY", hidePanelDur, 0f, dist);
                aHide.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mPlayerBody.setVisibility(View.INVISIBLE);
                    }
                });
            }
            if (mToolbarAB.isShowing()) mToolbarAB.hide();
            if (mVV != null) {
                mCrop.setVisibility(View.GONE);
                mAddSubtitle.setVisibility(View.GONE);
            }
        } else {
            if (mPlayerBody.getTranslationY() != 0f) {
                mPlayerBody.setVisibility(View.VISIBLE);
                VA(mPlayerBody, "translationY", hidePanelDur, dist, 0f);
            }
            if (!mToolbarAB.isShowing()) mToolbarAB.show();
            if (mVV != null) {
                mCrop.setVisibility(View.VISIBLE);
                mAddSubtitle.setVisibility(View.VISIBLE);
            }
        }
    }

    void notifyTouchMoved(boolean left) {
        if (left) {
            if (touchLMoved[1] >= touchMSensibility) {
                if (mBody.getAlpha() + opacityEachMove <= 1f)
                    setVAlpha(mBody.getAlpha() + opacityEachMove);
                else if (Math.abs(1f - mBody.getAlpha()) <= opacityEachMove) setVAlpha(1f);
                touchLMoved[1] = 0f;
                toast(0, true);
            }
            if (touchLMoved[0] <= -touchMSensibility) {
                if (mBody.getAlpha() >= leastOpacity + opacityEachMove)
                    setVAlpha(mBody.getAlpha() - opacityEachMove);
                else if (Math.abs(mBody.getAlpha() - leastOpacity) <= opacityEachMove)
                    setVAlpha(leastOpacity);
                touchLMoved[0] = 0f;
                toast(0, true);
            }
        } else {
            if (touchRMoved[1] >= touchMSensibility) {
                if (vVolume + vVolumeEachMove <= 1f) setVVolume(vVolume + vVolumeEachMove);
                else if (Math.abs(1f - vVolume) <= vVolumeEachMove) setVVolume(1f);
                touchRMoved[1] = 0f;
                toast(1, true);
            }
            if (touchRMoved[0] <= -touchMSensibility) {
                if (vVolume >= vVolumeEachMove) setVVolume(vVolume - vVolumeEachMove);//0f +
                else if (Math.abs(vVolume) <= vVolumeEachMove) setVVolume(0f);// - 0f
                touchRMoved[0] = 0f;
                toast(1, true);
            }
        }
    }

    void notifyTouchXMoved() {
        if (touchXMoved[1] >= touchMSensibility) {
            if (mVV.getCurrentPosition() + videoEachMove <= mVV.getDuration())
                touchXMove(mVV.getCurrentPosition() + videoEachMove);
            else if (Math.abs(mVV.getDuration() - mVV.getCurrentPosition()) <= videoEachMove)
                touchXMove(mVV.getDuration());
            touchXMoved[1] = 0f;
            touchXMovedEnough = true;
        }
        if (touchXMoved[0] <= -touchMSensibility) {
            if (mVV.getCurrentPosition() >= videoEachMove)
                touchXMove(mVV.getCurrentPosition() - videoEachMove);
            else if (mVV.getCurrentPosition() <= videoEachMove) touchXMove(0);
            touchXMoved[0] = 0f;
            touchXMovedEnough = true;
        }

    }

    void toast(int what, boolean show) {
        touchMovedEnough = show;
        float a, b, c;
        if (show) {
            mToast.setVisibility(View.VISIBLE);
            switch (what) {
                case 0://ALPHA
                    mToastIV.setImageResource(R.drawable.brightness_1_white);
                    a = 1f - leastOpacity;
                    b = mBody.getAlpha() - leastOpacity;
                    c = a / (highestVolume - 1);
                    mToastTV.setText(((int) (Math.floor(b / c) + 1)) + "");
                    break;
                case 1://VOLUME
                    mToastIV.setImageResource(R.drawable.volume_1_white);
                    c = 1f / highestVolume;
                    mToastTV.setText(Math.round(vVolume / c) + "");
                    break;
                case 2://ZOOM
                    break;
            }
        } else {
            mToast.setVisibility(View.GONE);
            mToastIV.setImageResource(R.drawable.square_1_transparent);
            mToastTV.setText("");

            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("mBodyAlpha", mBody.getAlpha());
            editor.putFloat("vVolume", vVolume);
            editor.apply();
        }
    }

    void setVVolume(float volume) {
        if (volume != -1) vVolume = volume;
        if (mVV != null) mVVP.setVolume(vVolume, vVolume);
        if (Amin.mp != null) Amin.mp.setVolume(vVolume, vVolume);
    }

    void setVAlpha(float alpha) {
        mBody.setAlpha(alpha);
        mTouchSensor.setAlpha(1f - alpha);
    }

    void touchXMove(int pos) {
        if (mVV != null) {
            mVV.seekTo(pos);
            if (!vPlaying) {
                try {
                    if (!changingTheSeekBar) {
                        mSeekBar.setProgress(Math.round((100f / (float) mVV.getDuration()) * (float) mVV.getCurrentPosition()));
                        mDur.setText(fixDur(mVV.getCurrentPosition()) + " / " + fixDur(mVV.getDuration()));
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    void removeVideoView() {
        if (mVVP != null) {
            try {
                if (mVVP.isPlaying()) mVVP.stop();
            } catch (Exception ignored) {
            }
            try {
                mVVP.release();
            } catch (Exception ignored) {
            }
            mVVP = null;
        }
        if (mVV != null) {
            try {
                mVV.suspend();
                mVV.stopPlayback();
            } catch (Exception ignored) {
            }
            try {
                mVVFrame.removeView(mVV);
            } catch (Exception ignored) {
            }
            mVV = null;
        }
        keepScreen(false);
        mCrop.setVisibility(View.GONE);
        mAddSubtitle.setVisibility(View.GONE);
    }

    void keepScreen(boolean on) {
        if (on) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        keepScreen = on;
    }

    public static Object resizeByRatio(Context c, Object Icon, float ratio, boolean toDrawable) {
        Bitmap icon;
        if (Icon instanceof Bitmap) icon = (Bitmap) Icon;
        else if (Icon instanceof LayerDrawable) {
            LayerDrawable LD = ((LayerDrawable) Icon);
            icon = Bitmap.createBitmap(LD.getIntrinsicWidth(), LD.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            LD.setBounds(0, 0, LD.getIntrinsicWidth(), LD.getIntrinsicHeight());
            LD.draw(new Canvas(icon));
        } else if (Icon instanceof Drawable)
            icon = ((BitmapDrawable) Icon).getBitmap();
        else if (Icon instanceof Integer)
            icon = BitmapFactory.decodeResource(c.getResources(), (int) Icon);
        else return null;

        int WH = (int) (icon.getHeight() * ratio);
        Bitmap overlay = Bitmap.createBitmap(WH, WH, icon.getConfig());
        Canvas canvas = new Canvas(overlay);
        Matrix amin = new Matrix();
        amin.setScale(ratio, ratio);
        canvas.drawBitmap(icon, amin, null);
        if (toDrawable) return new BitmapDrawable(c.getResources(), overlay);
        else return overlay;
    }

    void crop(int New) {
        if (New >= -1 && New <= 3) {
            if (New == -1) {
                if (crop < 3) crop += 1;
                else crop = 0;
                Toast.makeText(c, getResources().getStringArray(R.array.crop)[crop], Toast.LENGTH_SHORT).show();
            } else crop = New;//if (New >= 0)

            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("crop", crop);
            editor.apply();
        }

        if (mVV == null || mVVP == null) return;

        boolean auto = true, width = false, height = false;
        int W = ViewGroup.LayoutParams.WRAP_CONTENT, H = W;
        float videoRatio = (float) mVVP.getVideoWidth() / (float) mVVP.getVideoHeight(),
                screenRatio = (float) mVVFrame.getWidth() / (float) mVVFrame.getHeight();

        switch (crop) {
            case 0://100%
                auto = false;
                W = mVVP.getVideoWidth();
                H = mVVP.getVideoHeight();
                break;
            case 1://FIT TO SCREEN
                if (videoRatio > 1) {
                    if (screenRatio > 1) {//= in = (COMPLICATED...)
                        if (videoRatio > screenRatio) {
                            width = true;
                        } else if (videoRatio < screenRatio) {
                            height = true;
                        } else {
                            width = true;
                            height = true;
                        }
                    } else if (screenRatio < 1) {//= in I
                        width = true;
                    } else {//= in O
                        width = true;
                    }
                } else if (videoRatio < 1) {
                    if (screenRatio > 1) {//I in =
                        height = true;
                    } else if (screenRatio < 1) {//I in I (COMPLICATED...)
                        if (videoRatio > screenRatio) {
                            width = true;
                        } else if (videoRatio < screenRatio) {
                            height = true;
                        } else {
                            width = true;
                            height = true;
                        }
                    } else {//I in O
                        height = true;
                    }
                } else {
                    if (screenRatio > 1) {//O in =
                        height = true;
                    } else if (screenRatio < 1) {//O in I
                        width = true;
                    } else {//O in O
                        width = true;
                        height = true;
                    }
                }
                break;
            case 2://STRETCH
                width = true;
                height = true;
                break;
            case 3://CROP
                auto = false;
                W = ViewGroup.LayoutParams.MATCH_PARENT;
                H = W;
                if (videoRatio > 1) {
                    if (screenRatio > 1) {//= in =
                        if (videoRatio > screenRatio) {
                            W = (int) (videoRatio * mVVFrame.getHeight());
                        } else if (videoRatio < screenRatio) {
                            H = (int) (videoRatio * mVVFrame.getWidth());
                        }//else {}
                    } else if (screenRatio < 1) {//= in I
                        W = (int) (videoRatio * mVVFrame.getHeight());
                    } else {//= in O
                        W = (int) (videoRatio * mVVFrame.getHeight());
                    }
                } else if (videoRatio < 1) {
                    if (screenRatio > 1) {//I in =
                        H = (int) ((mVVP.getVideoHeight() / mVVP.getVideoWidth()) * mVVFrame.getWidth());
                    } else if (screenRatio < 1) {//I in I
                        if (videoRatio > screenRatio) {
                            W = (int) ((mVVP.getVideoHeight() / mVVP.getVideoWidth()) * mVVFrame.getHeight());
                        } else if (videoRatio < screenRatio) {
                            H = (int) ((mVVP.getVideoHeight() / mVVP.getVideoWidth()) * mVVFrame.getWidth());
                        }//else {}
                    } else {//I in O
                        H = (int) ((mVVP.getVideoHeight() / mVVP.getVideoWidth()) * mVVFrame.getWidth());
                    }
                } else {
                    if (screenRatio > 1) {//O in =
                        H = mVVFrame.getWidth();
                    } else if (screenRatio < 1) {//O in I
                        W = mVVFrame.getHeight();
                    }// else {//O in O}
                }
                break;
            default:
                auto = false;
                W = mVVP.getVideoWidth();
                H = mVVP.getVideoHeight();
                break;
        }

        ConstraintLayout.LayoutParams vLP = (ConstraintLayout.LayoutParams) mVV.getLayoutParams();
        if (auto) {
            if (width) vLP.width = ViewGroup.LayoutParams.MATCH_PARENT;
            else vLP.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            if (height) vLP.height = ViewGroup.LayoutParams.MATCH_PARENT;
            else vLP.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            vLP.width = W;
            vLP.height = H;
        }
        vLP.leftToLeft = mVVFrame.getId();
        vLP.topToTop = mVVFrame.getId();
        vLP.rightToRight = mVVFrame.getId();
        vLP.bottomToBottom = mVVFrame.getId();
        mVV.setLayoutParams(vLP);
    }

    void setupIcons(final boolean isAudio) {
        if (isAudio) settingMIcons = draughtMIcons.size() > 0;
        else settingFIcons = draughtFIcons.size() > 0;
        if ((isAudio && settingMIcons) || (!isAudio && settingFIcons)) {
            try {
                if (isAudio)
                    ((ImageView) findViewById(draughtMIcons.get(0).id)).setImageBitmap(draughtMIcons.get(0).bmp);
                else
                    ((ImageView) findViewById(draughtFIcons.get(0).id)).setImageBitmap(draughtFIcons.get(0).bmp);
            } catch (Exception ignored) {
            }
            try {
                if (isAudio) draughtMIcons.remove(0);
                else draughtFIcons.remove(0);
            } catch (Exception ignored) {
            }

            ValueAnimator setupImgRepr = VA(mMotor, "scaleY", setupImgDur, 2f, 1f);
            setupImgRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupIcons(isAudio);
                }
            });
        }
    }

    void setupItems(final int list) {
        if (list == 0) settingItems = draughtFolders.size() > 0;
        else settingItems = draughtMedia.size() > 0;
        if (settingItems) {
            try {
                if (list == 0) mFolders.addView(draughtFolders.get(0));
                else mSounds.addView(draughtMedia.get(0));
            } catch (Exception ignored) {
            }
            try {
                if (list == 0) draughtFolders.remove(0);
                else draughtMedia.remove(0);
            } catch (Exception ignored) {
            }

            ValueAnimator setupItemRepr = VA(mMotor, "alpha", setupItemDur, 0f, 1f);
            setupItemRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupItems(list);
                }
            });
        } else {
            if (!cancelMediaLoading) {
                try {
                    if (list == 0) new ImageLoader(fIconIds, false, 0).start();
                    else new ImageLoader(mIconIds, true, currentOpenedList).start();
                } catch (Exception ignored) {
                }
                try {
                    if (!video) {
                        if (Amin.mp != null && audioListIndex != -1)
                            highlightItem(0, audioListIndex);
                        if (Amin.mp != null && audioIndex != -1 && currentOpenedList == audioListIndex)
                            highlightItem(1, audioIndex);
                    } else {
                        if (mVV != null && videoListIndex != -1)
                            highlightItem(0, videoListIndex);
                        if (mVV != null && videoIndex != -1 && currentOpenedList == videoListIndex)
                            highlightItem(1, videoIndex);
                    }
                } catch (Exception ignored) {
                }
                if (list == currentOpenedList) changeNavList(list, true);
            } else cancelMediaLoading = false;
            mNavLoading(false);
        }
    }

    void fileInfo(boolean show, final Object media) {
        fileInfoOpened = show;
        if (show) {
            if (media == null) return;
            mFileInfo.removeAllViews();
            LayoutInflater.from(c).inflate(R.layout.file_info, mFileInfo);

            fiBody = findViewById(R.id.fiBody);
            fiTitle = findViewById(R.id.fiTitle);
            fiTIcon = findViewById(R.id.fiTIcon);
            fiTText = findViewById(R.id.fiTText);
            fiClose = findViewById(R.id.fiClose);
            fiSV = findViewById(R.id.fiSV);
            fiCL = findViewById(R.id.fiCL);
            fiAlbumArtDiv = findViewById(R.id.fiAlbumArtDiv);
            fiAlbumArt = findViewById(R.id.fiAlbumArt);
            fiNameDiv = findViewById(R.id.fiNameDiv);
            fiName = findViewById(R.id.fiName);
            fiNameEdit = findViewById(R.id.fiNameEdit);
            fiLL = findViewById(R.id.fiLL);
            fiSave = findViewById(R.id.fiSave);
            fiDelete = findViewById(R.id.fiDelete);

            fiSaveDisabled = fiSave.getAlpha();
            fiBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fiNameEdit.hasFocus())
                        Toast.makeText(c, "LLL", Toast.LENGTH_SHORT).show();
                }
            });
            fiClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fileInfo(false, null);
                }
            });
            if (changeColorPrimary) fiTitle.setBackgroundColor(colorPrimary);
            if (changeTCP) {
                fiBody.setBackgroundColor(textColorPrimary);
                fiClose.setColorFilter(textColorPrimary);
                fiTIcon.setColorFilter(textColorPrimary);
                fiTText.setTextColor(textColorPrimary);
            }
            fixTVFont(fiTText, Typeface.BOLD);
            fixTVFont(fiName, Typeface.NORMAL);
            fixTVFont(fiNameEdit, Typeface.NORMAL);

            String[] values = new String[fileInfoList.length];
            int nPad = (int) (mToolbar.getHeight() * 0.4);
            File file;
            if (!video) {
                Audio aud = (Audio) media;
                file = new File(aud.getData());
                Bitmap aa = Amin.getAlbumArt(c, aud.getAlbumId());
                if (aa != null) fiAlbumArt.setImageBitmap(aa);
                else {
                    fiAlbumArt.setImageResource(R.drawable.sound_2_blue);
                    fiAlbumArt.setPadding(nPad, nPad, nPad, nPad);
                    if (changeColorPrimary) fiAlbumArt.setColorFilter(colorPrimary);
                }
                fiName.setText(aud.getName().substring(0, aud.getName().lastIndexOf(".")));
                values[0] = aud.getMime();//"audio/*." + aud.getName().substring(aud.getName().lastIndexOf(".") + 1)
                values[1] = aud.getData();
                values[2] = fixFSize(c, new File(aud.getData()).length());
                values[3] = aud.getTitle();
                values[4] = aud.getAlbum();
                values[5] = aud.getArtist();
                values[6] = fixDur(aud.getDur()) + "";
                Calendar LM = Calendar.getInstance();
                LM.setTimeInMillis(file.lastModified());
                values[7] = dateTime1(LM, Locale.getDefault().getLanguage().equals("fa"));
            } else {
                Video vid = (Video) media;
                file = new File(vid.data);
                Bitmap tn = ThumbnailUtils.createVideoThumbnail(vid.data, MediaStore.Images.Thumbnails.MINI_KIND);
                if (tn != null) fiAlbumArt.setImageBitmap(tn);
                else {
                    fiAlbumArt.setImageResource(R.drawable.video_1_blue);
                    fiAlbumArt.setPadding(nPad, nPad, nPad, nPad);
                    if (changeColorPrimary) fiAlbumArt.setColorFilter(colorPrimary);
                }
                fiName.setText(vid.name.substring(0, vid.name.lastIndexOf(".")));
                values[0] = vid.mime;//"video/*." + vid.name.substring(vid.name.lastIndexOf(".") + 1)
                values[1] = vid.data;
                values[2] = fixFSize(c, new File(vid.data).length());
                values[3] = vid.title;
                values[4] = vid.album;
                values[5] = vid.artist;
                values[6] = fixDur(vid.dur) + "";
                Calendar LM = Calendar.getInstance();
                LM.setTimeInMillis(file.lastModified());
                values[7] = dateTime1(LM, Locale.getDefault().getLanguage().equals("fa"));
            }

            boolean itemColour = true;
            for (int i = 0; i < fileInfoList.length; i++) {
                LinearLayout fiItem = new LinearLayout(new ContextThemeWrapper(c, R.style.fiItem),
                        null, 0);
                int fiItemColour = ContextCompat.getColor(c, R.color.fiItem0);
                if (itemColour) {
                    fiItemColour = ContextCompat.getColor(c, R.color.fiItem1);
                    if (changeColorPrimary)
                        fiItemColour = Color.argb(44, Color.red(colorPrimary), Color.green(colorPrimary), Color.blue(colorPrimary));
                }
                itemColour = !itemColour;
                fiItem.setBackgroundColor(fiItemColour);
                fiLL.addView(fiItem);

                TextView fiAttr = new TextView(new ContextThemeWrapper(c, R.style.fiAttr), null, 0);
                LinearLayout.LayoutParams fiItemLP = new LinearLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
                fiAttr.setText(fileInfoList[i]);
                if (changeColorPrimary) fiAttr.setTextColor(colorPrimary);
                fixTVFont(fiAttr, Typeface.BOLD);
                fiItem.addView(fiAttr, fiItemLP);

                TextView fiValue = new TextView(new ContextThemeWrapper(c, R.style.fiValue), null, 0);
                LinearLayout.LayoutParams fiValueLP = new LinearLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 7f);
                fiValue.setText(values[i]);
                fixTVFont(fiValue, Typeface.NORMAL);
                fiItem.addView(fiValue, fiValueLP);
            }

            fiName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fiName.setVisibility(View.GONE);
                    fiNameEdit.setVisibility(View.VISIBLE);
                    fiNameEdit.setText(fiName.getText().toString());
                }
            });
            fiNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    fiCanSave(!fiNameEdit.getText().toString().equals(fiName.getText().toString()));
                }
            });
            final String PATH = values[1];
            final File FILE = file;
            fiSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fiCanSave && FILE.exists()) {
                        try {
                            File dest = new File(PATH.substring(0, PATH.lastIndexOf("/") + 1) +
                                    fiNameEdit.getText().toString() + PATH.substring(PATH.lastIndexOf(".")));
                            if (!dest.exists()) {
                                if (FILE.renameTo(dest)) {
                                    Toast.makeText(c, R.string.renamed,
                                            Toast.LENGTH_LONG).show();
                                    fiName.setText(fiNameEdit.getText().toString());
                                    fiNameEdit.setVisibility(View.GONE);
                                    fiName.setVisibility(View.VISIBLE);
                                    loadMedia(1, true);
                                    /*if (!video) ((Audio) media).setName(fiNameEdit.getText().toString());
                                    else ((Video) media).setName(fiNameEdit.getText().toString());*/
                                } else
                                    Toast.makeText(c, R.string.couldntRename,
                                            Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(c, R.string.suchFileExists,
                                        Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(c, R.string.couldntRename + "\n\n" +
                                    e.getClass().getName(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            fiDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if ((video && vPlaying) || (!video && playing))
                    makeBasicMessage(getResources().getString(R.string.deleteFileT),
                            getResources().getString(R.string.deleteFile) + PATH,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mb1Click();
                                    try {
                                        if (FILE.delete()) {
                                            fileInfo(false, null);
                                            loadMedia(1, true);
                                            Toast.makeText(c, R.string.deleted,
                                                    Toast.LENGTH_LONG).show();
                                        } else
                                            Toast.makeText(c, R.string.couldntDelete,
                                                    Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(c, R.string.couldntDelete + "\n\n" +
                                                e.getClass().getName(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }, true, null);
                    //else Toast.makeText(c, R.string.firstStopThenDelete, Toast.LENGTH_SHORT).show();
                }
            });
            Drawable fiSaveBG = fiSave.getBackground(), fiDeleteBG = fiDelete.getBackground();
            fiSaveBG.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(c, R.color.fiSave), PorterDuff.Mode.SRC_IN));
            fiDeleteBG.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(c, R.color.fiDelete), PorterDuff.Mode.SRC_IN));
            fiSave.setBackground(fiSaveBG);
            fiDelete.setBackground(fiDeleteBG);

            fiBody.setScaleX(0f);
            fiBody.setScaleY(0f);
            fiBody.setTranslationX(-(float) (dm.widthPixels / 2));
            fiBody.setTranslationY(-(float) (dm.heightPixels / 2));
            mFileInfo.setVisibility(View.VISIBLE);
            OA(fiBody, "scaleX", 1f, fiDur);
            OA(fiBody, "scaleY", 1f, fiDur);
            OA(fiBody, "translationX", 0f, fiDur);
            ValueAnimator fiAn = OA(fiBody, "translationY", 0f, fiDur);
            fiAn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    int pad = (int) ((fiClose.getWidth() - (dm.density * 16)) / 2);
                    fiClose.setPadding(pad, pad, pad, pad);
                }
            });
        } else {
            mFileInfo.setVisibility(View.GONE);
            mFileInfo.removeAllViews();
        }
    }

    void fiCanSave(boolean can) {
        fiCanSave = can;
        if (can) fiSave.setAlpha(1f);
        else fiSave.setAlpha(fiSaveDisabled);
    }

    void loadMedia(int type, boolean object) {
        cancelLoadings(true, true);
        draughtFolders = new ArrayList<>();
        draughtMedia = new ArrayList<>();
        draughtFIcons = new ArrayList<>();
        draughtMIcons = new ArrayList<>();
        new MediaLoader(type, object).start();

        mFoldersSV.setVisibility(View.GONE);
        mSoundsSV.setVisibility(View.GONE);
        mNavLoading(true);
    }

    void showItems(boolean isAudio, int folder) {
        if (isAudio) {
            cancelLoadings(false, true);
            draughtMedia = new ArrayList<>();
            mediaMaker = new ItemMaker(isAudio, folder);
            mediaMaker.start();

            if (mSounds.getChildCount() > 1)
                for (int i = mSounds.getChildCount() - 1; i > 0; i--) mSounds.removeViewAt(i);
            ConstraintLayout navBack = (ConstraintLayout) mSounds.getChildAt(0);
            ((TextView) ((LinearLayout) navBack.getChildAt(0)).getChildAt(1)).setText(folders.get(folder).name);
            changeNavList(1, false);
        } else {
            cancelLoadings(true, true);
            draughtFolders = new ArrayList<>();
            draughtMedia = null;
            folderMaker = new ItemMaker(isAudio, folder);
            folderMaker.start();

            if (mFolders.getChildCount() > 0) mFolders.removeAllViews();
            changeNavList(0, false);
        }
        mNavLoading(true);
    }

    void mNavLoading(boolean show) {
        mNLAnim = show;
        if (show) {
            if (mNLCircle == null) mNLCircle();
            mNavLoading.setVisibility(View.VISIBLE);
        } else mNavLoading.setVisibility(View.GONE);
    }

    void mNLCircle() {
        mNLCircle = new AnimatorSet();
        ValueAnimator circle1 = ObjectAnimator.ofFloat(mNLCircle1, "rotation", 0f, 360f),
                circle2 = ObjectAnimator.ofFloat(mNLCircle2, "rotation", 360f, 0f);
        mNLCircle.setDuration(mNLCircleDur);
        mNLCircle.playTogether(circle1, circle2);
        mNLCircle.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mNLAnim) mNLCircle();
                else mNLCircle = null;
            }
        });
        mNLCircle.start();

    }

    FoundFile findFile(File fSelected) {
        boolean plays = false;
        int selected = 0;

        ArrayList audioListGet = null;
        int audioListIndexGet = -1, audioIndexGet = -1;
        for (int f = 0; f < folders.size(); f++)
            if (folders.get(f).path.equals(fSelected.getParent())) {
                selected = f;
                audioListGet = folders.get(selected).files;
                audioListIndexGet = selected;
            }
        if (!video) {
            for (int a = 0; a < folders.get(selected).files.size(); a++)
                if (((Audio) folders.get(selected).files.get(a)).getData().equals(fSelected.getPath()))
                    audioIndexGet = a;
        } else {
            for (int a = 0; a < folders.get(selected).files.size(); a++)
                if (((Video) folders.get(selected).files.get(a)).data.equals(fSelected.getPath()))
                    audioIndexGet = a;
        }

        plays = audioListGet != null && audioListIndexGet != -1 && audioIndexGet != -1;
        if (plays) return new FoundFile(audioListGet, audioListIndexGet, audioIndexGet);
        else return null;
    }

    void btnSkip(boolean foreward) {
        if (foreward) {
            if (!video) sendToMSession(2);
            else skipVideo(true);
        } else {
            if (!video) sendToMSession(3);
            else skipVideo(false);
        }
    }

    void playPause() {
        if (!video) {
            if (playing) sendToMSession(1);
            else sendToMSession(0);
        } else {
            if (mVV != null) {
                if (vPlaying) mVV.pause();
                else mVV.start();
                vPlaying(!vPlaying);
            }
        }
    }

    Bitmap folderMark(int folder, int mark) {
        Bitmap icFolder, place;
        if (!changeColorPrimary) icFolder = bmpRes(folder);
        else icFolder = (Bitmap) changeColor(c, folder, colorPrimary, false);
        if (!changeColorPrimary) place = bmpRes(mark);
        else place = (Bitmap) changeColor(c, mark, textColorPrimary, false);

        Bitmap bmOverlay = Bitmap.createBitmap(icFolder.getWidth(), icFolder.getHeight(), icFolder.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(icFolder, new Matrix(), null);
        Matrix amin = new Matrix();//float scale = 0.85f;amin.setScale(scale, scale);
        amin.setTranslate(0f, icFolder.getHeight() - place.getHeight());//(int) (place.getHeight() * scale)
        canvas.drawBitmap(place, amin, null);//0, 0
        return bmOverlay;
    }

    void dlDialogue(boolean show) {
        if (show) {
            if (dlDialogue == null) {//"Mahdi.this" IS NECESSARY!!!
                dlDialogue = new ProgressDialog(Mahdi.this, R.style.ProgressDialogue1);
                dlDialogue.setMessage(getResources().getString(R.string.updating));
                dlDialogue.setIndeterminate(false);
                dlDialogue.setMax(100);
                dlDialogue.setProgressStyle(ProgressDialog.STYLE_SPINNER);//STYLE_HORIZONTAL
                dlDialogue.setCancelable(false);
                dlDialogue.setCanceledOnTouchOutside(false);
            }
            dlDialogue.show();
            View v = new View(c);
            dlDialogue.addContentView(v, new ViewGroup.LayoutParams(0, 0));
            FrameLayout mainParent = (FrameLayout) v.getParent();
            mainParent.setBackground(null);
            LinearLayout nextParent1 = (LinearLayout) mainParent.getChildAt(0);
            nextParent1.setBackground(null);
            ViewGroup parent = (ViewGroup) mainParent.getParent();
            parent.setBackground(null);
            ViewGroup parentParent = (ViewGroup) parent.getParent();
            parentParent.setBackground(null);
        } else if (dlDialogue != null) dlDialogue.dismiss();
    }

    public static String fixFSize(Context c, long length) {
        int g = 0, m = 0, k = 0, b = 0;
        long l = length;
        g = (int) (l / 1073741824);
        l -= g * 1073741824;
        m = (int) (l / 1048576);
        l -= m * 1048576;
        k = (int) (l / 1024);
        l -= k * 1024;
        b = (int) l;

        String r = "", z = "0", Gm = "", Mk = "", Kb = "", GET;
        String[] a = c.getResources().getStringArray(R.array.size);
        StringBuilder MM = new StringBuilder(), KK = new StringBuilder(), BB = new StringBuilder();
        for (int mmm = 0; mmm < 3 - Integer.toString(m).length(); mmm++) MM.append(z);
        for (int kkk = 0; kkk < 3 - Integer.toString(k).length(); kkk++) KK.append(z);
        for (int bbb = 0; bbb < 3 - Integer.toString(b).length(); bbb++) BB.append(z);
        if (m > 0) {
            GET = MM.toString() + m;
            if (GET.substring(2, 3).equals(z)) GET = GET.substring(0, 2);
            if (GET.substring(1, 2).equals(z)) GET = GET.substring(0, 1);
            Gm = "." + GET;
        }
        if (k > 0) {
            GET = KK.toString() + k;
            if (GET.substring(2, 3).equals(z)) GET = GET.substring(0, 2);
            if (GET.substring(1, 2).equals(z)) GET = GET.substring(0, 1);
            Mk = "." + GET;
        }
        if (b > 0) {
            GET = BB.toString() + b;
            if (GET.substring(2, 3).equals(z)) GET = GET.substring(0, 2);
            if (GET.substring(1, 2).equals(z)) GET = GET.substring(0, 1);
            Kb = "." + GET;
        }
        if (g != 0) r = g + Gm + " " + a[3];
        else {
            if (m != 0) r = m + Mk + " " + a[2];
            else {
                if (k != 0) r = k + Kb + " " + a[1];
                else r = b + " " + a[0];
            }
        }
        return r;
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) length += file.length();
            else length += folderSize(file);
        }
        return length;
    }

    void cancelLoadings(boolean f, boolean m) {
        if (f) {
            if (folderMaker != null) try {
                folderMaker.interrupt();
            } catch (Exception ignored) {
            }
            folderMaker = null;
            if (draughtFolders != null) {
                if (draughtFolders.size() > 0) cancelMediaLoading = true;
                draughtFolders.clear();
            }
            if (draughtFIcons != null) draughtFIcons.clear();
        }
        if (m) {
            if (mediaMaker != null) try {
                mediaMaker.interrupt();
            } catch (Exception ignored) {
            }
            mediaMaker = null;
            if (draughtMedia != null) {
                if (draughtMedia.size() > 0) cancelMediaLoading = true;
                draughtMedia.clear();
            }
            if (draughtMIcons != null) draughtMIcons.clear();
        }
    }

    void fixTVFont(TextView tv, int tf) {
        tv.setTypeface(Typeface.create(fonts[font], tf));
        tv.setTextSize((tv.getTextSize() / dm.density) * fontRatio);
    }

    String dateTime1(Calendar cl, boolean toPersian) {
        if (!toPersian)
            return cl.get(Calendar.YEAR) + "." + cl.get(Calendar.MONTH) + "." + cl.get(Calendar.DAY_OF_MONTH) + " " +
                    cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) + ":" + cl.get(Calendar.SECOND);
        else {
            int[] cnv = aConvert(cl);
            return cnv[0] + "." + cnv[1] + "." + cnv[2] + " " + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) +
                    ":" + cl.get(Calendar.SECOND);
        }
    }

    public static int[] aConvert(Calendar cl) {
        int y = cl.get(Calendar.YEAR), m = cl.get(Calendar.MONTH), d = cl.get(Calendar.DAY_OF_MONTH), l = 0, pl = 0, Y = 0, M = 0, D = 0;
        if (isLeapYear(y)) l = 1;
        Calendar clPl = Calendar.getInstance();
        clPl.set(y - 1, 1, 1);
        if (isLeapYear(clPl.get(Calendar.YEAR))) pl = 1;

        switch (m) {
            case 1:
                if (d <= (20 - pl)) {
                    Y = y - 622;
                    M = m + 9;
                    D = d + (10 + pl);
                } else {
                    Y = y - 622;
                    M = m + 10;
                    D = d - (20 - pl);
                }
                break;
            case 2:
                if (d <= (19 - pl)) {
                    Y = y - 622;
                    M = m + 9;
                    D = d + (11 + pl);
                } else {
                    Y = y - 622;
                    M = m + 10;
                    D = d - (19 - pl);
                }
                break;
            case 3:
                if (d <= (20 - l)) {
                    Y = y - 622;
                    M = m + 9;
                    D = d + ((9 + l) + pl);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (20 - l);
                }
                break;
            case 4:
                if (d <= (20 - l)) {
                    Y = y - 621;
                    M = m - 3;
                    D = d + (11 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (20 - l);
                }
                break;
            case 5:
                if (d <= (21 - l)) {
                    Y = y - 621;
                    M = m - 3;
                    D = d + (10 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (21 - l);
                }
                break;
            case 6:
                if (d <= (21 - l)) {
                    Y = y - 621;
                    M = m - 3;
                    D = d + (10 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (21 - l);
                }
                break;
            case 7:
                if (d <= (22 - l)) {
                    Y = y - 621;
                    M = m - 3;
                    D = d + (9 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (22 - l);
                }
                break;
            case 8:
                if (d <= (22 - l)) {
                    Y = y - 621;
                    M = m - 3;
                    D = d + (9 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (22 - l);
                }
                break;
            case 9:
                if (d <= (22 - l)) {
                    Y = y - 621;
                    M = m - 3;
                    D = d + (9 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (22 - l);
                }
                break;
            case 10:
                if (d <= (22 - l)) {
                    Y = y - 621;
                    M = m - 3;
                    D = d + (8 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (22 - l);
                }
                break;
            case 11:
                if (d <= (21 - l)) {
                    Y = y - 621;
                    M = m - 3;
                    D = d + (9 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (21 - l);
                }
                break;
            case 12:
                if (d <= (21 - l)) {
                    Y = y - 622;
                    M = m - 3;
                    D = d + (9 + l);
                } else {
                    Y = y - 621;
                    M = m - 2;
                    D = d - (21 - l);
                }
                break;
        }
        return new int[]{Y, M, D};
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    void destroyAmin() {
        if (serviceBound) try {
            unbindService(serviceConnection);//service is still active
            serviceBound = false;
        } catch (Exception ignored) {
        }
        if (player != null) player.stopSelf();//for precaution
    }


    static class Folder {
        String name, path;
        ArrayList files;
        boolean isExternal;

        Folder(String name, String path, ArrayList files, boolean isExternal) {
            this.name = name;
            this.path = path;
            this.files = files;
            this.isExternal = isExternal;
        }

        void addAudio(Audio aud) {
            this.files.add(aud);
        }

        void addVideo(Video vid) {
            this.files.add(vid);
        }

        void replaceFiles(ArrayList files) {
            this.files = files;
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlDialogue(true);
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();//this will be useful so that you can show a tipical 0-100% progress bar
                InputStream input = new BufferedInputStream(url.openStream(), 8192);//download the file
                OutputStream output = new FileOutputStream(updates);//pasteURL

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;// After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);//writing data to file
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception ignored) {//DON'T TOAST HERE... IT'S NOT A PROPER THREAD...
            }//IT APPEARS THAT YOU MOVED THIS CLASS INTO ANOTHER THREAD BY THOSE 4 LINES AT onCreate.
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            dlDialogue.setProgress(Integer.parseInt(progress[0]));
            dlDialogue.setMessage(getResources().getString(R.string.updating) + " (" + progress[0] + "%)");
        }

        @Override
        protected void onPostExecute(String file_url) {
            dlDialogue(false);

            if (updates.exists()) {
                Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= 24) {
                    promptInstall.setDataAndType(FileProvider.getUriForFile(c,
                            BuildConfig.APPLICATION_ID + ".provider", updates),
                            "application/vnd.android.package-archive");
                    List<ResolveInfo> resInfoList = c.getPackageManager().queryIntentActivities(promptInstall,
                            PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        c.grantUriPermission(resolveInfo.activityInfo.packageName, Uri.parse(updates.getPath()),
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    promptInstall.setDataAndType(Uri.fromFile(updates), "application/vnd.android.package-archive");
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(promptInstall);
            } else Toast.makeText(c, R.string.cfuError2, Toast.LENGTH_LONG).show();
        }

    }

    class sortFolders implements Comparator<Folder> {
        int by = 0;

        sortFolders(int by) {
            this.by = by;
        }

        public int compare(Folder a, Folder b) {
            File fa, fb;
            switch (by) {
                case 1:
                    return a.files.size() - b.files.size();
                case 2:
                    fa = new File(a.path);
                    fb = new File(b.path);
                    return (int) (folderSize(fa) - folderSize(fb));
                case 3:
                    int pa = 0, pb = 0;
                    if (a.isExternal) pa = 1;
                    if (b.isExternal) pb = 1;
                    return pa - pb;
                case 4:
                    fa = new File(a.path);
                    fb = new File(b.path);
                    return (int) (fb.lastModified() - fa.lastModified());
                default:
                    return a.name.compareTo(b.name);
            }
        }
    }

    class sortAudios implements Comparator<Audio> {
        int by = 0;

        sortAudios(int by) {
            this.by = by;
        }

        public int compare(Audio a, Audio b) {
            File fa, fb;
            switch (by) {
                case 1:
                    return a.getTitle().compareTo(b.getTitle());
                case 2:
                    fa = new File(a.getData());
                    fb = new File(b.getData());
                    return (int) (fa.length() - fb.length());
                case 3:
                    return a.getDur() - b.getDur();
                case 4:
                    return a.getAlbum().compareTo(b.getAlbum());
                case 5:
                    return a.getArtist().compareTo(b.getArtist());
                case 6:
                    return a.getMime().compareTo(b.getMime());
                case 7:
                    fa = new File(a.getData());
                    fb = new File(b.getData());
                    return (int) (fb.lastModified() - fa.lastModified());
                default:
                    return a.getName().compareTo(b.getName());
            }
        }
    }

    class sortVideos implements Comparator<Video> {
        int by = 0;

        sortVideos(int by) {
            this.by = by;
        }

        public int compare(Video a, Video b) {
            File fa, fb;
            switch (by) {
                case 1:
                    return a.title.compareTo(b.title);
                case 2:
                    fa = new File(a.data);
                    fb = new File(b.data);
                    return (int) (fa.length() - fb.length());
                case 3:
                    return a.dur - b.dur;
                case 4:
                    return a.album.compareTo(b.album);
                case 5:
                    return a.artist.compareTo(b.artist);
                case 6:
                    return a.mime.compareTo(b.mime);
                case 7:
                    fa = new File(a.data);
                    fb = new File(b.data);
                    return (int) (fb.lastModified() - fa.lastModified());
                default:
                    return a.name.compareTo(b.name);
            }
        }
    }

    class Video {
        private String data, name, title, album, artist, mime;
        private int dur;

        private Video(String data, String name, String title, String album, String artist, int dur, String mime) {
            this.data = data;
            this.name = name;
            this.title = title;
            this.album = album;
            this.artist = artist;
            this.dur = dur;
            this.mime = mime;
        }

        void setName(String name) {
            this.name = name;
        }
    }

    class ImageLoader extends Thread {
        private ArrayList<Integer> iconIds;
        private boolean isAudio = false;
        private int folder = 0, i = 0;

        private ImageLoader(ArrayList<Integer> iconIds, boolean isAudio, int folder) {
            this.iconIds = iconIds;
            this.isAudio = isAudio;
            this.folder = folder;
        }

        @Override
        public void run() {
            imageLoader(iconIds, isAudio, folder);
        }

        void imageLoader(final ArrayList<Integer> list, final boolean isAudio, final int folder) {
            try {
                Bitmap bmp;
                int iconRes;
                if (isAudio) {
                    if (!video) {
                        bmp = Amin.getAlbumArt(c, ((Audio) folders.get(folder).files.get(i)).getAlbumId());
                        if (bmp == null) {
                            iconRes = R.drawable.sound_2_blue;
                            if (!changeColorPrimary) bmp = bmpRes(iconRes);
                            else bmp = (Bitmap) changeColor(c, iconRes, colorPrimary, false);
                        }
                    } else {
                        bmp = ThumbnailUtils.createVideoThumbnail(((Video) folders.get(folder).files.get(i)).data,
                                MediaStore.Images.Thumbnails.MINI_KIND);
                        if (bmp == null) {
                            iconRes = R.drawable.video_1_blue;
                            if (!changeColorPrimary) bmp = bmpRes(iconRes);
                            else bmp = (Bitmap) changeColor(c, iconRes, colorPrimary, false);
                        }
                    }
                } else {
                    if (folders.get(i).isExternal) bmp = sdFolder;
                    else bmp = isFolder;
                }
                if (isAudio) iconMHandler.obtainMessage(list.get(i), bmp).sendToTarget();
                else iconFHandler.obtainMessage(list.get(i), bmp).sendToTarget();
            } catch (Exception ignored) {
            }
            i += 1;
            if (i < list.size()) imageLoader(list, isAudio, folder);//imageLoader = 22
        }
    }

    class MediaLoader extends Thread {
        private int type;
        private boolean Obj;

        private MediaLoader(int type, boolean Obj) {
            this.type = type;
            this.Obj = Obj;
        }

        @Override
        public void run() {
            Uri uri1, uri2;
            String selection = null, sortOrder;
            if (!video) {
                uri1 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
                sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
            } else {
                uri1 = MediaStore.Video.Media.INTERNAL_CONTENT_URI;
                uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                sortOrder = MediaStore.Video.Media.TITLE + " ASC";
            }
            Cursor cursor1 = getContentResolver().query(uri1, null, selection, null, sortOrder),
                    cursor2 = getContentResolver().query(uri2, null, selection, null, sortOrder);
            //"cursor2" NEEDS "READ_EXTERNAL_STORAGE" PERMISSION, along with FileUtils!
            folders = new ArrayList<>();
            readCursor(cursor1, false);
            readCursor(cursor2, true);

            boolean plays = false;
            int unable = 0;
            if (folders.size() > 0) {
                if (fArrangement != 0) Collections.sort(folders, new sortFolders(0));
                Collections.sort(folders, new sortFolders(fArrangement));
                if (!fArrangementAsc) Collections.reverse(folders);
                for (int f = 0; f < folders.size(); f++) {
                    ArrayList files = folders.get(f).files;
                    if (files != null) {
                        if (!video) {
                            if (mArrangement != 0) Collections.sort(files, new sortAudios(0));
                            Collections.sort(files, new sortAudios(mArrangement));
                        } else {
                            if (mArrangement != 0) Collections.sort(files, new sortVideos(0));
                            Collections.sort(files, new sortVideos(mArrangement));
                        }
                        if (!mArrangementAsc) Collections.reverse(files);
                        folders.get(f).replaceFiles(files);
                    }
                }

                //Find the selected file if exists
                if (getIntent() != null && getIntent().getData() != null) {
                    FoundFile file = findFile(FileUtils.getFile(c, getIntent().getData()));
                    plays = file != null;
                    if (!plays) unable = 1;
                    else {
                        if (!video) {
                            audioList = file.list;
                            audioListIndex = file.listIndex;
                            audioIndex = file.index;
                        } else {
                            videoList = file.list;
                            videoListIndex = file.listIndex;
                            videoIndex = file.index;
                        }
                    }
                }
            }

            if (Amin.exists && !video) {
                FoundFile file = findFile(new File(Amin.activeAudio.getData()));
                if (file != null) {
                    audioList = file.list;
                    audioListIndex = file.listIndex;
                    audioIndex = file.index;
                    Amin.audioList = file.list;
                    Amin.audioIndex = file.index;
                    StorageUtil storage = new StorageUtil(c);
                    storage.storeAudio(audioList);
                    storage.storeAudioIndex(audioIndex);
                    storage.storeAudioListIndex(audioListIndex);
                    if (reCreation) {
                        updateDescription(true);
                        mSeekBar.setEnabled(true);
                        AminHandler.obtainMessage(1).sendToTarget();//Amin.mp.isPlaying() & playing() botch the job!
                        if (!Amin.playing) AminHandler.obtainMessage(0).sendToTarget();
                    }
                } else destroyAmin();
            }//There was no need to find any video.

            int object = 0;
            switch (type) {
                case 0:
                    if (plays) object = 1;
                    break;
                case 1:
                    if (Obj) object = 1;
                    break;
            }
            loadedHandler.obtainMessage(type, unable, 0, object).sendToTarget();//"0" is nothing, void...
        }

        private void readCursor(Cursor cursor, boolean isExternal) {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String data, name, title, album, artist, mime;
                    int dur;
                    Audio aud = null;
                    Video vid = null;
                    if (!video) {
                        data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                        dur = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                        mime = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
                        if (supported(true, name.substring(name.lastIndexOf(".") + 1)))
                            aud = new Audio(data, name, title, album, artist, albumId, dur, mime);
                    } else {
                        data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                        album = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ALBUM));
                        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST));
                        dur = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                        mime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                        if (supported(false, name.substring(name.lastIndexOf(".") + 1)))
                            vid = new Video(data, name, title, album, artist, dur, mime);
                    }

                    if ((!video && aud != null) || (video && vid != null)) {
                        String folderPath = new File(data).getParent();
                        boolean gotFolder = false;
                        for (int f = 0; f < folders.size(); f++) {
                            if (folders.get(f).path.equals(folderPath)) {
                                gotFolder = true;
                                if (!video) folders.get(f).addAudio(aud);
                                else folders.get(f).addVideo(vid);
                            }
                        }
                        if (!gotFolder) {
                            ArrayList files = new ArrayList<>();
                            if (!video) files.add(aud);
                            else files.add(vid);
                            folders.add(new Folder(folderPath.substring(folderPath.lastIndexOf("/") + 1),
                                    folderPath, files, isExternal));
                        }
                    }
                }
            }
            if (cursor != null) cursor.close();
        }

        public boolean supported(boolean aud, String type) {
            boolean supported = false;
            String[] array = aSupport;
            if (!aud) array = vSupport;
            for (int s = 0; s < array.length; s++)
                if (array[s].equalsIgnoreCase(type)) supported = true;
            return supported;
        }
    }

    class GotBmp {
        private int id;
        private Bitmap bmp;

        private GotBmp(int id, Bitmap bmp) {
            this.id = id;
            this.bmp = bmp;
        }
    }

    class ItemMaker extends Thread {
        private boolean isAudio;
        private int folder;

        private ItemMaker(boolean isAudio, int folder) {
            this.isAudio = isAudio;
            this.folder = folder;
        }

        @Override
        public void run() {
            cancelMediaLoading = false;
            ArrayList<Integer> itemIds = new ArrayList<>();
            int length = folders.size(), list = 0;

            if (isAudio) {
                length = folders.get(folder).files.size();
                list = 1;
            } else if (mFolders.getChildCount() > 0) mFolders.removeAllViews();

            ArrayList<Integer> iconIds = new ArrayList<>();
            for (int f = 0; f < length; f++) {
                String tvItemT = "", tvItemDescT = "";
                View.OnClickListener itemClick;
                View.OnLongClickListener itemLongClick = null;
                final int F = f, FOLDER = folder;
                if (isAudio) {
                    if (!video) {
                        Audio aud = (Audio) folders.get(FOLDER).files.get(F);
                        tvItemT = aud.getName();
                        switch (mArrangement) {
                            case 1:
                                tvItemDescT = aud.getTitle();
                                break;
                            case 2:
                                tvItemDescT = fixFSize(c, new File(aud.getData()).length());
                                break;
                            case 4:
                                tvItemDescT = aud.getAlbum();
                                break;
                            case 5:
                                tvItemDescT = aud.getArtist();
                                break;
                            case 6:
                                Calendar LM = Calendar.getInstance();
                                File file = new File(aud.getData());
                                LM.setTimeInMillis(file.lastModified());
                                tvItemDescT = LM.get(Calendar.YEAR) + "." + LM.get(Calendar.MONTH) + "." +
                                        LM.get(Calendar.DAY_OF_MONTH) + " " + LM.get(Calendar.HOUR_OF_DAY) +
                                        ":" + LM.get(Calendar.MINUTE) + ":" + LM.get(Calendar.SECOND);
                                break;
                            default:
                                tvItemDescT = fixDur(aud.getDur());
                                break;
                        }
                    } else {
                        Video vid = (Video) folders.get(FOLDER).files.get(F);
                        tvItemT = vid.name;
                        switch (mArrangement) {
                            case 1:
                                tvItemDescT = vid.title;
                                break;
                            case 2:
                                tvItemDescT = fixFSize(c, new File(vid.data).length());
                                break;
                            case 4:
                                tvItemDescT = vid.album;
                                break;
                            case 5:
                                tvItemDescT = vid.artist;
                                break;
                            case 6:
                                tvItemDescT = vid.mime;
                                break;
                            case 7:
                                Calendar LM = Calendar.getInstance();
                                LM.setTimeInMillis(new File(vid.data).lastModified());
                                tvItemDescT = dateTime1(LM, Locale.getDefault().getLanguage().equals("fa"));
                                break;
                            default:
                                tvItemDescT = fixDur(vid.dur);
                                break;
                        }
                    }
                    itemClick = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!video) {
                                audioList = folders.get(FOLDER).files;
                                audioListIndex = FOLDER;
                                audioIndex = F;
                                playAudio();
                            } else {
                                videoList = folders.get(FOLDER).files;
                                videoListIndex = FOLDER;
                                videoIndex = F;
                                playVideo(true, false);
                            }
                        }
                    };
                    itemLongClick = new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            fileInfo(true, folders.get(FOLDER).files.get(F));
                            return true;
                        }
                    };
                } else {
                    Folder fol = folders.get(f);
                    tvItemT = fol.name;
                    switch (mArrangement) {
                        case 1:
                            int meds = R.string.audios;
                            if (video) meds = R.string.videos;
                            tvItemDescT = fol.files.size() + " " + getResources().getString(meds);
                            break;
                        case 2:
                            tvItemDescT = fixFSize(c, folderSize(new File(fol.path)));
                            break;
                        case 4:
                            Calendar LM = Calendar.getInstance();
                            LM.setTimeInMillis(new File(fol.path).lastModified());
                            tvItemDescT = dateTime1(LM, Locale.getDefault().getLanguage().equals("fa"));
                            break;
                        default:
                            tvItemDescT = folderPath(folders.get(f).path);
                            break;
                    }
                    itemClick = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currentOpenedList = F;
                            showItems(true, F);
                        }
                    };
                }

                ConstraintLayout clItem = new ConstraintLayout(
                        new ContextThemeWrapper(c, R.style.mClItem), null, 0);
                LinearLayout.LayoutParams clItemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                itemIds.add(View.generateViewId());
                clItem.setId(itemIds.get(f));
                clItem.setLayoutParams(clItemLP);
                clItem.setOnClickListener(itemClick);
                if (isAudio) {
                    clItem.setOnLongClickListener(itemLongClick);
                    clItem.setPaddingRelative(clItem.getPaddingStart(), clItem.getPaddingTop(), 0, clItem.getPaddingBottom());
                }
                int itmMoreId = View.generateViewId();

                ImageView ivItem = new ImageView(new ContextThemeWrapper(c, R.style.mIvItem), null, 0);
                ConstraintLayout.LayoutParams ivItemLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                ivItemLP.startToStart = itemIds.get(f);
                ivItemLP.topToTop = itemIds.get(f);
                ivItem.setId(View.generateViewId());
                iconIds.add(ivItem.getId());
                clItem.addView(ivItem, ivItemLP);

                LinearLayout llItemContents = new LinearLayout(
                        new ContextThemeWrapper(c, R.style.mLlItemContents), null, 0);
                ConstraintLayout.LayoutParams llItemContentsLP = new ConstraintLayout.LayoutParams(0,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                llItemContentsLP.startToEnd = ivItem.getId();
                if (!isAudio) llItemContentsLP.endToEnd = itemIds.get(f);
                else llItemContentsLP.endToStart = itmMoreId;
                llItemContentsLP.topToTop = itemIds.get(f);
                llItemContents.setOrientation(LinearLayout.VERTICAL);
                clItem.addView(llItemContents, llItemContentsLP);

                TextView tvItem = new TextView(new ContextThemeWrapper(c, R.style.mTvItem), null, 0);
                LinearLayout.LayoutParams tvItemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvItem.setText(tvItemT);
                tvItem.setTextColor(ContextCompat.getColor(c, R.color.mFolTvItem));
                fixTVFont(tvItem, Typeface.BOLD);
                llItemContents.addView(tvItem, tvItemLP);

                TextView tvItemDesc = new TextView(
                        new ContextThemeWrapper(c, R.style.mTvItemDesc), null, 0);
                LinearLayout.LayoutParams tvItemDescLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvItemDesc.setText(tvItemDescT);
                tvItemDesc.setTextColor(ContextCompat.getColor(c, R.color.mFolTvItemPath));
                fixTVFont(tvItemDesc, Typeface.NORMAL);
                llItemContents.addView(tvItemDesc, tvItemDescLP);

                if (isAudio) {
                    ImageView itmMore = new ImageView(new ContextThemeWrapper(c, R.style.mItmMore), null, 0);
                    ConstraintLayout.LayoutParams itmMoreLP = new ConstraintLayout.LayoutParams(
                            (int) (dm.density * 36), 0);
                    itmMoreLP.topToTop = itemIds.get(f);
                    itmMoreLP.bottomToBottom = itemIds.get(f);
                    itmMoreLP.endToEnd = itemIds.get(f);
                    itmMore.setId(itmMoreId);
                    clItem.addView(itmMore, itmMoreLP);
                    itmMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fileInfo(true, folders.get(FOLDER).files.get(F));
                        }
                    });
                }

                itemLoadedHandler.obtainMessage(list, clItem).sendToTarget();
            }
            itemsFinishedHandler.obtainMessage(list, itemIds).sendToTarget();
            if (isAudio) mIconIds = iconIds;
            else fIconIds = iconIds;
        }
    }

    class FoundFile {
        private ArrayList list;
        private int listIndex, index;

        private FoundFile(ArrayList list, int listIndex, int index) {
            this.list = list;
            this.listIndex = listIndex;
            this.index = index;
        }
    }
}
