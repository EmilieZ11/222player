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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
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

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    ConstraintLayout sContainer, sBody, sBGShower;
    View sMotor, sBGShowerCloseBG, sBGShowerDeleteBG;
    ImageView sShwBG, sBGShowerIV, sBGShowerClose, sBGShowerDelete;
    Toolbar sToolbar;
    ScrollView sSV;
    LinearLayout sLL;
    FloatingActionButton sFAB;

    //Inside sLL
    LinearLayout sWallpapers, sColourPrimaries, sCP1LL, sCP2LL, sArrangement;
    ConstraintLayout sAddWallpaper, sCP1RL, sCP2RL;
    View sAddWPBG;
    TextView sAddWPTV, sCP1TV, sCP1DEF, sCP2TV, sCP2DEF, sArngFTV, sArngMTV, sArngPTV, sFontTV;
    ImageView sAddWPIV, sArngFSw, sArngFSpMark, sArngMSw, sArngMSpMark, sArngPSw, sArngPSpMark;
    SeekBar sCP1SB1, sCP1SB2, sCP1SB3, sCP2SB1, sCP2SB2, sCP2SB3, sFontSB;
    Spinner sArngFSp, sArngMSp, sArngPSp, sFontSP;

    ActionBar sToolbarAB;
    SharedPreferences sp;
    boolean dirLtr = true, fArrangementAsc = true, mArrangementAsc = true, pArrangementAsc = true, gotSheetFont = false, sFABOn = false,
            ccpPreview = false, useDarkText = false, settingWP = false, minimizingWPs = false;
    TextView mTBTitle, mTBSubtitle;
    ArrayList<Integer> sWPBGOffIds, sWPCBClIds, sWPCBIds, wpQueue = new ArrayList<>();
    ArrayList<Wallpaper> wallpapers;
    int CP = 0, TCP = 0, fArrangement = 0, mArrangement = 0, pArrangement = 0, defCP = 0, font = 3, sFABDur = 79, showingWP = -1;
    final int cpBGAlpha = 222, arngSwDur = 92, sectAlpha = 239, sectTAlpha = 255, PICK_IMAGE = 1, setWPDur = 920;
    final int[] RGB = {Color.RED, Color.GREEN, Color.BLUE};
    float fontRatio = 1f;
    Context c;
    ValueAnimator arngAscAnim;
    DisplayMetrics dm = new DisplayMetrics();
    String[][] arngRadios;
    float[] fontSizes;
    String[] fonts;
    String sFTVP;
    Handler hndWP;
    ArrayList<Boolean> wpBooleans = new ArrayList<>();
    final float minimizeWPForBlur = 0.2f, minimizeWP = 0.48f, fBlurWP = 2f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        sContainer = findViewById(R.id.sContainer);
        sMotor = findViewById(R.id.sMotor);
        sShwBG = findViewById(R.id.sShwBG);
        sToolbar = findViewById(R.id.sToolbar);
        sBody = findViewById(R.id.sBody);
        sSV = findViewById(R.id.sSV);
        sLL = findViewById(R.id.sLL);
        sFAB = findViewById(R.id.sFAB);
        sBGShower = findViewById(R.id.sBGShower);
        sBGShowerIV = (ImageView) sBGShower.getChildAt(0);
        sBGShowerCloseBG = sBGShower.getChildAt(1);
        sBGShowerClose = findViewById(R.id.sBGShowerClose);
        sBGShowerDeleteBG = sBGShower.getChildAt(3);
        sBGShowerDelete = findViewById(R.id.sBGShowerDelete);

        sWallpapers = findViewById(R.id.sWallpapers);
        sAddWallpaper = findViewById(R.id.sAddWallpaper);
        sAddWPBG = sAddWallpaper.getChildAt(0);
        sAddWPTV = findViewById(R.id.sAddWPTV);
        sAddWPIV = findViewById(R.id.sAddWPIV);
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
        sArngPTV = findViewById(R.id.sArngPTV);
        sArngPSp = findViewById(R.id.sArngPSp);
        sArngPSw = findViewById(R.id.sArngPSw);
        sArngPSpMark = (ImageView) ((ConstraintLayout) sArrangement.getChildAt(2)).getChildAt(3);
        sFontSP = findViewById(R.id.sFontSP);
        sFontTV = findViewById(R.id.sFontTV);
        sFontSB = findViewById(R.id.sFontSB);

        c = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(c);
        for (int rtl = 0; rtl < Mahdi.supportedRtlLangs.length; rtl++)
            if (Locale.getDefault().getLanguage().equals(Mahdi.supportedRtlLangs[rtl])) {
                sContainer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                dirLtr = false;
            }
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (sp.contains("fArrangementAsc"))
            fArrangementAsc = sp.getBoolean("fArrangementAsc", true);
        if (sp.contains("mArrangementAsc"))
            mArrangementAsc = sp.getBoolean("mArrangementAsc", true);
        if (sp.contains("pArrangementAsc"))
            pArrangementAsc = sp.getBoolean("pArrangementAsc", true);
        if (sp.contains("fArrangement")) fArrangement = sp.getInt("fArrangement", 0);
        if (sp.contains("mArrangement")) mArrangement = sp.getInt("mArrangement", 0);
        if (sp.contains("pArrangement")) pArrangement = sp.getInt("pArrangement", 0);
        CP = ContextCompat.getColor(c, R.color.colorPrimary);
        defCP = CP;
        if (sp.contains(Mahdi.cpTag)) CP = sp.getInt(Mahdi.cpTag, CP);
        TCP = ContextCompat.getColor(c, R.color.textColorPrimary);
        if (sp.contains(Mahdi.tcpTag)) TCP = sp.getInt(Mahdi.tcpTag, TCP);
        arngRadios = new String[][]{getResources().getStringArray(R.array.sFRadios), getResources().getStringArray(R.array.sMRadios),
                getResources().getStringArray(R.array.sPRadios)};
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
                updateFonts(true, fontRatio != 1f, true);

                new WPHandler(0, -1).start();
            }
        });

        // Toolbar
        if (CP != defCP) sToolbar.setPopupTheme(R.style.TBPopupTheme1CngCP);
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
        if (!dirLtr)
            sToolbar.setNavigationIcon((Drawable) rotateDrawable(sToolbar.getNavigationIcon(), 180, true));
        sToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        // Floating Action Button
        Mahdi.OA(sFAB, "translationY", 555f, sFABDur);
        sSV.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                sFAB();
            }
        });
        sFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sSV.scrollTo(0, 0);
            }
        });


        // Set colour filter on the sSectTIVs
        for (int s = 0; s < sLL.getChildCount(); s++) {
            LinearLayout sect = (LinearLayout) sLL.getChildAt(s), sectTitle = (LinearLayout) sect.getChildAt(0);
            ImageView iv = (ImageView) sectTitle.getChildAt(0);
            iv.setColorFilter(CP);
            if (!dirLtr) iv.setRotation(180f);
            if (!gotSheetFont) {
                TextView tv = (TextView) sectTitle.getChildAt(1);
                fontSizes[0] = tv.getTextSize() / dm.density;
                gotSheetFont = true;
            }
            sect.setBackgroundColor(Color.argb(sectAlpha, Color.red(TCP), Color.green(TCP), Color.blue(TCP)));
            sectTitle.setBackgroundColor(Color.argb(sectTAlpha, Color.red(TCP), Color.green(TCP), Color.blue(TCP)));
        }

        // Handle wallpapers
        String[] split = sp.getString("wpBooleans", Mahdi.wpBoolsDef).split("");
        for (int b = 0; b < Mahdi.resWallpapers.length + Mahdi.adtWPMax; b++)
            if (b < split.length - 1) wpBooleans.add(split[b + 1].equals("1"));
        hndWP = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        new WPHandler(1, -1).start();
                        break;
                    case 1:
                        sShwBG.setImageBitmap((Bitmap) message.obj);
                        sWPBGOffIds = new ArrayList<>();
                        sWPCBClIds = new ArrayList<>();
                        sWPCBIds = new ArrayList<>();
                        for (int w = 0; w < wallpapers.size(); w++) addWallpaper(w);
                        setWPs();
                        break;
                    case 2:
                        if (wpQueue.size() > 0) wpQueue.remove(0);
                        minimizingWPs = false;
                        if (sWallpapers.getChildCount() > message.arg1) {
                            ConstraintLayout sWP = (ConstraintLayout) sWallpapers.getChildAt(message.arg1);
                            if (sWP != null) {
                                ImageView sWPIV = (ImageView) ((ConstraintLayout) sWP.getChildAt(1)).getChildAt(1);
                                if (sWPIV != null) sWPIV.setImageBitmap((Bitmap) message.obj);
                            }
                        }
                        break;
                    case 3:
                        sBGShowerIV.setImageBitmap((Bitmap) message.obj);
                        break;
                }
            }
        };
        View.OnClickListener close = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWP(-1);
            }
        };
        sBGShowerClose.setOnClickListener(close);
        sBGShowerIV.setOnClickListener(close);//Mahdi.doNothing
        sBGShowerDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingWP != -1) {
                    removeWP(showingWP);
                    showWP(-1);
                }
            }
        });
        sAddWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        getResources().getString(R.string.selectAPic)), PICK_IMAGE);
            }
        });

        // Handle Change Primary Colours
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
        handleArngSpinners(HASStyle);
        ImageView[] swchs = {sArngFSw, sArngMSw, sArngPSw};
        for (int sw = 0; sw < swchs.length; sw++) {
            Drawable swBG = swchs[sw].getBackground();
            if (swBG != null) {
                swBG.setColorFilter(new PorterDuffColorFilter(CP, PorterDuff.Mode.SRC_IN));
                swchs[sw].setBackground(swBG);
            }
            swchs[sw].setColorFilter(TCP);
            final int SW = sw;
            swchs[sw].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sp.edit();
                    switch (SW) {
                        case 0:
                            fArrangementAsc = !fArrangementAsc;
                            toastAscDsc(fArrangementAsc);
                            editor.putBoolean("fArrangementAsc", fArrangementAsc);
                            break;
                        case 1:
                            mArrangementAsc = !mArrangementAsc;
                            toastAscDsc(mArrangementAsc);
                            editor.putBoolean("mArrangementAsc", mArrangementAsc);
                            break;
                        case 2:
                            pArrangementAsc = !pArrangementAsc;
                            toastAscDsc(pArrangementAsc);
                            editor.putBoolean("pArrangementAsc", pArrangementAsc);
                            break;
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
        goBack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_IMAGE && intent != null && intent.getData() != null)
            try {//or resultCode == RESULT_OK
                String path = FileUtils.getPath(c, intent.getData());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Mahdi.adtWP + ((wallpapers.size() - Mahdi.resWallpapers.length) + 1), path);
                editor.apply();
                wallpapers.add(new Wallpaper(wallpapers.size(), false, -1, path));
                addWallpaper(wallpapers.size() - 1);
                setWPs();
            } catch (Exception e) {
                String msg = "";
                if (e.getMessage() != null) msg = e.getMessage() + "\n\n";
                Toast.makeText(c, msg + e.getClass().getName(), Toast.LENGTH_LONG).show();
            }
    }


    void checkWP(boolean change, int W, boolean b) {
        if (change) {
            wpBooleans.set(W, b);
            updateSPBools();
        }
        View sWPBGOff = findViewById(sWPBGOffIds.get(W));
        if (sWPBGOff != null) {
            if (b) sWPBGOff.setVisibility(View.VISIBLE);
            else sWPBGOff.setVisibility(View.INVISIBLE);
        }
        if (sWPCBIds != null && sWPCBIds.size() > W) {
            CheckBox sWPCB = (CheckBox) findViewById(sWPCBIds.get(W));
            int drw = R.drawable.checkbox_1_blue_normal;
            if (b) drw = R.drawable.checkbox_1_blue;
            if (sWPCB != null) {
                sWPCB.setChecked(b);
                sWPCB.setButtonDrawable(drw);
            }
        }
    }

    void updateColorPrimary() {
        PorterDuffColorFilter pdcf = new PorterDuffColorFilter(CP, PorterDuff.Mode.SRC_IN);
        if (ccpPreview)
            sCP1LL.setBackgroundColor(Color.argb(cpBGAlpha, Color.red(CP), Color.green(CP), Color.blue(CP)));
        sCP1SB1.setProgress(Color.red(CP));
        sCP1SB2.setProgress(Color.green(CP));
        sCP1SB3.setProgress(Color.blue(CP));

        sToolbar.setTitleTextColor(CP);
        sToolbar.setSubtitleTextColor(CP);
        Drawable navIcon = sToolbar.getNavigationIcon();
        if (navIcon != null) {
            navIcon.setColorFilter(pdcf);
            sToolbar.setNavigationIcon(navIcon);
        }
        for (int c = 0; c < sLL.getChildCount(); c++) {
            LinearLayout sect = (LinearLayout) sLL.getChildAt(c), sectTitle = (LinearLayout) sect.getChildAt(0);
            ImageView iv = (ImageView) sectTitle.getChildAt(0);
            TextView tv = (TextView) sectTitle.getChildAt(1);
            iv.setColorFilter(CP);
            tv.setTextColor(CP);
        }

        TextView[] tvs = {sCP1TV, sCP2TV, sArngFTV, sArngMTV, sArngPTV, sFontTV, sAddWPTV};
        for (TextView tv : tvs) tv.setTextColor(CP);
        View[] cngBGs = {sCP1DEF, sCP2DEF}, cngBGDrws = {sArngFSp, sArngMSp, sArngPSp, sFontSP, sArngFSw, sArngMSw, sArngPSw, sFAB};
        for (View v : cngBGs) v.setBackgroundColor(CP);
        for (View v : cngBGDrws) {
            Drawable bg = v.getBackground();
            if (bg != null) {
                bg.setColorFilter(pdcf);
                v.setBackground(bg);
            }
        }

        if (sWPCBIds != null) for (int cb = 0; cb < sWPCBIds.size(); cb++) {
            try {
                CheckBox check = findViewById(sWPCBIds.get(cb));
                int[][] cbStates = {{android.R.attr.state_checked}, {}};
                int[] cbColors = {CP, CP};
                CompoundButtonCompat.setButtonTintList(check, new ColorStateList(cbStates, cbColors));
            } catch (Exception ignored) {
            }
            try {
                View cbOff = findViewById(sWPBGOffIds.get(cb));
                if (cbOff != null) cbOff.setBackgroundColor(CP);
            } catch (Exception ignored) {
            }
        }
        //LayerDrawables must necessarily become Drawables in order to be edited correctly in changeColor():
        sAddWPIV.setImageDrawable((Drawable) Mahdi.changeColor(c, sAddWPIV.getDrawable(), CP, null, true));
        //And also GradientDrawables:
        sAddWPBG.setBackground((Drawable) Mahdi.changeColor(c, sAddWPBG.getBackground(), CP,
                new int[]{(int) ((dm.widthPixels * 0.85f) - (dm.density * 20)),
                        sAddWPBG.getBackground().getIntrinsicHeight()}, true));

        Drawable sFSBD = sFontSB.getProgressDrawable(), sFSBT = sFontSB.getThumb();
        if (sFSBD != null) {
            sFSBD.setColorFilter(pdcf);
            sFontSB.setProgressDrawable(sFSBD);
        }
        if (sFSBT != null) {
            sFSBT.setColorFilter(pdcf);
            sFontSB.setThumb(sFSBT);
        }
    }

    void updateTextColorPrimary() {
        if (ccpPreview)
            sCP2LL.setBackgroundColor(Color.argb(cpBGAlpha, Color.red(TCP), Color.green(TCP), Color.blue(TCP)));
        sCP2SB1.setProgress(Color.red(TCP));
        sCP2SB2.setProgress(Color.green(TCP));
        sCP2SB3.setProgress(Color.blue(TCP));

        sContainer.setBackgroundColor(TCP);
        sToolbar.setBackgroundColor(TCP);
        for (int c = 0; c < sLL.getChildCount(); c++) {
            LinearLayout sect = (LinearLayout) sLL.getChildAt(c), sectTitle = (LinearLayout) sect.getChildAt(0);
            //ImageView iv = (ImageView) sectTitle.getChildAt(0);
            //TextView tv = (TextView) sectTitle.getChildAt(1);
            sect.setBackgroundColor(Color.argb(sectAlpha, Color.red(TCP), Color.green(TCP), Color.blue(TCP)));
            sectTitle.setBackgroundColor(Color.argb(sectTAlpha, Color.red(TCP), Color.green(TCP), Color.blue(TCP)));
        }

        TextView[] tvs = {sCP1DEF, sCP2DEF};
        for (TextView tv : tvs) tv.setTextColor(TCP);
        ImageView[] clrFltrs = {sArngFSw, sArngMSw, sArngPSw, sArngFSpMark, sArngMSpMark, sArngPSpMark, sBGShowerClose, sBGShowerDelete};
        for (ImageView clrFltr : clrFltrs) clrFltr.setColorFilter(TCP);

        //Change TCP Additions
        useDarkText = Color.red(TCP) * 0.299 + Color.green(TCP) * 0.587 + Color.blue(TCP) * 0.114 > 186;
        if (!useDarkText) {
            sBGShowerDeleteBG.setBackgroundColor(Color.argb(Color.alpha(ContextCompat.getColor(c, R.color.sBGShowerBtnBG)),
                    255, 255, 255));
            sBGShowerCloseBG.setBackgroundColor(Color.argb(Color.alpha(ContextCompat.getColor(c, R.color.sBGShowerBtnBG)),
                    255, 255, 255));
        } else {
            sBGShowerDeleteBG.setBackgroundColor(ContextCompat.getColor(c, R.color.sBGShowerBtnBG));
            sBGShowerCloseBG.setBackgroundColor(ContextCompat.getColor(c, R.color.sBGShowerBtnBG));
        }
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
                    if (ccpPreview) try {
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
        CP = ColorPrimary;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Mahdi.cpTag, CP);
        editor.apply();
        updateColorPrimary();
    }

    void newTextColourPrimary(int TextColorPrimary) {
        TCP = TextColorPrimary;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Mahdi.tcpTag, TCP);
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

        if (pArrangementAsc && sArngPSw.getRotation() != 270f)
            arngAscAnim = Mahdi.OA(sArngPSw, "rotation", 270f, arngSwDur);
        else if (!pArrangementAsc && sArngPSw.getRotation() != 90f)
            arngAscAnim = Mahdi.OA(sArngPSw, "rotation", 90f, arngSwDur);
    }

    void toastAscDsc(Boolean b) {
        String t;
        if (b) t = getResources().getString(R.string.sArngAsc);
        else t = getResources().getString(R.string.sArngDsc);
        Toast.makeText(c, t, Toast.LENGTH_SHORT).show();
    }

    void handleArngSpinners(int style) {
        Spinner[] spnrs = {sArngFSp, sArngMSp, sArngPSp};
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
                    switch (P) {
                        case 0:
                            fArrangement = i;
                            editor.putInt("fArrangement", fArrangement);
                            break;
                        case 1:
                            mArrangement = i;
                            editor.putInt("mArrangement", mArrangement);
                            break;
                        case 2:
                            pArrangement = i;
                            editor.putInt("pArrangement", pArrangement);
                            break;
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
        sArngPSp.setSelection(pArrangement);
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
        for (int c = 0; c < sLL.getChildCount(); c++) {
            LinearLayout sect = (LinearLayout) sLL.getChildAt(c);
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
        TextView[] tvs1 = {sCP1TV, sCP2TV, sArngFTV, sArngMTV, sArngPTV, sFontTV, sAddWPTV}, tvs2 = {sCP1DEF, sCP2DEF};
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
        if (sSV.getScrollY() > sSV.getHeight() / 2) {
            if (!sFABOn) {
                sFABOn = true;
                Mahdi.OA(sFAB, "translationY", 0f, sFABDur);
            }
        } else if (sFABOn) {
            sFABOn = false;
            Mahdi.OA(sFAB, "translationY", (float) (sFAB.getHeight() * 1.79), sFABDur);
        }
    }

    public static Bitmap blur(Context c, Bitmap image, float radius) {
        if (null == image) return null;
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(c);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(radius);//0f < radius < 25f
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    void addWallpaper(int w) {
        if (w >= wpBooleans.size()) wpBooleans.add(false);
        LayoutInflater.from(c).inflate(R.layout.s_sect_wallpaper, sWallpapers);

        ConstraintLayout sWP = (ConstraintLayout) sWallpapers.getChildAt(sWallpapers.getChildCount() - 1),
                sWPOverlay = (ConstraintLayout) sWP.getChildAt(1), sWPCBCl = (ConstraintLayout) sWPOverlay.getChildAt(0);
        View sWPBGOff = sWP.getChildAt(0);
        CheckBox sWPCB = (CheckBox) sWPCBCl.getChildAt(0);
        ImageView sWPIV = (ImageView) sWPOverlay.getChildAt(1);

        if (wpBooleans.get(w)) sWPBGOff.setVisibility(View.VISIBLE);
        sWPBGOff.setId(View.generateViewId());
        sWPBGOffIds.add(sWPBGOff.getId());
        sWPBGOff.setBackgroundColor(CP);

        sWPCBCl.setId(View.generateViewId());
        sWPCBClIds.add(sWPCBCl.getId());
        checkWP(false, w, wpBooleans.get(w));
        sWPCB.setChecked(wpBooleans.get(w));
        int drw = R.drawable.checkbox_1_blue_normal;
        if (sWPCB.isChecked()) drw = R.drawable.checkbox_1_blue;
        sWPCB.setButtonDrawable(drw);
        sWPCB.setId(View.generateViewId());
        sWPCBIds.add(sWPCB.getId());
        final ConstraintLayout SWP = sWP;
        sWPCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int checked = 0;
                for (int bo = 0; bo < wpBooleans.size(); bo++) if (wpBooleans.get(bo)) checked += 1;
                if (checked == 1 && !b) {
                    compoundButton.setChecked(true);
                    Toast.makeText(c, R.string.sAtLeastOneWP, Toast.LENGTH_SHORT).show();
                } else checkWP(true, sWallpapers.indexOfChild(SWP), b);
            }
        });
        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {CP, CP};
        CompoundButtonCompat.setButtonTintList(sWPCB, new ColorStateList(states, colors));

        ConstraintLayout.LayoutParams sWPIVLP = (ConstraintLayout.LayoutParams) sWPIV.getLayoutParams();
        sWPIVLP.width = 0;
        sWPIVLP.startToEnd = sWPCBCl.getId();
        sWPIV.setLayoutParams(sWPIVLP);
        sWPIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWP(sWallpapers.indexOfChild(SWP));
            }
        });
        wpQueue.add(w);
        checkForMaxWP();
    }

    void checkForMaxWP() {
        if (wallpapers.size() >= Mahdi.resWallpapers.length + Mahdi.adtWPMax)
            sAddWallpaper.setVisibility(View.GONE);
        else sAddWallpaper.setVisibility(View.VISIBLE);
    }

    void removeWP(int w) {
        checkWP(true, w, false);
        wallpapers.remove(w);
        wpBooleans.remove(w);
        sWallpapers.removeViewAt(w);
        sWPBGOffIds.remove(w);
        sWPCBClIds.remove(w);
        sWPCBIds.remove(w);
        checkForMaxWP();

        SharedPreferences.Editor editor = sp.edit();
        int deleted = ((w - Mahdi.resWallpapers.length) + 1);
        editor.remove(Mahdi.adtWP + deleted);
        editor = moveTopAdtWPs(editor, deleted);
        editor.apply();
        updateSPBools();

        int checked = 0;
        for (boolean bool : wpBooleans) if (bool) checked += 1;
        if (checked == 0) checkWP(true, 0, true);
    }

    SharedPreferences.Editor moveTopAdtWPs(SharedPreferences.Editor editor, int last) {
        try {
            String next = Mahdi.adtWP + (last + 1);
            if (sp.contains(next)) {
                editor.putString(Mahdi.adtWP + last, sp.getString(next, ""));
                editor.remove(next);
                moveTopAdtWPs(editor, last + 1);
            }
        } catch (Exception e) {
            Toast.makeText(c, "moveTopAdtWPs: " + e.getClass().getName(), Toast.LENGTH_SHORT).show();
        }
        return editor;
    }

    void showWP(int i) {
        showingWP = i;
        if (i != -1) {
            new WPHandler(3, wallpapers.get(i)).start();
            sBGShower.setVisibility(View.VISIBLE);
            if (i >= Mahdi.resWallpapers.length) sBGShowerDelete.setVisibility(View.VISIBLE);
        } else {
            sBGShower.setVisibility(View.GONE);
            sBGShowerIV.setImageBitmap(null);
            sBGShowerDelete.setVisibility(View.GONE);
        }
    }

    void updateSPBools() {
        SharedPreferences.Editor editor = sp.edit();
        StringBuilder sb = new StringBuilder();
        for (int s = 0; s < wpBooleans.size(); s++) {
            if (wpBooleans.get(s) && s < wallpapers.size() && wallpapers.get(s) != null)
                sb.append("1");
            else sb.append("0");
        }
        editor.putString("wpBooleans", sb.toString());
        editor.apply();
    }

    void setWPs() {
        settingWP = wpQueue.size() != 0;
        if (!settingWP) return;
        if (!minimizingWPs) {
            minimizingWPs = true;
            new WPHandler(2, wpQueue.get(0)).start();
        }
        ValueAnimator anSet = Mahdi.VA(sMotor, "translationY", setWPDur, -222f, 0f);
        anSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setWPs();
            }
        });
    }

    int dp(int px) {
        return (int) (dm.density * px);
    }

    void goBack() {
        finish();
        startActivity(new Intent(c, Mahdi.class));
    }


    class WPHandler extends Thread {
        private int what;
        private Object object;

        WPHandler(int what, Object object) {
            this.what = what;
            this.object = object;
        }

        @Override
        public void run() {
            int num = -1, w = dm.widthPixels, h = dm.heightPixels;
            if (object instanceof Integer) num = (int) object;
            Object obj = null;
            int[] sizes;
            switch (what) {
                case 0:// Wallpaper List
                    wallpapers = new ArrayList<>();
                    for (int b = 0; b < Mahdi.resWallpapers.length; b++)
                        wallpapers.add(new Wallpaper(b, true, Mahdi.resWallpapers[b], null));
                    for (int a = 1; a < Mahdi.adtWPMax + 1; a++)
                        if (sp.contains(Mahdi.adtWP + a))
                            wallpapers.add(new Wallpaper((Mahdi.resWallpapers.length + a) - 1, false, -1,
                                    sp.getString(Mahdi.adtWP + a, "")));
                    break;
                case 1:// Blurred Wallpaper
                    w = (int) (w * minimizeWPForBlur);
                    h = (int) (h * minimizeWPForBlur);
                    Bitmap bmp = null;
                    if (Mahdi.wp < wallpapers.size()) {
                        if (Mahdi.wp < Mahdi.resWallpapers.length)
                            bmp = loadBitmap(wallpapers.get(Mahdi.wp).res, w, h);
                        else bmp = loadBitmap(wallpapers.get(Mahdi.wp).path, w, h);
                    }
                    if (bmp == null) bmp = loadBitmap(Mahdi.resWallpapers[0], w, h);
                    try {
                        Bitmap overlay = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(overlay);
                        canvas.drawBitmap(bmp, 0, 0, null);
                        bmp = overlay;
                        overlay = null;
                        bmp = blur(c, bmp, fBlurWP);//0f-25f
                    } catch (Exception ignored) {
                    }
                    if (bmp == null) bmp = loadBitmap(Mahdi.resWallpapers[0], w, h);
                    obj = bmp;
                    break;
                case 2:// Minimize Wallpaper
                    sizes = new int[]{(int) ((float) dm.widthPixels * 1f), dp(48)};
                    Object pic = wallpapers.get(num).res;
                    if (num >= Mahdi.resWallpapers.length) pic = wallpapers.get(num).path;
                    obj = Mahdi.cropBitmap(c, pic, sizes);
                    break;
                case 3:// Show Wallpaper
                    if (!(object instanceof Wallpaper)) return;
                    Wallpaper wall = (Wallpaper) object;
                    sizes = new int[]{w, h};
                    if (wall.isRes) obj = Mahdi.cropBitmap(c, wall.res, sizes);
                    else obj = Mahdi.cropBitmap(c, wall.path, sizes);
                    break;
            }
            hndWP.obtainMessage(what, num, 0, obj).sendToTarget();
        }

        Bitmap loadBitmap(Object obj, int w, int h) {
            boolean internalWP = obj instanceof Integer;
            if (!(obj instanceof String) && !(obj instanceof Integer)) return null;
            String file = "";
            int res = R.drawable.music_bg_1;
            if (!internalWP) file = (String) obj;
            else res = (int) obj;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (!internalWP) BitmapFactory.decodeFile(file, options);
            else BitmapFactory.decodeResource(getResources(), res, options);
            final int height = options.outHeight, width = options.outWidth;
            int inSampleSize = 1;
            if (height > h || width > w) {
                final int halfHeight = height / 2, halfWidth = width / 2;
                while ((halfHeight / inSampleSize) >= h && (halfWidth / inSampleSize) >= w)
                    inSampleSize *= 2;
            }
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            Bitmap got;
            if (!internalWP) got = BitmapFactory.decodeFile(file, options);
            else got = BitmapFactory.decodeResource(getResources(), res, options);
            return got;
        }
    }

    public static class Wallpaper {
        private int i, res;
        private boolean isRes;
        private String path;

        Wallpaper(int i, boolean isRes, int res, String path) {
            this.i = i;
            this.isRes = isRes;
            this.res = res;
            this.path = path;
        }
    }
}
