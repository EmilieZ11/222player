package org.ifaco.a222player;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import java.lang.reflect.Method;

public final class NotificationExtras {//REMEMBER view instanceof ViewGroup
    private static final Method MAKE_CONTENT_VIEW_METHOD;// Method reference to NotificationCompat.Builder#makeContentView

    static {
        Method m = null;
        try {
            m = Notification.Builder.class.getDeclaredMethod("makeContentView");
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        MAKE_CONTENT_VIEW_METHOD = m;
    }

    private NotificationExtras() {
    }


    static Notification buildWithBGResource(Context context, Notification.Builder builder, @DrawableRes int res, @ColorInt int tColor) {
        if (MAKE_CONTENT_VIEW_METHOD == null) return buildNotification(builder);
        RemoteViews remoteViews = obtainRemoteViews(builder);
        Notification notification = buildNotification(builder);

        if (remoteViews != null) {
            View v = LayoutInflater.from(context).inflate(remoteViews.getLayoutId(), null);
            remoteViews.setInt(v.getId(), "setBackgroundResource", res);
            applyTextColorToRemoteViews(remoteViews, v, tColor);
        }
        return notification;
    }

    public static Notification buildWithBGColor(Context context, Notification.Builder builder, @ColorInt int color, @ColorInt int tColor) {
        if (MAKE_CONTENT_VIEW_METHOD == null) return buildNotification(builder);
        RemoteViews remoteViews = obtainRemoteViews(builder);
        Notification notification = buildNotification(builder);

        if (remoteViews != null) {
            View v = LayoutInflater.from(context).inflate(remoteViews.getLayoutId(), null);
            remoteViews.setInt(v.getId(), "setBackgroundColor", color);

            // Calculate a contrasting text color to ensure readability, and apply it to all TextViews within the notification layout
            //boolean useDarkText = (Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114 > 186);
            //int textColor = useDarkText ? 0xff000000 : 0xffffffff;
            applyTextColorToRemoteViews(remoteViews, v, tColor);
        }
        return notification;
    }


    private static RemoteViews obtainRemoteViews(Notification.Builder builder) {
        try {
            // Explicitly force creation of the content view and re-assign it to the notification
            RemoteViews remoteViews = (RemoteViews) MAKE_CONTENT_VIEW_METHOD.invoke(builder);
            builder.setContent(remoteViews);
            return remoteViews;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Notification buildNotification(Notification.Builder builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) return builder.build();
        else return builder.getNotification();
    }

    private static void applyTextColorToRemoteViews(RemoteViews remoteViews, View view, int color) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0, count = vg.getChildCount(); i < count; i++)
                applyTextColorToRemoteViews(remoteViews, vg.getChildAt(i), color);
        } else if (view instanceof TextView) remoteViews.setTextColor(view.getId(), color);
    }
}

