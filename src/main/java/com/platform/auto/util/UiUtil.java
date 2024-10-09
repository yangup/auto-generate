package com.platform.auto.util;

import javax.swing.*;
import java.awt.*;

public class UiUtil {

    public static JPanel addComponentToPanel(JComponent component, JPanel panel, boolean needCursor) {
        // 添加按钮和其他组件
        JPanel out = new JPanel(new BorderLayout());
        out.add(component, BorderLayout.WEST);
        if (needCursor) {
            component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        out.setName(component.getName());
        out.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 3));
        panel.add(out);
        return out;
    }

    public static void setParentBackgroundColor(JComponent childComponent, Color defaultColor) {
        childComponent.getParent().setBackground(defaultColor);
    }

    public static void setParentVisible(JComponent childComponent, boolean visible) {
        childComponent.getParent().setVisible(visible);
    }

}
