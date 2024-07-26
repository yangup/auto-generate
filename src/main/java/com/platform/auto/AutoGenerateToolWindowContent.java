package com.platform.auto;

import com.intellij.openapi.wm.ToolWindow;
import com.platform.auto.sys.log.AutoLogger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AutoGenerateToolWindowContent {

    @Getter
    private final JPanel contentPanel = new JPanel();
    private final JButton saveButton = new JButton("Save");
    private final JButton runButton = new JButton("Run");
    private final JButton cancelButton = new JButton("Cancel");
    private final JTextArea logArea = new JTextArea();

    public AutoGenerateToolWindowContent(ToolWindow toolWindow) {
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        JPanel content = createCalendarPanel();
        content.setBorder(BorderFactory.createLineBorder(Color.RED));
        contentPanel.add(content, BorderLayout.PAGE_START);
        contentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
        AutoLogger.logArea = logArea;
    }

    @NotNull
    private JPanel createCalendarPanel() {
        JPanel calendarPanel = new JPanel();
        // 创建一个 JTextArea 对象
        logArea.setRows(30);
        logArea.setColumns(40);
        // 创建一个红色线条边框
        Border border = BorderFactory.createLineBorder(new Color(60, 62, 64));
        // 设置 JTextArea 的边框
        logArea.setBorder(border);
        // 将 JTextArea 放置在 JScrollPane 中，以支持滚动
        JScrollPane scrollPane = new JScrollPane(logArea);
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
                Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
                Application.start();
            } catch (Exception ex) {
                logArea.setText(logArea.getText() + "\n" + getExceptionInfo(ex));
            }
        });
        controlsPanel.add(cancelButton);
        cancelButton.addActionListener(e -> toolWindow.hide(null));
        return controlsPanel;
    }

    public static String getExceptionInfo(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

}
