package com.github.kancyframework.timewatcher.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * PopupMenuUtils
 *
 * @author kancy
 * @date 2020/6/13 0:00
 */
public class PopupMenuUtils {

    public static void addSeparator(JPopupMenu popupMenu){
        popupMenu.addSeparator();
    }


    public static JMenuItem addMenuItem(JPopupMenu popupMenu, String menuName,
                                        ActionListener actionListener, String actionCommand) {
        return addMenuItem(popupMenu, menuName, actionListener, actionCommand, null);
    }

    /**
     * 添加弹出菜单项
     * @param popupMenu
     * @param menuName
     * @param actionCommand
     */
    public static JMenuItem addMenuItem(JPopupMenu popupMenu, String menuName,
                               ActionListener actionListener, String actionCommand, KeyStroke keyStroke) {
        JMenuItem aboutMenuItem = initMenuItem(menuName, actionListener, actionCommand, keyStroke);
        popupMenu.add(aboutMenuItem);
        return aboutMenuItem;
    }

    private static JMenuItem initMenuItem(String menuName, ActionListener actionListener, String actionCommand, KeyStroke keyStroke) {
        JMenuItem aboutMenuItem = new JMenuItem(menuName);
        if (Objects.nonNull(keyStroke)) {
            aboutMenuItem.setAccelerator(keyStroke);
        }
        aboutMenuItem.setActionCommand(actionCommand);
        aboutMenuItem.addActionListener(actionListener);
        return aboutMenuItem;
    }

    public static void addPopupMenu(Component component, final JPopupMenu popup) {
        addPopupMenu(component, popup, DEFAULT_INTERCEPTOR);
    }

    /**
     * 添加弹出菜单
     * @param component
     * @param popup
     */
    public static <T extends Component> void addPopupMenu(T component, final JPopupMenu popup,
                                        Interceptor<T> interceptor) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (component instanceof JButton){
                    showMenu(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                interceptor.showBefore(component, popup, e);
                interceptor.show(component, popup, e);
                interceptor.showAfter(component, popup, e);
            }
        });
    }

    private static final Interceptor DEFAULT_INTERCEPTOR = new Interceptor(){};

    public interface Interceptor<T> {
        default void showBefore(T component, JPopupMenu popup, MouseEvent e){

        }

        default void show(T component, JPopupMenu popup, MouseEvent e){
            popup.show(e.getComponent(), e.getX(), e.getY());
        }

        default void showAfter(T component, JPopupMenu popup, MouseEvent e){

        }
    }
}
