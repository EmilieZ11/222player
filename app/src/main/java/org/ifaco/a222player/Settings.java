package org.ifaco.a222player;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    ConstraintLayout sContainer, sBody, sBGShower;
    View sMotor;
    Toolbar sToolbar;
    ScrollView sSVApp, sSVAudio, sSVVideo;
    LinearLayout sLLApp, sLLAudio, sLLVideo;
    FloatingActionButton sFAB;
    BottomNavigationView sBNV;
    ImageView sBGShowerIV, sBGShowerClose;

    //Sheet APP
    LinearLayout sWallpapers, sColourPrimaries, sCP1LL, sCP2LL, sArrangement;
    ConstraintLayout sCP1RL, sCP2RL;
    TextView sCP1TV, sCP1DEF, sCP2TV, sCP2DEF, sArngFTV, sArngMTV, sFontTV;
    SeekBar sCP1SB1, sCP1SB2, sCP1SB3, sCP2SB1, sCP2SB2, sCP2SB3, sFontSB;
    Spinner sArngFSp, sArngMSp, sFontSP;
    ImageView sArngFSw, sArngFSpMark, sArngMSw, sArngMSpMark;
    //Sheet AUDIO
    //Sheet VIDEO

    ActionBar sToolbarAB;
    SharedPreferences sp;
    boolean dirLtr = true, night = false, fArrangementAsc = true, mArrangementAsc = true, gotSheetFont = false, sFABOn = false;
    TextView mTBTitle, mTBSubtitle;
    ScrollView[] sheets;
    LinearLayout[] llSheets;
    boolean[] wpBooleans = new boolean[]{true, true, true, false};
    ArrayList<Integer> sWPBGOffIds, sWPCBClIds, sWPCBIds;
    ArrayList<Bitmap> wallpapers;
    int colorPrimary = 0, textColorPrimary = 0, fArrangement = 0, mArrangement = 0, defColorPrimary = 0, font = 3, sheet = 0, sFABDur = 79;
    final int stbNight = 0, cpBGAlpha = 179, arngSwDur = 92;
    final int[] RGB = {Color.RED, Color.GREEN, Color.BLUE};
    float wpRatio = 1f, fontRatio = 1f;
    Context c;
    ValueAnimator arngAscAnim;
    DisplayMetrics dm = new DisplayMetrics();
    String[][] arngRadios;
    float[] fontSizes;
    String[] fonts;
    String sFTVP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        sContainer = findViewById(R.id.sContainer);
        sMotor = findViewById(R.id.sMotor);
        sToolbar = findViewById(R.id.sToolbar);
        sBody = findViewById(R.id.sBody);
        sSVApp = (ScrollView) sBody.getChildAt(0);
        sLLApp = (LinearLayout) sSVApp.getChildAt(0);
        sSVAudio = (ScrollView) sBody.getChildAt(1);
        sLLAudio = (LinearLayout) sSVAudio.getChildAt(0);
        sSVVideo = (ScrollView) sBody.getChildAt(2);
        sLLVideo = (LinearLayout) sSVVideo.getChildAt(0);
        sFAB = findViewById(R.id.sFAB);
        sBNV = findViewById(R.id.sBNV);
        sBGShower = findViewById(R.id.sBGShower);
        sBGShowerIV = (ImageView) sBGShower.getChildAt(0);
        sBGShowerClose = (ImageView) sBGShower.getChildAt(1);

        //Sheet APP
        sWallpapers = findViewById(R.id.sWallpapers);
        sColourPrimaries = findViewById(R.id.sColourPrimaries);
        sCP1RL = (ConstraintLayout) sColourPrimaries.getChildAt(0);
        sCP1TV = (TextView) sCP1RL.getChildAt(0);
        sCP1DEF = findViewById(R.id.sCP1DEF);
        sCP1LL = (LinearLayout) sColourPrimaries.getChildAt(1);
        sCP1SB1 = (SeekBar) sCP1LL.getChildAt(0);
        sCP1SB2 = (SeekBar) sCP1LL.getChildAt(1);
        sCP1SB3 = (SeekBar) sCP1LL.getChildAt(2);
        sCP2RL = (ConstraintLayout) sColourPrimaries.getChildAt(2);
        sCP2TV = (TextView) sCP2RL.getChildAt(0);
        sCP2DEF = findViewById(R.id.sCP2DEF);
        sCP2LL = (LinearLayout) sColourPrimaries.getChildAt(3);
        sCP2SB1 = (SeekBar) sCP2LL.getChildAt(0);
        sCP2SB2 = (SeekBar) sCP2LL.getChildAt(1);
        sCP2SB3 = (SeekBar) sCP2LL.getChildAt(2);
        sArrangement = findViewById(R.id.sArrangement);
        sArngFTV = findViewById(R.id.sArngFTV);
        sArngFSp = findViewById(R.id.sArngFSp);
        sArngFSw = findViewById(R.id.sArngFSw);
        sArngFSpMark = (ImageView) ((ConstraintLayout) sArrangement.getChildAt(0)).getChildAt(3);
        sArngMTV = findViewById(R.id.sArngMTV);
        sArngMSp = findViewById(R.id.sArngMSp);
        sArngMSw = findViewById(R.id.sArngMSw);
        sArngMSpMark = (ImageView) ((ConstraintLayout) sArrangement.getChildAt(1)).getChildAt(3);
        sFontSP = findViewById(R.id.sFontSP);
        sFontTV = findViewById(R.id.sFontTV);
        sFontSB = findViewById(R.id.sFontSB);
        //Sheet AUDIO
        //Sheet Video

        c = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(c);
        for (int rtl = 0; rtl < Mahdi.supportedRtlLangs.length; rtl++)
            if (Locale.getDefault().getLanguage().equals(Mahdi.supportedRtlLangs[rtl])) {
                sContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                dirLtr = false;
            }
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        sheets = new ScrollView[]{sSVApp, sSVAudio, sSVVideo};
        llSheets = new LinearLayout[]{sLLApp, sLLAudio, sLLVideo};
        if (sp.contains("sNight")) night = sp.getBoolean("sNight", false);
        if (sp.contains("fArrangementAsc"))
            fArrangementAsc = sp.getBoolean("fArrangementAsc", true);
        if (sp.contains("mArrangementAsc"))
            mArrangementAsc = sp.getBoolean("mArrangementAsc", true);
        if (sp.contains("fArrangement")) fArrangement = sp.getInt("fArrangement", 0);
        if (sp.contains("mArrangement")) mArrangement = sp.getInt("mArrangement", 0);
        colorPrimary = ContextCompat.getColor(c, R.color.colorPrimary);
        defColorPrimary = colorPrimary;
        if (sp.contains("colorPrimary")) colorPrimary = sp.getInt("colorPrimary", colorPrimary);
        textColorPrimary = ContextCompat.getColor(c, R.color.textColorPrimary);
        if (sp.contains("textColorPrimary"))
            textColorPrimary = sp.getInt("textColorPrimary", textColorPrimary);
        arngRadios = new String[][]{getResources().getStringArray(R.array.sFRadios), getResources().getStringArray(R.array.sMRadios)};
        if (sp.contains("font")) font = sp.getInt("font", font);
        if (sp.contains("fontRatio")) fontRatio = sp.getFloat("fontRatio", fontRatio);
        fonts = getResources().getStringArray(R.array.sFonts);
        fontSizes = new float[]{15f, sCP1TV.getTextSize() / dm.density, sCP1DEF.getTextSize() / dm.density};

        ValueAnimator anLoad = Mahdi.VA(sMotor, "translationX", Mahdi.loadDur1, 1f, 0f);
        anLoad.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                updateColorPrimary();
                updateTextColorPrimary();
                if (night) night(true);
                updateFonts(true, fontRatio != 1f, true);
            }
        });

        // Toolbar
        if (colorPrimary != defColorPrimary) sToolbar.setPopupTheme(R.style.TBPopupTheme1CngCP);
        setSupportActionBar(sToolbar);
        sToolbarAB = getSupportActionBar();
        for (int g = 0; g < sToolbar.getChildCount(); g++) {
            View getTitle = sToolbar.getChildAt(g);
            if (getTitle.getClass().getName().equalsIgnoreCase("androidx.appcompat.widget.AppCompatTextView")) {
                if (((TextView) getTitle).getText().toString().equals(getResources().getString(R.string.app_name))) {
                    mTBTitle = ((TextView) getTitle);
                    mTBTitle.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
                } else if (((TextView) getTitle).getText().toString().equals(getResources().getString(R.string.tbSettings)))
                    mTBSubtitle = ((TextView) getTitle);
            }

        }
        Drawable ovIcon = (Drawable) Mahdi.resizeByRatio(c, ContextCompat.getDrawable(c, R.drawable.overflow_3_blue),
                Mahdi.navBtnRatio, true);
        if (ovIcon != null) sToolbar.setOverflowIcon(ovIcon);
        if (!dirLtr)
            sToolbar.setNavigationIcon((Drawable) rotateDrawable(sToolbar.getNavigationIcon(), 180, true));
        sToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c, Mahdi.class));
            }
        });

        // Floating Action Button
        Mahdi.OA(sFAB, "translationY", 555f, sFABDur);
        for (int s = 0; s < sheets.length; s++)
            sheets[s].getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    sFAB();
                }
            });
        sFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheets[sheet].scrollTo(0, 0);
            }
        });

        // Botton Navigation View
        updateBNVColours();
        sBNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sbnvApp:
                        bnvSelectItem(0);
                        return true;
                    case R.id.sbnvAudio:
                        bnvSelectItem(1);
                        return true;
                    case R.id.sbnvVideo:
                        bnvSelectItem(2);
                        return true;
                }
                return false;
            }
        });


        // Set colour filter on the sSectTIVs
        for (int l = 0; l < llSheets.length; l++) {
            for (int s = 0; s < llSheets[l].getChildCount(); s++) {
                LinearLayout sect = (LinearLayout) llSheets[l].getChildAt(s);
                ImageView iv = (ImageView) ((LinearLayout) sect.getChildAt(0)).getChildAt(0);
                iv.setColorFilter(colorPrimary);
                if (!dirLtr) iv.setRotation(180f);
                if (!gotSheetFont) {
                    TextView tv = (TextView) ((LinearLayout) sect.getChildAt(0)).getChildAt(1);
                    fontSizes[0] = tv.getTextSize() / dm.density;
                    gotSheetFont = true;
                }
            }
        }

        // Handle wallpapers
        if (sp.contains("wpBooleans")) {
            String[] split = sp.getString("wpBooleans", "11100000").split("");
            wpBooleans = new boolean[Mahdi.resWallpapers.length + Mahdi.adtWPMax];
            for (int b = 0; b < wpBooleans.length; b++) {
                if (b < split.length - 1) wpBooleans[b] = split[b + 1].equals("1");
            }
        }
        wallpapers = new ArrayList<>();
        for (int b = 0; b < Mahdi.resWallpapers.length; b++)
            wallpapers.add(BitmapFactory.decodeResource(getResources(), Mahdi.resWallpapers[b]));
        for (int a = 1; a < Mahdi.adtWPMax; a++)
            if (sp.contains(Mahdi.adtWP + a)) {
                Bitmap got = BitmapFactory.decodeFile(sp.getString(Mahdi.adtWP + a, ""));
                if (got != null) wallpapers.add(got);
            }
        sWPBGOffIds = new ArrayList<>();
        sWPCBClIds = new ArrayList<>();
        sWPCBIds = new ArrayList<>();
        for (int w = 0; w < wallpapers.size(); w++) {
            LayoutInflater.from(c).inflate(R.layout.s_sect_wallpaper, sWallpapers);

            ConstraintLayout sWP = (ConstraintLayout) sWallpapers.getChildAt(sWallpapers.getChildCount() - 1),
                    sWPOverlay = (ConstraintLayout) sWP.getChildAt(1), sWPCBCl = (ConstraintLayout) sWPOverlay.getChildAt(0);
            View sWPBGOff = sWP.getChildAt(0);
            CheckBox sWPCB = (CheckBox) sWPCBCl.getChildAt(0);
            ImageView sWPIV = (ImageView) sWPOverlay.getChildAt(1);

            if (wpBooleans[w]) sWPBGOff.setVisibility(View.VISIBLE);
            sWPBGOff.setId(View.generateViewId());
            sWPBGOffIds.add(sWPBGOff.getId());
            sWPBGOff.setBackgroundColor(colorPrimary);

            sWPCBCl.setId(View.generateViewId());
            sWPCBClIds.add(sWPCBCl.getId());
            sWPCB.setChecked(wpBooleans[w]);
            sWPCB.setId(View.generateViewId());
            sWPCBIds.add(sWPCB.getId());
            final int W = w;
            sWPCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int checked = 0;
                    for (int c = 0; c < wpBooleans.length; c++) if (wpBooleans[c]) checked += 1;
                    if (checked == 1 && !b) {
                        ((CheckBox) findViewById(sWPCBIds.get(W))).setChecked(true);
                        Toast.makeText(c, R.string.sAtLeastOneWP, Toast.LENGTH_SHORT).show();
                    } else checkWP(W, b);
                }
            });
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {colorPrimary, colorPrimary};
            CompoundButtonCompat.setButtonTintList(sWPCB, new ColorStateList(states, colors));

            Bitmap wp = wallpapers.get(w);
            if (dm.density < 2f) wpRatio = dm.density * 0.5f;
            int Wid = (int) (wp.getWidth() * wpRatio), Hig = (int) (wp.getHeight() * wpRatio);
            Bitmap overlay = Bitmap.createBitmap(Wid, Hig, wp.getConfig());
            Canvas canvas = new Canvas(overlay);
            Matrix amin = new Matrix();
            amin.setScale(wpRatio, wpRatio, 0f, 0f);
            canvas.drawBitmap(wp, amin, null);
            sWPIV.setImageBitmap(overlay);

            ConstraintLayout.LayoutParams sWPIVLP = (ConstraintLayout.LayoutParams) sWPIV.getLayoutParams();
            sWPIVLP.width = 0;
            sWPIVLP.startToEnd = sWPCBCl.getId();
            sWPIV.setLayoutParams(sWPIVLP);
            sWPIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sBGShowerIV.setImageBitmap(wallpapers.get(W));
                    sBGShower.setVisibility(View.VISIBLE);
                }
            });
        }
        View.OnClickListener close = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sBGShower.setVisibility(View.GONE);
                sBGShowerIV.setImageBitmap(null);
            }
        };
        sBGShowerClose.setOnClickListener(close);
        sBGShowerIV.setOnClickListener(close);//Mahdi.doNothing

        setClrSeekBars(new SeekBar[]{sCP1SB1, sCP1SB2, sCP1SB3}, 0);
        setClrSeekBars(new SeekBar[]{sCP2SB1, sCP2SB2, sCP2SB3}, 1);
        sCP1DEF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newColourPrimary(ContextCompat.getColor(c, R.color.colorPrimary));
            }
        });
        sCP2DEF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newTextColourPrimary(ContextCompat.getColor(c, R.color.textColorPrimary));
            }
        });

        //Handle Arrangement Settings
        int HASStyle = R.layout.s_arng_spn_dd;
        if (night) HASStyle = R.layout.s_arng_spn_dd_n;
        handleArngSpinners(HASStyle);
        ImageView[] swchs = {sArngFSw, sArngMSw};
        for (int sw = 0; sw < swchs.length; sw++) {
            Drawable swBG = swchs[sw].getBackground();
            if (swBG != null) {
                swBG.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
                swchs[sw].setBackground(swBG);
            }
            swchs[sw].setColorFilter(textColorPrimary);
            final int SW = sw;
            swchs[sw].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sp.edit();
                    if (SW == 0) {
                        fArrangementAsc = !fArrangementAsc;
                        toastAscDsc(fArrangementAsc);
                        editor.putBoolean("fArrangementAsc", fArrangementAsc);
                    } else {
                        mArrangementAsc = !mArrangementAsc;
                        toastAscDsc(mArrangementAsc);
                        editor.putBoolean("mArrangementAsc", mArrangementAsc);
                    }
                    editor.apply();
                    updateArngSws();
                }
            });
        }
        updateArngSws();

        //Handle Fonts
        handleFontSpinner(HASStyle);
        sFTVP = getResources().getString(R.string.sFontTV);
        sFontSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sFontTV.setText((int) (50 + (i * 5)) + sFTVP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float percent = 50f + ((float) seekBar.getProgress() * 5f);
                fontRatio = percent / 100f;
                SharedPreferences.Editor editor = sp.edit();
                editor.putFloat("fontRatio", fontRatio);
                editor.apply();
                updateFonts(false, true, false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(c, Mahdi.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_tb, menu);
        menu.getItem(stbNight).setChecked(night);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stbNight:
                night(!night);
                item.setChecked(night);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void bnvSelectItem(int item) {
        sheet = item;
        for (int s = 0; s < sheets.length; s++) sheets[s].setVisibility(View.GONE);
        sheets[item].setVisibility(View.VISIBLE);
        sFAB();
    }

    void checkWP(int W, boolean b) {
        wpBooleans[W] = b;
        SharedPreferences.Editor editor = sp.edit();
        StringBuilder sb = new StringBuilder();
        for (int s = 0; s < wpBooleans.length; s++) {
            if (wpBooleans[s]) sb.append("1");
            else sb.append("0");
        }
        editor.putString("wpBooleans", sb.toString());
        editor.apply();

        View sWPBGOff = findViewById(sWPBGOffIds.get(W));
        if (b) sWPBGOff.setVisibility(View.VISIBLE);
        else sWPBGOff.setVisibility(View.INVISIBLE);

        CheckBox sWPCB = (CheckBox) findViewById(sWPCBIds.get(W));
        sWPCB.setChecked(b);
    }

    void updateColorPrimary() {
        sCP1LL.setBackgroundColor(Color.argb(cpBGAlpha, Color.red(colorPrimary), Color.green(colorPrimary), Color.blue(colorPrimary)));
        sCP1SB1.setProgress(Color.red(colorPrimary));
        sCP1SB2.setProgress(Color.green(colorPrimary));
        sCP1SB3.setProgress(Color.blue(colorPrimary));
        int colour = colorPrimary;
        if (night) colour = ContextCompat.getColor(c, R.color.sNightOff);

        sToolbar.setTitleTextColor(colour);
        sToolbar.setSubtitleTextColor(colour);
        Drawable navIcon = sToolbar.getNavigationIcon(), ovIcon = sToolbar.getOverflowIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN));
            sToolbar.setNavigationIcon(navIcon);
        }
        if (ovIcon != null) {
            ovIcon.setColorFilter(new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN));
            sToolbar.setOverflowIcon(ovIcon);
        }
        for (int l = 0; l < llSheets.length; l++) {
            for (int c = 0; c < llSheets[l].getChildCount(); c++) {
                LinearLayout sect = (LinearLayout) llSheets[l].getChildAt(c);
                ImageView iv = (ImageView) ((LinearLayout) sect.getChildAt(0)).getChildAt(0);
                iv.setColorFilter(colour);
                TextView tv = (TextView) ((LinearLayout) sect.getChildAt(0)).getChildAt(1);
                tv.setTextColor(colour);
            }
        }
        updateBNVColours();

        TextView[] tvs = {sCP1TV, sCP2TV, sArngFTV, sArngMTV, sFontTV};
        for (TextView tv : tvs) tv.setTextColor(colour);
        View[] cngBGs = {sCP1DEF, sCP2DEF}, cngBGDrws = {sArngFSp, sArngMSp, sFontSP, sArngFSw, sArngMSw, sFAB};
        for (View v : cngBGs) v.setBackgroundColor(colorPrimary);
        for (View v : cngBGDrws) {
            Drawable bg = v.getBackground();
            if (bg != null) {
                bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
                v.setBackground(bg);
            }
        }

        if (sWPCBIds != null) for (int cb = 0; cb < sWPCBIds.size(); cb++) {
            try {
                CheckBox check = findViewById(sWPCBIds.get(cb));
                int[][] cbStates = {{android.R.attr.state_checked}, {}};
                int[] cbColors = {colorPrimary, colorPrimary};
                CompoundButtonCompat.setButtonTintList(check, new ColorStateList(cbStates, cbColors));
            } catch (Exception ignored) {
            }
            try {
                View cbOff = findViewById(sWPBGOffIds.get(cb));
                cbOff.setBackgroundColor(colorPrimary);
            } catch (Exception ignored) {
            }
        }

        Drawable sFSBD = sFontSB.getProgressDrawable(), sFSBT = sFontSB.getThumb();
        if (sFSBD != null) {
            sFSBD.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
            sFontSB.setProgressDrawable(sFSBD);
        }
        if (sFSBT != null) {
            sFSBT.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
            sFontSB.setThumb(sFSBT);
        }
    }

    void updateTextColorPrimary() {
        sCP2LL.setBackgroundColor(Color.argb(cpBGAlpha, Color.red(textColorPrimary), Color.green(textColorPrimary),
                Color.blue(textColorPrimary)));
        sCP2SB1.setProgress(Color.red(textColorPrimary));
        sCP2SB2.setProgress(Color.green(textColorPrimary));
        sCP2SB3.setProgress(Color.blue(textColorPrimary));

        int colour = textColorPrimary;
        if (night) colour = ContextCompat.getColor(c, R.color.sNight);
        updateTCPAndNightViews(colour);
        updateTCPAndNightConverseViews();
    }

    void updateBNVColours() {
        int[][] states = new int[][]{new int[]{android.R.attr.state_checked}, new int[]{-android.R.attr.state_checked}};
        int[] colors = new int[]{colorPrimary, ContextCompat.getColor(c, R.color.sbnvBtnFalse)};
        sBNV.setItemIconTintList(new ColorStateList(states, colors));
        sBNV.setItemTextColor(new ColorStateList(states, colors));
    }

    void night(boolean what) {
        night = what;
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("sNight", night);
        editor.apply();
        updateColorPrimary();

        int nc = textColorPrimary, HASStyle = R.layout.s_arng_spn_dd;
        if (night) {
            nc = ContextCompat.getColor(c, R.color.sNight);
            HASStyle = R.layout.s_arng_spn_dd_n;
        }
        updateTCPAndNightViews(nc);
        updateTCPAndNightConverseViews();
        handleArngSpinners(HASStyle);
        handleFontSpinner(HASStyle);
    }

    void updateTCPAndNightViews(int clr) {
        sContainer.setBackgroundColor(clr);
        sToolbar.setBackgroundColor(clr);
        sBNV.setBackgroundColor(clr);
    }

    void updateTCPAndNightConverseViews() {
        int convrsClr = textColorPrimary;
        if (night) convrsClr = ContextCompat.getColor(c, R.color.sNightOff);
        TextView[] tvs = {sCP1DEF, sCP2DEF};
        for (TextView tv : tvs) tv.setTextColor(convrsClr);
        ImageView[] clrFltrs = {sArngFSw, sArngMSw, sArngFSpMark, sArngMSpMark};
        for (ImageView clrFltr : clrFltrs) clrFltr.setColorFilter(textColorPrimary);
    }

    void setClrSeekBars(final SeekBar[] sikhism, final int which) {
        for (int s = 0; s < sikhism.length; s++) {
            // THUMB DRAWABLE IS UNIQUE; YOU CANNOT COPY IT AND EDIT IT FOR A SINGLE VIEW;...
            // IN THAT CASE ALL OTHER VIEWS WILL RECEIVE THE NEW CHANGES! UNLESS YOU DO IT WITHIN "OnSeekBarChangeListener".
            try {
                Drawable tm = sikhism[s].getThumb();
                if (tm != null) {
                    tm.setColorFilter(new PorterDuffColorFilter(RGB[s], PorterDuff.Mode.SRC_IN));
                    sikhism[s].setThumb(tm);
                }
            } catch (Exception ignored) {
            }
            try {
                Drawable pd = sikhism[s].getProgressDrawable();
                if (pd != null) {
                    pd.setColorFilter(new PorterDuffColorFilter(RGB[s], PorterDuff.Mode.SRC_IN));
                    sikhism[s].setProgressDrawable(pd);
                }
            } catch (Exception ignored) {
            }
            sikhism[s].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    try {
                        ((ViewGroup) seekBar.getParent()).setBackgroundColor(Color.argb(cpBGAlpha,
                                sikhism[0].getProgress(), sikhism[1].getProgress(), sikhism[2].getProgress()));
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (which == 0) newColourPrimary(Color.argb(255,
                            sikhism[0].getProgress(), sikhism[1].getProgress(), sikhism[2].getProgress()));
                    else if (which == 1) newTextColourPrimary(Color.argb(255,
                            sikhism[0].getProgress(), sikhism[1].getProgress(), sikhism[2].getProgress()));
                }
            });
            if (!dirLtr) sikhism[s].setRotation(180f);
        }
    }

    void newColourPrimary(int ColorPrimary) {
        colorPrimary = ColorPrimary;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("colorPrimary", colorPrimary);
        editor.apply();
        updateColorPrimary();
    }

    void newTextColourPrimary(int TextColorPrimary) {
        textColorPrimary = TextColorPrimary;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("textColorPrimary", textColorPrimary);
        editor.apply();
        updateTextColorPrimary();
    }

    Object rotateDrawable(Drawable drw, int rotate, boolean toDrw) {
        try {
            Bitmap bmpNav = ((BitmapDrawable) drw).getBitmap();
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmpNav, bmpNav.getWidth(), bmpNav.getHeight(), true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                    matrix, true);
            if (toDrw) return new BitmapDrawable(getResources(), rotatedBitmap);
            else return rotatedBitmap;
        } catch (Exception ignored) {
        }
        return null;
    }

    void updateArngSws() {
        if (fArrangementAsc && sArngFSw.getRotation() != 270f)
            arngAscAnim = Mahdi.OA(sArngFSw, "rotation", 270f, arngSwDur);
        else if (!fArrangementAsc && sArngFSw.getRotation() != 90f)
            arngAscAnim = Mahdi.OA(sArngFSw, "rotation", 90f, arngSwDur);

        if (mArrangementAsc && sArngMSw.getRotation() != 270f)
            arngAscAnim = Mahdi.OA(sArngMSw, "rotation", 270f, arngSwDur);
        else if (!mArrangementAsc && sArngMSw.getRotation() != 90f)
            arngAscAnim = Mahdi.OA(sArngMSw, "rotation", 90f, arngSwDur);
    }

    void toastAscDsc(Boolean b) {
        String t = "";
        if (b) t = getResources().getString(R.string.sArngAsc);
        else t = getResources().getString(R.string.sArngDsc);
        Toast.makeText(c, t, Toast.LENGTH_SHORT).show();
    }

    void handleArngSpinners(int style) {
        Spinner[] spnrs = {sArngFSp, sArngMSp};
        for (int p = 0; p < spnrs.length; p++) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(c, R.layout.s_arng_spn,
                    new ArrayList<>(Arrays.asList(arngRadios[p])));//items.addAll(Arrays.asList(radios[p]));
            dataAdapter.setDropDownViewResource(style);
            spnrs[p].setAdapter(dataAdapter);

            final int P = p;
            spnrs[p].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SharedPreferences.Editor editor = sp.edit();
                    if (P == 0) {
                        fArrangement = i;
                        editor.putInt("fArrangement", fArrangement);
                    } else {
                        mArrangement = i;
                        editor.putInt("mArrangement", mArrangement);
                    }
                    editor.apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
        sArngFSp.setSelection(fArrangement);
        sArngMSp.setSelection(mArrangement);
    }

    void handleFontSpinner(int style) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(c, R.layout.s_arng_spn,
                new ArrayList<>(Arrays.asList(fonts)));
        dataAdapter.setDropDownViewResource(style);
        sFontSP.setAdapter(dataAdapter);

        sFontSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sp.edit();
                font = i;
                editor.putInt("font", font);
                editor.apply();
                updateFonts(true, false, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        sFontSP.setSelection(font);
    }

    void updateFonts(boolean f, boolean r, boolean sb) {
        if (sb) sFontSB.setProgress((int) ((fontRatio * 100f) - 50) / 5);
        sFontTV.setText((int) (fontRatio * 100f) + sFTVP);
        for (int l = 0; l < llSheets.length; l++) {
            for (int c = 0; c < llSheets[l].getChildCount(); c++) {
                LinearLayout sect = (LinearLayout) llSheets[l].getChildAt(c);
                TextView tv = (TextView) ((LinearLayout) sect.getChildAt(0)).getChildAt(1);
                if (f) tv.setTypeface(Typeface.create(fonts[font], Typeface.BOLD));
                if (r) tv.setTextSize(fontSizes[0] * fontRatio);
                ImageView iv = (ImageView) ((LinearLayout) sect.getChildAt(0)).getChildAt(0);
                LinearLayout.LayoutParams ivLP = (LinearLayout.LayoutParams) iv.getLayoutParams();
                int WH = (int) ((dm.density * 20) * fontRatio);
                ivLP.width = WH;
                ivLP.height = WH;
                iv.setLayoutParams(ivLP);
            }
        }
        TextView[] tvs1 = {sCP1TV, sCP2TV, sArngFTV, sArngMTV, sFontTV}, tvs2 = {sCP1DEF, sCP2DEF};
        for (TextView tv : tvs1) {
            if (f) tv.setTypeface(Typeface.create(fonts[font], Typeface.NORMAL));
            if (r) tv.setTextSize(fontSizes[1] * fontRatio);
        }
        for (TextView tv : tvs2) {
            if (f) tv.setTypeface(Typeface.create(fonts[font], Typeface.BOLD));
            if (r) tv.setTextSize(fontSizes[2] * fontRatio);
        }
        if (f) {
            mTBTitle.setTypeface(Typeface.create(fonts[font], Typeface.BOLD));
            mTBSubtitle.setTypeface(Typeface.create(fonts[font], Typeface.NORMAL));
        }
    }

    void sFAB() {
        if (sheets[sheet].getScrollY() > sheets[sheet].getHeight() / 2) {
            if (!sFABOn) {
                sFABOn = true;
                Mahdi.OA(sFAB, "translationY", 0f, sFABDur);
            }
        } else if (sFABOn) {
            sFABOn = false;
            Mahdi.OA(sFAB, "translationY", (float) (sFAB.getHeight() * 1.79), sFABDur);
        }
    }
}
