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
    DisplayMetrics dm = new DisplayMetrics();

    @Override
    public void onReceive(Context c, Intent intent) {
        WindowManager windowManager = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) windowManager.getDefaultDisplay().getMetrics(dm);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(c);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(c, AppWidget.class));

        if (appWidgetIds != null) for (int id : appWidgetIds) {
            RemoteViews views = new RemoteViews(c.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.awAlbumArt,
                    PendingIntent.getActivity(c, 0, new Intent(c, Mahdi.class), 0));

            int aap = (int) (dm.density * 9), playPauseImg = R.drawable.play_3_ntf;
            PendingIntent playOrPause;
            if (Amin.playable) {
                views.setTextViewText(R.id.awTitle, Amin.activeAudio.getTitle());
                Bitmap b = Amin.getAlbumArt(c, Amin.activeAudio.getAlbumId());
                if (b != null) {
                    views.setImageViewBitmap(R.id.awAlbumArt, b);
                    aap = 0;
                }

                if (Amin.playing) {
                    playPauseImg = R.drawable.pause_3_ntf;
                    playOrPause = Amin.playbackAction(c, 1);
                } else playOrPause = Amin.playbackAction(c, 0);
                views.setOnClickPendingIntent(R.id.awLeft, Amin.playbackAction(c, 3));
                views.setOnClickPendingIntent(R.id.awPlayPause, playOrPause);
                views.setOnClickPendingIntent(R.id.awRight, Amin.playbackAction(c, 2));
            } else {
                views.setTextViewText(R.id.awTitle, "");
                views.setImageViewResource(R.id.awAlbumArt, R.drawable.play_3_ntf);

                views.setOnClickPendingIntent(R.id.awLeft, null);
                views.setOnClickPendingIntent(R.id.awPlayPause, null);
                views.setOnClickPendingIntent(R.id.awRight, null);
            }
            views.setViewPadding(R.id.awAlbumArt, aap, aap, aap, aap);
            int colour = ContextCompat.getColor(c, R.color.awButtons);
            views.setImageViewBitmap(R.id.awPlayPause, (Bitmap) Mahdi.changeColor(c, playPauseImg, colour, null, false));
            views.setImageViewBitmap(R.id.awLeft,
                    (Bitmap) Mahdi.changeColor(c, R.drawable.skip_2_ntf_prev, colour, null, false));
            views.setImageViewBitmap(R.id.awRight,
                    (Bitmap) Mahdi.changeColor(c, R.drawable.skip_2_ntf_next, colour, null, false));

            appWidgetManager.updateAppWidget(id, views);
        }
    }
}
