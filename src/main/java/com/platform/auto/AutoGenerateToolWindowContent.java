package com.platform.auto;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.platform.auto.config.Config;
import com.platform.auto.config.DbEntity;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoGenerateToolWindowContent {

    private static final Logger logger = AutoLogger.getLogger(AutoGenerateToolWindowContent.class);

    private AtomicBoolean runFalg = new AtomicBoolean(false);

    @Getter
    private final JPanel parentPanel = new JPanel();
    private final JPanel contentPanel = new JPanel();
    private final JPanel buttonPanel = new JPanel();

    // 刷新框
    private final JButton refresh = new JButton("REFRESH");
    // 生成所有的按钮
    private final JButton generateAll = new JButton("GENERATE-ALL");
    // 数据库名称显示框
    private final ComboBox dbNameComboBox = new ComboBox();

    // 表名称输入框
    private final JBTextField tableNameFilter = new JBTextField(25); // 设置列数限制
    // 表名称列表
    private List<JPanel> tableNamePanelList = new ArrayList<>();

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
        init(project);
        initStartAsync();
        parentPanel.setLayout(new BorderLayout(20, 20));
        parentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        try {
            createContentPanel();
            contentPanel.add(new JBScrollPane(buttonPanel));
            parentPanel.add(contentPanel, BorderLayout.PAGE_START);
        } catch (Exception e) {
            // 处理异常
        }
    }

    @NotNull
    private void createContentPanel() throws Exception {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 垂直排列
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // 垂直排列
        // 添加鼠标事件监听器
        refresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    logger.info("refresh");
                    initStartAsync();
                }
            }
        });
        addComponentToContent(refresh);
        dbNameComboBox.setEditable(false);
        addComponentToContent(dbNameComboBox);
        generateAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    List<String> buttonNameList = new ArrayList<>();
                    for (JPanel button : tableNamePanelList) {
                        if (button.isVisible()) {
                            buttonNameList.add(button.getName());
                        }
                    }
                    logger.info("generateAll");
                    startGenerateAsync(buttonNameList);
                }
            }
        });
        addComponentToContent(generateAll);
        addComponentToContent(tableNameFilter);

        tableNameFilter.getEmptyText().setText("table name filter");
        tableNameFilter.grabFocus();
        tableNameFilter.setText(Config.getLocal().getFilterTableNameText());
        tableNameFilter.addActionListener(e -> {
            Config.getLocal().setFilterTableNameText(tableNameFilter.getText());
            Config.refreshLocal();
            showTableName();
        });
        logger.info("create content panel");
    }

    /**
     * 添加组件到 content
     **/
    private JPanel addComponentToContent(JComponent component) {
        return addComponentToPanel(component, contentPanel);
    }

    private JPanel addComponentToButton(JComponent component) {
        return addComponentToPanel(component, buttonPanel);
    }

    private JPanel addComponentToPanel(JComponent component, JPanel panel) {
        // 添加按钮和其他组件
        JPanel temp = new JPanel();
        temp.setLayout(new BorderLayout());
//        temp.setBorder(BorderFactory.createLineBorder(Color.PINK, 2));
        temp.add(component, BorderLayout.WEST);
        if (component instanceof JButton || component instanceof ComboBox) {
            component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        temp.setBorder(new EmptyBorder(3, 3, 3, 3));
        panel.add(temp);
        return temp;
    }


    /**
     * db name
     **/
    private void addDbName() {
        logger.info("addDbName");
        dbNameComboBox.removeAllItems();
        dbNameComboBox.setModel(new DefaultComboBoxModel<>(
                new Vector<>(Config.getLocal().dbInfoList.stream().map(DbEntity::getDbName).toList())
        ));

        dbNameComboBox.setSelectedItem(Config.getLocal().selectedDbName);
        dbNameComboBox.addActionListener(e -> {
            Config.getLocal().selectedDbName = (String) dbNameComboBox.getSelectedItem();
            logger.info("db name selected: " + dbNameComboBox.getSelectedItem());
            addTableName();
        });
        addTableName();
    }

    /**
     * 将 table name 加入到列表中
     **/
    private void addTableName() {
        logger.info("addTableName-start");
        for (JPanel button : tableNamePanelList) {
            buttonPanel.remove(button);
        }
        tableNamePanelList.clear();

        for (DbEntity dbEntity : Config.getLocal().dbInfoList) {
            if (!StringUtils.equalsAnyIgnoreCase(Config.getLocal().selectedDbName, dbEntity.dbName)) {
                continue;
            }
            for (String tableName : dbEntity.tableNameList) {
                JButton button = new JButton(tableName);
                JPanel temp = addComponentToButton(button);
                temp.setName(tableName);
                tableNamePanelList.add(temp);
                logger.info("tableName_create: {}", tableName);
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            logger.info("startGenerateAsync: {}", button.getName());
                            startGenerateAsync(List.of(button.getName()));
                        }
                    }
                });
            }
        }
        logger.info("addTableName-end");
        showTableName();
    }

    private void showTableName() {
        for (JComponent button : tableNamePanelList) {
            if (StringUtils.isBlank(Config.getLocal().getFilterTableNameText())
                    || StringUtils.containsIgnoreCase(button.getName(), Config.getLocal().getFilterTableNameText())) {
                button.setVisible(true);
            } else {
                button.setVisible(false);
            }
        }
        logger.info("showTableName");
    }

    /**
     * 异步生成 文件
     **/
    public void startGenerateAsync(List<String> tableNameList) {
        if (ObjectUtils.isEmpty(tableNameList)) {
            return;
        }
        if (runFalg.get()) {
            logger.info("running");
            logger.info("can not run: {}", String.join(",", tableNameList));
            return;
        }
        logger.info("startGenerateAsync: {}", String.join(",", tableNameList));
        for (JPanel button : tableNamePanelList) {
            if (tableNameList.contains(button.getName())) {
                button.setBackground(Color.RED);
            } else {
                button.setBackground(UIManager.getColor("Button.background"));
            }
        }
        new Thread(() -> {
            try {
                runFalg.set(true);
//                Thread.sleep(1000 * 5);
                Application.start(tableNameList);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                runFalg.set(false);
                for (JPanel button : tableNamePanelList) {
                    button.setBackground(UIManager.getColor("Button.background"));
                }
                logger.info("startGenerateAsync.end: {}", String.join(",", tableNameList));
            }
        }).start();
    }

    public void initStartAsync() {
        new Thread(() -> {
            refresh.setBackground(Color.RED);
            initTableList();
            refresh.setBackground(UIManager.getColor("Button.background"));
        }).start();
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
            addDbName();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
