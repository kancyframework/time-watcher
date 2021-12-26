package com.github.kancyframework.timewatcher.span;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.Objects;

/**
 * Span
 *
 * @author huangchengkang
 * @date 2021/12/25 8:34
 */
public abstract class TimeSpan {

    private static Font font1 = new Font("黑体", Font.BOLD, 12);
    private static Font font2 = new Font("宋体", Font.PLAIN, 12);

    private static Color skyBlueColor = Color.decode("#87CDFE");

    public static final int MAX_WITH = 1600;
    public static final int LINE_HEIGHT = 26;
    public static final int LINE_SPACING = 5;
    public static final int LINE_CENTER = (LINE_HEIGHT + LINE_SPACING * 2 ) / 2 ;

    public static final int MARGIN = 60;
    public static final int MARGIN_TOP = 10;
    public static final int CHAR_WITH = 8;

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected final int index;



    public TimeSpan(int index) {
        this.index = index;
    }

    public Color getSpanColor(){
        return skyBlueColor;
    }

    public String getIndexLabel(){
        if (index < 10){
            return String.format("[0%d]", index);
        }
        return String.format("[%d]", index);
    }

    public AttributedString getIndexLabelAttributedString(){
        AttributedString as = new AttributedString(getIndexLabel());
        as.addAttribute(TextAttribute.FONT, font1);
        as.addAttribute(TextAttribute.FOREGROUND, Color.GRAY);
        return as;
    }


    public abstract String getSpanLabel();

    public AttributedString getSpanLabelAttributedString(){
        AttributedString as = new AttributedString(getSpanLabel());
        as.addAttribute(TextAttribute.FONT, font2);
        as.addAttribute(TextAttribute.FOREGROUND, Color.GRAY);
        return as;
    }

    public abstract String getRootSpanTimeLabel();

    public AttributedString getRootSpanTimeLabelAttributedString(){
        AttributedString as = new AttributedString(getRootSpanTimeLabel());
        as.addAttribute(TextAttribute.FONT, font2);
        as.addAttribute(TextAttribute.FOREGROUND, Color.GRAY);
        return as;
    }

    public boolean isFirst(){
        return Objects.equals(index, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}
