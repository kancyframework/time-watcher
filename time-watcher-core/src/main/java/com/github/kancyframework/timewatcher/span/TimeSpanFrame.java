package com.github.kancyframework.timewatcher.span;

import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.utils.ImageUtils;
import com.github.kancyframework.timewatcher.utils.PopupMenuUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

/**
 * TimeSpanFrame
 *
 * @author huangchengkang
 * @date 2021/12/25 21:45
 */
public class TimeSpanFrame extends JFrame implements ActionListener {

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
        JScrollPane jScrollPane = new JScrollPane(spanJPanel);
        initPopupMenu(jScrollPane);
        return jScrollPane;
    }

    private void initPopupMenu(Component component) {
        JPopupMenu popupMenu = new JPopupMenu();

        PopupMenuUtils.addMenuItem(popupMenu,
                "复制耗时报告", this, "copyReport", KeyStroke.getKeyStroke("ctrl C"));
        PopupMenuUtils.addMenuItem(popupMenu,
                "耗时报告另存为", this, "saveAs", KeyStroke.getKeyStroke("ctrl S"));
        PopupMenuUtils.addPopupMenu(component, popupMenu);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (Objects.equals(actionCommand, "saveAs")){
            FileDialog fileDialog = new FileDialog(this);
            fileDialog.setFile(getDefaultFileName(timeSpans));
            fileDialog.setMode(FileDialog.SAVE);
            fileDialog.setTitle("耗时报告另存为");
            fileDialog.setLocationRelativeTo(this);
            fileDialog.setModal(true);
            fileDialog.setVisible(true);

            String fileName = fileDialog.getFile();
            if (Objects.nonNull(fileName) && !fileName.isEmpty()){
                String filePath = String.format("%s%s",fileDialog.getDirectory(), fileName);
                TimeSpanImage timeSpanImage = new TimeSpanImage(timeSpans);
                timeSpanImage.save(filePath);
                JOptionPane.showMessageDialog(this, "保存成功！");
            }
            fileDialog.dispose();
        }

        if (Objects.equals(actionCommand, "copyReport")){
            TimeSpanImage timeSpanImage = new TimeSpanImage(timeSpans);
            byte[] imageBytes = timeSpanImage.getImageBytes();
            if (imageBytes.length > 0){
                ImageUtils.setClipboardImage(imageBytes);
            }
        }
    }

    private String getDefaultFileName(List<TimeSpan> timeSpans) {
        if (Objects.nonNull(timeSpans) && !timeSpans.isEmpty()){
            TimeSpan timeSpan = timeSpans.get(0);
            if (timeSpan instanceof SimpleTimeSpan){
                return ((SimpleTimeSpan) timeSpan).getFileName();
            }else if(timeSpan instanceof WatchContextTimeSpan){
                return ((WatchContextTimeSpan) timeSpan).getFileName();
            }
        }
        return "耗时分析报告.png";
    }
}
