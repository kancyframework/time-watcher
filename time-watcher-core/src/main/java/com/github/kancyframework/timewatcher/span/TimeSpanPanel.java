package com.github.kancyframework.timewatcher.span;

import javax.swing.*;
import java.awt.*;
import java.text.AttributedString;
import java.util.List;

/**
 * TimeSpanPanel
 *
 * @author huangchengkang
 * @date 2021/12/25 21:47
 */
public class TimeSpanPanel extends JPanel {
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
            g.fillRect(timeSpan.getX(), timeSpan.getY(), timeSpan.getWith(), timeSpan.getHeight());

            // 画span标签
            AttributedString spanLabelAttributedString = timeSpan.getSpanLabelAttributedString();
            g.drawString(spanLabelAttributedString.getIterator(),
                    timeSpan.getX() + 5, timeSpan.getY()+ TimeSpan.LINE_CENTER);

            if (timeSpan.isFirst()){
                AttributedString rootSpanTimeLabelAttributedString = timeSpan.getRootSpanTimeLabelAttributedString();
                g.drawString(rootSpanTimeLabelAttributedString.getIterator(),
                        timeSpan.getX() + TimeSpan.MAX_WITH - 355, timeSpan.getY()+ TimeSpan.LINE_CENTER);
            }
        }

        // 画左边测线(虚线)
        Stroke dash = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                2f, new float[] { 10, 5, },0f);
        ((Graphics2D)g).setStroke(dash);
        g.setColor(Color.decode("#8DCDFE"));
        g.drawLine(40, 3, 40, getHeight()-3);

        // 关闭资源
        g.dispose();
    }
}
