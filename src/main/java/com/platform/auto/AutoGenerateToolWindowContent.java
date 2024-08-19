package com.platform.auto;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.platform.auto.config.Config;
import com.platform.auto.config.DbEntity;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AutoGenerateToolWindowContent {

    private static final Logger logger = AutoLogger.getLogger(AutoGenerateToolWindowContent.class);

    @Getter
    private final JPanel parentPanel = new JPanel();
    private final JPanel contentPanel = new JPanel();

    // 刷新框
    private final JButton refresh = new JButton("REFRESH");
    // 生成所有的按钮
    private final JButton generateAll = new JButton("GENERATE-ALL");
    // 数据库名称显示框
    private final JLabel dbNameText = new JLabel();

    // 表名称输入框
    private final JTextField tableNameFilter = new JTextField(40); // 设置列数限制
    // 表名称列表
    private List<JButton> tableNameButtonList = new ArrayList<>();

    private GridBagConstraints gbc = new GridBagConstraints();

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
        init(project);
        initStartAsync();
        parentPanel.setLayout(new BorderLayout(20, 20));
        parentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // 设置 GridBagConstraints 配置
        gbc.fill = GridBagConstraints.NONE; // 不扩展组件，保持原大小
        gbc.anchor = GridBagConstraints.WEST; // 左对齐
        gbc.gridx = 0; // 第一列
        gbc.gridy = GridBagConstraints.RELATIVE; // 自动递增行号
        gbc.weightx = 0; // 不让组件扩展占满整个水平空间

        try {
            createContentPanel();
            parentPanel.add(contentPanel, BorderLayout.PAGE_START);
        } catch (Exception e) {
            // 处理异常
        }
    }

    @NotNull
    private void createContentPanel() throws Exception {
        contentPanel.setLayout(new GridBagLayout());

        // 添加鼠标事件监听器
        refresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    initStartAsync();
                }
            }
        });
        generateAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    List<String> buttonNameList = new ArrayList<>();
                    for (JButton button : tableNameButtonList) {
                        if (button.isVisible()) {
                            buttonNameList.add(button.getName());
                        }
                    }
                    startGenerateAsync(buttonNameList);
                }
            }
        });

        // 添加按钮和其他组件
        contentPanel.add(refresh, gbc);
        contentPanel.add(generateAll, gbc);
        contentPanel.add(dbNameText, gbc);
        contentPanel.add(tableNameFilter, gbc);

        tableNameFilter.grabFocus();
        tableNameFilter.setText(Config.getLocal().getFilterTableNameText());
        tableNameFilter.addActionListener(e -> {
            Config.getLocal().setFilterTableNameText(tableNameFilter.getText());
            Config.refreshLocal();
            showTableName();
        });
    }

    private void addTableName() {
        for (JButton button : tableNameButtonList) {
            contentPanel.remove(button);
        }
        tableNameButtonList.clear();

        for (DbEntity dbEntity : Config.getLocal().dbInfoList) {
            dbNameText.setText(dbEntity.dbName);
            for (String tableName : dbEntity.tableNameList) {
                JButton button = new JButton(tableName);
                button.setName(tableName);

                // 调整按钮大小
                Dimension preferredSize = button.getPreferredSize();
                button.setMaximumSize(preferredSize);
                button.setPreferredSize(preferredSize);

                tableNameButtonList.add(button);
                contentPanel.add(button, gbc);

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            startGenerateAsync(List.of(button.getName()));
                        }
                    }
                });
            }
        }
        showTableName();
    }

    private void showTableName() {
        for (JButton button : tableNameButtonList) {
            if (StringUtils.isBlank(Config.getLocal().getFilterTableNameText())
                    || StringUtils.containsIgnoreCase(button.getName(), Config.getLocal().getFilterTableNameText())) {
                button.setVisible(true);
            } else {
                button.setVisible(false);
            }
        }
    }

    public void startGenerateAsync(List<String> tableNameList) {
        if (ObjectUtils.isEmpty(tableNameList)) {
            return;
        }
        new Thread(() -> {
            try {
                Application.start(tableNameList);
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

    public void initTableList() {
        try {
            Config.initLocalData();
            addTableName();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
