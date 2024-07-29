package com.platform.auto;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.platform.auto.jdbc.Connection;
import com.platform.auto.jdbc.Constant;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutoGenerateToolWindowContent {

    private static final Logger logger = AutoLogger.getLogger(Connection.class);

    @Getter
    private final JPanel contentPanel = new JPanel();
    private final JButton runButton = new JButton("Run");
    private final JButton cancelButton = new JButton("Cancel");
    private List<String> dbNameList;
    private List<String> tableNameList;
    private List<JButton> tableNameButtonList;

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        initApplication(project);
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JPanel content = createCalendarPanel();
        content.setBorder(BorderFactory.createLineBorder(Color.RED));
//        contentPanel.setSize(200, tableNameList.size() * 20);
        contentPanel.add(content, BorderLayout.PAGE_START);
        contentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
    }

    @NotNull
    private JPanel createCalendarPanel() {
        initTableList();
        JPanel buttonPanel = new JPanel();
//        buttonPanel.setLayout(new GridLayout(3, 2)); // 3行2列
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // 垂直排列
        tableNameButtonList = new ArrayList<>();
        tableNameList.forEach(tableName -> {
            JButton button = new JButton(tableName);
            tableNameButtonList.add(button);
            buttonPanel.add(button);
        });
        // 创建一个 JTextArea 对象
//        logArea.setRows(30);
//        logArea.setColumns(40);
//        // 创建一个红色线条边框
//        Border border = BorderFactory.createLineBorder(new Color(60, 62, 64));
//        // 设置 JTextArea 的边框
//        logArea.setBorder(border);
//        // 将 JTextArea 放置在 JScrollPane 中，以支持滚动
//        JScrollPane scrollPane = new JScrollPane(logArea);
//        calendarPanel.add(scrollPane);
        return buttonPanel;
    }

    @NotNull
    private JPanel createControlsPanel(ToolWindow toolWindow) {
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(runButton);
        runButton.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        runButton.addActionListener(e -> {
            try {
                Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
                Application.start();
            } catch (Exception ex) {
                logger.info(AutoLogger.getExceptionInfo(ex));
            }
        });
        controlsPanel.add(cancelButton);
        cancelButton.addActionListener(e -> toolWindow.hide(null));
        return controlsPanel;
    }

    public void initApplication(Project project) {
        try {
            Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
            Constant.project = project;
            Application.init();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void initTableList() {
        try {
            Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
            Application.initStart();
            String sql = "SELECT distinct col.TABLE_SCHEMA\n" +
                    "FROM `information_schema`.`tables` col\n" +
                    "WHERE col.table_schema NOT IN ('information_schema', 'performance_schema', 'mysql', 'sys')\n" +
                    "order by 1 asc";
            this.dbNameList = Connection.getData("TABLE_SCHEMA", sql);
            sql = "SELECT distinct col.TABLE_NAME\n" +
                    "FROM `information_schema`.`tables` col\n" +
                    "WHERE col.table_schema NOT IN ('information_schema', 'performance_schema', 'mysql', 'sys')\n" +
                    "and col.table_schema IN ('" + Constant.getConfig().get("jdbc").get("database").asText() + "')\n" +
                    "order by 1 asc";
            this.tableNameList = Connection.getData("TABLE_NAME", sql);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
