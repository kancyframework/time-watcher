package com.github.kancyframework.timewatcher.span;

import com.github.kancyframework.timewatcher.WatchContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * TimeSpanFrame
 *
 * @author huangchengkang
 * @date 2021/12/25 21:45
 */
public class TimeSpanFrame extends JFrame {

    private List<TimeSpan> timeSpans;

    public TimeSpanFrame(List<TimeSpan> timeSpans) {
        this.timeSpans = timeSpans;
    }

    public static void show(WatchContext context){
        TimeSpanFrame frame = new TimeSpanFrame(TimeSpaner.getTimeSpans(context));
        frame.init();
    }

    public static void show(List<TimeSpan> timeSpans){
        TimeSpanFrame frame = new TimeSpanFrame(timeSpans);
        frame.init();
    }

    public void init(){
        this.setSize(TimeSpan.MAX_WITH+TimeSpan.MARGIN *2, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(initScrollTimeSpanPanel(timeSpans));
        this.setTitle("TimeWatcher 耗时分析");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JComponent initScrollTimeSpanPanel(List<TimeSpan> timeSpans) {
        TimeSpanPanel spanJPanel = new TimeSpanPanel(timeSpans);
        int width = getWidth();
        int height = timeSpans.size() * (TimeSpan.LINE_HEIGHT + TimeSpan.LINE_SPACING) + TimeSpan.MARGIN_TOP;
        spanJPanel.setPreferredSize(new Dimension(width,height));
        return new JScrollPane(spanJPanel);
    }

}
