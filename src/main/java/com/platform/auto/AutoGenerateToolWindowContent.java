package com.platform.auto;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.platform.auto.config.Config;
import com.platform.auto.config.DbEntity;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import com.platform.auto.ui.ComboBoxItem;
import com.platform.auto.ui.ComboBoxRenderer;
import lombok.Getter;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import java.util.concurrent.atomic.AtomicLong;

public class AutoGenerateToolWindowContent {

    private static final Logger logger = AutoLogger.getLogger(AutoGenerateToolWindowContent.class);

    private AtomicBoolean runFalg = new AtomicBoolean(false);
    private AtomicLong lastTime = new AtomicLong(0L);

    private static final Color SELECTED_COLOR = new Color(46, 67, 110);

    @Getter
    private final JBPanel parentPanel = new JBPanel();
    private final JBPanel contentPanel = new JBPanel();
    private final JBPanel buttonPanel = new JBPanel();

    // 刷新框
    private final JBLabel refresh = new JBLabel("REFRESH", AllIcons.General.InlineRefresh, JLabel.LEFT);
    private JBPanel refreshParent = null;
    // 生成所有的按钮
    private final JBLabel generateAll = new JBLabel("generate after filter", AllIcons.Actions.Execute, JLabel.LEFT);
    private JBPanel generateAllParent = null;
    // 数据库名称显示框
    private final ComboBox<ComboBoxItem> dbNameComboBox = new ComboBox<>();

    // 表名称输入框
    private final JBTextField tableNameFilter = new JBTextField(25); // 设置列数限制
    // 表名称列表
    private List<JBPanel> tableNamePanelList = new ArrayList<>();

    private Icon loadingIcon = null;

    private ToolWindow toolWindow = null;
    private Project project = null;

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
        refresh.setName(refresh.getText());
        generateAll.setName(generateAll.getText());
        loadingIcon = new ImageIcon(getClass().getResource("/icons/loading_dark.gif"));
        this.toolWindow = toolWindow;
        this.project = project;
        init(project);
        initStartAsync();
        parentPanel.setLayout(new BorderLayout(20, 20));
        parentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        try {
            createContentPanel();
            JBScrollPane jbScrollPane = new JBScrollPane(buttonPanel);
            jbScrollPane.setBorder(null);
            contentPanel.add(jbScrollPane);
            parentPanel.add(contentPanel, BorderLayout.PAGE_START);
        } catch (Exception e) {
            // 处理异常
        }
    }

    @NotNull
    private void createContentPanel() throws Exception {
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 垂直排列
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // 垂直排列
        buttonPanel.setBorder(null);
        // 下拉选择框
        addComponentToContent(dbNameComboBox, true);
        // 添加鼠标事件监听器
        refresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    buttonSelected(refreshParent);
                }
                if (e.getClickCount() == 2) {
                    logger.info("refresh");
                    initStartAsync();
                }
            }
        });
        refreshParent = addComponentToContent(refresh, true);

        // table name filter
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(AllIcons.Actions.Find), BorderLayout.WEST); // 图标在左侧
        panel.add(tableNameFilter, BorderLayout.CENTER); // 文本框在中间
        tableNameFilter.getEmptyText().setText("table name filter");
        tableNameFilter.grabFocus();
        tableNameFilter.setText(Config.getLocal().getFilterTableNameText());
        tableNameFilter.addActionListener(e -> {
            Config.getLocal().setFilterTableNameText(tableNameFilter.getText());
            Config.refreshLocal();
            showTableName();
        });
        addComponentToContent(panel, false);

        generateAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    buttonSelected(generateAllParent);
                }
                if (e.getClickCount() == 2) {
                    List<String> buttonNameList = new ArrayList<>();
                    for (JBPanel button : tableNamePanelList) {
                        if (button.isVisible()) {
                            buttonNameList.add(button.getName());
                        }
                    }
                    logger.info("generateAll");
                    generateAll.setIcon(loadingIcon);
                    startGenerateAsync(buttonNameList);
                }
            }
        });
        generateAllParent = addComponentToContent(generateAll, true);
        // 分割符号
        contentPanel.add(new JBPanel());

        logger.info("create content panel");
    }

    /**
     * 添加组件到 content
     **/
    private JBPanel addComponentToContent(JComponent component, boolean needCursor) {
        return addComponentToPanel(component, contentPanel, needCursor);
    }

    private JBPanel addComponentToButton(JComponent component) {
        return addComponentToPanel(component, buttonPanel, true);
    }

    private JBPanel addComponentToPanel(JComponent component, JBPanel panel, boolean needCursor) {
        // 添加按钮和其他组件
        JBPanel out = new JBPanel();
        out.setLayout(new BorderLayout());
        out.add(component, BorderLayout.WEST);
        if (needCursor) {
            component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        out.setBorder(BorderFactory.createEmptyBorder(5, 3, 5, 3));
        panel.add(out);
        return out;
    }

    private void buttonSelected(JComponent component) {
        Color defaultColor = UIManager.getColor("Button.background");
        refreshParent.setBackground(defaultColor);
        generateAllParent.setBackground(defaultColor);
        for (JBPanel button : tableNamePanelList) {
            button.setBackground(defaultColor);
        }
        component.setBackground(SELECTED_COLOR);
    }

    /**
     * db name
     **/
    private void addDbName() {
        logger.info("addDbName");
        dbNameComboBox.removeAllItems();
        dbNameComboBox.setModel(new DefaultComboBoxModel<>(
                new Vector<>(
                        Config.getLocal().dbInfoList.stream().map(dbEntity -> new ComboBoxItem(dbEntity.dbName, AllIcons.Nodes.DataSchema)).toList()
                )
        ));
        dbNameComboBox.setRenderer(new ComboBoxRenderer());

        dbNameComboBox.setSelectedItem(new ComboBoxItem(Config.getLocal().selectedDbName));
        dbNameComboBox.addActionListener(e -> {
            if (dbNameComboBox.getSelectedItem() != null) {
                dbNameComboBox.setEnabled(false);
                Config.getLocal().selectedDbName = ((ComboBoxItem) dbNameComboBox.getSelectedItem()).text;
                logger.info("db_name_selected: " + Config.getLocal().selectedDbName);
                Config.refreshLocal();
                addTableName();
            }
        });

        addTableName();
    }

    /**
     * 将 table name 加入到列表中
     **/
    private void addTableName() {
        // 1.5 秒以内
        if (System.currentTimeMillis() - lastTime.get() < 15 * 100L) {
            if (dbNameComboBox.getSelectedItem() != null) {
                String dbName = ((ComboBoxItem) dbNameComboBox.getSelectedItem()).text;
                if (dbName.equals(Config.getLocal().selectedDbName)) {
                    logger.info("repeat_db_name: {}", dbName);
                    return;
                }
            }
        }

        lastTime.set(System.currentTimeMillis());
        logger.info("addTableName-start");
        for (JBPanel button : tableNamePanelList) {
            buttonPanel.remove(button);
        }
        tableNamePanelList.clear();

        logger.info("addTableName-selectedDbName: {}", Config.getLocal().selectedDbName);

        for (DbEntity dbEntity : Config.getLocal().dbInfoList) {
            if (!StringUtils.equalsAnyIgnoreCase(Config.getLocal().selectedDbName, dbEntity.dbName)) {
                continue;
            }
            for (String tableName : dbEntity.tableNameList) {
                JBLabel tableNameLabel = new JBLabel(tableName, AllIcons.Nodes.DataTables, JLabel.LEFT);
                JBPanel temp = addComponentToButton(tableNameLabel);
                tableNameLabel.setName(tableName);
                temp.setName(tableName);
                tableNamePanelList.add(temp);
                logger.info("tableName_create: {}", tableName);
                tableNameLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 1) {
                            buttonSelected(temp);
                        }
                        if (e.getClickCount() == 2) {
                            logger.info("startGenerateAsync: {}", tableNameLabel.getName());
                            startGenerateAsync(List.of(tableNameLabel.getName()));
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
        dbNameComboBox.setEnabled(true);
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
        for (JBPanel button : tableNamePanelList) {
            if (tableNameList.contains(button.getName())) {
                ((JBLabel) button.getComponent(0)).setIcon(loadingIcon);
            }
        }
        dbNameComboBox.setEnabled(false);
        new Thread(() -> {
            try {
                runFalg.set(true);
                Application.start(tableNameList);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                dbNameComboBox.setEnabled(true);
                runFalg.set(false);
                for (JBPanel button : tableNamePanelList) {
                    ((JBLabel) button.getComponent(0)).setIcon(AllIcons.Nodes.DataTables);
                }
                generateAll.setIcon(AllIcons.Actions.Execute);
                logger.info("startGenerateAsync.end: {}", String.join(",", tableNameList));
            }
        }).start();
    }

    public void initStartAsync() {
        if (runFalg.get()) {
            return;
        }
        runFalg.set(true);
        refresh.setIcon(loadingIcon);
        dbNameComboBox.setEnabled(false);
        logger.info("initStartAsync");
        new Thread(() -> {
            initTableList();
            runFalg.set(false);
            refresh.setIcon(AllIcons.General.InlineRefresh);
            dbNameComboBox.setEnabled(true);
        }).start();
    }

    public void init(Project project) {
        try {
            Config.init(project);
        } catch (Exception ex) {
            logger.info(ex);
        }
    }

    public void initTableList() {
        try {
            logger.info("initTableList");
            Config.init(project);
            Config.initLocalData();
            addDbName();
        } catch (Exception ex) {
            logger.info(ex);
        }
    }
}
