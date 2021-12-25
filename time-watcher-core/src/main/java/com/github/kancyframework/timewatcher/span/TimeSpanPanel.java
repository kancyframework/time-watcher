package com.github.kancyframework.timewatcher.span;

import javax.swing.*;
import java.awt.*;
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

        // 序号字体: 黑体
        Font font1 = new Font("黑体", Font.BOLD, 12);
        // 标签字体: 宋体,Bernard MT
        Font font2 = new Font("宋体", Font.PLAIN, 12);

        for (TimeSpan timeSpan : timeSpans) {
            // span序号
            g.setFont(font1);
            g.setColor(Color.GRAY);
            g.drawString(timeSpan.getIndexLabel(), 5, timeSpan.getY()+ TimeSpan.LINE_CENTER);

            // 画span
            g.setColor(Color.decode("#87CEFA"));
            g.fillRect(timeSpan.getX(), timeSpan.getY(), timeSpan.getWith(), timeSpan.getHeight());

            // 画span标签
            g.setFont(font2);
            g.setColor(Color.GRAY);
            g.drawString(timeSpan.getSpanLabel(), timeSpan.getX() + 5, timeSpan.getY()+ TimeSpan.LINE_CENTER);

            if (timeSpan.isFirst()){
                String rootSpanTimeLabel = timeSpan.getRootSpanTimeLabel();
                g.drawString(rootSpanTimeLabel, timeSpan.getX() + TimeSpan.MAX_WITH - 300, timeSpan.getY()+ TimeSpan.LINE_CENTER);
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
