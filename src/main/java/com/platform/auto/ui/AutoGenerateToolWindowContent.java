package com.platform.auto.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.platform.auto.Application;
import com.platform.auto.AutoGenerateToolWindowFactory;
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
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.*;

public class AutoGenerateToolWindowContent {

    private static final Logger logger = AutoLogger.getLogger(AutoGenerateToolWindowContent.class);

    private final AtomicBoolean runFlag = new AtomicBoolean(false);
    private final AtomicLong lastTime = new AtomicLong(0L);

    private static final Color SELECTED_COLOR = new Color(46, 67, 110);

    @Getter
    private final JPanel parentPanel = new JPanel();
    private final JPanel contentPanel = new JPanel();
    private final JPanel buttonPanel = new JPanel();

    // 刷新框
    private final JBLabel refresh = new JBLabel("REFRESH", AllIcons.General.InlineRefresh, JLabel.LEFT);
    private JPanel refreshParent = null;
    // 生成所有的按钮
    private final JBLabel generateAll = new JBLabel("Generate after filter", AllIcons.Actions.Execute, JLabel.LEFT);
    private JPanel generateAllParent = null;
    // 数据库名称显示框
    private final ComboBox<ComboBoxItem> dbNameComboBox = new ComboBox<>();
    private JPanel dbNameComboBoxPanel = null;

    // 表名称输入框
    private final JBTextField tableNameFilter = new JBTextField(25); // 设置列数限制
    private JPanel tableNameFilterPanel = null;
    // 表名称列表
    private final List<JPanel> tableNamePanelList = new ArrayList<>();

    private Icon loadingIcon = null;

    private ToolWindow toolWindow = null;
    private Project project = null;

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
        refresh.setName(refresh.getText());
        generateAll.setName(generateAll.getText());
        loadingIcon = new ImageIcon(requireNonNull(getClass().getResource("/icons/loading_dark.gif")));
        this.toolWindow = toolWindow;
        this.project = project;
        Config.initProject(this.project);
        parentPanel.setLayout(new BorderLayout(20, 20));
        parentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        createContentPanel();
        JBScrollPane jbScrollPane = new JBScrollPane(buttonPanel);
        jbScrollPane.setBorder(null);
        contentPanel.add(jbScrollPane);
        parentPanel.add(contentPanel, BorderLayout.PAGE_START);
    }

    @NotNull
    private void createContentPanel() {
        // panel
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // 垂直排列
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // 垂直排列
        buttonPanel.setBorder(null);
        // 添加鼠标事件监听器
        refresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    buttonSelected(refreshParent);
                }
                if (e.getClickCount() == 2) {
                    initStartAsync();
                }
            }
        });
        refreshParent = addComponentToContent(refresh, true);
        // 下拉选择框
        dbNameComboBoxPanel = addComponentToContent(dbNameComboBox, true);
        dbNameComboBoxPanel.setVisible(false);

        // table name filter
        tableNameFilterPanel = new JPanel(new BorderLayout());
        tableNameFilterPanel.add(new JLabel(AllIcons.Actions.Find), BorderLayout.WEST); // 图标在左侧
        tableNameFilterPanel.add(tableNameFilter, BorderLayout.CENTER); // 文本框在中间
        tableNameFilter.getEmptyText().setText("Table name filter");
        tableNameFilter.grabFocus();
        tableNameFilter.setText(Config.getLocal() == null ? "" : Config.getLocal().getFilterTableNameText());
        tableNameFilter.addActionListener(e -> {
            Config.getLocal().setFilterTableNameText(tableNameFilter.getText());
            Config.refreshLocal();
            showTableName();
        });
        addComponentToContent(tableNameFilterPanel, false);
        tableNameFilterPanel.setVisible(false);

        generateAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    buttonSelected(generateAllParent);
                }
                if (e.getClickCount() == 2) {
                    List<String> buttonNameList = new ArrayList<>();
                    for (JPanel button : tableNamePanelList) {
                        if (button.isVisible()) {
                            buttonNameList.add(button.getName());
                        }
                    }
                    if (ObjectUtils.isEmpty(buttonNameList)) {
                        return;
                    }
                    logger.info("generateAll");
                    generateAll.setIcon(loadingIcon);
                    startGenerateAsync(buttonNameList);
                }
            }
        });
        generateAllParent = addComponentToContent(generateAll, true);
        generateAllParent.setVisible(false);
        // 分割符号
        contentPanel.add(new JPanel());

        logger.info("create content panel");
        if (Config.existLocal()) {
            initStartAsync();
        }
    }

    /**
     * 添加组件到 content
     **/
    private JPanel addComponentToContent(JComponent component, boolean needCursor) {
        return addComponentToPanel(component, contentPanel, needCursor);
    }

    private JPanel addComponentToButton(JComponent component) {
        return addComponentToPanel(component, buttonPanel, true);
    }

    private JPanel addComponentToPanel(JComponent component, JPanel panel, boolean needCursor) {
        // 添加按钮和其他组件
        JPanel out = new JPanel(new BorderLayout());
        out.add(component, BorderLayout.WEST);
        if (needCursor) {
            component.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        out.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 3));
        panel.add(out);
        return out;
    }

    private void buttonSelected(JComponent component) {
        Color defaultColor = UIManager.getColor("Button.background");
        refreshParent.setBackground(defaultColor);
        generateAllParent.setBackground(defaultColor);
        for (JPanel button : tableNamePanelList) {
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
            if (dbNameComboBox.getSelectedItem() != null && StringUtils.equals(Config.getLocal().selectedDbName, ((ComboBoxItem) dbNameComboBox.getSelectedItem()).text)) {
                logger.info("repeat_db_name: {}", Config.getLocal().selectedDbName);
                return;
            }
        }

        lastTime.set(System.currentTimeMillis());
        logger.info("addTableName-start");
        for (JPanel button : tableNamePanelList) {
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
                JPanel temp = addComponentToButton(tableNameLabel);
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
        Config.initProject(this.project);
        if (ObjectUtils.isEmpty(tableNameList)) {
            return;
        }
        if (runFlag.get()) {
            logger.info("running");
            logger.info("can not run: {}", String.join(",", tableNameList));
            return;
        }
        logger.info("startGenerateAsync: {}", String.join(",", tableNameList));
        for (JPanel button : tableNamePanelList) {
            if (tableNameList.contains(button.getName())) {
                ((JBLabel) button.getComponent(0)).setIcon(loadingIcon);
            }
        }
        dbNameComboBox.setEnabled(false);
        new Thread(() -> {
            try {
                runFlag.set(true);
                Application.start(tableNameList);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                dbNameComboBox.setEnabled(true);
                runFlag.set(false);
                for (JPanel button : tableNamePanelList) {
                    ((JBLabel) button.getComponent(0)).setIcon(AllIcons.Nodes.DataTables);
                }
                generateAll.setIcon(AllIcons.Actions.Execute);
                // 刷新文件系统
                requireNonNull(ProjectFileIndex.getInstance(ProjectUtil.currentOrDefaultProject(project))
                        .getContentRootForFile(requireNonNull(ProjectUtil.currentOrDefaultProject(project).getProjectFile())))
                        .refresh(false, true);
                logger.info("startGenerateAsync.end: {}", String.join(",", tableNameList));
            }
        }).start();
    }

    public void initStartAsync() {
        Config.initProject(this.project);
        logger.info("refresh");
        if (runFlag.get()) {
            return;
        }
        runFlag.set(true);
        refresh.setIcon(loadingIcon);
        dbNameComboBox.setEnabled(false);
        logger.info("initStartAsync");
        new Thread(() -> {
            initTableList();
            runFlag.set(false);
            refresh.setIcon(AllIcons.General.InlineRefresh);
            dbNameComboBox.setEnabled(true);
            requireNonNull(ProjectFileIndex.getInstance(ProjectUtil.currentOrDefaultProject(project))
                    .getContentRootForFile(requireNonNull(ProjectUtil.currentOrDefaultProject(project).getProjectFile())))
                    .refresh(false, true);
        }).start();
    }

    public void initTableList() {
        try {
            logger.info("initTableList");
            generateAllParent.setVisible(true);
            tableNameFilterPanel.setVisible(true);
            dbNameComboBoxPanel.setVisible(true);
            Config.initProject(this.project);
            Config.initFile();
            Config.initLocalData();
            addDbName();
            tableNameFilter.setText(Config.getLocal() == null ? "" : Config.getLocal().getFilterTableNameText());
        } catch (Exception ex) {
            logger.info(ex);
        }
    }
}