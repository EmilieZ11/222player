package org.ifaco.a222player;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

public class AppWidget extends AppWidgetProvider {
    DisplayMetrics displayMetrics = new DisplayMetrics();

    @Override
    public void onReceive(Context context, Intent intent) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
        if (appWidgetIds != null) for (int id : appWidgetIds) {
            int state = 0;
            if (intent.getExtras() != null) if (intent.getExtras().containsKey("playbackStatus"))
                state = intent.getExtras().getInt("playbackStatus", 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            PendingIntent openPlayer = PendingIntent.getActivity(context, 0, new Intent(context, Mahdi.class), 0);
            views.setOnClickPendingIntent(R.id.awAlbumArt, openPlayer);

            int aap = (int) (displayMetrics.density * 13), playPauseImg = R.drawable.play_3_ntf;
            PendingIntent playOrPause;
            if (state != 0) {
                views.setTextViewText(R.id.awTitle, Amin.activeAudio.getTitle());
                Bitmap b = Amin.getAlbumArt(context, Amin.activeAudio.getAlbumId());
                if (b != null) {
                    views.setImageViewBitmap(R.id.awAlbumArt, b);
                    aap = 0;
                }

                if (state == 1) {
                    playPauseImg = R.drawable.pause_3_ntf;
                    playOrPause = Amin.playbackAction(context, 1);
                } else playOrPause = Amin.playbackAction(context, 0);
                views.setOnClickPendingIntent(R.id.awLeft, Amin.playbackAction(context, 2));
                views.setOnClickPendingIntent(R.id.awPlayPause, playOrPause);
                views.setOnClickPendingIntent(R.id.awRight, Amin.playbackAction(context, 2));
            } else {
                views.setTextViewText(R.id.awTitle, "");
                views.setImageViewResource(R.id.awAlbumArt, R.mipmap.launcher);

                views.setOnClickPendingIntent(R.id.awLeft, null);
                views.setOnClickPendingIntent(R.id.awPlayPause, null);
                views.setOnClickPendingIntent(R.id.awRight, null);
            }
            views.setViewPadding(R.id.awAlbumArt, aap, aap, aap, aap);
            int colour = ContextCompat.getColor(context, R.color.awButtons);
            views.setImageViewBitmap(R.id.awPlayPause, (Bitmap) Mahdi.changeColor(context, playPauseImg, colour, false));
            views.setImageViewBitmap(R.id.awLeft, (Bitmap) Mahdi.changeColor(context, R.drawable.skip_2_ntf_prev, colour, false));
            views.setImageViewBitmap(R.id.awRight, (Bitmap) Mahdi.changeColor(context, R.drawable.skip_2_ntf_next, colour, false));

            appWidgetManager.updateAppWidget(id, views);
        }
    }
}
