package org.ifaco.a222player;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.media.ThumbnailUtils;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Mahdi extends AppCompatActivity {
    ConstraintLayout mContainer, mVVFrame, mBody, mSVCL, mPlayerBody, mSTManager, mPanel, mShuffle, mMoveB, mPlayPause, mMoveF,
            mRepeat, mNavigation, mNavTitle, mNavList, mNavLoading, mNavAddPL, mBadge1, mBadge2, mBadge3, mBadge4, mSelecting,
            mFileInfo, mLoading;
    View mMotor, mMotorItem, mMotorIcon, mTouchSensor, mPanelBg, mSeekBarReplica, mSideBtnsBorderRestL, mShuffleBG, mMoveBBG,
            mSideBtnsBorderL, mSideBtnsBorderR, mMoveFBG, mRepeatBG, mSideBtnsBorderRestR, mSlctBG;
    SurfaceView mVV;
    ImageView mShwBG, mTBBorderEnd, mAlbumArt, mSTManagerAdd, mSTManagerClose, mSHShine, mMBShine, mPPShine, mMFShine, mRPShine,
            mShuffleBT, mMoveBBT, mPlayPauseBG, mPlayPauseBT, mMoveFBT, mRepeatBT, mNavIconBorder, mNavIcon, mNavIconPlays, mToastIV,
            mNavTitleBack, mNTSearchClose, mNTSlctClose, mNTSearchBtn, mNLCircle1, mNLCircle2, mNavAddPLIV, mLoadingIV,
            mLoadingIVPlays;
    Toolbar mToolbar;
    SubtitleView mSV1, mSV2;
    SeekBar mSeekBar;
    TextView mDur, mRepeatTV, mToastTV, mNavTitleTV, mNTSlct, mLoadingTV, mLoadingVersion;
    ScrollView mAFoldersSV, mAudiosSV, mVFoldersSV, mVideosSV, mPlaylistsSV, mPLItemsSV, mSettingsSV, mBasicMessages;
    LinearLayout mSVLL, mSTManagerLL, mToast, mNavTitleLL, mNTSearchLL, mNTSlctLL, mAFolders, mAudios, mVFolders, mVideos,
            mPlaylists, mPLItems, mSettings, mBadges, mBasicMessagesLL;
    EditText mNTSearch, mNavAddPLET;
    FloatingActionButton mSlctMark, mSlctUnmark, mSlctDlt, mSlctAdd;

    //file_info.xml
    ConstraintLayout fiBody, fiTitle, fiCL, fiAlbumArtDiv, fiNameDiv;
    ImageView fiTIcon, fiClose, fiAlbumArt, fiSave, fiDelete;
    TextView fiTText, fiName;
    ScrollView fiSV;
    LinearLayout fiLL;
    EditText fiNameEdit, fiPLDesc;

    //display-settings.xml
    LinearLayout mDSIcons, mDSAudio, mSAPBSLL, mSAPBPLL, mSALELL, mDSVideo, mSVCrop1, mSVCrop2, mSVCrop3, mSVCrop4;
    RelativeLayout mDSIcon1P, mDSIcon2P;
    View mDSIcon1Selected, mDSIcon2Selected;
    ImageView mDSIcon1, mDSIcon2, mSAEqSpnMark, mSAPBSReset, mSAPBPReset, mSAPRSpnMark;
    ConstraintLayout mSAEq, mSAPBS, mSAPBP, mSAVr, mSABB, mSAPR, mSALE, mSVCrop;
    Spinner mSAEqSpinner, mSAPRSpinner;
    SeekBar mSAPBSSB, mSAPBPSB, mSAVrSB, mSABBSB, mSALESB;
    TextView mSAPBSTitle;

    public static int loadDur1 = 179, navDur = 78, repeat = 0, adtWPMax = 5, wp = 0, eqBands = 5;
    final int controlPlayerDur = 48, mLoadingDur = 480, exitDur = 48, hidePanelDur = 111, highestVolume = 15, setupImgDur = 22,
            fiDur = 222, setupItemDur = 11, highlightCPAlpha = 111, mNLCircleDur = 973, doubleTouchDur = 222, videoEachMove = 5000,
            badgeBG = 0, badgeBT = 1, shineDur = 480, itmMorePlace = 2, mDSIconsAlpha = 193, mtbSubtitle = 0, GET_SUBTITLE = 90,
            wc = ViewGroup.LayoutParams.WRAP_CONTENT, match = ViewGroup.LayoutParams.MATCH_PARENT;
    boolean playing = false, changingTheSeekBar = false, serviceBound = false, exiting = false, isNavOpen = false, reCreation = false,
            video = false, vPlaying = false, hiddenPanel = false, touchMovedEnough = false, keepScreen = false, showingBM = false,
            bmCancellable = true, fileInfoOpened = false, mNLAnim = false, changeCP = false, changeTCP = false, settingFIcons = false,
            settingAIcons = false, settingVIcons = false, mTSLeft = false, doubleTouch = false, swipeNav = false, vPlayOnResume = false,
            fArrangementAsc = true, mArrangementAsc = true, pArrangementAsc = true, touchXMovedEnough = false, settingAFolders = false,
            settingVFolders = false, settingAudios = false, settingVideos = false, aflCancelled = false, audCancelled = false,
            vflCancelled = false, vidCancelled = false, audToFolder = true, vidToFolder = true, pltToFolder = true, videoMode = false,
            selecting = false, newIntent = false, whitenBGPC = false, stManager = false, sv1Has = false, sv2Has = false,
            prepared = false, vpTrackLagging = true, fiSaving = false, movingPLI = false, searching = false, wpLoaded = false,
            folsLoaded = false;
    ValueAnimator reproduction;
    Amin player;
    ArrayList<Audio> audioList;
    public static final String PKG = "org.ifaco.a222player", Broadcast_PLAY_NEW_AUDIO = PKG + ".PlayNewAudio", adtWP = "adtWP",
            wpBoolsDef = "11100000", cpTag = "colorPrimary", tcpTag = "textColorPrimary", plDatabase = "playlists", eqBand = "eqBand",
            vrStrength = "vrStrength", bbStrength = "bbStrength", rvPreset = "rvPreset", leGain = "leGain", pbSpeed = "pbSpeed",
            pbPitch = "pbPitch", sEQMode = "eqMode";
    private static final int permWriteExerStor = 630, exitingDelay = 4080;
    public static Handler AminHandler, changeHandler, destroyMe;
    public static ArrayList<Folder> aFolders, vFolders;
    ActionBar mToolbarAB;
    String version = null, fiDescDef = null;
    ScrollView[] navLists;
    public static DisplayMetrics dm = new DisplayMetrics();
    int openedNav = 0, audioIndex = -1, audioListIndex = -1, openedAFolder = -1, openedVFolder = -1, videoIndex = -1,
            videoListIndex = -1, resumePos = 0, crop = 1, CP = 0, defCP = 0, TCP = 0, defTCP = 0, swipeToSkipMin = 111,
            swipeNavMin = 48, fArrangement = 0, mArrangement = 0, pArrangement = 0, font = 3, openedPL = -1, playingPL = -1,
            playingPLItem = -1, openedDS = 0, eqMode = 0;
    ArrayList<Integer> aflClItemIds, vflClItemIds, audClItemIds, vidClItemIds, shuffleHistory, aflIconIds, audIconIds, vflIconIds,
            vidIconIds, pllClItemIds, pliClItemIds, pllIconIds, pliIconIds, eqSBIds;
    final float mSBsDisabled = 0.58f, leastOpacity = 0.4f, opacityEachMove = 0.04f, vVolumeEachMove = 0.075f;
    public static boolean shuffle = false, dirLtr = true, customEQ = false;
    SharedPreferences sp;
    TextView mTBTitle;
    public static MediaPlayer vp;
    ArrayList<Video> videoList;
    Video activeVideo = null;
    float y1, yMove, x1, xMove, x2, fiSaveDisabled, touchMSensibility = 22f, fontRatio = 1f, badgeScale = 1.15f, minimizeWP = 1f;
    float[] touchLMoved, touchRMoved, touchXMoved;
    Handler hIconF, hIconA, hIconV, hLoaded, hItemsFinished, hItemLoaded, hPlaylistManager, hWPLoader;
    ArrayList<GotBmp> draughtFIcons = new ArrayList<>(), draughtAIcons = new ArrayList<>(), draughtVIcons = new ArrayList<>();
    String[] fileInfoList, plInfoList, fonts, rvPresets, eqModes, saCrop;
    ArrayList<ConstraintLayout> draughtAFolders = new ArrayList<>(), draughtVFolders = new ArrayList<>(),
            draughtAudios = new ArrayList<>(), draughtVideos = new ArrayList<>();
    public static final int[] resWallpapers = {R.drawable.music_bg_1, R.drawable.music_bg_2, R.drawable.music_bg_3};
    boolean[] wpBooleans = {true, true, true, false};
    public static final String[] supportedRtlLangs = {"fa"}, aSupport = {"3gp", "mp4", "m4a", "aac", "ts", "flac", "gsm", "mid",
            "xmf", "mxmf", "rtttl", "rtx", "ota", "imy", "mp3", "wav", "ogg", "mkv"}, vSupport = {"3gp", "mp4", "ts", "webm", "mkv"};
    Context c;
    ItemMaker audioMaker, videoMaker, aFolderMaker, vFolderMaker;
    AnimatorSet mNLCircle;
    public static float vVolume = 1f;
    public static final float navBtnRatio = 0.28f;
    Bitmap sdFolder, isFolder;
    SurfaceHolder mSH;
    ConstraintLayout[] badges;
    ImageView[] shines;
    boolean[] selection;
    LinearLayout slctList;
    ArrayList<String> searchItems;
    LinearLayout[] navListLLs, cropLLs;
    Intent playerIntent;
    ArrayList<P.Playlist> playlists;
    FloatingActionButton[] slctBtns;
    FoundFile activePLItem = null;
    short[] eqRange;
    Equalizer equalizer, testEq;
    Virtualizer virtualizer;
    BassBoost bassBoost;
    PresetReverb presetReverb;
    LoudnessEnhancer loudnessEnhancer;
    ArrayList<Subtitle> subtitles = new ArrayList<>();
    Uri incoming = null;
    public static short[] eqVals = {0, 0, 0, 0, 0};
    final short[][] wmpEQModes = {/*0. Default:*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, /*1. Rock:*/{-1, 1, 2, 3, -1, -1, 0, 0, 4, 4},
            /*2. Rap:*/{-1, 0, 2, 2, -1, -1, 0, 0, 4, 6}, /*3. Grunge:*/{-4, 0, 0, -2, 0, 0, 2, 3, 0, -3},
            /*4. Metal:*/{-4, 0, 0, 0, 0, 0, 3, 0, 3, 1}, /*5. Dance:*/{-1, 4, 5, 1, -1, -1, 0, 0, 4, 4},
            /*6. Techno:*/{-6, 1, 4, -1, -1, -2, 0, 0, 5, 5}, /*7. Country:*/{-1, 0, 0, 2, 2, 0, 0, 0, 3, 3},
            /*8. Jazz:*/{0, 0, 0, 3, 3, 3, 0, 2, 4, 4}, /*9. Acoustic:*/{0, 1, 2, 0, 0, 0, 0, 0, 2, 2},
            /*10. Folk:*/{-1, 0, 1, 0, 2, 0, 0, 0, 2, 0}, /*11. New Age:*/{0, 3, 3, 0, 0, 0, 0, 0, 1, 1},
            /*12. Classical:*/{0, 6, 6, 3, 0, 0, 0, 0, 2, 2}, /*13. Blues:*/{-1, 0, 2, 1, 0, 0, 0, 0, -1, -3},
            /*14. Oldies:*/{-2, 0, 2, 1, 0, 0, 0, 0, -2, -5}, /*15. Reggae:*/{-1, 0, 0, -3, 0, 3, 4, 0, 3, 4},
            /*16. Opera:*/{0, 0, 0, 3, 4, 2, 5, 2, 0, 0}, /*17. Swing:*/{-1, 0, 0, 0, 3, 3, 0, 2, 4, 4},
            /*18. Speech:*/{-2, 0, 2, 1, 0, 0, 0, 0, -2, -5}, /*19. Music 56K:*/{0, 0, 0, 0, 0, 0, 0, 0, -2, 0},
            /*20. Music 28K:*/{0, 0, 0, 0, 0, 0, 0, -2, -3, 0}};
    final int[] wmpEQBandFreqs = {31000, 62000, 125000, 250000, 500000, 1000000, 2000000, 4000000, 8000000, 16000000};//Millihertz
    int[][] eqFreqs;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        mContainer = findViewById(R.id.mContainer);
        mMotor = findViewById(R.id.mMotor);
        mMotorItem = findViewById(R.id.mMotorItem);
        mMotorIcon = findViewById(R.id.mMotorIcon);
        mVVFrame = findViewById(R.id.mVVFrame);
        mVV = findViewById(R.id.mVV);
        mTouchSensor = findViewById(R.id.mTouchSensor);
        mBody = findViewById(R.id.mBody);
        mShwBG = findViewById(R.id.mShwBG);
        mTBBorderEnd = findViewById(R.id.mTBBorderEnd);
        mToolbar = findViewById(R.id.mToolbar);
        mSVCL = findViewById(R.id.mSVCL);
        mSVLL = (LinearLayout) mSVCL.getChildAt(0);
        mSV2 = (SubtitleView) mSVLL.getChildAt(0);
        mSV1 = (SubtitleView) mSVLL.getChildAt(1);
        mPlayerBody = findViewById(R.id.mPlayerBody);
        mAlbumArt = findViewById(R.id.mAlbumArt);
        mSTManager = findViewById(R.id.mSTManager);
        mSTManagerAdd = findViewById(R.id.mSTManagerAdd);
        mSTManagerLL = findViewById(R.id.mSTManagerLL);
        mSTManagerClose = findViewById(R.id.mSTManagerClose);
        mPanelBg = findViewById(R.id.mPanelBg);
        mSeekBarReplica = findViewById(R.id.mSeekBarReplica);
        mSeekBar = findViewById(R.id.mSeekBar);
        mPanel = findViewById(R.id.mPanel);
        mDur = (TextView) mPanel.getChildAt(0);
        mSHShine = (ImageView) mPanel.getChildAt(1);
        mMBShine = (ImageView) mPanel.getChildAt(2);
        mPPShine = (ImageView) mPanel.getChildAt(3);
        mMFShine = (ImageView) mPanel.getChildAt(4);
        mRPShine = (ImageView) mPanel.getChildAt(5);

        mSideBtnsBorderRestL = mPanel.getChildAt(6);
        mShuffle = findViewById(R.id.mShuffle);
        mShuffleBG = mShuffle.getChildAt(0);
        mShuffleBT = (ImageView) mShuffle.getChildAt(1);
        mMoveB = findViewById(R.id.mMoveB);
        mMoveBBG = mMoveB.getChildAt(0);
        mMoveBBT = (ImageView) mMoveB.getChildAt(1);
        mSideBtnsBorderL = mPanel.getChildAt(9);
        mPlayPause = findViewById(R.id.mPlayPause);
        mPlayPauseBG = (ImageView) mPlayPause.getChildAt(0);
        mPlayPauseBT = (ImageView) mPlayPause.getChildAt(1);
        mSideBtnsBorderR = mPanel.getChildAt(11);
        mMoveF = findViewById(R.id.mMoveF);
        mMoveFBG = mMoveF.getChildAt(0);
        mMoveFBT = (ImageView) mMoveF.getChildAt(1);
        mRepeat = findViewById(R.id.mRepeat);
        mRepeatBG = mRepeat.getChildAt(0);
        mRepeatBT = (ImageView) mRepeat.getChildAt(1);
        mRepeatTV = (TextView) mRepeat.getChildAt(2);
        mSideBtnsBorderRestR = mPanel.getChildAt(14);
        mNavIconBorder = findViewById(R.id.mNavIconBorder);
        mNavIcon = findViewById(R.id.mNavIcon);
        mNavIconPlays = findViewById(R.id.mNavIconPlays);
        mToast = findViewById(R.id.mToast);
        mToastIV = findViewById(R.id.mToastIV);
        mToastTV = findViewById(R.id.mToastTV);

        mNavigation = findViewById(R.id.mNavigation);
        mNavTitle = findViewById(R.id.mNavTitle);
        mNavTitleLL = (LinearLayout) mNavTitle.getChildAt(0);
        mNavTitleBack = (ImageView) mNavTitleLL.getChildAt(0);
        mNavTitleTV = (TextView) mNavTitleLL.getChildAt(1);
        mNTSearchLL = (LinearLayout) mNavTitle.getChildAt(1);
        mNTSearchClose = (ImageView) mNTSearchLL.getChildAt(0);
        mNTSearch = (EditText) mNTSearchLL.getChildAt(1);
        mNTSlctLL = (LinearLayout) mNavTitle.getChildAt(2);
        mNTSlctClose = (ImageView) mNTSlctLL.getChildAt(0);
        mNTSlct = (TextView) mNTSlctLL.getChildAt(1);
        mNTSearchBtn = findViewById(R.id.mNTSearchBtn);
        mNavList = findViewById(R.id.mNavList);
        mAFoldersSV = findViewById(R.id.mAFoldersSV);
        mAFolders = findViewById(R.id.mAFolders);
        mAudiosSV = findViewById(R.id.mAudiosSV);
        mAudios = findViewById(R.id.mAudios);
        mVFoldersSV = findViewById(R.id.mVFoldersSV);
        mVFolders = findViewById(R.id.mVFolders);
        mVideosSV = findViewById(R.id.mVideosSV);
        mVideos = findViewById(R.id.mVideos);
        mPlaylistsSV = findViewById(R.id.mPlaylistsSV);
        mPlaylists = findViewById(R.id.mPlaylists);
        mPLItemsSV = findViewById(R.id.mPLItemsSV);
        mPLItems = findViewById(R.id.mPLItems);
        mSettingsSV = findViewById(R.id.mSettingsSV);
        mSettings = findViewById(R.id.mSettings);
        mNavLoading = findViewById(R.id.mNavLoading);
        mNLCircle1 = findViewById(R.id.mNLCircle1);
        mNLCircle2 = findViewById(R.id.mNLCircle2);
        mNavAddPL = findViewById(R.id.mNavAddPL);
        mNavAddPLET = (EditText) mNavAddPL.getChildAt(0);
        mNavAddPLIV = (ImageView) mNavAddPL.getChildAt(1);
        mBadges = findViewById(R.id.mBadges);
        mBadge1 = (ConstraintLayout) mBadges.getChildAt(0);
        mBadge2 = (ConstraintLayout) mBadges.getChildAt(1);
        mBadge3 = (ConstraintLayout) mBadges.getChildAt(2);
        mBadge4 = (ConstraintLayout) mBadges.getChildAt(3);

        mSelecting = findViewById(R.id.mSelecting);
        mSlctBG = findViewById(R.id.mSlctBG);
        mSlctMark = (FloatingActionButton) mSelecting.getChildAt(1);
        mSlctUnmark = (FloatingActionButton) mSelecting.getChildAt(2);
        mSlctDlt = (FloatingActionButton) mSelecting.getChildAt(3);
        mSlctAdd = (FloatingActionButton) mSelecting.getChildAt(4);
        mFileInfo = findViewById(R.id.mFileInfo);
        mBasicMessages = findViewById(R.id.mBasicMessages);
        mBasicMessagesLL = (LinearLayout) mBasicMessages.getChildAt(0);
        mLoading = findViewById(R.id.mLoading);
        mLoadingIV = findViewById(R.id.mLoadingIV);
        mLoadingIVPlays = findViewById(R.id.mLoadingIVPlays);
        mLoadingTV = findViewById(R.id.mLoadingTV);
        mLoadingVersion = findViewById(R.id.mLoadingVersion);

        //display-settings.xml
        mDSIcons = (LinearLayout) mSettings.getChildAt(0);
        mDSIcon1P = (RelativeLayout) mDSIcons.getChildAt(0);
        mDSIcon1Selected = mDSIcon1P.getChildAt(0);
        mDSIcon1 = (ImageView) mDSIcon1P.getChildAt(1);
        mDSIcon2P = (RelativeLayout) mDSIcons.getChildAt(1);
        mDSIcon2Selected = mDSIcon2P.getChildAt(0);
        mDSIcon2 = (ImageView) mDSIcon2P.getChildAt(1);

        mDSAudio = (LinearLayout) mSettings.getChildAt(1);
        mSAEq = findViewById(R.id.mSAEq);
        mSAEqSpinner = findViewById(R.id.mSAEqSpinner);
        mSAEqSpnMark = findViewById(R.id.mSAEqSpnMark);
        mSAPBSLL = findViewById(R.id.mSAPBSLL);
        mSAPBSTitle = (TextView) ((LinearLayout) mSAPBSLL.getChildAt(0)).getChildAt(1);
        mSAPBS = (ConstraintLayout) mSAPBSLL.getChildAt(1);
        mSAPBSSB = (SeekBar) mSAPBS.getChildAt(0);
        mSAPBSReset = findViewById(R.id.mSAPBSReset);
        mSAPBPLL = findViewById(R.id.mSAPBPLL);
        mSAPBP = (ConstraintLayout) mSAPBPLL.getChildAt(1);
        mSAPBPSB = (SeekBar) mSAPBP.getChildAt(0);
        mSAPBPReset = findViewById(R.id.mSAPBPReset);
        mSAVr = findViewById(R.id.mSAVr);
        mSAVrSB = (SeekBar) mSAVr.getChildAt(0);
        mSABB = findViewById(R.id.mSABB);
        mSABBSB = (SeekBar) mSABB.getChildAt(0);
        mSAPR = findViewById(R.id.mSAPR);
        mSAPRSpinner = (Spinner) mSAPR.getChildAt(0);
        mSAPRSpnMark = (ImageView) mSAPR.getChildAt(1);
        mSALELL = findViewById(R.id.mSALELL);
        mSALE = (ConstraintLayout) mSALELL.getChildAt(1);
        mSALESB = (SeekBar) mSALE.getChildAt(0);

        mDSVideo = (LinearLayout) mSettings.getChildAt(2);
        mSVCrop = findViewById(R.id.mSVCrop);
        mSVCrop1 = findViewById(R.id.mSVCrop1);
        mSVCrop2 = findViewById(R.id.mSVCrop2);
        mSVCrop3 = findViewById(R.id.mSVCrop3);
        mSVCrop4 = findViewById(R.id.mSVCrop4);

        c = getApplicationContext();
        for (String lang : supportedRtlLangs)
            if (Locale.getDefault().getLanguage().equals(lang)) {
                mContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                dirLtr = false;
            }
        try {
            version = c.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ignored) {
            version = "5";
        }
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        sp = PreferenceManager.getDefaultSharedPreferences(c);
        video = sp.getBoolean("video", false);
        vVolume = sp.getFloat("vVolume", 1f);
        fileInfoList = getResources().getStringArray(R.array.fileInfoList);
        plInfoList = getResources().getStringArray(R.array.plInfoList);
        setArrangement();
        setFonts();
        shines = new ImageView[]{mSHShine, mMBShine, mPPShine, mMFShine, mRPShine};
        playerIntent = new Intent(c, Amin.class);
        defCP = ContextCompat.getColor(c, R.color.colorPrimary);
        defTCP = ContextCompat.getColor(c, R.color.textColorPrimary);
        setPrimaryColours();
        rvPresets = getResources().getStringArray(R.array.rvPresets);
        eqModes = getResources().getStringArray(R.array.eqModes);
        cropLLs = new LinearLayout[]{mSVCrop1, mSVCrop2, mSVCrop3, mSVCrop4};
        saCrop = getResources().getStringArray(R.array.crop);
        if (getIntent() != null && getIntent().getData() != null) incoming = getIntent().getData();

        // Toolbar
        if (CP != defCP) mToolbar.setPopupTheme(R.style.TBPopupTheme1CngCP);
        setSupportActionBar(mToolbar);
        mToolbarAB = getSupportActionBar();
        for (int g = 0; g < mToolbar.getChildCount(); g++) {
            View getTitle = mToolbar.getChildAt(g);
            if (getTitle.getClass().getName().equalsIgnoreCase("androidx.appcompat.widget.AppCompatTextView") &&
                    ((TextView) getTitle).getText().toString().equals(getResources().getString(R.string.app_name)))
                mTBTitle = ((TextView) getTitle);
        }
        Drawable ovIcon = (Drawable) resizeByRatio(c, ContextCompat.getDrawable(c, R.drawable.overflow_3_blue),
                navBtnRatio, true);
        if (ovIcon != null) mToolbar.setOverflowIcon(ovIcon);

        // Navigation
        mNavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNavList(true);
            }
        });
        navLists = new ScrollView[]{mAFoldersSV, mAudiosSV, mVFoldersSV, mVideosSV, mPlaylistsSV, mPLItemsSV, mSettingsSV};
        navListLLs = new LinearLayout[]{mAFolders, mAudios, mVFolders, mVideos, mPlaylists, mPLItems, mSettings};
        mNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selecting) openNavList(false);
            }
        });
        mNavList.setOnClickListener(doNothing);
        mNavTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openedNav == 1 || openedNav == 3)
                    cancelLoadings(false, false, openedNav == 1, openedNav == 3);
                changeNavList(openedNav - 1, true);
            }
        });
        mNavTitleLL.setOnClickListener(doNothing);
        mNavTitleLL.setClickable(false);
        if (!dirLtr) {
            mNavTitleBack.setRotationY(180f);
            mTBBorderEnd.setRotationY(180f);
        }
        isFolder = folderMark(false);
        sdFolder = folderMark(true);
        mFileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileInfo(false, null, -1);
            }
        });
        badges = new ConstraintLayout[mBadges.getChildCount()];
        for (int b = 0; b < badges.length; b++)
            badges[b] = (ConstraintLayout) mBadges.getChildAt(b);
        mBadges.setOnClickListener(doNothing);
        mBadge1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = 0;
                if (!audToFolder) n = 1;
                changeNavList(n, true);
                lastOpenedNav();
            }
        });
        mBadge2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = 2;
                if (!vidToFolder) n = 3;
                changeNavList(n, true);
                lastOpenedNav();
            }
        });
        mBadge3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = 4;
                if (!pltToFolder) n = 5;
                changeNavList(n, true);
                lastOpenedNav();
            }
        });
        mBadge4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNavList(6, false);
                lastOpenedNav();
            }
        });
        changeNavList(sp.getInt("lastOpenedNav", 0), false);
        mNavAddPLIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t = mNavAddPLET.getText().toString();
                if (t.equals("")) return;
                new PlaylistManager(2, t).start();
            }
        });
        mNavAddPL.setOnClickListener(doNothing);

        // Handlers for Amin
        AminHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                playing(inputMessage.what == 1);
                if (!vPlaying) {
                    if (inputMessage.obj != null) {
                        mSeekBar.setEnabled(false);
                        audioList = null;
                        audioListIndex = -1;
                        audioIndex = -1;
                        activePLItem = null;
                        playingPL = -1;
                        playingPLItem = -1;
                        Amin.playlistOn = false;
                        highlightItem(0, -1);
                        highlightItem(1, -1);
                        highlightItem(4, -1);
                        highlightItem(5, -1);
                        updateDescription(false);
                        normalisePanel();
                    }
                } else mPlayPauseBT.setImageResource(R.drawable.pause_3);
            }
        };
        changeHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if (!Amin.playlistOn) {
                    audioIndex = inputMessage.what;
                    activePLItem = null;
                    playingPL = -1;
                    playingPLItem = -1;
                    highlightItem(4, -1);
                    highlightItem(5, -1);
                } else {
                    playingPLItem = Amin.audioIndex;
                    activePLItem = findFile(new File(Amin.audioList.get(inputMessage.what).getData()));
                    if (activePLItem != null) {
                        audioList = activePLItem.list;
                        audioListIndex = activePLItem.listIndex;
                        audioIndex = activePLItem.index;
                        if (playingPL == openedPL && openedPL != -1)
                            highlightItem(5, playingPLItem);
                    }
                }
                highlightItem(0, audioListIndex);
                if (audioListIndex == openedAFolder) highlightItem(1, audioIndex);
                updateDescription(true);

                /*SharedPreferences.Editor editor = sp.edit();
                editor.putString("lastAPlayed", ((Audio) aFolders.get(audioListIndex).files.get(audioIndex)).getData());
                editor.apply();*/
            }
        };
        destroyMe = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                destroyAmin();
            }
        };

        // Recreation of Amin
        mSeekBar.setEnabled(false);
        if (Amin.exists) {
            reCreation = true;
            video = false;
            if (!serviceBound) {
                startService(playerIntent);
                bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }

        // Loading Animations
        hWPLoader = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if (message.obj != null && message.obj instanceof Bitmap)
                    mShwBG.setImageBitmap((Bitmap) message.obj);
                else mShwBG.setImageResource(resWallpapers[0]);
                wpLoaded = true;
                checkLoaded();
            }
        };
        ValueAnimator anLoad = VA(mMotor, "translationX", loadDur1, 1f, 0f);
        anLoad.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (video) switchMedia(false);
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

                ConstraintLayout.LayoutParams mSeekBarReplicaLP = (ConstraintLayout.LayoutParams) mSeekBarReplica.getLayoutParams();
                int sbrMar = (int) (mSeekBar.getHeight() / 2.7);
                mSeekBarReplicaLP.leftMargin = sbrMar;
                mSeekBarReplicaLP.rightMargin = sbrMar;
                mSeekBarReplica.setLayoutParams(mSeekBarReplicaLP);

                swipeToSkipMin = dp(105);
                swipeNavMin = dp(30);
                touchMSensibility = dm.density * 15f;

                regulateEqSBs();
                applyWP();
            }
        });
        mLoading.setOnClickListener(doNothing);
        if (version != null) mLoadingVersion.setText("v" + version);

        // Media Player Buttons
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!changingTheSeekBar) return;
                if (!video) {
                    if (Amin.exists && Amin.mp != null) {
                        int s = Math.round(((float) Amin.mp.getDuration() / 100f) * (float) seekBar.getProgress());
                        mDur.setText(fixDur(s) + " / " + fixDur(((Audio) aFolders.get(audioListIndex).files.get(audioIndex)).getDur()));
                    }
                } else {
                    int s = Math.round(((float) vp.getDuration() / 100f) * (float) seekBar.getProgress());
                    mDur.setText(fixDur(s) + " / " + fixDur(vp.getDuration()));
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
                                    fixDur(((Audio) aFolders.get(audioListIndex).files.get(audioIndex)).getDur()));
                        }
                        heartbeat(mSeekBarReplica, 3.69f, new int[]{666, 179}, 0.55f);
                    } catch (Exception ignored) {
                    }
                } else if (vp != null) try {
                    int s = Math.round(((float) vp.getDuration() / 100f) * (float) seekBar.getProgress());
                    vp.seekTo(s);
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
        if (!dirLtr) mShuffleBT.setRotationY(180f);

        // Loading the lists
        hLoaded = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
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
                        if (reCreation) updateMPAfterRecreation();
                    } else destroyAmin();
                }
                folsLoaded = true;
                checkLoaded();

                //Find the selected file if exists
                if (incoming != null) {
                    FoundFile ff = findFile(FileUtils.getFile(c, incoming));
                    if (ff != null) playMedia(ff.v, ff.list, ff.listIndex, ff.index, true, true, 0);
                    else makeBasicMessage(getResources().getString(R.string.audioPlayer),
                            getResources().getString(R.string.unreadable), null, true, null);
                }

                if (!video) {//BY PRIORITY
                    if (aFolders.size() > 0) showItems(0, -1);
                    if (vFolders.size() > 0) showItems(2, -1);
                } else {
                    if (vFolders.size() > 0) showItems(2, -1);
                    if (aFolders.size() > 0) showItems(0, -1);
                }
                showPlaylists(-1);
            }
        };
        hItemLoaded = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        draughtAFolders.add((ConstraintLayout) message.obj);
                        if (!settingAFolders) setupAFolders();
                        break;
                    case 1:
                        draughtAudios.add((ConstraintLayout) message.obj);
                        if (!settingAudios) setupAudios();
                        break;
                    case 2:
                        draughtVFolders.add((ConstraintLayout) message.obj);
                        if (!settingVFolders) setupVFolders();
                        break;
                    case 3:
                        draughtVideos.add((ConstraintLayout) message.obj);
                        if (!settingVideos) setupVideos();
                        break;
                    case 4:
                        mPlaylists.addView((ConstraintLayout) message.obj);
                        break;
                    case 5:
                        mPLItems.addView((ConstraintLayout) message.obj);
                        break;
                }
            }
        };
        hItemsFinished = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                ArrayList<Integer>[] arr = (ArrayList<Integer>[]) message.obj;
                ArrayList<Integer> itemIds = arr[0], iconIds = arr[1];
                switch (message.what) {
                    case 0:
                        aflClItemIds = itemIds;
                        aflIconIds = iconIds;
                        break;
                    case 1:
                        audClItemIds = itemIds;
                        audIconIds = iconIds;
                        break;
                    case 2:
                        vflClItemIds = itemIds;
                        vflIconIds = iconIds;
                        break;
                    case 3:
                        vidClItemIds = itemIds;
                        vidIconIds = iconIds;
                        break;
                    case 4:
                        pllClItemIds = itemIds;
                        pllIconIds = iconIds;
                        if (Amin.mp != null && Amin.playlistOn) {
                            highlightItem(4, playingPL);
                            if (openedNav == 4) scrollToSelected(4);
                        }
                        break;
                    case 5:
                        pliClItemIds = itemIds;
                        pliIconIds = iconIds;
                        if (Amin.mp != null && Amin.playlistOn) {
                            highlightItem(5, playingPLItem);
                            if (openedNav == 5) scrollToSelected(5);
                        }
                        break;
                }
            }
        };
        hIconF = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                draughtFIcons.add(new GotBmp(message.what, (Bitmap) message.obj, message.arg1 == 1));
                if (!settingFIcons) setupFIcons();
            }
        };//Don't add the raw Messages to the ArrayList; they will corrupt that way.
        hIconA = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                draughtAIcons.add(new GotBmp(message.what, (Bitmap) message.obj, message.arg1 == 1));
                if (!settingAIcons) setupAIcons();
            }
        };
        hIconV = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                draughtVIcons.add(new GotBmp(message.what, (Bitmap) message.obj, message.arg1 == 1));
                if (!settingVIcons) setupVIcons();
            }
        };
        hPlaylistManager = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Object[] objs = (Object[]) message.obj;
                if (objs[0] instanceof String)
                    Toast.makeText(c, (String) objs[0], message.arg1).show();
                else if (objs[0] instanceof Integer)
                    Toast.makeText(c, (int) objs[0], message.arg1).show();


                if ((message.what == 0 || message.what == 1)) {// ADD TO A PLAYLIST + DELETE (FROM) A PLAYLIST
                    if (objs[1] == null) return;
                    int which = (int) objs[1];
                    if (openedPL == which) showPlaylists(which);
                    if (which != -1 && (fArrangement == 0 || fArrangement == 1) && pllClItemIds != null && pllClItemIds.size() > which) {
                        ConstraintLayout clItem = (ConstraintLayout) findViewById(pllClItemIds.get(which));
                        if (clItem != null) {
                            LinearLayout contents = (LinearLayout) clItem.getChildAt(1);
                            TextView desc = (TextView) contents.getChildAt(1);
                            desc.setText(playlists.get(which).items.size() + " " + getResources().getString(R.string.items));
                        }
                    }
                }
                switch (message.what) {
                    case 2:// ADD A PLAYLIST
                        boolean reload = true;
                        if (objs[1] != null) reload = (boolean) objs[1];
                        if (!reload) return;
                        if (playingPL != -1 && playingPLItem != -1) {
                            Object[] returns = preparePLAudList(playingPL, playingPLItem);
                            Amin.audioList = (ArrayList<Audio>) returns[0];
                            Amin.audioIndex = (int) returns[1];
                            StorageUtil storage = new StorageUtil(c);
                            storage.storeAudio(Amin.audioList);
                            storage.storeAudioIndex(Amin.audioIndex);
                        }
                        showPlaylists(-1);
                        mNavAddPLET.setText("");
                        break;
                    case 3:// EDIT A PLAYLIST
                        boolean done = true;
                        if (objs[2] != null) done = (boolean) objs[2];
                        if (done) {
                            fiDescDef = fiPLDesc.getText().toString();
                            String name = fiNameEdit.getText().toString();
                            if (objs[1] != null) name = (String) objs[1];
                            fiName.setText(name);
                            showPlaylists(-1);
                        }
                        fiSaving = false;
                        fiCanSave();
                        break;
                    case 4:// ADD AN ITEM
                        int number = (int) ((long) objs[1]), newNum = (int) ((long) objs[2]);
                        ConstraintLayout THIS = makeItem(3, number, openedPL, 5, false),
                                RW = makeItem(3, newNum, openedPL, 5, false);
                        boolean doSelectThis = selection[newNum], doSelectThat = selection[number];//YEAH, CONVERSELY & RIGHT HERE!
                        mPLItems.removeViewAt(number);
                        mPLItems.addView(THIS, number);
                        pliClItemIds.remove(number);
                        pliClItemIds.add(number, THIS.getId());
                        mPLItems.removeViewAt(newNum);
                        mPLItems.addView(RW, newNum);
                        pliClItemIds.remove(newNum);
                        pliClItemIds.add(newNum, RW.getId());
                        if (selecting) {
                            select(number, doSelectThis);
                            select(newNum, doSelectThat);
                            mobility(number, true);
                            mobility(newNum, true);
                        }
                        movingPLI = false;
                        break;
                }//DON'T MAKE "default", WE HAVE 0 + 1 UP THERE!
                //DON'T PUT ANYTHING HERE; WE HAVE "return;"S UP THERE!
            }
        };

        // Touching
        touchLMoved = new float[]{0f, 0f};
        touchRMoved = new float[]{0f, 0f};
        touchXMoved = new float[]{0f, 0f};
        mTouchSensor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if (e.getPointerCount() != 2) switch (e.getAction()) {
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

                        if (!swipeNav && video && vp != null && !touchMovedEnough) {
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
                                        if (!doubleTouch) return;
                                        doubleTouch = false;
                                        if (video && videoMode) hidePanel(!hiddenPanel);
                                    }
                                });
                            }
                        }
                        break;
                }
                return true;
            }
        });
        setVAlpha(sp.getFloat("mBodyAlpha", 1f));

        // Surface Holder for Video
        if (sp.contains("crop")) crop(sp.getInt("crop", 1));
        mSH = mVV.getHolder();
        mVV.setVisibility(View.VISIBLE);
        mVV.setBackgroundColor(Color.TRANSPARENT);
        mVV.setAlpha(1f);

        // Selecting
        slctBtns = new FloatingActionButton[]{mSlctMark, mSlctUnmark, mSlctDlt, mSlctAdd};
        for (int b = 0; b < slctBtns.length; b++) {
            Drawable drw = slctBtns[b].getDrawable();
            if (drw != null) {
                int criterion = dm.widthPixels, WH;
                if (dm.heightPixels < dm.widthPixels) criterion = dm.heightPixels;
                WH = (int) (criterion / 15f);//ONLY FOR mSlctUnmark

                Bitmap source = toBmp(c, drw, new int[]{WH, WH}), overlay;
                if (source != null) try {
                    overlay = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(overlay);
                    Matrix amin = new Matrix();
                    float scale = 0.79f;
                    if (b == 2) scale = 0.94f;
                    amin.setScale(scale, scale, (int) (source.getWidth() / 2), (int) (source.getHeight() / 2));
                    canvas.drawBitmap(source, amin, null);
                    slctBtns[b].setImageBitmap(overlay);
                } catch (Exception ignored) {
                }
            }
        }
        mSlctMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int c = 0; c < slctList.getChildCount(); c++) select(c, true);
            }
        });
        mSlctUnmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int c = 0; c < slctList.getChildCount(); c++) select(c, false);
            }
        });
        mSlctDlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int title = R.string.deleteFileT, text = R.string.deleteMass;
                if (openedNav == 4 || openedNav == 5) {
                    title = R.string.mBM1PLEditor;
                    if (openedNav == 4) text = R.string.mBM1PLDelMass;
                    if (openedNav == 5) text = R.string.mBM1PLItemDelMass;
                }
                boolean atLeastOne = false;
                for (int b = 0; b < selection.length; b++) if (selection[b]) atLeastOne = true;
                if (atLeastOne) makeBasicMessage(getResources().getString(title),
                        getResources().getString(text),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mb1Click();
                                if (openedNav == 1 || openedNav == 3) {
                                    boolean happilyDone = true;
                                    for (int b = selection.length - 1; b >= 0; b--)
                                        if (selection[b]) {
                                            boolean done;
                                            if (openedNav == 1) done = singleDelete(
                                                    ((Audio) aFolders.get(openedAFolder).files.get(b)).getData(), false);
                                            else done = singleDelete(
                                                    ((Video) vFolders.get(openedVFolder).files.get(b)).data, false);
                                            if (!done) happilyDone = false;
                                        }
                                    if (!happilyDone)
                                        Toast.makeText(c, R.string.couldntDeleteSome, Toast.LENGTH_LONG).show();
                                    int fol = openedAFolder;
                                    if (openedNav == 3) fol = openedVFolder;
                                    showItems(openedNav, fol);

                                } else if (openedNav == 4 || openedNav == 5) {
                                    ArrayList<Integer> alIds = new ArrayList<>();
                                    boolean desAmin = false;
                                    for (int b = 0; b < selection.length; b++)
                                        if (selection[b]) {
                                            alIds.add(b);
                                            if (b == playingPL) desAmin = true;
                                        }
                                    int pl = -1;
                                    if (openedNav == 5) {
                                        pl = openedPL;
                                        if (playingPL == openedPL && openedPL != -1) destroyAmin();
                                    } else if (desAmin) destroyAmin();
                                    new PlaylistManager(1, alIds.toArray(), pl).start();
                                }
                                selecting(false);
                            }
                        }, true, null);
            }
        });
        mSlctAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playlists == null) {
                    Toast.makeText(c, R.string.noPlaylists, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (playlists.size() > 0) {
                    boolean atLeastOne = false;
                    for (int b = 0; b < selection.length; b++) if (selection[b]) atLeastOne = true;
                    if (atLeastOne) addToPL(new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (openedNav != 1 && openedNav != 3) return;
                            ArrayList<String> alPaths = new ArrayList<>();
                            for (int b = 0; b < selection.length; b++)
                                if (selection[b]) {
                                    if (openedNav == 1)
                                        alPaths.add(((Audio) aFolders.get(openedAFolder).files.get(b)).getData());
                                    else
                                        alPaths.add(((Video) vFolders.get(openedVFolder).files.get(b)).data);
                                }
                            new PlaylistManager(0, alPaths.toArray(), which).start();
                        }
                    });
                } else Toast.makeText(c, R.string.noPlaylists, Toast.LENGTH_SHORT).show();
            }
        });
        mNTSlctClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecting(false);
            }
        });

        // Searching
        mNTSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnSearch(true);
            }
        });
        mNTSearchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnSearch(false);
            }
        });
        mNTSearchLL.setOnClickListener(doNothing);
        if (!dirLtr) mNTSearchBtn.setRotationY(180f);
        mNTSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();
                if (text.equals("") || searchItems == null)
                    for (int ch = 0; ch < navListLLs[openedNav].getChildCount(); ch++)
                        navListLLs[openedNav].getChildAt(ch).setVisibility(View.VISIBLE);
                else for (int s = 0; s < searchItems.size(); s++) {
                    int vis = View.GONE;
                    if (searchItems.get(s).toLowerCase().contains(text.toLowerCase()))
                        vis = View.VISIBLE;
                    navListLLs[openedNav].getChildAt(s).setVisibility(vis);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        // Display Settings
        mDSIcon1P.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDS(0);
            }
        });
        mDSIcon2P.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDS(1);
            }
        });
        openDS(0);

        // Equalization
        MediaPlayer testMP = new MediaPlayer();
        testEq = new Equalizer(0, testMP.getAudioSessionId());
        eqBands = testEq.getNumberOfBands();
        eqRange = testEq.getBandLevelRange();
        eqFreqs = new int[eqBands][];
        int[] phIds = new int[eqBands];
        for (int b = 0; b < eqBands; b++) {
            phIds[b] = View.generateViewId();
            eqFreqs[b] = testEq.getBandFreqRange((short) b);
        }
        testEq = null;
        testMP = null;
        for (int b = 0; b < eqBands; b++) {
            ImageView ph = new ImageView(c);
            ConstraintLayout.LayoutParams phLP = new ConstraintLayout.LayoutParams(0, wc);
            phLP.topToTop = mSAEq.getId();
            ph.setId(phIds[b]);
            if (b == 0) phLP.leftToLeft = mSAEq.getId();
            else phLP.leftToRight = phIds[b - 1];
            if (b == eqBands - 1) phLP.rightToRight = mSAEq.getId();
            else phLP.rightToLeft = phIds[b + 1];
            ph.setImageResource(R.drawable.square_1_transparent);
            ph.setAdjustViewBounds(true);
            mSAEq.addView(ph, phLP);
        }
        eqSBIds = new ArrayList<>();
        for (int b = 0; b < eqBands; b++) {
            SeekBar band = new SeekBar(c);//NO STYLES ARE ALLOWED!!!
            ConstraintLayout.LayoutParams bandLP = new ConstraintLayout.LayoutParams(dp(150), dp(12));
            bandLP.rightToLeft = phIds[b];
            bandLP.topToTop = phIds[b];
            band.setMax(eqRange[1] - eqRange[0]);
            band.setPivotX(bandLP.width);
            band.setPivotY(0f);
            band.setRotation(270f);
            band.setProgressDrawable(ContextCompat.getDrawable(c, R.drawable.seekbar_3_xml));
            band.setThumb(ContextCompat.getDrawable(c, R.drawable.sb3_thumb));
            band.setId(View.generateViewId());
            eqSBIds.add(band.getId());
            mSAEq.addView(band, bandLP);
            final short B = (short) b;
            band.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    eqVals[B] = (short) (i - (0 - eqRange[0]));
                    if (Amin.equalizer != null) Amin.equalizer.setBandLevel(B, eqVals[B]);
                    else if (Amin.mp != null) Amin.initEqualizer(sp);
                    if (equalizer != null) equalizer.setBandLevel(B, eqVals[B]);
                    else if (vp != null) initEqualizer();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (!customEQ) customizeEQ();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (customEQ) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(eqBand + B, (seekBar.getProgress() - (0 - eqRange[0])));
                        editor.apply();
                    }
                }
            });
        }
        setEQMode(sp.getInt(sEQMode, 0));//"onItemSelected" HANDLES THIS!???????????????????????
        mSAEqSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setEQMode(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Playback Speed
            mSAPBSLL.setVisibility(View.VISIBLE);
            setPBSpeed(pbsToInt(sp.getFloat(pbSpeed, 1f)), true, false, true, false);
            mSAPBSSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    setPBSpeed(i, false, true, true, false);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    setPBSpeed(seekBar.getProgress(), false, false, false, true);
                }
            });
            mSAPBSReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPBSpeed(100, true, true, true, true);
                }
            });
            // Playback Pitch
            mSAPBPLL.setVisibility(View.VISIBLE);
            setPBPitch(pbsToInt(sp.getFloat(pbPitch, 1f)), true, false, false);
            mSAPBPSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    setPBPitch(i, false, true, false);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    setPBPitch(seekBar.getProgress(), false, false, true);
                }
            });
            mSAPBPReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPBPitch(100, true, true, true);
                }
            });
        }

        // Virtualization
        mSAVrSB.setProgress(sp.getInt(vrStrength, 0));
        mSAVrSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (Amin.virtualizer != null) Amin.virtualizer.setStrength((short) i);
                else if (Amin.mp != null) Amin.initVirtualizer(sp);//EU TE AMO!!!
                if (virtualizer != null) virtualizer.setStrength((short) i);
                else if (vp != null) initVirtualizer();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(vrStrength, seekBar.getProgress());
                editor.apply();
            }
        });

        // Bass
        mSABBSB.setProgress(sp.getInt(bbStrength, 0));
        mSABBSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (Amin.bassBoost != null) Amin.bassBoost.setStrength((short) i);
                else if (Amin.mp != null) Amin.initBassBoost(sp);
                if (bassBoost != null) bassBoost.setStrength((short) i);
                else if (vp != null) initBassBoost();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(bbStrength, seekBar.getProgress());
                editor.apply();
            }
        });

        // Reverberation
        final short[] presets = {PresetReverb.PRESET_NONE, PresetReverb.PRESET_PLATE, PresetReverb.PRESET_SMALLROOM,
                PresetReverb.PRESET_MEDIUMROOM, PresetReverb.PRESET_LARGEROOM, PresetReverb.PRESET_MEDIUMHALL,
                PresetReverb.PRESET_LARGEHALL};
        mSAPRSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(rvPreset, presets[i]);
                editor.apply();
                if (Amin.presetReverb != null) Amin.presetReverb.setPreset(presets[i]);
                else if (Amin.mp != null) Amin.initPresetReverb(sp);
                if (presetReverb != null) presetReverb.setPreset(presets[i]);
                else if (vp != null) initPresetReverb();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Loudness Enhancement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSALELL.setVisibility(View.VISIBLE);
            mSALESB.setMax(1000);
            mSALESB.setProgress(sp.getInt(Mahdi.leGain, 0));
            mSALESB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (Amin.loudnessEnhancer != null) Amin.loudnessEnhancer.setTargetGain(i);
                    else if (Amin.mp != null) Amin.initLoudnessEnhancer(sp);
                    if (loudnessEnhancer != null) loudnessEnhancer.setTargetGain(i);
                    else if (vp != null) initLoudnessEnhancer();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt(leGain, seekBar.getProgress());
                    editor.apply();
                }
            });
        }

        // Cropping
        for (int l = 0; l < cropLLs.length; l++) {
            final int L = l;
            cropLLs[l].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crop(L);
                }
            });
            TextView tv = (TextView) cropLLs[l].getChildAt(1);
            tv.setText(saCrop[l]);
        }
        mSVCrop.getChildAt(crop).setVisibility(View.VISIBLE);

        // Subtitle Manager
        mSTManagerAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");//application/x-subrip
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.mGetST)), GET_SUBTITLE);
            }
        });
        mSTManagerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stManager(false);
            }
        });

        newIntent = false;
        onNewIntent(getIntent());//startActivity(new Intent(this, Settings.class));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Update Primary Colours
        if (newIntent) setPrimaryColours();
        PorterDuffColorFilter pdCP = new PorterDuffColorFilter(CP, PorterDuff.Mode.SRC_IN),
                pdTCP = new PorterDuffColorFilter(TCP, PorterDuff.Mode.SRC_IN);
        if (changeCP) {
            mToolbar.setTitleTextColor(CP);
            mToolbar.setSubtitleTextColor(CP);
            Drawable drwNIcon = mNavIcon.getBackground(), ovIcon = mToolbar.getOverflowIcon();
            if (drwNIcon != null) {
                drwNIcon.setColorFilter(pdCP);
                mNavIcon.setBackground(drwNIcon);
            }
            if (ovIcon != null) {
                ovIcon.setColorFilter(pdCP);
                mToolbar.setOverflowIcon(ovIcon);
            }
            TextView[] tvTCs = {mNavTitleTV, mNavAddPLET, mNTSearch, mNTSlct, mRepeatTV, mLoadingTV};
            for (TextView tvTC : tvTCs) tvTC.setTextColor(CP);
            ImageView[] ccpBtns = {mShuffleBT, mMoveBBT, mPlayPauseBT, mMoveFBT, mRepeatBT, mDSIcon1, mDSIcon2, mNTSearchClose,
                    mNTSearchBtn};
            for (ImageView ccpBtn : ccpBtns) ccpBtn.setColorFilter(CP);
            mNavAddPLIV.setImageBitmap((Bitmap) changeColor(c, mNavAddPLIV.getDrawable(), CP, null, false));
            Drawable mLCircle = mLoadingIV.getBackground();
            if (mLCircle != null) {
                mLCircle.setColorFilter(pdCP);
                mLoadingIV.setBackground(mLCircle);
            }
            if (TCP != defTCP) mLoadingVersion.setTextColor(CP);
        }
        if (changeTCP) {
            View[] setBGCs = {mToolbar, mNavTitle, mNavAddPL, mShuffleBG, mMoveBBG, mMoveFBG, mRepeatBG, mLoading};
            for (View setBGC : setBGCs) setBGC.setBackgroundColor(TCP);
            ImageView[] ivCFs = {mNavIconBorder, mTBBorderEnd, mNavIconPlays, mPlayPauseBG, mToastIV};
            for (ImageView ivCF : ivCFs) ivCF.setColorFilter(TCP);
            for (ImageView shine : shines) shine.setColorFilter(TCP);
            TextView[] setTCs = {mDur, mToastTV, mSV1, mSV2};
            for (TextView setTC : setTCs) setTC.setTextColor(TCP);
            View[] fixBGs = {mSideBtnsBorderL, mSideBtnsBorderR, mSideBtnsBorderRestL, mSideBtnsBorderRestR};
            for (View fixBG : fixBGs) {
                Drawable bg = fixBG.getBackground();
                if (bg != null) {
                    bg.setColorFilter(pdTCP);
                    fixBG.setBackground(bg);
                }
            }
            Drawable getIcon = mSeekBar.getThumb(), getPD = mSeekBar.getProgressDrawable(), mLPlays = mLoadingIVPlays.getDrawable();
            if (getIcon != null) {
                getIcon.setColorFilter(pdTCP);
                mSeekBar.setThumb(getIcon);
            }
            if (getPD != null) {
                getPD.setColorFilter(pdTCP);
                mSeekBar.setProgressDrawable(getPD);
            }
            if (mLPlays != null) {
                mLPlays.setColorFilter(pdTCP);
                mLoadingIVPlays.setImageDrawable(mLPlays);
            }
            mSeekBarReplica.setBackgroundColor(Color.argb(33, Color.red(TCP), Color.green(TCP), Color.blue(TCP)));
            mDSIcons.setBackgroundColor(Color.argb(mDSIconsAlpha, Color.red(TCP), Color.green(TCP), Color.blue(TCP)));
            View[] dsSlctd = {mDSIcon1Selected, mDSIcon2Selected};
            dsSlctd[openedDS].setBackgroundColor(TCP);
            for (int b = 0; b < eqSBIds.size(); b++) {
                SeekBar band = (SeekBar) findViewById(eqSBIds.get(b));
                if (band != null) styleSB3(band, pdTCP);
            }
            SeekBar[] styleSB3 = {mSAPBSSB, mSAPBPSB, mSAVrSB, mSABBSB, mSALESB};
            for (SeekBar sb : styleSB3) styleSB3(sb, pdTCP);
            for (int s = 0; s < mSTManagerLL.getChildCount(); s++) {
                LinearLayout ll = (LinearLayout) mSTManagerLL.getChildAt(s);
                ImageView cb = (ImageView) ll.getChildAt(0), del = (ImageView) ll.getChildAt(2);
                cb.setColorFilter(TCP);
                del.setColorFilter(TCP);
                TextView tv = (TextView) ll.getChildAt(1);
                tv.setTextColor(TCP);
            }
        }

        //Change TCP Additions
        View[] fixViewBG = {mPanelBg, mNavigation, mNavList, mToast, mFileInfo, mBasicMessages, mSlctBG, mSV1, mSV2, mSTManager};
        int[] fixViewBGC = {R.color.mPanelBg, R.color.mNavigationV, R.color.mNavList, R.color.mToast, R.color.mFileInfo,
                R.color.mFileInfo, R.color.bm1Container, R.color.mSelectingBG, R.color.mSVV, R.color.mSVV, R.color.mSTManager};
        if (whitenBGPC) for (int v = 0; v < fixViewBG.length; v++)
            fixViewBG[v].setBackgroundColor(Color.argb(Color.alpha(ContextCompat.getColor(c, fixViewBGC[v])),
                    255, 255, 255));
        else for (int v = 0; v < fixViewBG.length; v++)
            fixViewBG[v].setBackgroundColor(ContextCompat.getColor(c, fixViewBGC[v]));

        // Update Fonts
        if (newIntent) setFonts();
        if (mTBTitle != null) mTBTitle.setTypeface(Typeface.create(fonts[font], Typeface.BOLD));
        fixTVFont(mLoadingTV, Typeface.BOLD, 22f);
        fixTVFont(mDur, Typeface.BOLD, 14f);
        fixTVFont(mNavTitleTV, Typeface.NORMAL, 18f);
        fixTVFont(mNTSearch, Typeface.NORMAL, 16f);
        fixTVFont(mNTSlct, Typeface.NORMAL, 18f);
        fixTVFont(mNavAddPLET, Typeface.NORMAL, 17f);
        fixTVFont(mSV1, Typeface.NORMAL, 20f);
        fixTVFont(mSV2, Typeface.NORMAL, 20f);

        // Global
        for (int b = 0; b < badges.length; b++) {
            ImageView BT = (ImageView) badges[b].getChildAt(badgeBT);
            if (changeCP || b == 2) BT.setColorFilter(CP);
            if (changeTCP) {
                BT.setBackgroundColor(Color.argb(Color.alpha(ContextCompat.getColor(c, R.color.mBadgeBT)), Color.red(TCP),
                        Color.green(TCP), Color.blue(TCP)));
                badges[b].getChildAt(badgeBG).setBackgroundColor(TCP);
            }
        }
        mNavTitleBack.setColorFilter(CP);
        for (int b = 0; b < slctBtns.length; b++) {
            if (changeCP) {
                Drawable bg = slctBtns[b].getBackground();
                if (bg != null) {
                    bg.setColorFilter(pdCP);
                    slctBtns[b].setBackground(bg);
                }
            }
            if (b == 3)
                slctBtns[b].setImageDrawable((Drawable) changeColor(c, slctBtns[b].getDrawable(), TCP, null, true));
            else slctBtns[b].setColorFilter(TCP);

        }
        mNavAddPLET.setHintTextColor(Color.argb(123, Color.red(CP), Color.green(CP), Color.blue(CP)));
        mNTSearchClose.setColorFilter(CP);
        mNTSlctClose.setColorFilter(CP);
        isFolder = folderMark(false);
        sdFolder = folderMark(true);
        LinearLayout[] dsLists = {mDSAudio, mDSVideo};
        for (LinearLayout ds : dsLists)
            for (int s = 0; s < ds.getChildCount(); s++) {
                LinearLayout title = (LinearLayout) ((LinearLayout) ds.getChildAt(s)).getChildAt(0);
                ImageView iv = (ImageView) title.getChildAt(0);
                TextView tv = (TextView) title.getChildAt(1);
                if (!dirLtr) iv.setRotation(180f);
                if (changeTCP) {
                    iv.setColorFilter(TCP);
                    tv.setTextColor(TCP);
                }
                fixTVFont(tv, Typeface.BOLD, 15f);
            }
        if (!whitenBGPC) {
            setSpinner(mSAPRSpinner, rvPresets, R.layout.reverb_spn, sp.getInt(rvPreset, 0));
            mSAPRSpnMark.setColorFilter(Color.BLACK);
            setSpinner(mSAEqSpinner, eqModes, R.layout.reverb_spn, sp.getInt(sEQMode, 0));
            mSAEqSpnMark.setColorFilter(Color.BLACK);
        } else {
            setSpinner(mSAPRSpinner, rvPresets, R.layout.reverb_spn_whitened, sp.getInt(rvPreset, 0));
            mSAPRSpnMark.setColorFilter(Color.WHITE);
            setSpinner(mSAEqSpinner, eqModes, R.layout.reverb_spn_whitened, sp.getInt(sEQMode, 0));
            mSAEqSpnMark.setColorFilter(Color.WHITE);
        }
        Spinner[] fixSpn = {mSAEqSpinner, mSAPRSpinner};
        for (Spinner spn : fixSpn) {
            Drawable prSpnBG = spn.getBackground();
            if (prSpnBG != null) {
                prSpnBG.setColorFilter(pdTCP);
                spn.setBackground(prSpnBG);
            }
        }
        for (int l = 0; l < cropLLs.length; l++) {
            ImageView iv = (ImageView) cropLLs[l].getChildAt(0);
            iv.setImageBitmap((Bitmap) changeColor(c, iv.getDrawable(), TCP, null, false));
            TextView tv = (TextView) cropLLs[l].getChildAt(1);
            tv.setTextColor(TCP);
            fixTVFont(tv, Typeface.BOLD, 12f);
            View bg = mSVCrop.getChildAt(l);
            bg.setBackgroundColor(CP);
        }
        mSAPBSReset.setColorFilter(TCP);
        mSAPBPReset.setColorFilter(TCP);
        mSTManagerAdd.setImageBitmap((Bitmap) changeColor(c, mSTManagerAdd.getDrawable(), TCP, null, false));
        mSTManagerClose.setImageBitmap((Bitmap) changeColor(c, mSTManagerAdd.getDrawable(), TCP, null, false));

        // Set Wallpapers
        String[] split = sp.getString("wpBooleans", wpBoolsDef).split("");
        wpBooleans = new boolean[resWallpapers.length + adtWPMax];
        for (int b = 0; b < wpBooleans.length; b++)
            if (b < split.length - 1) wpBooleans[b] = split[b + 1].equals("1");
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int n = 0; n < wpBooleans.length; n++) if (wpBooleans[n]) numbers.add(n);
        if (numbers.size() > 0) wp = numbers.get(new Random().nextInt(numbers.size()));

        // Reload the lists
        if (newIntent) {
            if (openedNav == 1) changeNavList(0, false);
            if (openedNav == 3) changeNavList(2, false);
            if (openedNav == 5) changeNavList(4, false);
            audToFolder = true;
            vidToFolder = true;
            setArrangement();
            incoming = intent.getData();//getIntent() is unacceptable!
            start(true);
        }

        newIntent = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isNavOpen && !selecting) openNavList(false);
        if (vp != null) {
            destroyVP(true);
            playMedia(true, videoList, videoListIndex, videoIndex, vPlayOnResume, false, resumePos);
        }
        applyWP();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (vp != null) {
            try {
                resumePos = vp.getCurrentPosition();
            } catch (Exception ignored) {
                resumePos = 0;
            }
            vPlayOnResume = vPlaying;
            if (vPlaying) {
                vp.pause();
                vPlaying(false);
            }
        }
        reCreation = false;
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
                if (!selecting) {
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
                    } else switch (openedNav) {
                        case 1:
                            changeNavList(0, true);
                            break;
                        case 3:
                            changeNavList(2, true);
                            break;
                        case 5:
                            changeNavList(4, true);
                            break;
                        default:
                            openNavList(false);
                            break;
                    }
                } else selecting(false);
            } else fileInfo(false, null, -1);
        } else if (bmCancellable) mb1Click();
    }

    @Override
    public void onSaveInstanceState(Bundle SIS) {
        SIS.putBoolean("AminState", serviceBound);
        super.onSaveInstanceState(SIS);
    }

    @Override
    public void onRestoreInstanceState(Bundle SIS) {
        super.onRestoreInstanceState(SIS);
        serviceBound = SIS.getBoolean("AminState");
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
            case R.id.mtbSubtitle:
                stManager(true);
                return true;
            case R.id.mtbSettings:
                startActivity(new Intent(c, Settings.class));
                return true;
            case R.id.mtbAboutUs:
                makeBasicMessage(getResources().getString(R.string.tbAboutUs),
                        getResources().getString(R.string.aboutUs), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mb1Click();
                                Intent wi = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.myInsta)));
                                if (wi.resolveActivity(getPackageManager()) != null)
                                    startActivity(wi);
                            }
                        }, true, new String[]{getResources().getString(R.string.ok1),
                                getResources().getString(R.string.visit)});
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        ValueAnimator anUpdate = VA(mMotor, "rotationX", 522, 180f, 0f);
        anUpdate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (vp != null) crop(-2);
                regulateEqSBs();
                applyWP();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == GET_SUBTITLE && intent != null && intent.getData() != null)//or resultCode == RESULT_OK
            try {
                String path = FileUtils.getPath(c, intent.getData());
                if (path != null) {//MIMETYPE: "application/x-subrip"
                    if (path.substring(path.lastIndexOf(".") + 1).equalsIgnoreCase("srt")) {
                        if (getSTPosByPath(path) == -1) addST(path);
                    } else
                        Toast.makeText(c, getResources().getString(R.string.unsupportedST), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(c, getResources().getString(R.string.unknownErrorOccurred), Toast.LENGTH_SHORT).show();
            } catch (Exception ignored) {
                Toast.makeText(c, getResources().getString(R.string.unknownErrorOccurred), Toast.LENGTH_SHORT).show();
            }
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
        if (Amin.mp == null || !playing) return;
        if (!changingTheSeekBar) try {
            if (Amin.mp.getCurrentPosition() > -1 && Amin.mp.getDuration() > -1 &&
                    Amin.mp.getDuration() >= Amin.mp.getCurrentPosition()) {
                mSeekBar.setProgress(Math.round((100f / (float) Amin.mp.getDuration()) *
                        (float) Amin.mp.getCurrentPosition()));
                mDur.setText(fixDur(Amin.mp.getCurrentPosition()) + " / " + fixDur(Amin.mp.getDuration()));
            } else normalisePanel();
        } catch (Exception ignored) {
            normalisePanel();
        }
        reproduction = VA(mMotor, "scaleX", controlPlayerDur, 1.8f, 1f);
        reproduction.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                controlPlayer();
            }
        });
    }

    void controlVPlayer() {
        if (vp == null || !vPlaying) return;
        if (!changingTheSeekBar) try {
            if (vp.getCurrentPosition() > -1 && vp.getDuration() > -1 && vp.getDuration() >= vp.getCurrentPosition()) {
                mSeekBar.setProgress(Math.round((100f / (float) vp.getDuration()) * (float) vp.getCurrentPosition()));
                mDur.setText(fixDur(vp.getCurrentPosition()) + " / " + fixDur(vp.getDuration()));
            } else normalisePanel();
        } catch (Exception ignored) {
            normalisePanel();
        }
        reproduction = VA(mMotor, "scaleX", controlPlayerDur, 1.8f, 1f);
        reproduction.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                controlVPlayer();
            }
        });
    }

    void playing(boolean p) {
        if (p == playing) return;
        playing = p;
        if (playing) {
            mPlayPauseBT.setImageResource(R.drawable.pause_3);
            controlPlayer();
        } else {
            mPlayPauseBT.setImageResource(R.drawable.play_3);
            if (Amin.mp != null) try {
                mDur.setText(fixDur(Amin.mp.getCurrentPosition()) + " / " + fixDur(Amin.mp.getDuration()));
            } catch (Exception ignored) {
                normalisePanel();
            }
            else normalisePanel();
        }
    }

    void vPlaying(boolean p) {
        if (p == vPlaying) return;
        vPlaying = p;
        if (vPlaying) {
            mPlayPauseBT.setImageResource(R.drawable.pause_3);
            controlVPlayer();
        } else {
            mPlayPauseBT.setImageResource(R.drawable.play_3);
            if (vp != null) try {
                mDur.setText(fixDur(vp.getCurrentPosition()) + " / " + fixDur(vp.getDuration()));
            } catch (Exception ignored) {
                normalisePanel();
            }
            else normalisePanel();
        }
    }

    void normalisePanel() {
        mSeekBar.setProgress(0);
        mDur.setText(R.string.mNormalDur);
    }

    void playAudio(ArrayList<Audio> list, int index, boolean plOn) {
        Amin.playlistOn = plOn;
        StorageUtil storage = new StorageUtil(c);
        storage.storeAudio(list);
        storage.storeAudioIndex(index);

        mSeekBar.setEnabled(true);
        if (!Amin.exists || !serviceBound) {
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
        TextView Title = (TextView) ((LinearLayout) ((ConstraintLayout) ll.getChildAt(0)).getChildAt(0))
                .getChildAt(1), body = (TextView) ((ConstraintLayout) ll.getChildAt(1)).getChildAt(0);
        Title.setText(title);
        fixTVFont(Title, Typeface.BOLD, (Title.getTextSize() / dm.density));
        body.setText(message);
        fixTVFont(body, Typeface.NORMAL, (body.getTextSize() / dm.density));
        ImageView ivTitle = (ImageView) ((LinearLayout) ((ConstraintLayout) ll.getChildAt(0)).getChildAt(0))
                .getChildAt(0);
        ivTitle.setColorFilter(ContextCompat.getColor(c, R.color.bm1TitleIV));
        int buttonVGId = View.generateViewId();
        ConstraintLayout buttonVG = (ConstraintLayout) ll.getChildAt(2);
        buttonVG.setId(buttonVGId);
        if (changeCP) {
            m.setBackgroundColor(CP);
            ivTitle.setColorFilter(CP);
            if (TCP != defTCP) body.setTextColor(CP);
        }
        if (changeTCP) {
            Title.setTextColor(TCP);
            body.setBackgroundColor(TCP);
            buttonVG.setBackgroundColor(TCP);
            Drawable titleIcon = ivTitle.getBackground();
            if (titleIcon != null) {
                titleIcon.setColorFilter(new PorterDuffColorFilter(TCP, PorterDuff.Mode.SRC_IN));
                ivTitle.setBackground(titleIcon);
            }
        }

        TextView button = new TextView(new ContextThemeWrapper(c, R.style.BMButton1), null, 0);
        ConstraintLayout.LayoutParams buttonLP = new ConstraintLayout.LayoutParams(wc, wc);
        buttonLP.topToTop = buttonVGId;
        if (changeCP) button.setBackgroundColor(CP);
        if (changeTCP) button.setTextColor(TCP);
        fixTVFont(button, Typeface.BOLD, (button.getTextSize() / dm.density));
        final ConstraintLayout M = m;
        if (quesClick == null) {
            buttonLP.endToEnd = buttonVGId;
            buttonLP.startToStart = buttonVGId;
            button.setText(R.string.ok1);
            if (buttonTexts != null && buttonTexts[0] != null) button.setText(buttonTexts[0]);
        } else {
            int buttonYesId = View.generateViewId();
            buttonLP.endToStart = buttonYesId;
            buttonLP.setMarginEnd(buttonVG.getPaddingEnd());
            button.setText(R.string.no1);
            if (buttonTexts != null && buttonTexts[0] != null) button.setText(buttonTexts[0]);

            TextView buttonYes = new TextView(new ContextThemeWrapper(c, R.style.BMButton1), null, 0);
            ConstraintLayout.LayoutParams buttonYesLP = new ConstraintLayout.LayoutParams(wc, wc);
            buttonYesLP.endToEnd = buttonVGId;
            buttonYesLP.topToTop = buttonVGId;
            buttonYes.setText(R.string.yes1);
            if (changeCP) buttonYes.setBackgroundColor(CP);
            buttonYes.setTextColor(ContextCompat.getColor(c, R.color.textColorPrimary));
            if (buttonTexts != null) {
                if (buttonTexts[1] != null) {
                    buttonYes.setText(buttonTexts[1]);
                    buttonYes.setBackgroundColor(ContextCompat.getColor(c, R.color.bm1BtnYesSpecial));
                } else if (changeTCP) buttonYes.setTextColor(TCP);
            } else if (changeTCP) buttonYes.setTextColor(TCP);//DON'T SIMPLIFY THIS!
            buttonYes.setId(buttonYesId);
            fixTVFont(buttonYes, Typeface.BOLD, (buttonYes.getTextSize() / dm.density));
            buttonVG.addView(buttonYes, buttonYesLP);
            buttonYes.setOnClickListener(quesClick);
        }
        buttonVG.addView(button, buttonLP);

        final int dur = 111;
        OA(mBasicMessages, "alpha", 1f, dur);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBM1(M, dur);
            }
        });
        if (cancellabe) mBasicMessages.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                cancelBM1(M, dur);
                return false;
            }
        });
        else mBasicMessages.setOnClickListener(doNothing);
        bmCancellable = cancellabe;
        m.setOnClickListener(doNothing);
    }

    void cancelBM1(final View M, int dur) {
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

    void mb1Click() {
        mBasicMessagesLL.removeAllViews();
        mBasicMessages.setVisibility(View.GONE);
        showingBM = false;
    }

    void openNavList(boolean open) {
        isNavOpen = open;
        float fListDest = (float) mNavList.getWidth() + 11f;
        if (dirLtr) fListDest = -fListDest;
        if (open) {
            mNavList.setTranslationX(fListDest);
            mNavigation.setVisibility(View.VISIBLE);
            ObjectAnimator an = OA(mNavList, "translationX", 0f, navDur);
            an.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mBadges.setVisibility(View.VISIBLE);
                }
            });
            scrollToSelected(openedNav);
        } else {
            mBadges.setVisibility(View.GONE);
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
        if (selecting || n < 0 || n >= navLists.length) return;
        openedNav = n;
        for (int l = 0; l < navLists.length; l++)
            if (navLists[l].getVisibility() == View.VISIBLE)
                navLists[l].setVisibility(View.GONE);
        navLists[n].setVisibility(View.VISIBLE);

        for (ConstraintLayout badge : badges) {
            badge.getChildAt(badgeBG).setVisibility(View.INVISIBLE);
            badge.getChildAt(badgeBT).setScaleX(1f);
            badge.getChildAt(badgeBT).setScaleY(1f);
            badge.setClickable(true);
        }
        int nn = n;
        if (n > 0) nn -= 1;
        if (n > 2) nn -= 1;
        if (n > 4) nn -= 1;
        badges[nn].getChildAt(badgeBG).setVisibility(View.VISIBLE);
        badges[nn].setClickable(false);
        badges[nn].getChildAt(badgeBT).setScaleX(badgeScale);
        badges[nn].getChildAt(badgeBT).setScaleY(badgeScale);

        if (scroll) scrollToSelected(n);
        if (n == 0 || n == 1) audToFolder = n == 0;
        if (n == 2 || n == 3) vidToFolder = n == 2;
        if (n == 4 || n == 5) pltToFolder = n == 4;
        boolean main = n == 0 || n == 2 || n == 4 || n == 6;
        int ntbPad = 7;
        if (main) {
            if (n == 0) mNavTitleTV.setText(R.string.mNavTAudios);
            if (n == 2) mNavTitleTV.setText(R.string.mNavTVideos);
            if (n == 4) mNavTitleTV.setText(R.string.mNavTPlaylists);
            if (n == 6) mNavTitleTV.setText(R.string.mNavTSettings);
            mNavTitleBack.setImageResource(R.drawable.play_3_ntf);
            ntbPad = 10;
        } else {
            if (n == 1 && aFolders != null && aFolders.size() > openedAFolder)
                mNavTitleTV.setText(aFolders.get(openedAFolder).name);
            if (n == 3 && vFolders != null && vFolders.size() > openedVFolder)
                mNavTitleTV.setText(vFolders.get(openedVFolder).name);
            if (n == 5 && playlists != null && playlists.size() > openedPL)
                mNavTitleTV.setText(playlists.get(openedPL).name);
            mNavTitleBack.setImageResource(R.drawable.back_1_blue);
        }
        ntbPad = dp(ntbPad);
        mNavTitleBack.setPadding(ntbPad, ntbPad, ntbPad, ntbPad);
        mNavTitle.setClickable(!main);
        mNavTitleLL.setClickable(main);

        turnSearch(false);
        canSearch(n != 6);
        int pd = dp(40);
        if (n != 6) pd = 0;
        mNavTitleLL.setPaddingRelative(mNavTitleLL.getPaddingStart(), mNavTitleLL.getPaddingTop(), pd,
                mNavTitleLL.getPaddingBottom());

        if (n == 4) mNavAddPL.setVisibility(View.VISIBLE);
        else mNavAddPL.setVisibility(View.GONE);

        if (n == 6) {
            int ds = 0;
            if (video) ds = 1;
            openDS(ds);
        }
    }

    void exit() {
        moveTaskToBack(true);//REQUIRED!
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    void start(boolean go) {
        if (go) {
            folsLoaded = false;
            mLoading(true);
            new MediaLoader().start();
        } else {
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
        btnShine(mSHShine);
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
        btnShine(mRPShine);
    }

    void highlightItem(int list, int item) {
        ArrayList<Integer>[] idLists = new ArrayList[]{aflClItemIds, audClItemIds, vflClItemIds, vidClItemIds, pllClItemIds,
                pliClItemIds};
        ArrayList<Integer> idList = idLists[list];
        if (idList == null) return;
        for (int i = 0; i < idList.size(); i++) {
            ConstraintLayout view = findViewById(idList.get(i));
            if (view != null) highlight(false, view);
        }
        if (item != -1 && idList.size() > item) {
            ConstraintLayout view = findViewById(idList.get(item));
            if (view != null) highlight(true, view);
        }

    }

    void highlight(boolean Do, ConstraintLayout cl) {
        if (Do) {
            if (!changeCP) cl.setBackgroundResource(R.drawable.square_1_blue_1_alpha);
            else
                cl.setBackgroundColor(Color.argb(highlightCPAlpha, Color.red(CP), Color.green(CP), Color.blue(CP)));
        } else cl.setBackgroundResource(R.drawable.square_1_tp_to_white_alpha_xml);
    }

    void updateDescription(boolean New) {
        if (!video) {
            if (New) try {
                Audio aud = (Audio) aFolders.get(audioListIndex).files.get(audioIndex);
                Bitmap bmp = Amin.getAlbumArt(c, aud.getAlbumId());
                mAlbumArt.setImageBitmap(bmp);
                mToolbar.setSubtitle(aud.getName());
                for (int g = 0; g < mToolbar.getChildCount(); g++) {
                    View title = mToolbar.getChildAt(g);
                    if (title.getClass().getName().equalsIgnoreCase("androidx.appcompat.widget.AppCompatTextView")
                            && ((TextView) title).getText().toString().equals(aud.getName()))
                        ((TextView) title).setTypeface(Typeface.create(fonts[font], Typeface.NORMAL));
                }
            } catch (Exception ignored) {
            }
            else {
                mAlbumArt.setImageBitmap(null);
                mToolbar.setSubtitle("");
            }
        } else {
            if (New) try {
                mToolbar.setSubtitle(activeVideo.name);
            } catch (Exception ignored) {
            }
            else mToolbar.setSubtitle("");
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

    void scrollToSelected(int list) {
        if (list < 0 || list > 5) return;
        int listIndex, Index, index, openedFol;
        ArrayList<Integer>[] ids = new ArrayList[]{aflClItemIds, audClItemIds, vflClItemIds, vidClItemIds, pllClItemIds, pliClItemIds};
        MediaPlayer mmm;
        if (list == 0 || list == 1) {
            listIndex = audioListIndex;
            Index = audioIndex;
            mmm = Amin.mp;
            openedFol = openedAFolder;
        } else if (list == 2 || list == 3) {
            listIndex = videoListIndex;
            Index = videoIndex;
            mmm = vp;
            openedFol = openedVFolder;
        } else {
            listIndex = playingPL;
            Index = playingPLItem;
            mmm = Amin.mp;
            openedFol = openedPL;
        }

        if (mmm == null || ((openedNav == 1 || openedNav == 3 || openedNav == 5) && listIndex != openedFol))
            return;
        if (list == 0 || list == 2 || list == 4) index = listIndex;
        else index = Index;
        if (index == -1 || ids[list] == null) return;

        int distance = 0;
        for (int i = 0; i < index; i++)
            if (findViewById(ids[list].get(i)) != null)
                distance += findViewById(ids[list].get(i)).getMeasuredHeight();
        if (distance > navLists[openedNav].getHeight()) navLists[openedNav].scrollTo(0, distance);
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
        if (!Amin.exists || Amin.mp == null) return;
        PendingIntent pi = Amin.playbackAction(c, act);
        if (pi != null) try {
            pi.send();
        } catch (Exception ignored) {
        }
    }

    public static Object changeColor(Context c, Object Icon, int colour, int[] gdSizes, boolean toDrw) {
        try {
            Bitmap bmIcon = toBmp(c, Icon, gdSizes);
            if (bmIcon != null) {
                Paint paint = new Paint();
                paint.setColorFilter(new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN));
                Canvas canvas = new Canvas(bmIcon);
                canvas.drawBitmap(bmIcon, 0, 0, paint);
                if (toDrw) return new BitmapDrawable(c.getResources(), bmIcon);
                else return bmIcon;
            } else return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Bitmap toBmp(Context c, Object Icon, int[] gdSizes) {
        Bitmap bmIcon;
        try {
            if (Icon instanceof Bitmap) bmIcon = (Bitmap) Icon;
            else if (Icon instanceof LayerDrawable) {
                LayerDrawable LD = ((LayerDrawable) Icon);
                bmIcon = Bitmap.createBitmap(LD.getIntrinsicWidth(), LD.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                LD.setBounds(0, 0, LD.getIntrinsicWidth(), LD.getIntrinsicHeight());
                LD.draw(new Canvas(bmIcon));
            } else if (Icon instanceof GradientDrawable) {
                GradientDrawable GD = ((GradientDrawable) Icon);
                int w = GD.getIntrinsicWidth(), h = GD.getIntrinsicHeight();
                if (gdSizes != null) {
                    if (gdSizes.length > 0) w = gdSizes[0];
                    if (gdSizes.length > 1) h = gdSizes[1];
                }
                bmIcon = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                GD.setBounds(0, 0, w, h);
                GD.draw(new Canvas(bmIcon));
            } else if (Icon instanceof Drawable)
                bmIcon = ((BitmapDrawable) Icon).getBitmap();
            else if (Icon instanceof Integer)
                bmIcon = BitmapFactory.decodeResource(c.getResources(), (int) Icon).copy(Bitmap.Config.ARGB_8888, true);
            else return null;
            if (!bmIcon.isMutable()) bmIcon = bmIcon.copy(Bitmap.Config.ARGB_8888, true);
            return bmIcon;
        } catch (Exception ignored) {
            return null;
        }
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
        effect.setImageResource(R.drawable.solid_white_circle);
        effect.setColorFilter(TCP);
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
        destroyAmin();
        playing = false;
        vPlaying = false;
        AminHandler = null;
        changeHandler = null;
        destroyMe = null;
        destroyVP(true);
        keepScreen(false);
        cancelLoadings(true, true, true, true);

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

    void switchMedia(final boolean audio) {
        mSeekBar.setEnabled(false);
        updateDescription(false);
        video = !audio;
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("video", video);
        editor.apply();

        if (audio) {
            if (vPlaying) vPlaying(false);
            shuffleHistory = null;
            destroyVP(true);
            mVVFrame.setVisibility(View.INVISIBLE);
            highlightItem(2, -1);
            highlightItem(3, -1);

            if (hiddenPanel) hidePanel(false);
            mAlbumArt.setVisibility(View.VISIBLE);
        } else {
            if (Amin.exists) destroyAmin();
            mAlbumArt.setVisibility(View.GONE);
            highlightItem(0, -1);
            highlightItem(1, -1);

            mVVFrame.setVisibility(View.VISIBLE);
        }
        normalisePanel();
    }

    void playVideo(boolean doPlay, final boolean autoHide, final int autoSeekTo) {
        boolean autoVPlay = vPlaying;
        if (doPlay) autoVPlay = true;
        if (vPlaying) vPlaying(false);
        destroyVP(false);

        activeVideo = (Video) vFolders.get(videoListIndex).files.get(videoIndex);
        highlightItem(2, videoListIndex);
        if (videoListIndex == openedVFolder) highlightItem(3, videoIndex);
        updateDescription(true);
        mSeekBar.setEnabled(true);

        /*SharedPreferences.Editor editor = sp.edit();
        editor.putString("lastVPlayed", ((Video) vFolders.get(videoListIndex).files.get(videoIndex)).data);
        editor.apply();*/

        videoMode(true);
        vp = new MediaPlayer();//mVV.setZOrderOnTop(false);
        vp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                switch (repeat) {
                    case 0:
                        vPlaying(false);
                        vp.seekTo(0);
                        break;
                    case 1:
                        skipVideo(true);
                        break;
                    case 2:
                        vp.seekTo(0);
                        vp.start();
                        break;//FORGETTING "break;" IS A DISASTER!!!
                }
            }
        });
        vp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
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
                        destroyVP(true);
                        return true;
                    case -38://IT MEANS YOU'VE DONE SOMETHING BEFORE THE PREPARATION OF MEDIAPLAYER.
                        //Toast.makeText(c, "-38", Toast.LENGTH_SHORT).show();
                        return true;
                    case 1:
                        if (extra == -2147483648)
                            Toast.makeText(c, R.string.mpUnreadable, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(c, getResources().getString(msg) + desc, Toast.LENGTH_SHORT).show();
                        return true;
                    default://MediaPlayer.MEDIA_ERROR_UNKNOWN
                        Toast.makeText(c, getResources().getString(msg) + desc, Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
        vpTrackLagging = true;
        vp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
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
                        if (vpTrackLagging) {
                            makeBasicMessage(getResources().getString(R.string.audioPlayer),
                                    getResources().getString(R.string.vpTrackLagging),
                                    null, false, null);
                            vpTrackLagging = false;
                        }
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
        final boolean AutoVPlay = autoVPlay;
        vp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // If it performs this action before the mp is prepared,
                // it crashes; because the surface has been released then!
                prepared = true;
                try {
                    vp.setDisplay(mSH);
                } catch (IllegalStateException ignored) {
                    destroyVP(true);
                    return;
                }
                if (AutoVPlay) {//mVV.requestFocus();
                    if (autoSeekTo > 0 && autoSeekTo < vp.getDuration()) try {
                        vp.seekTo(autoSeekTo);
                    } catch (IllegalStateException ignored) {
                    }
                    try {
                        vp.start();
                        vPlaying(true);
                    } catch (Exception ignored) {
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PlaybackParams pp = new PlaybackParams();
                    pp.setSpeed(sp.getFloat(Mahdi.pbSpeed, 1f));
                    pp.setPitch(sp.getFloat(Mahdi.pbPitch, 1f));
                    pp.setAudioFallbackMode(PlaybackParams.AUDIO_FALLBACK_MODE_DEFAULT);
                    try {
                        vp.setPlaybackParams(pp);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                //Toast.makeText(c, EffectFactory.isEffectSupported(EffectFactory.EFFECT_BRIGHTNESS) + "", Toast.LENGTH_LONG).show();
                /*if (EffectFactory.isEffectSupported(EffectFactory.EFFECT_BRIGHTNESS)) {
                    EffectContext ec = EffectContext.createWithCurrentGlContext();
                    EffectFactory ef = ec.getFactory();
                    Effect e = ef.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
                    //e.apply(R.id.mVV, mVV.getWidth(), mVV.getHeight(), );
                }*/
                if (autoHide) hidePanel(true);
                keepScreen(true);
                crop(-2);
            }
        });
        vp.reset();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            vp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        else try {
            vp.setAudioAttributes(new AudioAttributes.Builder()
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
        } catch (Exception ignored) {
        }
        setVVolume(-1);

        // AudioEffects
        initEqualizer();
        initVirtualizer();
        initBassBoost();
        initPresetReverb();
        initLoudnessEnhancer();

        try {// Final Setup
            vp.setDataSource(activeVideo.data);
            vp.prepareAsync();
        } catch (Exception e) {
            String msg = "\n\n";
            if (e.getMessage() != null) msg = msg + e.getMessage();
            else msg = msg + e.getClass().getName();
            Toast.makeText(c, getResources().getString(R.string.cannotOpenFile) + msg, Toast.LENGTH_SHORT).show();
        }
    }

    void initEqualizer() {
        try {
            equalizer = new Equalizer(0, vp.getAudioSessionId());//Must be done before preparation.
            equalizer.setEnabled(true);//ABSOLUTELY NECESSARY!
            vp.attachAuxEffect(equalizer.getId());
            vp.setAuxEffectSendLevel(1.0f);
            /*for (int b = 0; b < equalizer.getNumberOfBands(); b++)
                equalizer.setBandLevel((short) b, (short) sp.getInt(eqBand + b, 0));*/
            setEQValsForVP();
        } catch (Exception ignored) {
        }
    }

    void initVirtualizer() {
        try {
            virtualizer = new Virtualizer(0, vp.getAudioSessionId());
            virtualizer.setEnabled(true);
            vp.attachAuxEffect(virtualizer.getId());
            virtualizer.setStrength((short) sp.getInt(vrStrength, 0));
        } catch (Exception ignored) {
        }
    }

    void initBassBoost() {
        try {
            bassBoost = new BassBoost(0, vp.getAudioSessionId());
            bassBoost.setEnabled(true);
            vp.attachAuxEffect(bassBoost.getId());
            bassBoost.setStrength((short) sp.getInt(bbStrength, 0));
        } catch (Exception ignored) {
        }
    }

    void initPresetReverb() {
        try {
            presetReverb = new PresetReverb(0, vp.getAudioSessionId());
            presetReverb.setEnabled(true);
            vp.attachAuxEffect(presetReverb.getId());
            presetReverb.setPreset((short) sp.getInt(rvPreset, 0));
        } catch (Exception ignored) {
        }
    }

    void initLoudnessEnhancer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) try {
            loudnessEnhancer = new LoudnessEnhancer(vp.getAudioSessionId());
            loudnessEnhancer.setEnabled(true);
            vp.attachAuxEffect(loudnessEnhancer.getId());
            loudnessEnhancer.setTargetGain(sp.getInt(Mahdi.leGain, 0));
        } catch (Exception ignored) {
        }
    }

    void skipVideo(boolean next) {
        if (vp == null) return;
        boolean plays = true;
        if (shuffle) chooseRandom(!next);
        else {
            if (next) {
                if (videoIndex < vFolders.get(videoListIndex).files.size() - 1) videoIndex += 1;
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
        if (plays) playVideo(false, false, 0);
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
                        regulateSVs();
                    }
                });
            }
            if (mToolbarAB.isShowing()) mToolbarAB.hide();
            mTBBorderEnd.setVisibility(View.INVISIBLE);
            mNavIcon.setVisibility(View.GONE);
            mNavIconBorder.setVisibility(View.INVISIBLE);
        } else {
            if (mPlayerBody.getTranslationY() != 0f) {
                mPlayerBody.setVisibility(View.VISIBLE);
                VA(mPlayerBody, "translationY", hidePanelDur, dist, 0f);
            }
            if (!mToolbarAB.isShowing()) mToolbarAB.show();
            mTBBorderEnd.setVisibility(View.VISIBLE);
            mNavIcon.setVisibility(View.VISIBLE);
            mNavIconBorder.setVisibility(View.VISIBLE);
            regulateSVs();
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
            if (vp.getCurrentPosition() + videoEachMove <= vp.getDuration())
                touchXMove(vp.getCurrentPosition() + videoEachMove);
            else if (Math.abs(vp.getDuration() - vp.getCurrentPosition()) <= videoEachMove)
                touchXMove(vp.getDuration());
            touchXMoved[1] = 0f;
            touchXMovedEnough = true;
        }
        if (touchXMoved[0] <= -touchMSensibility) {
            if (vp.getCurrentPosition() >= videoEachMove)
                touchXMove(vp.getCurrentPosition() - videoEachMove);
            else if (vp.getCurrentPosition() <= videoEachMove) touchXMove(0);
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
        if (vp != null) vp.setVolume(vVolume, vVolume);
        if (Amin.mp != null) Amin.mp.setVolume(vVolume, vVolume);
    }

    void setVAlpha(float alpha) {
        mBody.setAlpha(alpha);
        mTouchSensor.setAlpha(1f - alpha);
    }

    void touchXMove(int pos) {
        if (vp != null) {
            vp.seekTo(pos);
            if (!vPlaying) try {
                if (!changingTheSeekBar) {
                    mSeekBar.setProgress(Math.round((100f / (float) vp.getDuration()) * (float) vp.getCurrentPosition()));
                    mDur.setText(fixDur(vp.getCurrentPosition()) + " / " + fixDur(vp.getDuration()));
                }
            } catch (Exception ignored) {
            }
        }
    }

    void destroyVP(boolean removeVideoMode) {
        prepared = false;
        if (vp != null) {
            if (vPlaying) try {
                vp.stop();
            } catch (Exception ignored) {
            }
            try {
                vp.release();
            } catch (Exception ignored) {
            }
            vp = null;
            equalizer = null;
            virtualizer = null;
            bassBoost = null;
            presetReverb = null;
        }
        activeVideo = null;
        mVV.clearAnimation();
        keepScreen(false);
        if (removeVideoMode) videoMode(false);
    }

    void videoMode(boolean yes) {
        videoMode = yes;
        if (yes) {
            mShwBG.setVisibility(View.INVISIBLE);
            mPanelBg.setVisibility(View.VISIBLE);
        } else {
            mShwBG.setVisibility(View.VISIBLE);
            mPanelBg.setVisibility(View.INVISIBLE);
            stManager(false);
        }
        mToolbar.getMenu().getItem(mtbSubtitle).setVisible(yes);
    }

    void keepScreen(boolean on) {
        if (on) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        keepScreen = on;
    }

    public static Object resizeByRatio(Context c, Object Icon, float ratio, boolean toDrawable) {
        Bitmap icon = toBmp(c, Icon, null);
        if (icon != null) {
            int WH = (int) (icon.getHeight() * ratio);
            Bitmap overlay = Bitmap.createBitmap(WH, WH, icon.getConfig());
            Canvas canvas = new Canvas(overlay);
            Matrix amin = new Matrix();
            amin.setScale(ratio, ratio);
            canvas.drawBitmap(icon, amin, null);
            if (toDrawable) return new BitmapDrawable(c.getResources(), overlay);
            else return overlay;
        } else return null;
    }

    void crop(int New) {
        if (New > -1 && New < 4) {
            crop = New;
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("crop", crop);
            editor.apply();
            for (int l = 0; l < 4; l++) mSVCrop.getChildAt(l).setVisibility(View.INVISIBLE);
            mSVCrop.getChildAt(crop).setVisibility(View.VISIBLE);
        }

        if (vp == null) return;
        int W = match, H = W;
        float videoRatio = (float) vp.getVideoWidth() / (float) vp.getVideoHeight(),
                screenRatio = (float) mVVFrame.getWidth() / (float) mVVFrame.getHeight(),
                converseVRatio = (float) vp.getVideoHeight() / (float) vp.getVideoWidth();
        switch (crop) {
            case 0:// 100%
                W = vp.getVideoWidth();
                H = vp.getVideoHeight();
                break;
            case 1:// FIT TO SCREEN
                if (videoRatio > 1) {
                    if (screenRatio > 1) {//= in = (COMPLICATED)
                        if (videoRatio > screenRatio) H = (int) (mVVFrame.getWidth() / videoRatio);
                        else if (videoRatio < screenRatio)
                            W = (int) (mVVFrame.getHeight() * videoRatio);
                    } else if (screenRatio < 1)
                        H = (int) (mVVFrame.getWidth() / videoRatio);//= in I
                    else H = (int) (mVVFrame.getWidth() / videoRatio);//= in O
                } else if (videoRatio < 1) {
                    if (screenRatio > 1) W = (int) (mVVFrame.getHeight() * videoRatio);//I in =
                    else if (screenRatio < 1) {//I in I (COMPLICATED)
                        if (videoRatio > screenRatio) H = (int) (mVVFrame.getWidth() * videoRatio);
                        else if (videoRatio < screenRatio)
                            W = (int) (mVVFrame.getHeight() * videoRatio);
                    } else W = (int) (mVVFrame.getHeight() * videoRatio);//I in O
                } else {
                    if (screenRatio > 1) W = (int) (mVVFrame.getHeight() * videoRatio);//O in =
                    else if (screenRatio < 1) H = (int) (mVVFrame.getWidth() / videoRatio);//O in I
                }
                break;
            case 2:// STRETCH
                break;
            case 3:// CROP
                if (videoRatio > 1) {
                    if (screenRatio > 1) {//= in = (COMPLICATED)
                        if (videoRatio > screenRatio) W = (int) (videoRatio * mVVFrame.getHeight());
                        else if (videoRatio < screenRatio)
                            H = (int) (videoRatio * mVVFrame.getWidth());
                    } else if (screenRatio < 1)
                        W = (int) (videoRatio * mVVFrame.getHeight());//= in I
                    else W = (int) (videoRatio * mVVFrame.getHeight());//= in O
                } else if (videoRatio < 1) {
                    if (screenRatio > 1) H = (int) (mVVFrame.getWidth() * converseVRatio);//I in =
                    else if (screenRatio < 1) {//I in I (COMPLICATED)
                        if (videoRatio > screenRatio)
                            W = (int) (mVVFrame.getHeight() * converseVRatio);
                        else if (videoRatio < screenRatio)
                            H = (int) (mVVFrame.getWidth() * converseVRatio);
                    } else H = (int) (mVVFrame.getWidth() * converseVRatio);//I in O
                } else {
                    if (screenRatio > 1) H = mVVFrame.getWidth();//O in =
                    else if (screenRatio < 1) W = mVVFrame.getHeight();//O in I
                }
                break;
        }
        ConstraintLayout.LayoutParams vLP = (ConstraintLayout.LayoutParams) mVV.getLayoutParams();
        vLP.width = W;
        vLP.height = H;
        mVV.setLayoutParams(vLP);
    }

    void setupFIcons() {
        settingFIcons = draughtFIcons.size() > 0;
        if (settingFIcons) {
            ImageView iv = (ImageView) findViewById(draughtFIcons.get(0).id);
            if (iv != null) iv.setImageBitmap(draughtFIcons.get(0).bmp);
            draughtFIcons.remove(0);
            ValueAnimator setupImgRepr = VA(mMotorIcon, "scaleX", setupImgDur, 2f, 1f);
            setupImgRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupFIcons();
                }
            });
        }
    }

    void setupAIcons() {
        settingAIcons = draughtAIcons.size() > 0;
        if (settingAIcons) {
            ImageView iv = (ImageView) findViewById(draughtAIcons.get(0).id);
            if (iv != null) {
                iv.setImageBitmap(draughtAIcons.get(0).bmp);
                if (!draughtAIcons.get(0).albumArt)
                    iv.setPaddingRelative(dp(8), dp(11), dp(8), dp(18));
            }
            draughtAIcons.remove(0);
            ValueAnimator setupImgRepr = VA(mMotorIcon, "translationX", setupImgDur, -10f, 0f);
            setupImgRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupAIcons();
                }
            });
        }
    }

    void setupVIcons() {
        settingVIcons = draughtVIcons.size() > 0;
        if (settingVIcons) {
            ImageView iv = (ImageView) findViewById(draughtVIcons.get(0).id);
            if (iv != null) {
                iv.setImageBitmap(draughtVIcons.get(0).bmp);
                if (!draughtVIcons.get(0).albumArt)
                    iv.setPaddingRelative(dp(8), dp(11), dp(8), dp(18));
            }
            draughtVIcons.remove(0);
            ValueAnimator setupImgRepr = VA(mMotorIcon, "translationY", setupImgDur, -10f, 0f);
            setupImgRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupVIcons();
                }
            });
        }
    }

    void setupAFolders() {
        if (draughtAFolders == null) return;
        settingAFolders = draughtAFolders.size() > 0;
        if (settingAFolders) {
            mAFolders.addView(draughtAFolders.get(0));
            draughtAFolders.remove(0);
            ValueAnimator setupItemRepr = VA(mMotorItem, "scaleX", setupItemDur, 0f, 1f);
            setupItemRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupAFolders();
                }
            });
        } else {
            if (!aflCancelled) {
                new ImageLoader(aflIconIds, 0, -1).start();
                if (Amin.mp != null && audioListIndex != -1) highlightItem(0, audioListIndex);
                if (openedNav == 0) scrollToSelected(0);
            } else aflCancelled = false;
            isLoadingCompleted();
        }
    }

    void setupAudios() {
        if (draughtAudios == null) return;
        settingAudios = draughtAudios.size() > 0;
        if (settingAudios) {
            mAudios.addView(draughtAudios.get(0));
            draughtAudios.remove(0);
            ValueAnimator setupItemRepr = VA(mMotorItem, "translationX", setupItemDur, -10f, 0f);
            setupItemRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupAudios();
                }
            });
        } else {
            if (!audCancelled) {
                new ImageLoader(audIconIds, 1, openedAFolder).start();
                if (Amin.mp != null && audioIndex != -1 && openedAFolder == audioListIndex)
                    highlightItem(1, audioIndex);
                if (openedNav == 1) scrollToSelected(1);
            } else audCancelled = false;
            isLoadingCompleted();
        }
    }

    void setupVFolders() {
        if (draughtVFolders == null) return;
        settingVFolders = draughtVFolders.size() > 0;
        if (settingVFolders) {
            mVFolders.addView(draughtVFolders.get(0));
            draughtVFolders.remove(0);
            ValueAnimator setupItemRepr = VA(mMotorItem, "scaleY", setupItemDur, 0f, 1f);
            setupItemRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupVFolders();
                }
            });
        } else {
            if (!vflCancelled) {
                new ImageLoader(vflIconIds, 2, -1).start();
                if (vp != null && videoListIndex != -1) highlightItem(2, videoListIndex);
                if (openedNav == 2) scrollToSelected(2);
            } else vflCancelled = false;
            isLoadingCompleted();
        }
    }

    void setupVideos() {
        if (draughtVideos == null) return;
        settingVideos = draughtVideos.size() > 0;
        if (settingVideos) {
            mVideos.addView(draughtVideos.get(0));
            draughtVideos.remove(0);

            ValueAnimator setupItemRepr = VA(mMotorItem, "translationY", setupItemDur, -10f, 0f);
            setupItemRepr.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator a) {
                    setupVideos();
                }
            });
        } else {
            if (!vidCancelled) {
                new ImageLoader(vidIconIds, 3, openedVFolder).start();
                if (vp != null && videoIndex != -1 && openedVFolder == videoListIndex)
                    highlightItem(3, videoIndex);
                if (openedNav == 3) scrollToSelected(3);
            } else vidCancelled = false;
            isLoadingCompleted();
        }
    }

    void isLoadingCompleted() {
        if (!settingAFolders && !settingAudios && !settingVFolders && !settingVideos)
            mNavLoading(false);
    }

    void fileInfo(boolean show, final Object media, final int type) {
        fileInfoOpened = show;
        if (!show) {
            if (fiCanSave()) {
                fileInfoOpened = true;
                int tit = R.string.musicInfoTitle;
                if (openedNav == 4) tit = R.string.plInfoTitle;
                makeBasicMessage(getResources().getString(tit),
                        getResources().getString(R.string.discardChanges),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mb1Click();
                                fiClose();
                                fileInfoOpened = false;
                            }
                        }, true, null);
            } else fiClose();
            return;
        }
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
        fiBody.setOnClickListener(doNothing);
        fiClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileInfo(false, null, -1);
            }
        });
        if (changeCP) {
            fiTitle.setBackgroundColor(CP);
            if (TCP != defTCP) {
                fiName.setTextColor(CP);
                fiNameEdit.setTextColor(CP);
            }
        }
        if (changeTCP) {
            fiBody.setBackgroundColor(TCP);
            fiClose.setColorFilter(TCP);
            fiTIcon.setColorFilter(TCP);
            fiTText.setTextColor(TCP);
        }
        fixTVFont(fiTText, Typeface.BOLD, fiTText.getTextSize() / dm.density);
        fixTVFont(fiName, Typeface.NORMAL, fiName.getTextSize() / dm.density);
        fixTVFont(fiNameEdit, Typeface.NORMAL, fiNameEdit.getTextSize() / dm.density);

        String[] attrs = fileInfoList, values = new String[attrs.length];
        if (type == 2) attrs = plInfoList;
        int nPad = (int) (mToolbar.getHeight() * 0.4);
        File file = null;
        Calendar LM = Calendar.getInstance();
        final boolean persian = Locale.getDefault().getLanguage().equals("fa");
        switch (type) {
            case 0:
                Audio aud = (Audio) media;
                file = new File(aud.getData());
                Bitmap aa = Amin.getAlbumArt(c, aud.getAlbumId());
                if (aa != null) fiAlbumArt.setImageBitmap(aa);
                else {
                    fiAlbumArt.setImageResource(R.drawable.sound_2_blue);
                    fiAlbumArt.setPadding(nPad, nPad, nPad, nPad);
                    if (changeCP) fiAlbumArt.setColorFilter(CP);
                }
                fiName.setText(aud.getName().substring(0, aud.getName().lastIndexOf(".")));
                values[0] = aud.getMime();
                values[1] = aud.getData();
                values[2] = fixFSize(c, new File(aud.getData()).length());
                values[3] = aud.getTitle();
                values[4] = aud.getAlbum();
                values[5] = aud.getArtist();
                values[6] = fixDur(aud.getDur()) + "";
                LM.setTimeInMillis(file.lastModified());
                values[7] = dateTime1(LM, persian);
                break;
            case 1:
                Video vid = (Video) media;
                file = new File(vid.data);
                Bitmap tn = ThumbnailUtils.createVideoThumbnail(vid.data, MediaStore.Images.Thumbnails.MINI_KIND);
                if (tn != null) fiAlbumArt.setImageBitmap(tn);
                else {
                    fiAlbumArt.setImageResource(R.drawable.video_1_blue);
                    fiAlbumArt.setPadding(nPad, nPad, nPad, nPad);
                    if (changeCP) fiAlbumArt.setColorFilter(CP);
                }
                fiName.setText(vid.name.substring(0, vid.name.lastIndexOf(".")));
                values[0] = vid.mime;
                values[1] = vid.data;
                values[2] = fixFSize(c, new File(vid.data).length());
                values[3] = vid.title;
                values[4] = vid.album;
                values[5] = vid.artist;
                values[6] = fixDur(vid.dur) + "";
                LM.setTimeInMillis(file.lastModified());
                values[7] = dateTime1(LM, persian);
                break;
            case 2:
                fiTText.setText(R.string.plInfoTitle);
                fiAlbumArt.setImageResource(R.drawable.list_4_white);
                fiAlbumArt.setPadding(nPad, nPad, nPad, nPad);
                fiAlbumArt.setColorFilter(CP);
                P.Playlist pll = playlists.get((int) media);
                fiName.setText(pll.name);
                values[0] = pll.items.size() + " " + getResources().getString(R.string.items);
                LM.setTimeInMillis(pll.dateModified);
                values[1] = dateTime1(LM, persian);
                LM.setTimeInMillis(pll.dateCreated);
                values[2] = dateTime1(LM, persian);
                values[3] = pll.description;
                break;
        }
        fiNameEdit.setText(fiName.getText().toString());

        boolean itemColour = true;
        for (int i = 0; i < attrs.length; i++) {
            LinearLayout fiItem = new LinearLayout(new ContextThemeWrapper(c, R.style.fiItem), null, 0);
            int fiItemColour = ContextCompat.getColor(c, R.color.fiItem0);
            if (itemColour) {
                fiItemColour = ContextCompat.getColor(c, R.color.fiItem1);
                if (changeCP)
                    fiItemColour = Color.argb(44, Color.red(CP), Color.green(CP), Color.blue(CP));
            }
            itemColour = !itemColour;
            fiItem.setBackgroundColor(fiItemColour);
            fiLL.addView(fiItem);

            TextView fiAttr = new TextView(new ContextThemeWrapper(c, R.style.fiAttr), null, 0);
            LinearLayout.LayoutParams fiItemLP = new LinearLayout.LayoutParams(0, wc, 3f);
            fiAttr.setText(attrs[i]);
            if (changeCP) fiAttr.setTextColor(CP);
            fixTVFont(fiAttr, Typeface.BOLD, fiAttr.getTextSize() / dm.density);
            fiItem.addView(fiAttr, fiItemLP);

            if (i != attrs.length - 1 || type != 2) {
                TextView fiValue = new TextView(new ContextThemeWrapper(c, R.style.fiValue), null, 0);
                LinearLayout.LayoutParams fiValueLP = new LinearLayout.LayoutParams(0, wc, 7f);
                fiValue.setText(values[i]);
                if (changeCP && TCP != defTCP) fiValue.setTextColor(CP);
                fixTVFont(fiValue, Typeface.NORMAL, fiValue.getTextSize() / dm.density);
                fiItem.addView(fiValue, fiValueLP);
            } else {
                fiPLDesc = new EditText(new ContextThemeWrapper(c, R.style.fiValue), null, 0);
                LinearLayout.LayoutParams fiPLDescLP = new LinearLayout.LayoutParams(0, wc, 7f);
                fiPLDesc.setText(values[i]);
                fiDescDef = fiPLDesc.getText().toString();
                fiPLDesc.setPadding(dp(3), dp(3), dp(3), dp(3));
                fiPLDesc.setMaxLines(10);
                fiPLDesc.setHint(R.string.typeSomething);
                fiPLDesc.setHintTextColor(ContextCompat.getColor(c, R.color.fiValueHint));
                if (changeCP && TCP != defTCP) {
                    fiPLDesc.setTextColor(CP);
                    fiPLDesc.setHintTextColor(Color.argb(123, Color.red(CP), Color.green(CP), Color.blue(CP)));
                }
                fixTVFont(fiPLDesc, Typeface.NORMAL, fiPLDesc.getTextSize() / dm.density);
                fiItem.addView(fiPLDesc, fiPLDescLP);
                fiPLDesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.requestFocusFromTouch();
                    }
                });
                fiPLDesc.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        fiCanSave();
                    }
                });
            }
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
                fiCanSave();
            }
        });
        View.OnClickListener saveOCL = null, deleteOCL = null;
        if (type == 0 || type == 1) {
            final String PATH = values[1];
            final File FILE = file;
            saveOCL = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!fiCanSave()) return;
                    if (!FILE.exists()) {
                        Toast.makeText(c, R.string.notExists, Toast.LENGTH_LONG).show();
                        return;
                    }
                    String newName = fiNameEdit.getText().toString() + PATH.substring(PATH.lastIndexOf("."));
                    File dest = new File(PATH.substring(0, PATH.lastIndexOf("/") + 1) + newName);
                    if (dest.exists()) {
                        Toast.makeText(c, R.string.suchFileExists, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fiSaving = true;//BE CAREFUL OF THIS!!!
                    fiCanSave();
                    boolean renamed = false;
                    try {
                        renamed = FILE.renameTo(dest);
                    } catch (Exception ignored) {
                        Toast.makeText(c, R.string.errorOccurred, Toast.LENGTH_SHORT).show();
                    }
                    if (renamed) {
                        fiName.setText(fiNameEdit.getText().toString());
                        fiNameEdit.setVisibility(View.GONE);
                        fiName.setVisibility(View.VISIBLE);

                        FoundFile ff = findFile(FILE);
                        if (ff != null) {
                            if (!ff.v) {
                                ArrayList<Audio> auds = aFolders.get(ff.listIndex).files;
                                Audio aud = auds.get(ff.index);
                                auds.remove(ff.index);
                                aud.setName(newName);
                                aud.setData(dest.getPath());
                                auds.add(ff.index, aud);
                                if (mArrangement != 0) Collections.sort(auds, new sortAudios(0));
                                Collections.sort(auds, new sortAudios(mArrangement));
                                if (!mArrangementAsc) Collections.reverse(auds);
                                aFolders.get(ff.listIndex).replaceFiles(auds);
                                if (openedNav == 1 && openedAFolder == ff.listIndex)
                                    showItems(1, ff.listIndex);
                            } else {
                                ArrayList<Video> vids = vFolders.get(ff.listIndex).files;
                                Video vid = vids.get(ff.index);
                                vids.remove(ff.index);
                                vid.setName(newName);
                                vid.setData(dest.getPath());
                                vids.add(ff.index, vid);
                                if (mArrangement != 0) Collections.sort(vids, new sortVideos(0));
                                Collections.sort(vids, new sortVideos(mArrangement));
                                if (!mArrangementAsc) Collections.reverse(vids);
                                vFolders.get(ff.listIndex).replaceFiles(vids);
                                if (openedNav == 3 && openedVFolder == ff.listIndex)
                                    showItems(3, ff.listIndex);
                            }
                            fiCanSave();
                        } else Toast.makeText(c, R.string.errorOccurred, Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(c, R.string.couldntRename, Toast.LENGTH_SHORT).show();
                    fiSaving = false;
                    fiCanSave();
                }
            };
            deleteOCL = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fiSaving = true;
                    singleDelete(PATH, true);
                    fiSaving = false;
                }
            };
        } else if (type == 2) {
            saveOCL = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!fiCanSave()) return;
                    String name = fiName.getText().toString();
                    if (fiNameEdit.getVisibility() == View.VISIBLE)
                        name = fiNameEdit.getText().toString();
                    fiSaving = true;
                    fiCanSave();
                    new PlaylistManager(3, media, name, fiPLDesc.getText().toString()).start();
                }
            };
            deleteOCL = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fiSaving = true;
                    plSingleDelete((int) media);
                    fiSaving = false;
                }
            };
        }
        fiSave.setOnClickListener(saveOCL);
        fiDelete.setOnClickListener(deleteOCL);
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
                int pad = (int) ((fiClose.getWidth() - dp(16)) / 2);
                fiClose.setPadding(pad, pad, pad, pad);
            }
        });
    }

    void fiClose() {
        mFileInfo.setVisibility(View.GONE);
        mFileInfo.removeAllViews();
        fiDescDef = null;
    }

    boolean singleDelete(String PATH, final boolean solo) {
        boolean aBP = false, vBP = false;//Means "is being played...?"
        if (Amin.mp != null && Amin.activeAudio != null)
            aBP = PATH.equals(Amin.activeAudio.getData());
        if (vp != null && activeVideo != null) vBP = PATH.equals(activeVideo.data);
        String bp = "";
        if (aBP || vBP) bp = getResources().getString(R.string.deletePlaying);
        final File FILE = new File(PATH);
        if (solo) {
            final boolean ABP = aBP, VBP = vBP;
            makeBasicMessage(getResources().getString(R.string.deleteFileT),
                    getResources().getString(R.string.deleteFile) + PATH + bp,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mb1Click();
                            delete(FILE, ABP, VBP, solo);
                        }
                    }, true, null);
            return true;
        } else return delete(FILE, aBP, vBP, solo);
    }

    boolean delete(File FILE, boolean ABP, boolean VBP, boolean solo) {
        if (ABP) destroyAmin();
        if (VBP) destroyVP(true);
        boolean deleted = false;
        try {
            deleted = FILE.delete();
        } catch (Exception ignored) {
            if (solo) Toast.makeText(c, R.string.errorOccurred, Toast.LENGTH_SHORT).show();
        }
        if (deleted || !FILE.exists()) {
            if (fileInfoOpened) fileInfo(false, null, -1);
            FoundFile ff = findFile(FILE);
            if (ff != null) {
                if (!ff.v) {
                    ArrayList<Audio> auds = aFolders.get(ff.listIndex).files;
                    auds.remove(ff.index);
                    aFolders.get(ff.listIndex).replaceFiles(auds);
                    //if (openedAFolder == ff.listIndex) mAudios.removeViewAt(ff.index);
                    if (audioListIndex == ff.listIndex) destroyAmin();
                    if (solo && ff.listIndex == openedAFolder) showItems(1, openedAFolder);
                } else {
                    ArrayList<Video> vids = vFolders.get(ff.listIndex).files;
                    vids.remove(ff.index);
                    vFolders.get(ff.listIndex).replaceFiles(vids);
                    //if (openedVFolder == ff.listIndex) mVideos.removeViewAt(ff.index);
                    if (videoListIndex == ff.listIndex) destroyVP(true);
                    if (solo && ff.listIndex == openedVFolder) showItems(3, openedVFolder);
                }
            } else if (solo) Toast.makeText(c, R.string.errorOccurred, Toast.LENGTH_SHORT).show();
        } else if (solo) Toast.makeText(c, R.string.couldntDelete, Toast.LENGTH_SHORT).show();
        return deleted;
    }

    void plSingleDelete(final int media) {
        makeBasicMessage(getResources().getString(R.string.mBM1PLEditor),
                getResources().getString(R.string.mBM1PLDel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mb1Click();
                        if (playingPL == media) destroyAmin();
                        if (fileInfoOpened) fileInfo(false, null, -1);
                        new PlaylistManager(1, new Object[]{media}, -1).start();
                    }
                }, true, null);
    }

    boolean fiCanSave() {
        boolean b = !fiSaving && !fiNameEdit.getText().toString().equals("") &&
                (!fiNameEdit.getText().toString().equals(fiName.getText().toString()) ||
                        (fiPLDesc != null && !fiPLDesc.getText().toString().equals(fiDescDef)));
        if (b) fiSave.setAlpha(1f);
        else fiSave.setAlpha(fiSaveDisabled);
        return b;
    }

    void showItems(int list, int folder) {
        if (list < 0 || list > 3) return;
        cancelLoadings(list == 0, list == 2, list == 1, list == 3);
        navListLLs[list].removeAllViews();
        switch (list) {
            case 0:
                aFolderMaker = new ItemMaker(list, folder);
                aFolderMaker.start();
                break;
            case 1:
                audioMaker = new ItemMaker(list, folder);
                audioMaker.start();
                break;
            case 2:
                vFolderMaker = new ItemMaker(list, folder);
                vFolderMaker.start();
                break;
            case 3:
                videoMaker = new ItemMaker(list, folder);
                videoMaker.start();
                break;
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

    FoundFile findFile(File file) {
        if (file == null) return null;
        boolean v = false;
        int selected = 0;

        ArrayList audioListGet = null;
        int audioListIndexGet = -1, audioIndexGet = -1;
        ArrayList<Folder>[] arrr = new ArrayList[]{aFolders, vFolders};
        for (int fol = 0; fol < arrr.length; fol++) {
            ArrayList<Folder> folders = arrr[fol];
            boolean gotIt = false;
            for (int f = 0; f < folders.size(); f++)
                if (folders.get(f).path.equals(file.getParent())) {
                    ArrayList media = folders.get(f).files;
                    for (int m = 0; m < media.size(); m++) {
                        if (fol == 0) {
                            if (((Audio) media.get(m)).getData().equals(file.getPath())) {
                                gotIt = true;
                                selected = f;
                                audioListGet = media;
                                audioListIndexGet = selected;
                                v = false;
                            }
                        } else if (((Video) media.get(m)).data.equals(file.getPath())) {
                            gotIt = true;
                            selected = f;
                            audioListGet = media;
                            audioListIndexGet = selected;
                            v = true;
                        }
                    }
                }
            if (gotIt) {
                if (fol == 0) {
                    for (int a = 0; a < folders.get(selected).files.size(); a++)
                        if (((Audio) folders.get(selected).files.get(a)).getData().equals(file.getPath()))
                            audioIndexGet = a;
                } else {
                    for (int a = 0; a < folders.get(selected).files.size(); a++)
                        if (((Video) folders.get(selected).files.get(a)).data.equals(file.getPath()))
                            audioIndexGet = a;
                }
            }
        }

        if (audioListGet != null && audioListIndexGet != -1 && audioIndexGet != -1)
            return new FoundFile(v, audioListGet, audioListIndexGet, audioIndexGet);
        else return null;
    }

    void btnSkip(boolean foreward) {
        if (foreward) {
            if (!video && Amin.exists && Amin.mp != null) {
                sendToMSession(2);
                btnShine(mMFShine);
            } else if (vp != null) {
                skipVideo(true);
                btnShine(mMFShine);
            }
        } else {
            if (!video && Amin.exists && Amin.mp != null) {
                sendToMSession(3);
                btnShine(mMBShine);
            } else if (vp != null) {
                skipVideo(false);
                btnShine(mMBShine);
            }
        }
    }

    void playPause() {
        if (!video && Amin.exists && Amin.mp != null) {
            if (playing) sendToMSession(1);
            else sendToMSession(0);
            btnShine(mPPShine);
        } else if (vp != null) {
            if (vPlaying) vp.pause();
            else vp.start();
            vPlaying(!vPlaying);
            btnShine(mPPShine);
        }
    }

    Bitmap folderMark(boolean isExternal) {
        Bitmap icFolder;
        icFolder = (Bitmap) changeColor(c, ContextCompat.getDrawable(c, R.drawable.folder_2_white), TCP, null, false);
        if (icFolder == null) return null;

        Bitmap bmOverlay = Bitmap.createBitmap(icFolder.getWidth(), icFolder.getHeight(), icFolder.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(icFolder, new Matrix(), null);
        int colour = Color.BLACK;
        if (whitenBGPC) colour = Color.WHITE;
        if (isExternal) {
            Paint paint = new Paint();
            paint.setColor(colour);
            paint.setTextSize(dm.density * 111f);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            String mText = "SD";
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            canvas.drawText(mText, (int) ((icFolder.getWidth() - bounds.width()) / 2),
                    (int) ((icFolder.getHeight() + bounds.height()) / 1.79), paint);
        } else {
            Bitmap mobile;
            mobile = (Bitmap) changeColor(c, ContextCompat.getDrawable(c, R.drawable.mobile_2_black), colour,
                    null, false);
            if (mobile != null) canvas.drawBitmap(mobile, 0, 0, null);
        }
        return bmOverlay;
    }

    public static String fixFSize(Context c, long length) {
        int g, m, k, b;
        long l = length;
        g = (int) (l / 1073741824);
        l -= g * 1073741824;
        m = (int) (l / 1048576);
        l -= m * 1048576;
        k = (int) (l / 1024);
        l -= k * 1024;
        b = (int) l;

        String r, z = "0", Gm = "", Mk = "", Kb = "", GET;
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

    void cancelLoadings(boolean fa, boolean fb, boolean a, boolean v) {
        if (fa) {
            if (aFolderMaker != null) try {
                aFolderMaker.interrupt();
            } catch (Exception ignored) {
            }
            aFolderMaker = null;
            if (draughtAFolders != null) {
                if (draughtAFolders.size() > 0) aflCancelled = true;
                draughtAFolders.clear();
            }
        }
        if (fb) {
            if (aFolderMaker != null) try {
                aFolderMaker.interrupt();
            } catch (Exception ignored) {
            }
            aFolderMaker = null;
            if (vFolderMaker != null) try {
                vFolderMaker.interrupt();
            } catch (Exception ignored) {
            }
            vFolderMaker = null;
            if (draughtVFolders != null) {
                if (draughtVFolders.size() > 0) vflCancelled = true;
                draughtVFolders.clear();
            }
        }
        if (fa && fb) if (draughtFIcons != null) draughtFIcons.clear();
        if (a) {
            if (audioMaker != null) try {
                audioMaker.interrupt();
            } catch (Exception ignored) {
            }
            audioMaker = null;
            if (draughtAudios != null) {
                if (draughtAudios.size() > 0) audCancelled = true;
                draughtAudios.clear();
            }
            if (draughtAIcons != null) draughtAIcons.clear();
        }
        if (v) {
            if (videoMaker != null) try {
                videoMaker.interrupt();
            } catch (Exception ignored) {
            }
            videoMaker = null;
            if (draughtVideos != null) {
                if (draughtVideos.size() > 0) vidCancelled = true;
                draughtVideos.clear();
            }
            if (draughtVIcons != null) draughtVIcons.clear();
        }
    }

    void fixTVFont(TextView tv, int tf, float norm) {
        tv.setTypeface(Typeface.create(fonts[font], tf));
        tv.setTextSize(norm * fontRatio);
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
        int y = cl.get(Calendar.YEAR), m = cl.get(Calendar.MONTH), d = cl.get(Calendar.DAY_OF_MONTH), l = 0, pl = 0,
                Y = 0, M = 0, D = 0;
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
        try {
            unbindService(serviceConnection);
            serviceBound = false;
        } catch (Exception ignored) {
        }//service is still active
        if (player != null) player.stopSelf();
        if (Amin.exists) stopService(playerIntent);//for precaution
    }

    void playMedia(boolean isVideo, ArrayList arr, int li, int i, boolean doPlay, boolean autoHide, int autoSeekTo) {
        if (isVideo != video) switchMedia(!isVideo);
        if (!isVideo) {
            if (arr != null) audioList = arr;
            if (li != -1) audioListIndex = li;
            if (i != -1) audioIndex = i;
            playAudio(audioList, audioIndex, false);
        } else {
            if (arr != null) videoList = arr;
            if (li != -1) videoListIndex = li;
            if (i != -1) videoIndex = i;
            playVideo(doPlay, autoHide, autoSeekTo);
        }
    }

    void btnShine(View v) {
        VA(v, "alpha", shineDur, 1f, 0f);
    }

    void menu(final View view, final Object media, final int list) {
        PopupMenu popup = new PopupMenu(c, view);
        PopupMenu.OnMenuItemClickListener click = null;
        int inflate = -1;
        switch (list) {
            case 1:
                click = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.mMAudAddToPL:
                                if (playlists == null) {
                                    Toast.makeText(c, R.string.noPlaylists, Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                if (playlists.size() > 0)
                                    addToPL(new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            new PlaylistManager(0, new String[]{((Audio) media).getData()}, which).start();
                                        }
                                    });
                                else
                                    Toast.makeText(c, R.string.noPlaylists, Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.mMAudDlt:
                                singleDelete(((Audio) media).getData(), true);
                                return true;
                            case R.id.mMAudProp:
                                fileInfo(true, media, 0);
                                return true;
                        }
                        return false;
                    }
                };
                inflate = R.menu.m_aud_more;
                break;
            case 3:
                click = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.mMVidDlt:
                                singleDelete(((Video) media).data, true);
                                return true;
                            case R.id.mMVidProp:
                                fileInfo(true, media, 1);
                                return true;
                        }
                        return false;
                    }
                };
                inflate = R.menu.m_vid_more;
                break;
            case 4:
                click = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.mMPllDlt:
                                plSingleDelete((int) media);
                                return true;
                            case R.id.mMPllProp:
                                fileInfo(true, media, 2);
                                return true;
                        }
                        return false;
                    }
                };
                inflate = R.menu.m_pll_more;
                break;
            case 5:
                click = new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.mMPliRemove:
                                if (playingPL == openedPL && openedPL != -1 && playingPLItem == (int) media)
                                    destroyAmin();
                                new PlaylistManager(1, new Object[]{media}, openedPL).start();
                                return true;
                            case R.id.mMPliDelete:
                                if (playingPL == openedPL && openedPL != -1 && playingPLItem == (int) media)
                                    destroyAmin();
                                new PlaylistManager(1, new Object[]{media}, openedPL).start();
                                singleDelete(playlists.get(openedPL).items.get((int) media).path, true);
                                return true;
                            case R.id.mMPliProp:
                                FoundFile ff = findFile(new File(playlists.get(openedPL).items.get((int) media).path));
                                boolean notFound = true;
                                if (ff != null && !ff.v) {
                                    fileInfo(true, aFolders.get(ff.listIndex).files.get(ff.index), 0);
                                    notFound = false;
                                }
                                if (notFound)
                                    Toast.makeText(c, R.string.fileNotFound, Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return false;
                    }
                };
                inflate = R.menu.m_pli_more;
                break;
        }

        if (click != null) popup.setOnMenuItemClickListener(click);
        else return;
        popup.inflate(inflate);
        popup.show();
    }

    void addToPL(DialogInterface.OnClickListener diOCL) {
        String[] items = new String[playlists.size()];
        for (int i = 0; i < items.length; i++)
            items[i] = playlists.get(i).name + " { " + playlists.get(i).items.size() + " }";
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(Mahdi.this, R.style.mAddToPL));
        builder.setTitle(R.string.mMAudAddToPL).setItems(items, diOCL);
        builder.create().show();
    }

    void selecting(boolean b) {
        selecting = b;
        if (selecting) {
            slctList = navListLLs[openedNav];
            selection = new boolean[slctList.getChildCount()];
            for (int m = 0; m < slctList.getChildCount(); m++) {
                selectCl(slctList, m, 0);
                if (openedNav == 5) mobility(m, true);
            }
            mSelecting.setVisibility(View.VISIBLE);
            if (openedNav == 1) ((View) mSlctAdd).setVisibility(View.VISIBLE);
            checkNTSlct();
            mNavTitleLL.setVisibility(View.GONE);
            mNTSlctLL.setVisibility(View.VISIBLE);
        } else {
            for (int m = 0; m < slctList.getChildCount(); m++) {
                selectCl(navListLLs[openedNav], m, -1);
                if (openedNav == 5) mobility(m, false);
            }
            slctList = null;
            selection = null;
            ((View) mSlctAdd).setVisibility(View.GONE);
            mSelecting.setVisibility(View.GONE);
            if (Amin.mp != null && openedAFolder == audioListIndex)
                highlightItem(openedNav, audioIndex);
            if (vp != null && openedVFolder == videoListIndex) highlightItem(openedNav, videoIndex);
            mNTSlctLL.setVisibility(View.GONE);
            mNavTitleLL.setVisibility(View.VISIBLE);
            mNTSlct.setText("");
        }
    }

    void select(int i, boolean b) {
        if (selection != null && selection.length > i) selection[i] = b;
        checkNTSlct();
        int bb = 0;
        if (b) bb = 1;
        selectCl(slctList, i, bb);
    }

    void selectCl(LinearLayout ll, int i, int w) {
        if (ll == null) return;
        try {
            ConstraintLayout clItem = (ConstraintLayout) ll.getChildAt(i);
            ImageView more = (ImageView) clItem.getChildAt(itmMorePlace);
            int res = R.drawable.more_2_white;
            if (w == -1)
                clItem.setBackground(ContextCompat.getDrawable(c, R.drawable.square_1_tp_to_white_alpha_xml));
            else {
                int colour = Color.TRANSPARENT;
                res = R.drawable.checkbox_1_blue_normal;
                if (w == 1) {
                    res = R.drawable.checkbox_1_blue;
                    colour = ContextCompat.getColor(c, R.color.mItemSelected);
                }
                clItem.setBackgroundColor(colour);
            }
            more.setImageResource(res);
            more.setClickable(w == -1);
        } catch (Exception ignored) {
        }
    }

    void checkNTSlct() {
        int n = 0;
        for (boolean b : selection) if (b) n += 1;
        mNTSlct.setText(n + " " + getResources().getString(R.string.nItemsSelected));
    }

    void turnSearch(boolean on) {
        searching = on;
        if (searching) {
            if (!selecting) mNavTitleLL.setVisibility(View.GONE);
            else mNTSlctLL.setVisibility(View.GONE);
            mNTSearchBtn.setVisibility(View.GONE);
            mNTSearchLL.setVisibility(View.VISIBLE);
            searchItems = new ArrayList<>();
            for (int n = 0; n < navListLLs[openedNav].getChildCount(); n++) {
                ConstraintLayout clItem = (ConstraintLayout) navListLLs[openedNav].getChildAt(n);
                TextView title = (TextView) ((LinearLayout) clItem.getChildAt(1)).getChildAt(0);
                searchItems.add(title.getText().toString());
            }
        } else {
            searchItems = null;
            mNTSearchLL.setVisibility(View.GONE);
            if (!selecting) mNavTitleLL.setVisibility(View.VISIBLE);
            else mNTSlctLL.setVisibility(View.VISIBLE);
            mNTSearchBtn.setVisibility(View.VISIBLE);
            mNTSearch.setText("");
            for (int ch = 0; ch < navListLLs[openedNav].getChildCount(); ch++)
                navListLLs[openedNav].getChildAt(ch).setVisibility(View.VISIBLE);
        }
    }

    void canSearch(boolean can) {
        if (can) mNTSearchBtn.setVisibility(View.VISIBLE);
        else mNTSearchBtn.setVisibility(View.GONE);
    }

    void setPrimaryColours() {
        CP = defCP;
        if (sp.contains(cpTag)) {
            CP = sp.getInt(cpTag, CP);
            changeCP = true;//if (colorPrimary != defColorPrimary)
        }
        TCP = defTCP;
        if (sp.contains(tcpTag)) {
            TCP = sp.getInt(tcpTag, TCP);
            changeTCP = true;
        }
        whitenBGPC = Color.red(TCP) * 0.299 + Color.green(TCP) * 0.587 + Color.blue(TCP) * 0.114 <= 186;
    }

    void updateMPAfterRecreation() {
        updateDescription(true);
        mSeekBar.setEnabled(true);
        AminHandler.obtainMessage(1).sendToTarget();//Amin.mp.isPlaying() & playing() botch the job!
        if (!Amin.playing) AminHandler.obtainMessage(0).sendToTarget();
    }

    void setArrangement() {
        if (sp.contains("fArrangementAsc"))
            fArrangementAsc = sp.getBoolean("fArrangementAsc", true);
        if (sp.contains("mArrangementAsc"))
            mArrangementAsc = sp.getBoolean("mArrangementAsc", true);
        if (sp.contains("pArrangementAsc"))
            pArrangementAsc = sp.getBoolean("pArrangementAsc", true);
        if (sp.contains("fArrangement")) fArrangement = sp.getInt("fArrangement", 0);
        if (sp.contains("mArrangement")) mArrangement = sp.getInt("mArrangement", 0);
        if (sp.contains("pArrangement")) pArrangement = sp.getInt("pArrangement", 0);
    }

    void setFonts() {
        if (sp.contains("font")) font = sp.getInt("font", font);
        if (sp.contains("fontRatio")) fontRatio = sp.getFloat("fontRatio", fontRatio);
        fonts = getResources().getStringArray(R.array.sFonts);
    }

    void applyWP() {
        wpLoaded = false;
        new WPLoader().start();
    }

    void showPlaylists(int which) {
        if (which != -1) mPLItems.removeAllViews();
        else mPlaylists.removeAllViews();
        new PlaylistLoader(which).start();
    }

    public void reloadPlaylists(P.PlaylistDao plDAO) {
        long pplId = -1, pplItemId = -1;
        if (playingPL != -1 && playlists != null && playlists.size() > playingPL) {
            pplId = playlists.get(playingPL).id;
            if (playingPLItem != -1 && playlists.get(playingPL).items != null)
                if (playlists.get(playingPL).items.size() > playingPLItem)
                    pplItemId = playlists.get(playingPL).items.get(playingPLItem).id;
        }
        playlists = (ArrayList<P.Playlist>) plDAO.getAll();
        if (playlists != null && playlists.size() > 0) {
            for (P.Playlist pl : playlists) {//FIRST ITEMS MUST BE SET!!!
                ArrayList<P.Media> items = (ArrayList<P.Media>) plDAO.getItems(pl.id);
                Collections.sort(items, new sortPLItems());
                pl.setItems(items);
            }

            if (pArrangement != 0) Collections.sort(playlists, new sortPlaylists(0));
            Collections.sort(playlists, new sortPlaylists(pArrangement));
            if (!pArrangementAsc) Collections.reverse(playlists);

            if (pplId != -1) for (int p = 0; p < playlists.size(); p++)
                if (playlists.get(p).id == pplId) {
                    playingPL = p;
                    if (pplItemId != -1) {
                        ArrayList<P.Media> items = playlists.get(playingPL).items;
                        for (int i = 0; i < items.size(); i++)
                            if (items.get(i).id == pplItemId) playingPLItem = i;
                    }
                }
        }
    }

    Object[] preparePLAudList(int pl, int i) {
        ArrayList<Audio> arList = new ArrayList<>();
        ArrayList<Integer> toBeDeleted = new ArrayList<>();
        int index = -1;
        for (int f = 0; f < playlists.get(pl).items.size(); f++) {
            File file = new File(playlists.get(pl).items.get(f).path);
            if (file.exists()) {
                activePLItem = findFile(file);
                if (activePLItem != null) {
                    if (!activePLItem.v) {
                        arList.add((Audio) aFolders.get(activePLItem.listIndex).files.get(activePLItem.index));
                        if (f == i) {
                            index = arList.size() - 1;
                            audioList = activePLItem.list;
                            audioListIndex = activePLItem.listIndex;
                            audioIndex = activePLItem.index;
                        }
                    } else toBeDeleted.add(f);
                } else toBeDeleted.add(f);
            } else toBeDeleted.add(f);
        }
        if (toBeDeleted.size() > 0) new PlaylistManager(1, toBeDeleted.toArray(), pl).start();
        return new Object[]{arList, index};
    }

    int dp(int px) {
        return (int) (dm.density * px);
    }

    void lastOpenedNav() {
        int nnn = openedNav;
        if (openedNav == 1) nnn = 0;
        if (openedNav == 3) nnn = 2;
        if (openedNav == 5) nnn = 4;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("lastOpenedNav", nnn);
        editor.apply();
    }

    void openDS(int which) {
        openedDS = which;
        switch (which) {
            case 0:
                mDSAudio.setVisibility(View.VISIBLE);
                mDSVideo.setVisibility(View.GONE);
                mDSIcon1Selected.setBackgroundColor(TCP);
                mDSIcon2Selected.setBackgroundColor(Color.TRANSPARENT);
                break;
            case 1:
                mDSAudio.setVisibility(View.GONE);
                mDSVideo.setVisibility(View.VISIBLE);
                mDSIcon1Selected.setBackgroundColor(Color.TRANSPARENT);
                mDSIcon2Selected.setBackgroundColor(TCP);
                break;
        }
    }

    void regulateEqSBs() {
        for (int b = 0; b < eqSBIds.size(); b++) {
            SeekBar band = (SeekBar) findViewById(eqSBIds.get(b));
            float ph = (float) (mSAEq.getWidth() / eqSBIds.size());
            band.setTranslationX((ph - (float) band.getHeight()) / 2f);
        }
    }

    void setSpinner(Spinner spn, String[] items, int layout, int select) {
        ArrayAdapter<String> prAdapter = new ArrayAdapter<>(c, layout, new ArrayList<>(Arrays.asList(items)));
        prAdapter.setDropDownViewResource(R.layout.reverb_spn_dd);
        spn.setAdapter(prAdapter);
        spn.setSelection(select);
    }

    void styleSB3(SeekBar sb, ColorFilter pdTCP) {
        Drawable thumb = sb.getThumb(), pd = sb.getProgressDrawable();
        if (thumb != null) {
            thumb.setColorFilter(pdTCP);
            sb.setThumb(thumb);
        }
        if (pd != null) {
            pd.setColorFilter(pdTCP);
            sb.setProgressDrawable(pd);
        }
    }

    float pbsToFloat(int i) {
        float f;
        if (i < 100) f = (((float) i) / 200f) + 0.5f;
        else f = ((float) i) / 100f;
        return f;
    }

    int pbsToInt(float f) {
        int i;
        if (f < 1f) i = (int) ((f - 0.5) * 200);
        else i = (int) (f * 100);
        return i;
    }

    void setPBSpeed(int i, boolean sb, boolean mp, boolean tt, boolean SP) {
        if (sb) mSAPBSSB.setProgress(i);
        if (mp && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Amin.mp != null) {
                boolean paused = !playing;
                try {
                    Amin.mp.setPlaybackParams(Amin.mp.getPlaybackParams().setSpeed(pbsToFloat(i)));
                } catch (IllegalArgumentException ignored) {
                }
                if (paused) Amin.mp.pause();
            }
            if (vp != null) {
                boolean paused = !vPlaying;
                try {
                    vp.setPlaybackParams(vp.getPlaybackParams().setSpeed(pbsToFloat(i)));
                } catch (IllegalArgumentException ignored) {
                }
                if (paused) vp.pause();
            }
        }
        if (tt) mSAPBSTitle.setText(getResources().getString(R.string.mDSPBS) +
                " (" + (int) (pbsToFloat(mSAPBSSB.getProgress()) * 100) + "%)");
        if (SP) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(pbSpeed, pbsToFloat(i));
            editor.apply();
        }
    }

    void setPBPitch(int i, boolean sb, boolean mp, boolean SP) {
        if (sb) mSAPBPSB.setProgress(i);
        if (mp && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Amin.mp != null) try {
                Amin.mp.setPlaybackParams(Amin.mp.getPlaybackParams().setPitch(pbsToFloat(i)));
            } catch (IllegalArgumentException ignored) {
            }
            if (vp != null) try {
                vp.setPlaybackParams(vp.getPlaybackParams().setPitch(pbsToFloat(i)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (SP) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat(pbPitch, pbsToFloat(i));
            editor.apply();
        }
    }

    void stManager(boolean show) {
        stManager = show;
        ConstraintLayout.LayoutParams mSTManagerLP = (ConstraintLayout.LayoutParams) mSTManager.getLayoutParams();
        if (show) mSTManagerLP.height = dp(40);
        else mSTManagerLP.height = 0;
        mSTManager.setLayoutParams(mSTManagerLP);
        regulateSVs();
    }

    void addST(String path) {
        LinearLayout ll = new LinearLayout(c);
        LinearLayout.LayoutParams llLP = new LinearLayout.LayoutParams(wc, match);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        final int id = View.generateViewId();
        ll.setId(id);
        mSTManagerLL.addView(ll, llLP);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = getSTPosById(id);
                checkST(i, subtitles.get(i).which == -1);
            }
        });

        ImageView cb = new ImageView(c);
        LinearLayout.LayoutParams cbLP = new LinearLayout.LayoutParams(wc, match);
        cb.setImageResource(R.drawable.checkbox_1_blue_normal);
        cb.setAdjustViewBounds(true);
        int pad = dp(12);
        cb.setPadding(pad, pad, pad, pad);
        cb.setBackgroundResource(R.drawable.square_1_tp_to_white_alpha_xml);
        cb.setColorFilter(TCP);
        ll.addView(cb, cbLP);

        TextView tv = new TextView(c);
        LinearLayout.LayoutParams tvLP = new LinearLayout.LayoutParams(wc, match);
        tv.setText(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
        tv.setTextColor(TCP);
        tv.setTextSize(16f);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        ll.addView(tv, tvLP);
        fixTVFont(tv, Typeface.BOLD, 16f);

        ImageView del = new ImageView(c);
        LinearLayout.LayoutParams delLP = new LinearLayout.LayoutParams(wc, match);
        del.setAdjustViewBounds(true);
        del.setImageResource(R.drawable.delete_1_white);
        pad = dp(11);
        del.setPadding(pad, pad, pad, pad);
        if (TCP != defTCP) del.setColorFilter(TCP);
        del.setBackgroundResource(R.drawable.square_1_tp_to_white_alpha_xml);
        ll.addView(del, delLP);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delST(id);
            }
        });

        subtitles.add(new Subtitle(path, id, -1));
        checkST(subtitles.size() - 1, true);
    }

    void delST(int id) {
        int i = getSTPosById(id);
        View ll = findViewById(id);
        if (ll != null) mSTManagerLL.removeView(ll);
        if (i != -1) {
            checkST(i, false);
            subtitles.remove(i);
        }
    }

    void checkST(int i, boolean check) {
        if (subtitles.size() <= i) return;
        LinearLayout ll = null;
        ImageView cb = null;
        try {
            ll = (LinearLayout) findViewById(subtitles.get(i).id);
            if (ll != null) cb = (ImageView) ll.getChildAt(0);
        } catch (Exception ignored) {
        }

        int which = -1;
        if (check) {
            if ((!sv1Has || !sv2Has) && subtitles.get(i).which == -1) {
                if (!sv1Has) {
                    mSV1.onAttachedToWindow();
                    mSV1.setSubSource(subtitles.get(i).path);
                    which = 0;
                    sv1Has = true;
                } else {
                    mSV2.onAttachedToWindow();
                    mSV2.setSubSource(subtitles.get(i).path);
                    which = 1;
                    sv2Has = true;
                }
                if (cb != null) cb.setImageResource(R.drawable.checkbox_1_blue);
            }
        } else if ((sv1Has || sv2Has) && subtitles.get(i).which != -1) {
            switch (subtitles.get(i).which) {
                case 0:
                    mSV1.onDetachedFromWindow();
                    mSV1.setText("");
                    mSV1.setVisibility(View.GONE);
                    sv1Has = false;
                    break;
                case 1:
                    mSV2.onDetachedFromWindow();
                    mSV2.setText("");
                    mSV2.setVisibility(View.GONE);
                    sv2Has = false;
                    break;
            }
            if (cb != null) cb.setImageResource(R.drawable.checkbox_1_blue_normal);
        }
        subtitles.get(i).setWhich(which);
    }

    int getSTPosById(int id) {
        int i = -1;
        for (int s = 0; s < subtitles.size(); s++) if (subtitles.get(s).id == id) i = s;
        return i;
    }

    int getSTPosByPath(String path) {
        int i = -1;
        for (int s = 0; s < subtitles.size(); s++) if (subtitles.get(s).path.equals(path)) i = s;
        return i;
    }

    void regulateSVs() {
        int pb = 0;
        if (!hiddenPanel) {
            pb += 130;
            if (stManager) pb += 40;
        }
        ViewGroup.MarginLayoutParams mSVCLMLP = (ViewGroup.MarginLayoutParams) mSVCL.getLayoutParams();
        mSVCLMLP.setMargins(0, 0, 0, dp(pb));
        mSVCL.setLayoutParams(mSVCLMLP);
    }

    void setEQMode(int i) {
        eqMode = i;
        customEQ = eqMode == eqModes.length - 1;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(sEQMode, eqMode);
        editor.apply();

        if (eqMode == 21)
            for (int b = 0; b < eqBands; b++) eqVals[b] = (short) sp.getInt(eqBand + b, 0);
        else {
            short[] norms = wmpEQModes[0];
            if (eqMode > -1 && eqMode < 21) norms = wmpEQModes[i];
            for (int b = 0; b < eqBands; b++) {
                ArrayList<Short> values = new ArrayList<>();
                for (int n = 0; n < norms.length; n++)
                    if (eqFreqs[b][0] <= wmpEQBandFreqs[n] && eqFreqs[b][1] > wmpEQBandFreqs[n])
                        values.add(norms[n]);

                if (values.size() > 0) {
                    int addition = 0;
                    for (int s = 0; s < values.size(); s++) addition += values.get(s);
                    eqVals[b] = (short) ((float) (addition / values.size()) * ((float) eqRange[1] / 14f));
                } else eqVals[b] = 0;
            }
        }
        setEQValsForMP(sp);
        setEQValsForVP();
        setEQValsForSBs();
    }

    public static void setEQValsForMP(SharedPreferences sp) {
        if (Amin.equalizer != null)
            for (int b = 0; b < eqBands; b++) Amin.equalizer.setBandLevel((short) b, eqVals[b]);
        else if (Amin.mp != null) Amin.initEqualizer(sp);
    }

    void setEQValsForVP() {
        if (equalizer != null)
            for (int b = 0; b < eqBands; b++) equalizer.setBandLevel((short) b, eqVals[b]);
        else if (vp != null) initEqualizer();
    }

    void setEQValsForSBs() {
        for (int b = 0; b < eqBands; b++)
            if (eqSBIds.size() > b) {
                SeekBar band = findViewById(eqSBIds.get(b));
                if (band != null) band.setProgress(eqVals[b] + (0 - eqRange[0]));
            }
    }

    void customizeEQ() {
        eqMode = eqModes.length - 1;
        customEQ = true;
        try {
            mSAEqSpinner.setSelection(eqMode);
        } catch (Exception ignored) {
        }
        SharedPreferences.Editor editor = sp.edit();
        for (int b = 0; b < eqBands; b++) editor.putInt(eqBand + b, eqVals[b]);
        editor.apply();
    }

    void mobility(int n, boolean yes) {
        int vis = View.GONE, moreMarEnd = 0;
        if (yes) {
            vis = View.VISIBLE;
            moreMarEnd = dp(36);
        }
        ConstraintLayout item = (ConstraintLayout) navListLLs[openedNav].getChildAt(n);
        if (item != null) {
            ImageView more = (ImageView) item.getChildAt(2);
            if (more != null) {
                ConstraintLayout.LayoutParams moreLP = (ConstraintLayout.LayoutParams) more.getLayoutParams();
                moreLP.setMarginEnd(moreMarEnd);
                more.setLayoutParams(moreLP);
            }
            LinearLayout move = (LinearLayout) item.getChildAt(3);
            if (move != null) move.setVisibility(vis);
        }
    }

    void movePLItem(int i, boolean dir) {
        if (((i == 0 && !dir) || (i >= playlists.get(openedPL).getItems().size() - 1 && dir)) ||
                !selecting || movingPLI || searching) return;
        if (playingPL == openedPL && openedPL != -1) destroyAmin();
        movingPLI = true;
        new PlaylistManager(4, i, dir).start();
    }

    ConstraintLayout makeItem(final int l, final int i, final int which, final int list, final boolean v) {
        if (l < 0 || l > 3) return null;//0 => Fol; 1 => Med; 2 => PLL; 3 => PLI;
        int itemId = View.generateViewId(), ivItemId = View.generateViewId(), itmMoreId = View.generateViewId(),
                clMoveId = ViewGroup.generateViewId(), imgSrc = 0;
        boolean isPL = l == 2 || l == 3;
        View.OnClickListener itemClick = null;
        Bitmap imgBmp = null;
        String tvItemT = "", tvItemDescT = "";
        ArrayList<Folder> folders = null;
        if (!isPL) {
            if (!v) folders = aFolders;
            else folders = vFolders;
        }
        switch (l) {
            case 0:
                Folder fol = folders.get(i);
                tvItemT = fol.name;
                switch (fArrangement) {
                    case 1:
                        int meds = R.string.audios;
                        if (v) meds = R.string.videos;
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
                        tvItemDescT = folderPath(folders.get(i).path);
                        break;
                }
                itemClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int destList = 1;
                        if (v) {
                            openedVFolder = i;
                            destList = 3;
                        } else openedAFolder = i;
                        changeNavList(destList, false);
                        showItems(destList, i);
                    }
                };
                break;
            case 1:
                if (!v) {
                    Audio aud = (Audio) aFolders.get(which).files.get(i);
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
                            tvItemDescT = aud.getMime();
                            break;
                        case 7:
                            Calendar LM = Calendar.getInstance();
                            File file = new File(aud.getData());
                            LM.setTimeInMillis(file.lastModified());
                            tvItemDescT = LM.get(Calendar.YEAR) + "." + LM.get(Calendar.MONTH) + "." +
                                    LM.get(Calendar.DAY_OF_MONTH) + " " + LM.get(Calendar.HOUR_OF_DAY) + ":" +
                                    LM.get(Calendar.MINUTE) + ":" + LM.get(Calendar.SECOND);
                            break;
                        default:
                            tvItemDescT = fixDur(aud.getDur());
                            break;
                    }
                } else {
                    Video vid = (Video) vFolders.get(which).files.get(i);
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
                final ArrayList files = folders.get(which).files;
                itemClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!selecting)
                            playMedia(v, files, which, i, true, false, 0);
                        else select(i, !selection[i]);
                    }
                };
                break;
            case 2:
                P.Playlist pll = playlists.get(i);
                tvItemT = pll.name;
                imgSrc = R.drawable.list_4_white;
                Calendar LM = Calendar.getInstance();
                switch (pArrangement) {
                    case 2:
                        LM.setTimeInMillis(pll.dateModified);
                        tvItemDescT = dateTime1(LM, Locale.getDefault().getLanguage().equals("fa"));
                        break;
                    case 3:
                        LM.setTimeInMillis(pll.dateCreated);
                        tvItemDescT = dateTime1(LM, Locale.getDefault().getLanguage().equals("fa"));
                        break;
                    default://1 & etc.
                        tvItemDescT = pll.items.size() + " " + getResources().getString(R.string.items);
                        break;
                }
                itemClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!selecting) {
                            openedPL = i;
                            changeNavList(5, false);
                            showPlaylists(i);
                        } else select(i, !selection[i]);
                    }
                };
                break;
            case 3:
                imgSrc = R.drawable.sound_2_blue;
                Audio aud = null;
                FoundFile ff = findFile(new File(playlists.get(which).items.get(i).path));
                if (ff != null)
                    if (!ff.v) aud = (Audio) aFolders.get(ff.listIndex).files.get(ff.index);
                if (aud != null) {
                    tvItemT = (playlists.get(which).items.get(i).number + 1) + ". " + aud.getName();
                    imgBmp = Amin.getAlbumArt(c, aud.getAlbumId());
                    tvItemDescT = folderPath(aud.getData().substring(0, aud.getData().lastIndexOf("/") + 1));
                } else tvItemT = playlists.get(which).items.get(i).path;
                itemClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!selecting) {
                            Object[] returns = preparePLAudList(which, i);
                            ArrayList<Audio> arList = (ArrayList<Audio>) returns[0];
                            int index = (int) returns[1];
                            if (arList.size() > 0) {
                                playingPL = openedPL;
                                playingPLItem = i;
                                highlightItem(4, playingPL);

                                if (index == -1) index = 0;
                                if (video) switchMedia(true);
                                playAudio(arList, index, true);
                            }
                        } else select(i, !selection[i]);
                    }
                };
                break;
        }

        ConstraintLayout clItem = (ConstraintLayout) LayoutInflater.from(c).inflate(R.layout.item, null);
        clItem.setId(itemId);
        clItem.setOnClickListener(itemClick);
        if (l != 0) clItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!selecting) {
                    selecting(true);
                    select(i, true);
                }
                return true;
            }
        });
        else clItem.setPaddingRelative(clItem.getPaddingStart(), clItem.getPaddingTop(),
                dp(10), clItem.getPaddingBottom());

        ImageView ivItem = (ImageView) clItem.getChildAt(0);
        ivItem.setId(ivItemId);
        if (!isPL) {
            if (l == 0) ivItem.setPadding(dp(10), dp(14), dp(10), dp(16));
        } else {
            if (l == 2 || imgBmp == null) {
                ivItem.setImageResource(imgSrc);
                ivItem.setColorFilter(TCP);
                ivItem.setPadding(dp(10), dp(14), dp(10), dp(16));
            } else ivItem.setImageBitmap(imgBmp);
        }

        LinearLayout llContents = (LinearLayout) clItem.getChildAt(1);
        ConstraintLayout.LayoutParams llContentsLP = (ConstraintLayout.LayoutParams) llContents.getLayoutParams();
        llContentsLP.startToEnd = ivItemId;
        if (l == 0) llContentsLP.endToEnd = itemId;
        else llContentsLP.endToStart = itmMoreId;
        llContents.setLayoutParams(llContentsLP);

        TextView tvItem = (TextView) llContents.getChildAt(0), tvItemDesc = (TextView) llContents.getChildAt(1);
        tvItem.setText(tvItemT);
        tvItemDesc.setText(tvItemDescT);
        tvItem.setTextColor(TCP);
        tvItemDesc.setTextColor(TCP);
        fixTVFont(tvItem, Typeface.BOLD, tvItem.getTextSize() / dm.density);
        fixTVFont(tvItemDesc, Typeface.NORMAL, tvItemDesc.getTextSize() / dm.density);

        ImageView itmMore = (ImageView) clItem.getChildAt(2);
        if (l != 0) {
            itmMore.setId(itmMoreId);
            itmMore.setColorFilter(TCP);
            if (isPL) itmMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menu(view, i, list);
                }
            });
            else {
                final ArrayList files = folders.get(which).files;
                itmMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        menu(view, files.get(i), list);
                    }
                });
            }
        } else clItem.removeViewAt(2);

        if (l == 3) {
            LinearLayout clMove = new LinearLayout(c);
            ConstraintLayout.LayoutParams clMoveLP = new ConstraintLayout.LayoutParams(dp(36), 0);
            clMoveLP.endToEnd = itemId;
            clMoveLP.topToTop = itemId;
            clMoveLP.bottomToBottom = itemId;
            clMove.setId(clMoveId);
            clMove.setOrientation(LinearLayout.VERTICAL);
            clMove.setVisibility(View.GONE);
            clItem.addView(clMove, clMoveLP);

            ImageView ivMoveUp = new ImageView(new ContextThemeWrapper(c, R.style.ivMove), null, 0);
            LinearLayout.LayoutParams ivMoveUpLP = new LinearLayout.LayoutParams(match, 0, 0.5f);
            if (changeTCP) ivMoveUp.setColorFilter(TCP);
            ivMoveUp.setRotation(-90f);
            clMove.addView(ivMoveUp, ivMoveUpLP);
            ivMoveUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movePLItem(i, false);
                }
            });

            ImageView ivMoveDown = new ImageView(new ContextThemeWrapper(c, R.style.ivMove), null, 0);
            LinearLayout.LayoutParams ivMoveDownLP = new LinearLayout.LayoutParams(match, 0, 0.5f);
            if (changeTCP) ivMoveDown.setColorFilter(TCP);
            ivMoveDown.setRotation(90f);
            clMove.addView(ivMoveDown, ivMoveDownLP);
            ivMoveDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    movePLItem(i, true);
                }
            });
        }
        return clItem;
    }

    void checkLoaded() {
        if (folsLoaded && wpLoaded) mLoading(false);
    }

    public static Bitmap cropBitmap(Context c, Object pic, int[] norms) {
        if (pic == null) return null;
        Bitmap resized = null;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        if (pic instanceof Integer) BitmapFactory.decodeResource(c.getResources(), (int) pic, opt);
        else if (pic instanceof String) BitmapFactory.decodeFile((String) pic, opt);
        else return null;

        // Minimize the picture somehow...
        int w = opt.outWidth, h = opt.outHeight;
        float ratio = (float) w / (float) h, screenRatio = (float) norms[0] / norms[1];
        int inSampleSize = 1;
        if (ratio < screenRatio) {//Narrower Picture => Take Width...
            if (w > norms[0]) while ((int) (w / inSampleSize) > norms[0]) inSampleSize *= 2;
        } else if (ratio > screenRatio) {//Wider Picture => Take Height...
            if (h > norms[1]) while ((int) (h / inSampleSize) > norms[1]) inSampleSize *= 2;
        } else if (w > norms[0] || h > norms[1])
            while ((int) (w / inSampleSize) > norms[0] || (int) (h / inSampleSize) > norms[1])
                inSampleSize *= 2;
        opt.inSampleSize = inSampleSize;
        opt.inJustDecodeBounds = false;
        if (pic instanceof Integer)
            resized = BitmapFactory.decodeResource(c.getResources(), (int) pic, opt);
        else if (pic instanceof String) resized = BitmapFactory.decodeFile((String) pic, opt);

        // Make its ratio equal to that of the screen...
        w = resized.getWidth();
        h = resized.getHeight();
        ratio = (float) w / (float) h;
        screenRatio = (float) norms[0] / norms[1];
        if (ratio < screenRatio) {//Narrower Picture
            h = (int) ((float) w / screenRatio);
            resized = Bitmap.createBitmap(resized, 0, (int) (((float) resized.getHeight() - (float) h) / 2f), w, h);
        } else if (ratio > screenRatio) {//Wider Picture
            w = (int) (screenRatio * (float) h);
            resized = Bitmap.createBitmap(resized, (int) (((float) resized.getWidth() - (float) w) / 2f), 0, w, h);
        }// else resized = resized;//IMPOSSIBLE!*/

        // Then set a scale for it...
        if (norms[0] < w || norms[1] < h) {//Heavy Twice... DANGEROUS....!
            Bitmap mini = Bitmap.createBitmap(norms[0], norms[1], Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mini);
            Matrix amin = new Matrix();
            amin.setScale((float) norms[0] / (float) w, (float) norms[1] / (float) h, 0, 0);
            canvas.drawBitmap(resized, amin, null);
            resized = mini;
            mini = null;
        }
        return resized;
    }


    static class Folder {
        private String name, path;
        private ArrayList files;
        private boolean isExternal;

        Folder(String name, String path, ArrayList files, boolean isExternal) {
            this.name = name;
            this.path = path;
            this.files = files;
            this.isExternal = isExternal;
        }

        private void addAudio(Audio aud) {
            this.files.add(aud);
        }

        private void addVideo(Video vid) {
            this.files.add(vid);
        }

        private void replaceFiles(ArrayList files) {
            this.files = files;
        }
    }

    class sortFolders implements Comparator<Folder> {
        private int by;

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
        private int by;

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
        private int by;

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

    class sortPlaylists implements Comparator<P.Playlist> {
        private int by;

        sortPlaylists(int by) {
            this.by = by;
        }

        public int compare(P.Playlist a, P.Playlist b) {
            switch (by) {
                case 1:
                    return a.items.size() - b.items.size();
                case 2:
                    return (int) (b.dateModified - a.dateModified);
                case 3:
                    return (int) (b.dateCreated - a.dateCreated);
                default:
                    return a.name.toLowerCase().compareTo(b.name.toLowerCase());
            }
        }
    }

    class sortPLItems implements Comparator<P.Media> {
        public int compare(P.Media a, P.Media b) {
            return (int) (a.number - b.number);
        }
    }

    class Video {
        private String data, name, title, album, artist, mime;
        private int dur;

        Video(String data, String name, String title, String album, String artist, int dur, String mime) {
            this.data = data;
            this.name = name;
            this.title = title;
            this.album = album;
            this.artist = artist;
            this.dur = dur;
            this.mime = mime;
        }

        private void setData(String data) {
            this.data = data;
        }

        private void setName(String name) {
            this.name = name;
        }
    }

    class ImageLoader extends Thread {
        private ArrayList<Integer> iconIds;
        private int list, folder, i = 0;
        private boolean isMedia, v;

        ImageLoader(ArrayList<Integer> iconIds, int list, int folder) {
            this.iconIds = iconIds;
            this.list = list;
            this.folder = folder;
            isMedia = list == 1 || list == 3;
            v = list == 2 || list == 3;
        }

        @Override
        public void run() {
            imageLoader(iconIds, list, folder);
        }

        void imageLoader(final ArrayList<Integer> list, final int l, final int folder) {
            try {
                Bitmap bmp;
                int iconRes;
                ArrayList<Folder> folders = aFolders;
                boolean albumArt = true;
                if (v) folders = vFolders;
                if (isMedia) {
                    if (!v) {
                        bmp = Amin.getAlbumArt(c, ((Audio) folders.get(folder).files.get(i)).getAlbumId());
                        if (bmp == null) {
                            iconRes = R.drawable.sound_2_blue;
                            bmp = (Bitmap) changeColor(c, iconRes, TCP, null, false);
                            albumArt = false;
                        }
                    } else {
                        bmp = ThumbnailUtils.createVideoThumbnail(((Video) folders.get(folder).files.get(i)).data,
                                MediaStore.Images.Thumbnails.MINI_KIND);
                        if (bmp == null) {
                            iconRes = R.drawable.video_1_blue;
                            bmp = (Bitmap) changeColor(c, iconRes, TCP, null, false);
                            albumArt = false;
                        }
                    }
                } else {
                    if (folders.get(i).isExternal) bmp = sdFolder;
                    else bmp = isFolder;
                }
                if (isMedia) {
                    if (l == 1)
                        hIconA.obtainMessage(list.get(i), albumArt ? 1 : 0, 0, bmp).sendToTarget();
                    else hIconV.obtainMessage(list.get(i), albumArt ? 1 : 0, 0, bmp).sendToTarget();
                } else hIconF.obtainMessage(list.get(i), 0, 0, bmp).sendToTarget();
            } catch (Exception ignored) {
            }
            i += 1;
            if (list != null && i < list.size()) imageLoader(list, l, folder);
        }
    }

    private class MediaLoader extends Thread {
        @Override
        public void run() {
            Uri isAUrl = MediaStore.Audio.Media.INTERNAL_CONTENT_URI, sdAUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    isVUrl = MediaStore.Video.Media.INTERNAL_CONTENT_URI, sdVUrl = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String aSelection = MediaStore.Audio.Media.IS_MUSIC + "!= 0", aSortOrder = MediaStore.Audio.Media.TITLE + " ASC",
                    vSortOrder = MediaStore.Video.Media.TITLE + " ASC";
            Cursor isACursor = getContentResolver().query(isAUrl, null, aSelection, null, aSortOrder),
                    sdACursor = getContentResolver().query(sdAUrl, null, aSelection, null, aSortOrder),
                    isVCursor = getContentResolver().query(isVUrl, null, null, null, vSortOrder),
                    sdVCursor = getContentResolver().query(sdVUrl, null, null, null, vSortOrder);
            //"sdACursor" and "sdVCursor" NEED "READ_EXTERNAL_STORAGE" PERMISSION, along with FileUtils!
            aFolders = new ArrayList<>();
            vFolders = new ArrayList<>();
            readCursor(isACursor, false, false);
            readCursor(sdACursor, true, false);
            readCursor(isVCursor, false, true);
            readCursor(sdVCursor, true, true);

            if (aFolders.size() > 0) {
                if (fArrangement != 0) Collections.sort(aFolders, new sortFolders(0));
                Collections.sort(aFolders, new sortFolders(fArrangement));
                if (!fArrangementAsc) Collections.reverse(aFolders);
                for (int f = 0; f < aFolders.size(); f++) {
                    ArrayList files = aFolders.get(f).files;
                    if (files != null) {
                        if (mArrangement != 0) Collections.sort(files, new sortAudios(0));
                        Collections.sort(files, new sortAudios(mArrangement));
                        if (!mArrangementAsc) Collections.reverse(files);
                        aFolders.get(f).replaceFiles(files);
                    }
                }
            }
            if (vFolders.size() > 0) {
                if (fArrangement != 0) Collections.sort(vFolders, new sortFolders(0));
                Collections.sort(vFolders, new sortFolders(fArrangement));
                if (!fArrangementAsc) Collections.reverse(vFolders);
                for (int f = 0; f < vFolders.size(); f++) {
                    ArrayList files = vFolders.get(f).files;
                    if (files != null) {
                        if (mArrangement != 0) Collections.sort(files, new sortVideos(0));
                        Collections.sort(files, new sortVideos(mArrangement));
                        if (!mArrangementAsc) Collections.reverse(files);
                        vFolders.get(f).replaceFiles(files);
                    }
                }
            }
            hLoaded.obtainMessage().sendToTarget();
        }

        private void readCursor(Cursor cursor, boolean isExternal, boolean v) {
            if (cursor != null && cursor.getCount() > 0) while (cursor.moveToNext()) {
                String data, name, title, album, artist, mime;
                int dur;
                Audio aud = null;
                Video vid = null;
                if (!v) {
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

                if (!v && aud != null) {
                    String folderPath = new File(data).getParent();
                    boolean gotFolder = false;
                    for (int f = 0; f < aFolders.size(); f++)
                        if (aFolders.get(f).path.equals(folderPath)) {
                            gotFolder = true;
                            aFolders.get(f).addAudio(aud);
                        }
                    if (!gotFolder) {
                        ArrayList files = new ArrayList<>();
                        files.add(aud);
                        aFolders.add(new Folder(folderPath.substring(folderPath.lastIndexOf("/") + 1),
                                folderPath, files, isExternal));
                    }
                } else if (v && vid != null) {
                    String folderPath = new File(data).getParent();
                    boolean gotFolder = false;
                    for (int f = 0; f < vFolders.size(); f++)
                        if (vFolders.get(f).path.equals(folderPath)) {
                            gotFolder = true;
                            vFolders.get(f).addVideo(vid);
                        }
                    if (!gotFolder) {
                        ArrayList files = new ArrayList<>();
                        files.add(vid);
                        vFolders.add(new Folder(folderPath.substring(folderPath.lastIndexOf("/") + 1),
                                folderPath, files, isExternal));
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
        private boolean albumArt;

        GotBmp(int id, Bitmap bmp, boolean albumArt) {
            this.id = id;
            this.bmp = bmp;
            this.albumArt = albumArt;
        }
    }

    class ItemMaker extends Thread {
        private int list, folder;
        private ArrayList<Folder> folders;
        private boolean isMedia, v;

        ItemMaker(int list, int folder) {
            this.list = list;
            this.folder = folder;
            isMedia = list == 1 || list == 3;
            v = list == 2 || list == 3;
        }

        @Override
        public void run() {
            ArrayList<Integer> itemIds = new ArrayList<>();
            if (!v) folders = aFolders;
            else folders = vFolders;
            int length;
            if (isMedia) length = folders.get(folder).files.size();
            else length = folders.size();
            ArrayList<Integer> iconIds = new ArrayList<>();

            for (int f = 0; f < length; f++) {
                int l = 0;
                if (isMedia) l = 1;
                ConstraintLayout clItem = makeItem(l, f, folder, list, v);
                itemIds.add(clItem.getId());
                iconIds.add(((ImageView) clItem.getChildAt(0)).getId());
                hItemLoaded.obtainMessage(list, clItem).sendToTarget();
            }
            hItemsFinished.obtainMessage(list, new ArrayList[]{itemIds, iconIds}).sendToTarget();
        }
    }

    class FoundFile {
        private boolean v;
        private ArrayList list;
        private int listIndex, index;

        FoundFile(boolean v, ArrayList list, int listIndex, int index) {
            this.v = v;
            this.list = list;
            this.listIndex = listIndex;
            this.index = index;
        }
    }

    class PlaylistLoader extends Thread {
        // ROOM operation must necessarily be done inside a separate Thread;
        // because, as Android Studio warned (with throwing an Exception), they might take long times.
        private boolean isMedia;
        private int which, list;

        PlaylistLoader(int which) {
            this.isMedia = which != -1;
            this.which = which;
            this.list = 4;
            if (isMedia) list = 5;
        }

        // If you insert media before inserting their playlist, it throws an SQLiteConstraintException:
        // FOREIGN KEY constraint failed (code 787)!
        @Override
        public void run() {
            if (!isMedia) {
                P.Playlists plDB = Room.databaseBuilder(c, P.Playlists.class, plDatabase).build();
                reloadPlaylists(plDB.plDAO());
                plDB.close();
            }

            ArrayList<Integer> itemIds = new ArrayList<>(), iconIds = new ArrayList<>();
            int length = playlists.size();
            if (isMedia) length = playlists.get(which).items.size();
            for (int i = 0; i < length; i++) {
                ConstraintLayout clItem = makeItem(list - 2, i, which, list, false);
                itemIds.add(clItem.getId());
                iconIds.add(((ImageView) clItem.getChildAt(0)).getId());
                hItemLoaded.obtainMessage(list, clItem).sendToTarget();
            }
            hItemsFinished.obtainMessage(list, new ArrayList[]{itemIds, iconIds}).sendToTarget();
        }
    }

    class PlaylistManager extends Thread {
        private int type;
        private Object[] objects;

        PlaylistManager(int type, Object... objects) {
            this.type = type;
            this.objects = objects;
        }

        @Override
        public void run() {
            String msg = "", erMsg = "";
            int send = -1, iMsg = R.string.errorOccurred, tLength = Toast.LENGTH_SHORT;
            Object[] objs = new Object[5];
            P.Playlists plDB = Room.databaseBuilder(c, P.Playlists.class, plDatabase).build();
            P.PlaylistDao plDAO = plDB.plDAO();
            boolean toast;
            long plId;
            switch (type) {
                case 0:// ADD TO A PLAYLIST
                    Object[] paths = (Object[]) objects[0];//NOT STRING[]!
                    int which = (int) objects[1];
                    toast = paths.length == 1;
                    plId = getPLId(which);
                    if (plId != -1) for (int p = 0; p < paths.length; p++) {
                        if (plDAO.findItemByPath(plId, (String) paths[p]) == null) try {
                            plDAO.insertItem(new P.Media((String) paths[p], plDAO.getItems(plId).size(), plId));

                            P.Playlist replaceable = playlists.get(which).set(P.MODIFIED, Calendar.getInstance().getTimeInMillis());
                            plDAO.update(replaceable);
                            playlists.remove(which);
                            playlists.add(which, replaceable);
                        } catch (Exception e) {
                            send = 0;
                            if (e.getMessage() != null) erMsg = e.getMessage();
                            msg = e.getClass().getName() + ": " + erMsg;
                        }
                        else {
                            send = 1;
                            iMsg = R.string.mPLExists;
                        }
                    }
                    else send = 1;
                    updateItems(plDAO, which);
                    if (!toast) send = -1;
                    objs[1] = objects[1];
                    break;
                case 1:// DELETE (FROM) PLAYLISTS
                    Object[] numbers = (Object[]) objects[0];
                    int pl = (int) objects[1];
                    toast = numbers.length == 1;
                    for (int n = 0; n < numbers.length; n++)
                        try {
                            int num = (int) numbers[n];
                            if (pl == -1) {//DELETE PLAYLISTS
                                plDAO.delete(plDAO.get(playlists.get(num).id));
                                playlists.remove((int) numbers[n]);
                            } else {//DELETE MEDIA
                                if (getPLId(pl) != -1)
                                    plDAO.deleteItem(playlists.get(pl).items.get(num));
                                else send = 1;
                            }
                        } catch (Exception e) {
                            send = 0;
                            if (e.getMessage() != null) erMsg = e.getMessage();
                            msg = e.getClass().getName() + ": " + erMsg;
                        }
                    if (pl != -1) {
                        List<P.Media> ms = plDAO.getItems(playlists.get(pl).id);
                        for (int m = 0; m < ms.size(); m++)
                            plDAO.updateItem(ms.get(m).set(P.N, (long) m));
                        updateItems(plDAO, pl);

                        P.Playlist replaceable = playlists.get(pl).set(P.MODIFIED, Calendar.getInstance().getTimeInMillis());
                        plDAO.update(replaceable);
                        playlists.remove(pl);
                        playlists.add(pl, replaceable);
                    }
                    if (!toast) send = -1;
                    objs[1] = objects[1];
                    break;
                case 2:// ADD A PLAYLIST
                    String New = (String) objects[0];
                    boolean reload = true;
                    if (plDAO.findByName(New) == null) {
                        long id = -1;
                        try {
                            id = plDAO.insert(new P.Playlist(New));
                        } catch (Exception ignored) {
                            send = 1;
                        }
                        if (id == -1) send = 1;
                    } else {
                        send = 1;
                        iMsg = R.string.plExists;
                        reload = false;
                    }
                    objs[1] = reload;
                    break;
                case 3:// EDIT A PLAYLIST
                    int num = (int) objects[0];
                    String name = (String) objects[1], desc = (String) objects[2];
                    try {
                        P.Playlist replaceable = playlists.get(num);
                        boolean replace = false;
                        if (name != null && !name.equals(replaceable.name)) {
                            if (!name.equals("")) {
                                if (plDAO.findByName(name) == null) {
                                    replaceable.set(P.NAME, name);
                                    replace = true;
                                } else {
                                    send = 1;
                                    iMsg = R.string.plExists;
                                }
                            } else send = 1;
                        }
                        if (desc != null && !desc.equals(replaceable.description)) {
                            replaceable.set(P.DESC, desc);
                            replace = true;
                        }
                        if (replace) {
                            replaceable.set(P.MODIFIED, Calendar.getInstance().getTimeInMillis());
                            plDAO.update(replaceable);
                            objs[1] = replaceable.name;
                            playlists.remove(num);
                            playlists.add(num, replaceable);
                        }
                        objs[2] = replace;
                    } catch (Exception ignored) {
                        send = 1;
                    }
                    break;
                case 4:// MOVE AN ITEM
                    int i = (int) objects[0];
                    boolean dir = (boolean) objects[1];
                    P.Media THIS = playlists.get(openedPL).getItems().get(i), RW;
                    long number = THIS.number, newNum = number;
                    if (dir) newNum += 1;
                    else newNum -= 1;
                    RW = plDAO.findItemByNumber(playlists.get(openedPL).id, newNum);
                    plDAO.updateItem(THIS.set(P.N, RW.number));
                    plDAO.updateItem(RW.set(P.N, number));

                    P.Playlist replaceable = playlists.get(openedPL).set(P.MODIFIED, Calendar.getInstance().getTimeInMillis());
                    plDAO.update(replaceable);
                    playlists.remove(openedPL);
                    playlists.add(openedPL, replaceable);
                    updateItems(plDAO, openedPL);
                    objs[1] = number;
                    objs[2] = newNum;
                    break;
            }
            plDB.close();

            if (send == 0) objs[0] = msg;
            if (send == 1) objs[0] = iMsg;
            hPlaylistManager.obtainMessage(type, tLength, 0, objs).sendToTarget();
        }

        long getPLId(int which) {
            long id = -1;
            try {
                id = playlists.get(which).id;
            } catch (Exception ignored) {
            }
            return id;
        }

        void updateItems(P.PlaylistDao plDAO, int i) {
            ArrayList<P.Media> items = (ArrayList<P.Media>) plDAO.getItems(playlists.get(i).id);
            Collections.sort(items, new sortPLItems());
            playlists.get(i).setItems(items);
        }
    }

    public class Subtitle {
        public String path;
        public int id, which;

        Subtitle(String path, int id, int which) {
            this.path = path;
            this.id = id;
            this.which = which;
        }

        void setWhich(int which) {
            this.which = which;
        }
    }

    class WPLoader extends Thread {
        @Override
        public void run() {
            boolean internalWP = wp < resWallpapers.length;
            Bitmap bmp;
            int[] scr = {dm.widthPixels, dm.heightPixels};
            if (!internalWP)
                bmp = cropBitmap(c, sp.getString(adtWP + ((wp - resWallpapers.length) + 1), ""), scr);
            else bmp = cropBitmap(c, resWallpapers[wp], scr);//if (wp > -1)
            hWPLoader.obtainMessage(0, bmp).sendToTarget();//ALWAYS NECESSARY; otherwise mLoading wouldn't work!
        }
    }
}
