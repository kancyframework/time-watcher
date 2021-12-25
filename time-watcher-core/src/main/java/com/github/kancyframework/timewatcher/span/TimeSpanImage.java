package com.github.kancyframework.timewatcher.span;

import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.utils.ImageUtils;

import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * TimeSpanImage
 *
 * @author huangchengkang
 * @date 2021/12/25 22:10
 */
public class TimeSpanImage {
    private final List<TimeSpan> timeSpans;

    public TimeSpanImage(List<TimeSpan> timeSpans) {
        this.timeSpans = timeSpans;
    }

    public static TimeSpanImage create(WatchContext context){
        return new TimeSpanImage(TimeSpaner.getTimeSpans(context));
    }

    public static TimeSpanImage create(List<TimeSpan> timeSpans){
        return new TimeSpanImage(timeSpans);
    }

    public void save(String filePath){
        save(new File(filePath));
    }

    public void save(File file){
        int width = TimeSpan.MAX_WITH + TimeSpan.MARGIN * 2;
        int height = timeSpans.size() * (TimeSpan.LINE_HEIGHT + TimeSpan.LINE_SPACING) + TimeSpan.MARGIN_TOP;
        ImageUtils.drawImage(width, height, g -> {
            //设置背景颜色
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            // 绘制内容
            drawTimeSpanImage(g);
        }, file.getAbsolutePath());
    }

    public byte[] getImageBytes(){
        int width = TimeSpan.MAX_WITH + TimeSpan.MARGIN * 2;
        int height = timeSpans.size() * (TimeSpan.LINE_HEIGHT + TimeSpan.LINE_SPACING) + TimeSpan.MARGIN_TOP;
        return ImageUtils.drawImage(width, height, g -> {
            //设置背景颜色
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            // 绘制内容
            drawTimeSpanImage(g);
        });
    }

    private void drawTimeSpanImage(Graphics2D g) {

        // 序号字体: 黑体
        Font font1 = new Font("黑体", Font.BOLD, 12);
        // 标签字体: 宋体,Bernard MT
        Font font2 = new Font("宋体", Font.PLAIN, 12);

        for (TimeSpan timeSpan : timeSpans) {
            // span序号
            g.setFont(font1);
            g.setColor(Color.decode("#87CEFA"));
            g.drawString(timeSpan.getIndexLabel(), 15, timeSpan.getY() + 20);

            // 画span
            g.fillRect(timeSpan.getX(), timeSpan.getY(), timeSpan.getWith(), timeSpan.getHeight());

            // 画span标签
            g.setFont(font2);
            g.setColor(Color.red);
            g.drawString(timeSpan.getSpanLabel(), timeSpan.getX() + 5, timeSpan.getY()+20);

            if (timeSpan.isFirst()){
                String rootSpanTimeLabel = timeSpan.getRootSpanTimeLabel();
                g.drawString(rootSpanTimeLabel, timeSpan.getX() + TimeSpan.MAX_WITH - 400, timeSpan.getY()+20);
            }

        }
    }

}
