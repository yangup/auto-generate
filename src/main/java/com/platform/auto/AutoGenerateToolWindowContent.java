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

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
        init(project);
        initStartAsync();
        parentPanel.setLayout(new BorderLayout(20, 20));
//        parentPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 20));
        try {
            createContentPanel();
            parentPanel.add(new JScrollPane(contentPanel), BorderLayout.PAGE_START);
        } catch (Exception e) {
            // 处理异常
        }
    }

    @NotNull
    private void createContentPanel() throws Exception {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 垂直排列
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

        addComponentToContent(refresh);
        addComponentToContent(generateAll);
        addComponentToContent(dbNameText);
        addComponentToContent(tableNameFilter);

        tableNameFilter.grabFocus();
        tableNameFilter.setText(Config.getLocal().getFilterTableNameText());
        tableNameFilter.addActionListener(e -> {
            Config.getLocal().setFilterTableNameText(tableNameFilter.getText());
            Config.refreshLocal();
            showTableName();
        });
    }

    private void addComponentToContent(JComponent component) {
        // 添加按钮和其他组件
        JPanel temp = new JPanel();
        temp.setLayout(new BorderLayout());
//        temp.setBorder(BorderFactory.createLineBorder(Color.PINK, 2));
        temp.add(component, BorderLayout.WEST);
        contentPanel.add(temp);
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

                tableNameButtonList.add(button);
                addComponentToContent(button);

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
