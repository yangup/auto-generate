package com.platform.auto;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.platform.auto.config.Config;
import com.platform.auto.config.LocalEntity;
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
    // 数据库 名称输入框
    private final JTextField dbFilter = new JTextField(20);
    // 符合数据库名称的 所有数据库列表信息
    private List<JButton> dbButtonList = new ArrayList<>();
    // 表名称输入框
    private final JTextField tableNameInput = new JTextField(20);
    // 符合表名称的 所有表名称信息
    private List<JButton> tableNameButtonList = new ArrayList<>();
    private Color colorDefault = new JButton().getBackground();

    private JPanel allPanel = new JPanel();
    private JPanel dbPanel = new JPanel();
    private JPanel tableNamePanel = new JPanel();

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
        dbFilter.addActionListener(e -> {
            // 当按下回车键时执行的操作
            Config.getLocal().setFilterDbNameText(dbFilter.getText());
            refreshDbList();
        });
        allPanel.add(dbFilter);
        refreshDbList();
        allPanel.add(dbPanel);

//        int count = 0;
//        for (LocalEntity.TableEntity tableEntity : Config.getLocal().tableList) {
//            count++;
//            JButton button = new JButton(tableEntity.tableSchema);
////            JFXButton button = new JFXButton(tableEntity.tableSchema);
//            tableNameButtonList.add(button);
//            buttonPanel.add(button);
//            if (count > 5) {
//                break;
//            }
//        }
//
//        count = 0;
//        tableNameButtonList = new ArrayList<>();
//        for (LocalEntity.TableEntity tableEntity : Config.getLocal().tableList) {
//            count++;
//            JButton button = new JButton(tableEntity.tableSchema);
////            JFXButton button = new JFXButton(tableEntity.tableSchema);
//            tableNameButtonList.add(button);
//            buttonPanel.add(button);
//            if (count > 5) {
//                break;
//            }
//        }
    }

    private void filterTableName() {
        Config.getLocal().setFilterTableNameText(tableNameInput.getText());
    }


    private void refreshDbList() {
        logger.info("refreshDbList");
        Set<String> dbNameList = new HashSet<>();
        for (LocalEntity.TableEntity table : Config.getLocal().tableList) {
//            if (StringUtils.isNotEmpty(Config.getLocal().getFilterDbNameText())) {
//                if (StringUtils.containsAnyIgnoreCase(table.tableSchema, Config.getLocal().getFilterDbNameText())) {
//                    dbNameList.add(table.tableSchema);
//                }
//            } else {
            dbNameList.add(table.tableSchema);
//            }
        }
//        // 查询出来的数据库名称列表
        List<String> data = new ArrayList<>(dbNameList);
        Collections.sort(data);
//        dbButtonList.clear();
//        dbPanel.removeAll();
        int count = 0;
        for (String dbName : data) {
            count++;
            JButton button = new JButton(dbName);
            button.setBackground(Color.RED);
            if (StringUtils.equals(Config.getLocal().getFilterDbNameText(), button.getText())) {
                button.setBackground(Color.BLUE);
            }
            if (StringUtils.equals(Config.getLocal().getSelectedDbName(), button.getText())) {
                button.setBackground(Color.LIGHT_GRAY);
            }
            dbButtonList.add(button);


            // 点击 button ， 背景蓝色
            button.addActionListener(e -> {
                Config.getLocal().setSelectedDbName(button.getText());
                refreshDbList();
            });

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Config.getLocal().setSelectedDbName(button.getText());
                        Config.getLocal().setFilterDbNameText(button.getText());
                        refreshDbList();
                    }
                }
            });

            if (count > 5) {
                break;
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
