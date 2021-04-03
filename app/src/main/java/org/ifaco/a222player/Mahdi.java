package org.ifaco.a222player;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Mahdi extends AppCompatActivity {
    ConstraintLayout mBody, mPlayerBody, mShower, mPanel, mShuffle, mMoveB, mPlayPause, mMoveF, mRepeat, mNavigation, mNavList, mBasicMessages,
            mLoading;
    View mMotor, mSeekBarReplica;
    Toolbar mToolbar;
    ImageView mShwBG, mAlbumArt, mShuffleBT, mBtnMenuBorderL, mPlayPauseBT, mBtnMenuBorderR, mRepeatBT;
    SeekBar mSeekBar;
    TextView mDur;
    ScrollView mFoldersSV, mSoundsSV;
    LinearLayout mFolders, mSounds;

    public static int loadDur1 = 178, navDur = 78, repeat = 0, controlPlayerDur = 48, IMLoadDur1 = 89, mLoadingDur = 480, imageLoader = 22,
            exitDur = 48;
    boolean playing = false, changingTheSeekBar = false, serviceBound = false, checkingFU = false, exiting = false, isNavOpen = false,
            notFirst, buttonsEnabled = false, reCreation = false, couldNotGainPlayingDataOnReCreation = false;
    ValueAnimator reproduction;
    Amin player;
    ArrayList<Audio> audioList;
    public static final String Broadcast_PLAY_NEW_AUDIO = "org.ifaco.a222player.PlayNewAudio", cfuTag = "checkForUpdates",
            cloudFolder = "http://ifaco.org/android/222player/", ver = "last_version.txt", app = "222_Player.apk";
    private static final int permReadExerStor = 630, permWriteExerStor = 480, exitingDelay = 4080, progress_bar_type = 0;
    public static Handler AminHandler, changeHandler;
    public static ArrayList<Folder> folders;
    ActionBar mToolbarAB;
    File updates;
    ProgressDialog dlDialogue;
    RequestQueue cfuQueue;
    String version = null, pasteURL;
    ScrollView[] navLists;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    int openedNav = 0, audioIndex = -1, audioListIndex = -1, currentOpenedAudioList = -1;
    ArrayList<Integer> folClItemIds, sndClItemIds;
    int[] backfulLists;
    final float mSBsDisabled = 0.58f;
    public static boolean shuffle = false, direction = true;
    SharedPreferences sp;
    TextView mTBTitle;

    View.OnClickListener doNothing = new View.OnClickListener() {
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
        setContentView(R.layout.main);

        mBody = findViewById(R.id.mBody);
        mMotor = findViewById(R.id.mMotor);//translationX, translationY, scaleX, scaleY
        mToolbar = findViewById(R.id.mToolbar);
        mShwBG = findViewById(R.id.mShwBG);
        mPlayerBody = findViewById(R.id.mPlayerBody);
        mShower = findViewById(R.id.mShower);
        mAlbumArt = findViewById(R.id.mAlbumArt);
        mSeekBarReplica = findViewById(R.id.mSeekBarReplica);
        mSeekBar = findViewById(R.id.mSeekBar);
        mPanel = findViewById(R.id.mPanel);
        mDur = findViewById(R.id.mDur);
        mShuffle = findViewById(R.id.mShuffle);
        mShuffleBT = findViewById(R.id.mShuffleBT);
        mMoveB = findViewById(R.id.mMoveB);
        mBtnMenuBorderL = findViewById(R.id.mBtnMenuBorderL);
        mPlayPause = findViewById(R.id.mPlayPause);
        mPlayPauseBT = findViewById(R.id.mPlayPauseBT);
        mBtnMenuBorderR = findViewById(R.id.mBtnMenuBorderR);
        mMoveF = findViewById(R.id.mMoveF);
        mRepeat = findViewById(R.id.mRepeat);
        mRepeatBT = findViewById(R.id.mRepeatBT);
        mNavigation = findViewById(R.id.mNavigation);
        mNavList = findViewById(R.id.mNavList);
        mFoldersSV = findViewById(R.id.mFoldersSV);
        mFolders = findViewById(R.id.mFolders);
        mSoundsSV = findViewById(R.id.mSoundsSV);
        mSounds = findViewById(R.id.mSounds);
        mBasicMessages = findViewById(R.id.mBasicMessages);
        mLoading = findViewById(R.id.mLoading);

        notFirst = false;
        direction = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
        pasteURL = Environment.getExternalStorageDirectory().toString() + "/" + app;
        updates = new File(pasteURL);
        if (updates.exists()) updates.delete();
        try {
            version = Mahdi.this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        setSupportActionBar(mToolbar);
        mToolbarAB = getSupportActionBar();
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(Mahdi.this, R.drawable.overflow_2_blue));
        for (int g = 0; g < mToolbar.getChildCount(); g++) {
            View getTitle = mToolbar.getChildAt(g);
            if (getTitle.getClass().getName().equalsIgnoreCase("androidx.appcompat.widget.AppCompatTextView") &&
                    ((TextView) getTitle).getText().toString().equals(getResources().getString(R.string.app_name))) {
                mTBTitle = ((TextView) getTitle);
                mTBTitle.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            }
        }

        enableButtons(false);
        mShuffleBT.setAlpha(mSBsDisabled);
        if (sp.contains("shuffle")) shuffle(sp.getBoolean("shuffle", false));
        mRepeatBT.setAlpha(mSBsDisabled);
        if (sp.contains("repeat")) repeat(sp.getInt("repeat", 0));

        if (Amin.exists) {
            reCreation = true;
            if (!serviceBound) {
                Intent playerIntent = new Intent(Mahdi.this, Amin.class);
                bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            StorageUtil storage = new StorageUtil(this);
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();
            audioListIndex = storage.loadAudioListIndex();
            updateDescription(true);
            enableButtons(true);
            try {
                playing(Amin.mp.isPlaying());
            } catch (IllegalStateException | NullPointerException ignored) {
                couldNotGainPlayingDataOnReCreation = true;
            }
        }

        ValueAnimator anLoad = VA(mMotor, "translationX", loadDur1, 1f, 0f);
        anLoad.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!reCreation) {
                    if (isExternalStorageReadable()) {
                        if (ContextCompat.checkSelfPermission(Mahdi.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(Mahdi.this,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE))
                                Toast.makeText(Mahdi.this, R.string.accessStorage, Toast.LENGTH_LONG).show();
                            else ActivityCompat.requestPermissions(Mahdi.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, permReadExerStor);
                        } else start(true);
                    } else start(false);
                }

                ConstraintLayout.LayoutParams mSeekBarReplicaLP = (ConstraintLayout.LayoutParams) mSeekBarReplica.getLayoutParams();
                int sbrMar = (int) (mSeekBar.getHeight() / 2.7);
                mSeekBarReplicaLP.leftMargin = sbrMar;
                mSeekBarReplicaLP.rightMargin = sbrMar;
                mSeekBarReplica.setLayoutParams(mSeekBarReplicaLP);
            }
        });

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
        for (int b = 0; b < backfulLists.length; b++) {
            ConstraintLayout clBack = (ConstraintLayout) ((LinearLayout) navLists[backfulLists[b]].getChildAt(0)).getChildAt(0);
            final int B = b;
            clBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeNavList(backfulLists[B] - 1);
                }
            });
        }

        AminHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                playing(inputMessage.what == 1);
                if (inputMessage.obj != null) {
                    enableButtons(false);
                    audioList = null;
                    audioListIndex = -1;
                    audioIndex = -1;
                    highlightItem(0, -1);
                    highlightItem(1, -1);
                    updateDescription(false);
                    mSeekBar.setProgress(0);
                }
            }
        };
        changeHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                audioIndex = inputMessage.what;
                if (audioListIndex == currentOpenedAudioList) highlightItem(1, audioIndex);
                updateDescription(true);
            }
        };

        //Improvements for class DownloadFileFromURL
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notFirst) {
            if (isNavOpen) openNavList(false);
            mLoading(true);
            refresh();
        } else if (reCreation) refresh();
        notFirst = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy(false);
    }

    @Override
    public void onBackPressed() {
        if (!isNavOpen) {
            if (!playing) {
                if (exiting) {
                    if (Amin.mp == null) exit();
                    else destroy(true);
                } else {
                    exiting = true;
                    Toast.makeText(Mahdi.this, getResources().getText(R.string.toExit), Toast.LENGTH_SHORT).show();
                    ValueAnimator anExit = VA(mMotor, "translationY", exitingDelay, 111f, 0f);
                    anExit.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            exiting = false;
                        }
                    });
                }
            } else
                makeBasicMessage(getResources().getString(R.string.audioPlayer), getResources().getString(R.string.onExitWhilePlaying),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                destroy(true);
                            }
                        }, true);
        } else {
            if (openedNav == 0) openNavList(false);
            else changeNavList(0);
        }
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
            case permReadExerStor:
                start(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
            case permWriteExerStor:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new DownloadFileFromURL().execute(cloudFolder + app);
                } else Toast.makeText(Mahdi.this, R.string.noPermWES, Toast.LENGTH_LONG).show();
                break;
        }//case 0: {} FUCKS UP!!!
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_tb, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mtbRefreshList:
                refresh();
                return true;
            case R.id.mtbUpdate:
                if (!checkingFU) {
                    if (isOnline()) {
                        if (version != null) {
                            cfuQueue = Volley.newRequestQueue(Mahdi.this);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, cloudFolder + ver,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            checkingFU = false;
                                            if (response != null) compareTheVersions(response);
                                            else
                                                Toast.makeText(Mahdi.this, R.string.cfuError1, Toast.LENGTH_LONG).show();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    checkingFU = false;
                                    String mess = getResources().getString(R.string.cfuError1);
                                    if (error != null && error.getMessage() != null)
                                        if (!error.getMessage().equals(""))
                                            mess = getResources().getString(R.string.cfuError5) + error.getMessage();
                                    Toast.makeText(Mahdi.this, mess, Toast.LENGTH_LONG).show();
                                }
                            });
                            cfuQueue.add(stringRequest.setTag(cfuTag));
                            checkingFU = true;
                        } else
                            Toast.makeText(Mahdi.this, R.string.cfuError3, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(Mahdi.this, R.string.noInternet, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.mtbAboutUs:
                makeBasicMessage(getResources().getString(R.string.tbAboutUs), getResources().getString(R.string.aboutUs), null, true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == progress_bar_type) {
            dlDialogue = new ProgressDialog(this, R.style.ProgressDialogue1);
            dlDialogue.setMessage(getResources().getString(R.string.updating));
            dlDialogue.setIndeterminate(false);
            dlDialogue.setMax(100);
            dlDialogue.setProgressStyle(ProgressDialog.STYLE_SPINNER);//STYLE_HORIZONTAL
            dlDialogue.setCancelable(false);
            dlDialogue.setCanceledOnTouchOutside(false);
            dlDialogue.show();

            View v = new View(Mahdi.this);
            dlDialogue.addContentView(v, new ViewGroup.LayoutParams(0, 0));
            FrameLayout mainParent = (FrameLayout) v.getParent();
            mainParent.setBackground(null);
            LinearLayout nextParent1 = (LinearLayout) mainParent.getChildAt(0);
            nextParent1.setBackground(null);
            ViewGroup parent = (ViewGroup) mainParent.getParent();
            parent.setBackground(null);
            ViewGroup parentParent = (ViewGroup) parent.getParent();
            parentParent.setBackground(null);
            return dlDialogue;
        }
        return null;
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

    void enableButtons(boolean Do) {
        buttonsEnabled = Do;
        if (Do) {
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (Amin.exists && Amin.mp != null) {
                        if (changingTheSeekBar) {
                            int s = Math.round(((float) Amin.mp.getDuration() / 100f) * (float) seekBar.getProgress());
                            mDur.setText(fixDur(s) + " / " + fixDur(folders.get(audioListIndex).files.get(audioIndex).getDur()));
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    changingTheSeekBar = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (Amin.exists && Amin.mp != null) {
                        try {
                            int s = Math.round(((float) Amin.mp.getDuration() / 100f) * (float) seekBar.getProgress());
                            if (playing) Amin.mp.seekTo(s);
                            else {
                                Amin.resumePosition = s;
                                mDur.setText(fixDur(s) + " / " + fixDur(folders.get(audioListIndex).files.get(audioIndex).getDur()));
                            }
                            heartbeat(mSeekBarReplica, 3.69f, new int[]{666, 179}, 0.55f);
                        } catch (IllegalStateException | NullPointerException e) {
                            if (e.getMessage() != null)
                                Toast.makeText(Mahdi.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    changingTheSeekBar = false;
                }
            });
            mPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (playing) sendToMSession(1);
                    else sendToMSession(0);
                }
            });
            mMoveF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendToMSession(2);
                }
            });
            mMoveB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendToMSession(3);
                }
            });
            mShuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shuffle(!shuffle);
                }
            });
            mRepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    repeat(-1);
                }
            });
        } else {
            mSeekBar.setOnSeekBarChangeListener(null);
            mPlayPause.setOnClickListener(null);
            mMoveF.setOnClickListener(null);
            mMoveB.setOnClickListener(null);
            mShuffle.setOnClickListener(null);
            mRepeat.setOnClickListener(null);
        }
        mSeekBar.setEnabled(Do);
    }

    void controlPlayer() {
        if (Amin.mp != null && playing) {
            try {
                if (!changingTheSeekBar) {
                    mSeekBar.setProgress(Math.round((100f / (float) Amin.mp.getDuration()) * (float) Amin.mp.getCurrentPosition()));
                    mDur.setText(fixDur(Amin.mp.getCurrentPosition()) + " / " + fixDur(Amin.mp.getDuration()));
                }
            } catch (IllegalStateException | NullPointerException e) {
                if (e.getMessage() != null)
                    Toast.makeText(Mahdi.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            reproduction = VA(mMotor, "scaleX", controlPlayerDur, 1.8f, 1f);
            reproduction.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    controlPlayer();
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
                } catch (IllegalStateException | NullPointerException e) {
                    if (e.getMessage() != null)
                        Toast.makeText(Mahdi.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else mDur.setText(R.string.mNormalDur);
        }
    }

    void playAudio() {
        StorageUtil storage = new StorageUtil(getApplicationContext());
        storage.storeAudio(audioList);
        storage.storeAudioIndex(audioIndex);
        storage.storeAudioListIndex(audioListIndex);
        highlightItem(0, audioListIndex);
        if (audioListIndex == currentOpenedAudioList) highlightItem(1, audioIndex);
        updateDescription(true);

        if (!buttonsEnabled) enableButtons(true);
        if (!Amin.exists || !serviceBound) {
            Intent playerIntent = new Intent(Mahdi.this, Amin.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else sendBroadcast(new Intent(Broadcast_PLAY_NEW_AUDIO));
    }

    boolean loadAudio() {
        Uri uri1 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI, uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0", sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor1 = getContentResolver().query(uri1, null, selection, null, sortOrder),
                cursor2 = getContentResolver().query(uri2, null, selection, null, sortOrder);

        //Prepare the folders list
        folders = new ArrayList<>();
        readCursor(cursor1);
        readCursor(cursor2);
        Collections.sort(folders, new sortByName());
        for (int f = 0; f < folders.size(); f++) {
            ArrayList<Audio> files = folders.get(f).files;
            Collections.sort(files, new sortByNameAudio());
            folders.get(f).replaceFiles(files);
        }
        if (folders.size() > 0) folClItemIds = showItems(false, 0);
        else
            makeBasicMessage(getResources().getString(R.string.audioPlayer), getResources().getString(R.string.noSound), null, true);

        //Find the selected file if exists
        boolean plays = false;
        int selected = 0;
        if (getIntent() != null && getIntent().getData() != null) {
            Uri gotUri = getIntent().getData();
            File fSelected = FileUtils.getFile(Mahdi.this, gotUri);

            ArrayList<Audio> audioListGet = null;
            int audioListIndexGet = -1, audioIndexGet = -1;
            for (int f = 0; f < folders.size(); f++)
                if (folders.get(f).path.equals(fSelected.getParent())) {
                    selected = f;
                    audioListGet = folders.get(selected).files;
                    audioListIndexGet = selected;
                }
            for (int a = 0; a < folders.get(selected).files.size(); a++)
                if (folders.get(selected).files.get(a).getData().equals(fSelected.getPath()))
                    audioIndexGet = a;

            plays = audioListGet != null && audioListIndexGet != -1 && audioIndexGet != -1;
            if (!plays) {
                makeBasicMessage(getResources().getString(R.string.audioPlayer),
                        getResources().getString(R.string.unreadable), null, true);
            } else {
                audioList = audioListGet;
                audioListIndex = audioListIndexGet;
                audioIndex = audioIndexGet;
            }
        }

        if (reCreation) {
            if (audioListIndex != -1) highlightItem(0, audioListIndex);
            if (couldNotGainPlayingDataOnReCreation) plays = true;
        }

        mLoading(false);
        return plays;
    }

    void readCursor(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),
                        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                int dur = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                Audio audio = new Audio(data, name, title, album, artist, albumId, dur);

                File file = new File(data);
                String folderPath = file.getParent();
                boolean gotFolder = false;
                for (int f = 0; f < folders.size(); f++) {
                    if (folders.get(f).path.equals(folderPath)) {
                        gotFolder = true;
                        folders.get(f).addFile(audio);
                    }
                }
                if (!gotFolder) {
                    ArrayList<Audio> files = new ArrayList<>();
                    files.add(audio);
                    folders.add(new Folder(folderPath.substring(folderPath.lastIndexOf("/") + 1), folderPath, files));
                }
            }
        }
        if (cursor != null) cursor.close();
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    void makeBasicMessage(String title, String message, View.OnClickListener quesClick, boolean cancellabe) {
        mBasicMessages.setVisibility(View.VISIBLE);
        mBasicMessages.removeAllViews();
        LayoutInflater.from(Mahdi.this).inflate(R.layout.basic_message_1, mBasicMessages);
        ConstraintLayout mbg = (ConstraintLayout) mBasicMessages.getChildAt(mBasicMessages.getChildCount() - 1);
        ConstraintLayout m = (ConstraintLayout) mbg.getChildAt(0);
        LinearLayout ll = (LinearLayout) m.getChildAt(0);
        TextView Title = (TextView) ((LinearLayout) ((ConstraintLayout) ll.getChildAt(0)).getChildAt(0)).getChildAt(1),
                body = (TextView) ((ConstraintLayout) ll.getChildAt(1)).getChildAt(0);
        Title.setText(title);
        body.setText(message);

        int buttonVGId = View.generateViewId();
        ConstraintLayout buttonVG = (ConstraintLayout) ll.getChildAt(2);
        buttonVG.setId(buttonVGId);
        TextView button = new TextView(new ContextThemeWrapper(Mahdi.this, R.style.BMButton1), null, 0);
        ConstraintLayout.LayoutParams buttonLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLP.topToTop = buttonVGId;
        final ConstraintLayout MBG = mbg;
        if (quesClick == null) {
            buttonLP.endToEnd = buttonVGId;
            buttonLP.startToStart = buttonVGId;
            button.setText(R.string.ok1);
        } else {
            int buttonYesId = View.generateViewId();
            buttonLP.endToStart = buttonYesId;
            buttonLP.setMarginEnd(20);
            button.setText(R.string.no1);

            TextView buttonYes = new TextView(new ContextThemeWrapper(Mahdi.this, R.style.BMButton1), null, 0);
            ConstraintLayout.LayoutParams buttonYesLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonYesLP.endToEnd = buttonVGId;
            buttonYesLP.topToTop = buttonVGId;
            buttonYes.setText(R.string.yes1);
            buttonYes.setId(buttonYesId);
            buttonVG.addView(buttonYes, buttonYesLP);
            buttonYes.setOnClickListener(quesClick);
            buttonYes.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mBasicMessages.removeView(MBG);
                    mBasicMessages.setVisibility(View.GONE);
                    return false;//"true" disables OnClickListener
                }
            });
        }
        buttonVG.addView(button, buttonLP);

        final int dur = 111;
        OA(mbg, "alpha", 1f, dur);
        View.OnClickListener cancel = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator a = OA(MBG, "alpha", 0f, dur);
                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBasicMessages.removeView(MBG);
                        mBasicMessages.setVisibility(View.GONE);
                    }
                });
            }
        };
        button.setOnClickListener(cancel);
        if (cancellabe) mBasicMessages.setOnClickListener(cancel);
        else mBasicMessages.setOnClickListener(doNothing);
        m.setOnClickListener(doNothing);
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
                                downloadUpdates();
                            }
                        }, false);
            } else if (newVer == curVer)
                makeBasicMessage(title, getResources().getString(R.string.upToDate) + " (" + version + ")", null, true);
            else
                makeBasicMessage(title, getResources().getString(R.string.cfuError4) + "\n\n" +
                        getResources().getString(R.string.cfuReceived) + "\n" + response, null, true);
        }
    }

    void downloadUpdates() {
        if (updates.exists()) updates.delete();
        if (isExternalStorageWritable()) {
            if (ContextCompat.checkSelfPermission(Mahdi.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(Mahdi.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(Mahdi.this, R.string.updateExpln, Toast.LENGTH_LONG).show();
                } else //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    ActivityCompat.requestPermissions(Mahdi.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permWriteExerStor);
            } else new DownloadFileFromURL().execute(cloudFolder + app);
        } else Toast.makeText(Mahdi.this, R.string.noPermWES, Toast.LENGTH_LONG).show();
    }

    void openNavList(boolean open) {
        isNavOpen = open;
        float fListDest = 0f - ((float) mNavList.getWidth() + 11f);
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
                    super.onAnimationEnd(animation);
                    mNavigation.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    void changeNavList(int n) {
        openedNav = n;
        for (int l = 0; l < navLists.length; l++)
            if (navLists[l].getVisibility() == View.VISIBLE)
                navLists[l].setVisibility(View.INVISIBLE);
        navLists[n].setVisibility(View.VISIBLE);

        ValueAnimator anSTS = VA(mMotor, "translationX", IMLoadDur1, 1f, 0f);
        anSTS.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scrollToSelected();
            }
        });
    }

    ArrayList<Integer> showItems(boolean isAudio, int folder) {
        ArrayList<Integer> itemIds = new ArrayList<>();
        int length = folders.size(), ivItemSrc;
        LinearLayout llList;
        if (isAudio) {
            llList = mSounds;
            length = folders.get(folder).files.size();
            if (llList.getChildCount() > 1)
                for (int i = llList.getChildCount() - 1; i > 0; i--) llList.removeViewAt(i);
            ivItemSrc = R.drawable.sound_1_blue;
        } else {
            llList = mFolders;
            if (llList.getChildCount() > 0) llList.removeAllViews();
            ivItemSrc = R.drawable.folder_1_blue;
        }

        ArrayList<Integer> iconIds = new ArrayList<>();
        for (int f = 0; f < length; f++) {
            String tvItemT;
            //int ivItemSrc;
            View.OnClickListener itemClick;
            View.OnLongClickListener itemLongClick;
            final int F = f, FOLDER = folder;
            if (isAudio) {
                final Audio aud = folders.get(folder).files.get(f);
                tvItemT = aud.getName();
                //ivItemSrc = R.drawable.sound_1_blue;
                itemClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        audioList = folders.get(FOLDER).files;
                        audioListIndex = FOLDER;
                        audioIndex = F;
                        playAudio();
                    }
                };
                itemLongClick = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        String beginning = "- ", text = beginning + getResources().getString(R.string.MIName) + aud.getName() + "\n" +
                                beginning + getResources().getString(R.string.MITitle) + aud.getTitle() + "\n" +
                                beginning + getResources().getString(R.string.MIAlbum) + aud.getAlbum() + "\n" +
                                beginning + getResources().getString(R.string.MIArtist) + aud.getArtist() + "\n" +
                                beginning + getResources().getString(R.string.MIDur) + fixDur(aud.getDur());
                        makeBasicMessage(getResources().getString(R.string.musicInfoTitle), text, null, true);
                        return true;//if "false", normal OnClick works too, if not, only OnLongClick works.
                    }
                };
            } else {
                Folder fol = folders.get(f);
                tvItemT = fol.name;//fol.files.size()
                //ivItemSrc = R.drawable.folder_1_blue;
                itemClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentOpenedAudioList = F;
                        sndClItemIds = showItems(true, F);
                        changeNavList(1);
                        if (Amin.mp != null && F == audioListIndex) highlightItem(1, audioIndex);
                    }
                };
                itemLongClick = null;
            }

            ConstraintLayout clItem = new ConstraintLayout(
                    new ContextThemeWrapper(Mahdi.this, R.style.mClItem), null, 0);
            LinearLayout.LayoutParams clItemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            itemIds.add(View.generateViewId());
            clItem.setId(itemIds.get(f));
            llList.addView(clItem, clItemLP);
            clItem.setOnClickListener(itemClick);
            clItem.setOnLongClickListener(itemLongClick);

            LinearLayout llItem = new LinearLayout(Mahdi.this);
            ConstraintLayout.LayoutParams llItemLP = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llItemLP.startToStart = itemIds.get(f);
            llItemLP.topToTop = itemIds.get(f);
            llItem.setOrientation(LinearLayout.HORIZONTAL);
            llItem.setWeightSum(5f);
            clItem.addView(llItem, llItemLP);

            ImageView ivItem = new ImageView(new ContextThemeWrapper(Mahdi.this, R.style.mIvItem), null, 0);
            LinearLayout.LayoutParams ivItemLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            //ivItem.setImageResource(ivItemSrc);
            ivItem.setId(View.generateViewId());
            iconIds.add(ivItem.getId());
            llItem.addView(ivItem, ivItemLP);


            LinearLayout llItemContents = new LinearLayout(
                    new ContextThemeWrapper(Mahdi.this, R.style.mLlItemContents), null, 0);
            LinearLayout.LayoutParams llItemContentsLP = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 4f);
            if (isAudio) llItemContentsLP.gravity = Gravity.CENTER_VERTICAL;
            llItemContents.setOrientation(LinearLayout.VERTICAL);
            llItem.addView(llItemContents, llItemContentsLP);

            TextView tvItem = new TextView(new ContextThemeWrapper(Mahdi.this, R.style.mTvItem), null, 0);
            LinearLayout.LayoutParams tvItemLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvItem.setText(tvItemT);
            tvItem.setTextColor(ContextCompat.getColor(Mahdi.this, R.color.mFolTvItem));
            llItemContents.addView(tvItem, tvItemLP);

            if (!isAudio) {
                TextView tvItemDesc = new TextView(new ContextThemeWrapper(Mahdi.this, R.style.mTvItemDesc), null, 0);
                LinearLayout.LayoutParams tvItemDescLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvItemDesc.setText(folderPath(folders.get(f).path));
                tvItemDesc.setTextColor(ContextCompat.getColor(Mahdi.this, R.color.mFolTvItemPath));
                llItemContents.addView(tvItemDesc, tvItemDescLP);
            }
        }
        imageLoader(iconIds, 0, ivItemSrc);
        return itemIds;
    }

    void exit() {
        moveTaskToBack(true);//REQUIRED!
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    void start(boolean go) {
        if (go) {
            if (loadAudio()) playAudio();
        } else {
            Toast.makeText(Mahdi.this, R.string.noPermRES, Toast.LENGTH_LONG).show();
            exit();
        }
    }

    void shuffle(boolean on) {
        shuffle = on;
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("shuffle", shuffle);
        editor.apply();

        if (shuffle) mShuffleBT.setAlpha(1f);
        else {
            mShuffleBT.setAlpha(mSBsDisabled);
            Amin.shuffleHistory = null;
        }
    }

    void repeat(int set) {
        if (set == -1) {
            repeat += 1;
            if (repeat == 3) repeat = 0;
        } else if (set >= 0 && set <= 2) repeat = set;
        else return;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("repeat", repeat);
        editor.apply();

        switch (repeat) {
            case 0:
                mRepeatBT.setAlpha(mSBsDisabled);
                mRepeatBT.setImageResource(R.drawable.repeat_2);
                break;
            case 1:
                mRepeatBT.setAlpha(1f);
                mRepeatBT.setImageResource(R.drawable.repeat_2);
                break;
            case 2:
                mRepeatBT.setAlpha(1f);
                mRepeatBT.setImageResource(R.drawable.repeat_2_once);
                break;
        }
    }

    void highlightItem(int list, int item) {
        ArrayList[] idLists = new ArrayList[]{folClItemIds, sndClItemIds};
        ArrayList<Integer> idList = (ArrayList<Integer>) idLists[list];
        if (idList != null) {
            for (int i = 0; i < idList.size(); i++)
                highlight(false, (ConstraintLayout) findViewById(idList.get(i)));
            if (item != -1)
                highlight(true, (ConstraintLayout) findViewById(idList.get(item)));
        }
    }

    void highlight(boolean Do, ConstraintLayout cl) {
        if (Do) cl.setBackgroundResource(R.drawable.square_1_blue_1_alpha);
        else
            cl.setBackgroundResource(R.drawable.square_1_tp_to_white_alpha_xml);
    }

    void updateDescription(boolean New) {
        if (New) {
            Audio aud = folders.get(audioListIndex).files.get(audioIndex);
            mAlbumArt.setImageBitmap(Amin.getAlbumArt(Mahdi.this, aud.getAlbumId()));
            mToolbar.setSubtitle(aud.getName());
        } else {
            mAlbumArt.setImageBitmap(null);
            mToolbar.setSubtitle("");
        }
    }

    void refresh() {
        loadAudio();
        if (openedNav == 1) changeNavList(0);
        if (Amin.mp != null && audioListIndex != -1) highlightItem(0, audioListIndex);
        if (Amin.mp != null && audioListIndex != -1) highlightItem(0, audioListIndex);
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
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException ignored) {
            return path;
        }
    }

    void scrollToSelected() {
        if (Amin.mp == null || (openedNav == 1 && audioListIndex != currentOpenedAudioList)) return;
        ArrayList<Integer> ids;
        int index;
        if (openedNav == 0) {
            ids = folClItemIds;
            index = audioListIndex;
        } else {
            ids = sndClItemIds;
            index = audioIndex;
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
                ObjectAnimator l = OA(mLoading, "translationX", 0f - (float) (displayMetrics.widthPixels + 22), mLoadingDur);
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
            PendingIntent pi = Amin.playbackAction(Mahdi.this, act);
            if (pi != null) try {
                pi.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    void imageLoader(final ArrayList<Integer> list, final int i, final int src) {
        try {
            ImageView v = (ImageView) findViewById(list.get(i));
            v.setImageResource(src);
        } catch (Exception ignored) {
        }
        if ((i + 1) < list.size()) {
            ValueAnimator a = VA(mMotor, "scaleY", imageLoader, 2f, 1f);
            a.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imageLoader(list, i + 1, src);
                }
            });
        }
    }

    public static Object changeColor(Context c, int drw, int colour, boolean toDrw) {
        Bitmap bmIcon = BitmapFactory.decodeResource(c.getResources(), drw).copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(c, colour),
                PorterDuff.Mode.SRC_IN));
        Canvas canvas = new Canvas(bmIcon);
        canvas.drawBitmap(bmIcon, 0, 0, paint);
        if (toDrw) return new BitmapDrawable(c.getResources(), bmIcon);
        else return bmIcon;
    }

    void heartbeat(View v, float Scale, int[] durs, float alpha) {
        ConstraintLayout vp = (ConstraintLayout) v.getParent();
        final ImageView effect = new ImageView(Mahdi.this);
        ConstraintLayout.LayoutParams effectLP = new ConstraintLayout.LayoutParams(v.getHeight(), v.getHeight());
        effectLP.leftToLeft = v.getId();
        effectLP.topToTop = v.getId();
        effectLP.rightToRight = v.getId();
        effectLP.bottomToBottom = v.getId();
        effectLP.horizontalBias = ((float) mSeekBar.getProgress()) / 100f;
        effect.setImageResource(R.drawable.activated_animation_1_white);
        effect.setAlpha(alpha);
        vp.addView(effect, effectLP);

        ValueAnimator scale1 = ObjectAnimator.ofFloat(effect, "scaleX", Scale).setDuration(durs[0]);
        ValueAnimator scale2 = ObjectAnimator.ofFloat(effect, "scaleY", Scale).setDuration(durs[0]);
        ValueAnimator scale3 = ObjectAnimator.ofFloat(effect, "alpha", 0f).setDuration(durs[1]);
        scale3.setStartDelay(durs[0] - durs[1]);
        final AnimatorSet scale = new AnimatorSet();
        scale.playTogether(scale1, scale2, scale3);
        final ImageView effectGet = effect;
        final ConstraintLayout VP = vp;
        scale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                VP.removeView(effectGet);
            }
        });
        scale.start();
    }

    void destroy(boolean delayedExit) {
        if (Amin.exists) {
            if (AminHandler != null) AminHandler.obtainMessage(0, 0).sendToTarget();
            if (Amin.hDestroyer != null) Amin.hDestroyer.obtainMessage().sendToTarget();
            if (serviceBound) {
                unbindService(serviceConnection);//service is still active
                if (player != null) player.stopSelf();//for precaution
            }
        }
        playing = false;
        AminHandler = null;
        changeHandler = null;

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


    static class Folder {
        String name, path;
        ArrayList<Audio> files;

        Folder(String name, String path, ArrayList<Audio> files) {
            this.name = name;//IK HOU VAN YE!!!
            this.path = path;
            this.files = files;
        }

        void addFile(Audio audio) {
            this.files.add(audio);
        }

        void replaceFiles(ArrayList<Audio> files) {
            this.files = files;
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
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
                OutputStream output = new FileOutputStream(pasteURL);

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
            } catch (Exception e) {
                if (e.getMessage() != null)
                    Toast.makeText(Mahdi.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            dlDialogue.setProgress(Integer.parseInt(progress[0]));
            dlDialogue.setMessage(getResources().getString(R.string.updating) + " (" + progress[0] + "%)");
        }

        @Override
        protected void onPostExecute(String file_url) {
            dismissDialog(progress_bar_type);

            if (updates.exists()) {
                Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= 24) {
                    promptInstall.setDataAndType(FileProvider.getUriForFile(Mahdi.this,
                            BuildConfig.APPLICATION_ID + ".provider", updates), "application/vnd.android.package-archive");
                    List<ResolveInfo> resInfoList = Mahdi.this.getPackageManager().queryIntentActivities(promptInstall,
                            PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        Mahdi.this.grantUriPermission(resolveInfo.activityInfo.packageName, Uri.parse(updates.getPath()),
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    promptInstall.setDataAndType(Uri.fromFile(updates), "application/vnd.android.package-archive");
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(promptInstall);
            } else Toast.makeText(Mahdi.this, R.string.cfuError2, Toast.LENGTH_LONG).show();
        }

    }

    class sortByName implements Comparator<Folder> {
        public int compare(Folder a, Folder b) {
            return a.name.compareTo(b.name);
        }
    }

    class sortByNameAudio implements Comparator<Audio> {
        public int compare(Audio a, Audio b) {
            return a.getName().compareTo(b.getName());
        }
    }
}
