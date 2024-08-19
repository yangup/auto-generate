package com.platform.auto;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.platform.auto.config.Config;
import com.platform.auto.config.DbEntity;
import com.platform.auto.jdbc.Connection;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
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
        initStartAsync(project);
        parentPanel.setLayout(new BorderLayout(20, 20));
        parentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        try {
            createContentPanel();
//            contentPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
            parentPanel.add(contentPanel, BorderLayout.PAGE_START);
//            parentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
        } catch (Exception e) {
        }
    }

    @NotNull
    private void createContentPanel() throws Exception {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 垂直排列
        contentPanel.add(tableNameFilter);

    }

    private void addTableName() {
        for (DbEntity dbEntity : Config.getLocal().dbInfoList) {
            for (String tableName : dbEntity.tableNameList) {
                JButton button = new JButton(String.format("%s.%s", dbEntity.dbName, tableName));
                tableNameButtonList.add(button);
                contentPanel.add(button);
            }
        }
    }

    public void initStartAsync(Project project) {
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
