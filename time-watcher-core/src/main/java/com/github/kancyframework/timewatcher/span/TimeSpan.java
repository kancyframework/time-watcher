package com.github.kancyframework.timewatcher.span;

import java.util.Objects;

/**
 * Span
 *
 * @author huangchengkang
 * @date 2021/12/25 8:34
 */
public abstract class TimeSpan {

    public static final int MAX_WITH = 1500;
    public static final int LINE_HEIGHT = 26;
    public static final int LINE_SPACING = 5;
    public static final int LINE_CENTER = (LINE_HEIGHT + LINE_SPACING * 2 ) / 2 ;

    public static final int MARGIN = 50;
    public static final int MARGIN_TOP = 10;

    protected int x;
    protected int y;
    protected int with;
    protected int height;
    protected final int index;

    public TimeSpan(int index) {
        this.index = index;
    }

    public String getIndexLabel(){
        if (index < 10){
            return String.format("[0%d]", index);
        }
        return String.format("[%d]", index);
    }

    public abstract String getSpanLabel();

    public abstract String getRootSpanTimeLabel();

    public boolean isFirst(){
        return Objects.equals(index, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWith() {
        return with;
    }

    public int getHeight() {
        return height;
    }


}
