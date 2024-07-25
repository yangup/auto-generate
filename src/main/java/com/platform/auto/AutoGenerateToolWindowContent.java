package com.platform.auto;

import com.intellij.openapi.wm.ToolWindow;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Calendar;
import java.util.Objects;

public class AutoGenerateToolWindowContent {

    //    private static final String CALENDAR_ICON_PATH = "/toolWindow/Calendar-icon.png";
//    private static final String TIME_ZONE_ICON_PATH = "/toolWindow/Time-zone-icon.png";
//    private static final String TIME_ICON_PATH = "/toolWindow/Time-icon.png";
//
    private final JPanel contentPanel = new JPanel();
    //    private final JLabel currentDate = new JLabel();
//    private final JLabel timeZone = new JLabel();
//    private final JLabel currentTime = new JLabel();
    private final JButton saveButton = new JButton("Save");
    private final JButton runButton = new JButton("Run");
    private final JButton cancelButton = new JButton("Cancel");

    public AutoGenerateToolWindowContent(ToolWindow toolWindow) {
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        JPanel content = createCalendarPanel();
        content.setBorder(BorderFactory.createLineBorder(Color.RED));
        contentPanel.add(content, BorderLayout.PAGE_START);
        contentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
//        updateCurrentDateTime();
    }

    @NotNull
    private JPanel createCalendarPanel() {
        JPanel calendarPanel = new JPanel();
        // 创建一个 JTextArea 对象
        JTextArea textArea = new JTextArea();
        textArea.setRows(30);
        textArea.setColumns(100);
        // 创建一个红色线条边框
        Border border = BorderFactory.createLineBorder(new Color(60, 62, 64));
        // 设置 JTextArea 的边框
        textArea.setBorder(border);
        // 将 JTextArea 放置在 JScrollPane 中，以支持滚动
        JScrollPane scrollPane = new JScrollPane(textArea);
        calendarPanel.add(scrollPane);
        return calendarPanel;
    }

    private void setIconLabel(JLabel label, String imagePath) {
        label.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath))));
    }

    @NotNull
    private JPanel createControlsPanel(ToolWindow toolWindow) {
        JPanel controlsPanel = new JPanel();
//        JButton refreshDateAndTimeButton = new JButton("Refresh");
//        refreshDateAndTimeButton.addActionListener(e -> updateCurrentDateTime());
        controlsPanel.add(saveButton);
//        JButton hideToolWindowButton = new JButton("Hide");
//        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        controlsPanel.add(runButton);
        controlsPanel.add(cancelButton);
        cancelButton.addActionListener(e -> toolWindow.hide(null));
        return controlsPanel;
    }

//    private void updateCurrentDateTime() {
//        Calendar calendar = Calendar.getInstance();
//        currentDate.setText(getCurrentDate(calendar));
//        timeZone.setText(getTimeZone(calendar));
//        currentTime.setText(getCurrentTime(calendar));
//    }
//
//    private String getCurrentDate(Calendar calendar) {
//        return calendar.get(Calendar.DAY_OF_MONTH) + "/"
//                + (calendar.get(Calendar.MONTH) + 1) + "/"
//                + calendar.get(Calendar.YEAR);
//    }
//
//    private String getTimeZone(Calendar calendar) {
//        long gmtOffset = calendar.get(Calendar.ZONE_OFFSET); // offset from GMT in milliseconds
//        String gmtOffsetString = String.valueOf(gmtOffset / 3600000);
//        return (gmtOffset > 0) ? "GMT + " + gmtOffsetString : "GMT - " + gmtOffsetString;
//    }
//
//    private String getCurrentTime(Calendar calendar) {
//        return getFormattedValue(calendar, Calendar.HOUR_OF_DAY) + ":" + getFormattedValue(calendar, Calendar.MINUTE);
//    }
//
//    private String getFormattedValue(Calendar calendar, int calendarField) {
//        int value = calendar.get(calendarField);
//        return StringUtils.leftPad(Integer.toString(value), 2, "0");
//    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

}
