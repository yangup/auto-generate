package com.platform.auto.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

// 自定义渲染器，用于显示图标和文本
public class ComboBoxRenderer extends JLabel implements ListCellRenderer<ComboBoxItem> {

    public ComboBoxRenderer() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // 设置手型光标
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // 当鼠标移出时恢复默认光标
                setCursor(Cursor.getDefaultCursor());
            }
        });
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
        return this;
    }
}
