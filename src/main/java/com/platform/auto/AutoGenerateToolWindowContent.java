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
import java.util.*;
import java.util.List;

public class AutoGenerateToolWindowContent {

    private static final Logger logger = AutoLogger.getLogger(Connection.class);

    @Getter
    private final JPanel contentPanel = new JPanel();
    // 数据刷新框
    private final JButton refreshButton = new JButton("refresh");
    // 表名称输入框
    private final JTextField tableNameFilter = new JTextField(20);
    // 符合表名称的 所有表名称信息
    private Color colorDefault = Color.RED;

    private JPanel allPanel = new JPanel();
    private JPanel tableNamePanel = new JPanel();
    private List<JButton> tableNameButtonList = new ArrayList<>();

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
        init(project);
        initTableList();
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        try {
            createPanel();
            contentPanel.add(allPanel, BorderLayout.PAGE_START);
        } catch (Exception e) {
        }
    }

    @NotNull
    private void createPanel() throws Exception {
        allPanel.setLayout(new BoxLayout(allPanel, BoxLayout.Y_AXIS)); // 垂直排列
        // 刷新按钮
        allPanel.add(refreshButton);
        refreshButton.addActionListener(e -> {
            try {
                Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
                initTableList();
            } catch (Exception ex) {
                logger.info(AutoLogger.getExceptionInfo(ex));
            }
        });
        // 添加 ActionListener 来监听回车键
        tableNameFilter.addActionListener(e -> {
            // 当按下回车键时执行的操作
            Config.getLocal().setFilterTableNameText(tableNameFilter.getText());
            refreshTableNameList();
        });
        allPanel.add(tableNameFilter);
        refreshTableNameList();
        allPanel.add(tableNamePanel);
    }

    private void refreshTableNameList() {
        for (DbEntity dbEntity : Config.getLocal().dbInfoList) {
            if (StringUtils.equalsAnyIgnoreCase(Config.getConfig().jdbc.database, dbEntity.dbName)) {
                for (String tableName : dbEntity.tableNameList) {
                    JButton button = new JButton(tableName);
                    button.setBackground(colorDefault);
                    button.setVisible(true);
                    tableNamePanel.add(button);
                    tableNameButtonList.add(button);

                    button.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() == 2) {
                                try {
                                    button.setBackground(Color.blue);
                                    Application.start(List.of(button.getText()));
                                    refreshTableNameList();
                                } catch (Exception ex) {
                                    logger.info(AutoLogger.getExceptionInfo(ex));
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public void init(Project project) {
        try {
            Config.init(project);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void initTableList() {
        try {
            Config.initLocalData();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
