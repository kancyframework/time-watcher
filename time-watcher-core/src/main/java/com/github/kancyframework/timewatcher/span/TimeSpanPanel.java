package com.github.kancyframework.timewatcher.span;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.AttributedString;
import java.util.List;

/**
 * TimeSpanPanel
 *
 * @author huangchengkang
 * @date 2021/12/25 21:47
 */
public class TimeSpanPanel extends JPanel {

    private static final Color indexLineColor = Color.decode("#8DCDFE");

    private final List<TimeSpan> timeSpans;

    public TimeSpanPanel(List<TimeSpan> timeSpans) {
        this.timeSpans = timeSpans;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        //设置背景颜色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (TimeSpan timeSpan : timeSpans) {
            // span序号
            AttributedString indexLabelAttributedString = timeSpan.getIndexLabelAttributedString();
            g.drawString(indexLabelAttributedString.getIterator(), 5, timeSpan.getY()+ TimeSpan.LINE_CENTER);

            // 画span
            g.setColor(timeSpan.getSpanColor());
            g.fillRect(timeSpan.getX(), timeSpan.getY(), timeSpan.getWidth(), timeSpan.getHeight());

            // 画span标签
            AttributedString spanLabelAttributedString = timeSpan.getSpanLabelAttributedString();
            g.drawString(spanLabelAttributedString.getIterator(),
                    timeSpan.getX() + 5, timeSpan.getY()+ TimeSpan.LINE_CENTER);

            if (timeSpan.isFirst()){
                AttributedString rootSpanTimeLabelAttributedString = timeSpan.getRootSpanTimeLabelAttributedString();
                g.drawString(rootSpanTimeLabelAttributedString.getIterator(),
                        timeSpan.getX() + TimeSpan.MAX_WITH - 355, timeSpan.getY()+ TimeSpan.LINE_CENTER);
                // 画时间刻度
                // drawTimeScale(g, timeSpan);
            }
        }

        // 画左边测线(虚线)
        Stroke dash = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                2f, new float[] { 10, 5, },0f);
        ((Graphics2D)g).setStroke(dash);
        g.setColor(indexLineColor);
        g.drawLine(45, 3, 45, getHeight()-3);

        // 关闭资源
        g.dispose();
    }

    private void drawTimeScale(Graphics g, TimeSpan timeSpan) {
        long totalTime = timeSpan.getTotalTime();
        int num = 20;
        double rate = TimeSpan.MAX_WITH * 1.0 / totalTime;
        double ms = (totalTime * 1.0 / num);
        double spanLen = ms * rate;
        int x = 0;
        int t = 0;

        boolean secFlag = false;
        if (totalTime > num * 500){
            secFlag = true;
        }

        for (int i = 0; i <= num; i++) {
            t = (int) (i * ms);
            x = (int) (spanLen * i + TimeSpan.MARGIN);
            if (x > TimeSpan.MAX_WITH + TimeSpan.MARGIN){
                break;
            }

            if (secFlag){
                String st = new BigDecimal(String.valueOf(t / 1000.0)).setScale(2).stripTrailingZeros().toPlainString() + "s";
                g.drawString(st, i == num ? x - 20 : x, 11);
            }else {
                g.drawString(String.valueOf(t), i == num ? x - 20 : x, 11);
            }
        }
    }
}
