package com.platform.auto;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.platform.auto.config.Config;
import com.platform.auto.config.DbEntity;
import com.platform.auto.jdbc.Connection;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AutoGenerateToolWindowContent {

    private static final Logger logger = AutoLogger.getLogger(Connection.class);

    @Getter
    private final JPanel parentPanel = new JPanel();
    private final JPanel contentPanel = new JPanel();

    // 表名称输入框
    private final JTextField tableNameFilter = new JTextField(20);
    // 表名称列表
    private List<JButton> tableNameButtonList = new ArrayList<>();

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
        init(project);
        initStartAsync();
        parentPanel.setLayout(new BorderLayout(20, 20));
        parentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        try {
            createContentPanel();
            parentPanel.add(contentPanel, BorderLayout.PAGE_START);
        } catch (Exception e) {
        }
    }

    @NotNull
    private void createContentPanel() throws Exception {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 垂直排列
        contentPanel.add(tableNameFilter);

        // 添加 ActionListener 来监听回车键
        tableNameFilter.addActionListener(e -> {
            // 当按下回车键时执行的操作
            Config.getLocal().setFilterTableNameText(tableNameFilter.getText());
            Config.refreshLocal();
            showTableName();
        });
    }

    /**
     * 将按钮添加到列表
     **/
    private void addTableName() {
        for (DbEntity dbEntity : Config.getLocal().dbInfoList) {
            for (String tableName : dbEntity.tableNameList) {
                JButton button = new JButton(String.format("%s.%s", dbEntity.dbName, tableName));
                button.setName(tableName);
                tableNameButtonList.add(button);
                contentPanel.add(button);
                // 双击事件
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            startGenerateAsync(button.getName());
                        }
                    }
                });
            }
        }
        showTableName();
    }

    /**
     * 满足要求的按钮, 显示
     **/
    private void showTableName() {
        if (StringUtils.isBlank(Config.getLocal().getFilterTableNameText())) {
            return;
        }
        for (JButton button : tableNameButtonList) {
            if (StringUtils.containsIgnoreCase(button.getName(), Config.getLocal().getFilterTableNameText())) {
                button.setVisible(true);
            } else {
                button.setVisible(false);
            }
        }
    }

    public void startGenerateAsync(String tableName) {
        new Thread(() -> {
            try {
                Application.start(List.of(tableName));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).start();
    }

    public void initStartAsync() {
        new Thread(this::initTableList).start();
    }


    public void init(Project project) {
        try {
            Config.init(project);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 网络走异步
     **/
    public void initTableList() {
        try {
            Config.initLocalData();
            addTableName();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
