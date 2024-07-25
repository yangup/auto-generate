package com.platform.auto;

import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class AutoGenerateToolWindowContent {

    private final JPanel contentPanel = new JPanel();
    private final JButton saveButton = new JButton("Save");
    private final JButton runButton = new JButton("Run");
    private final JButton cancelButton = new JButton("Cancel");
    private final JTextArea textArea = new JTextArea();

    public AutoGenerateToolWindowContent(ToolWindow toolWindow) {
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        JPanel content = createCalendarPanel();
        content.setBorder(BorderFactory.createLineBorder(Color.RED));
        contentPanel.add(content, BorderLayout.PAGE_START);
        contentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
    }

    @NotNull
    private JPanel createCalendarPanel() {
        JPanel calendarPanel = new JPanel();
        // 创建一个 JTextArea 对象
        textArea.setRows(30);
        textArea.setColumns(60);
        // 创建一个红色线条边框
        Border border = BorderFactory.createLineBorder(new Color(60, 62, 64));
        // 设置 JTextArea 的边框
        textArea.setBorder(border);
        // 将 JTextArea 放置在 JScrollPane 中，以支持滚动
        JScrollPane scrollPane = new JScrollPane(textArea);
        calendarPanel.add(scrollPane);
        return calendarPanel;
    }

    @NotNull
    private JPanel createControlsPanel(ToolWindow toolWindow) {
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(saveButton);
        controlsPanel.add(runButton);
        runButton.addActionListener(e -> {
            try {
                Application.start();
            } catch (Exception ex) {
                textArea.setText(ex.getMessage());
            }
        });
        controlsPanel.add(cancelButton);
        cancelButton.addActionListener(e -> toolWindow.hide(null));
        return controlsPanel;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

}
