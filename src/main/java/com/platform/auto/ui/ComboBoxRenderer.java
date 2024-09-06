package com.platform.auto.ui;

import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// 自定义渲染器，用于显示图标和文本
public class ComboBoxRenderer extends JLabel implements ListCellRenderer<ComboBoxItem> {

    private static final Logger logger = AutoLogger.getLogger(ComboBoxRenderer.class);

    public boolean isListened = false;

    public ComboBoxRenderer() {
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ComboBoxItem> list, ComboBoxItem value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null) {
            setText(value.getText());
            setIcon(value.getIcon());
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setOpaque(true);


//        logger.info("getListCellRendererComponent");
        if (!isListened) {
//            logger.info("getListCellRendererComponent_isListered");
            // 为 JList 添加鼠标监听器
            list.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                logger.info("HAND_CURSOR");
                }
            });
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    list.setCursor(Cursor.getDefaultCursor());
//                logger.info("getDefaultCursor");
                }
            });
        }
        isListened = true;

        return this;
    }
}
