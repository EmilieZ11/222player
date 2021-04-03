package org.ifaco.a222player;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.TreeMap;

public class SubtitleView extends AppCompatTextView implements Runnable {
    private TreeMap<Long, Line> track;
    private int delay = 222;

    public SubtitleView(Context context) {
        super(context);
    }

    public SubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubtitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void run() {
        String text = "";
        int vis = GONE;
        if (Mahdi.vp != null && track != null) {
            try {
                text = getTimedText(Mahdi.vp.getCurrentPosition());
            } catch (Exception ignored) {
            }
            if (text == null) text = "";
            if (text.length() > 0) {
                vis = VISIBLE;
                text = text.substring(0, text.length() - 1);
            }
        }
        setVisibility(vis);
        setText(text);
        postDelayed(this, delay);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        postDelayed(this, delay);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this);
    }


    private String getTimedText(long currentPosition) {
        String result = "";
        for (Map.Entry<Long, Line> entry : track.entrySet()) {
            if (currentPosition < entry.getKey()) break;
            if (currentPosition < entry.getValue().to) result = entry.getValue().text;
        }
        return result;
    }

    public void setSubSource(String path) {
        track = getSubtitleFile(path);
    }

    private TreeMap<Long, Line> getSubtitleFile(String path) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            return parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static TreeMap<Long, Line> parse(InputStream is) throws IOException {
        LineNumberReader r = new LineNumberReader(new InputStreamReader(is, "utf-8"));
        TreeMap<Long, Line> track = new TreeMap<>();
        while ((r.readLine()) != null) /*Read cue number*/ {
            String timeString = r.readLine(), lineString = "", s;
            while (!((s = r.readLine()) == null || s.trim().equals(""))) lineString += s + "\n";
            long startTime = parse(timeString.split("-->")[0]), endTime = parse(timeString.split("-->")[1]);
            track.put(startTime, new Line(startTime, endTime, lineString));
        }
        return track;
    }

    private static long parse(String in) {
        long hours = Long.parseLong(in.split(":")[0].trim()),
                minutes = Long.parseLong(in.split(":")[1].trim()),
                seconds = Long.parseLong(in.split(":")[2].split(",")[0].trim()),
                millies = Long.parseLong(in.split(":")[2].split(",")[1].trim());
        return hours * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + millies;
    }


    public static class Line {
        long from, to;
        String text;

        Line(long from, long to, String text) {
            this.from = from;
            this.to = to;
            this.text = text;
        }
    }
}