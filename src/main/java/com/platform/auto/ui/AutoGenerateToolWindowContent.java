package com.platform.auto.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.platform.auto.Application;
import com.platform.auto.AutoGenerateToolWindowFactory;
import com.platform.auto.config.Config;
import com.platform.auto.entity.DbEntity;
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
import static com.platform.auto.util.UiUtil.*;

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
    // 生成所有的按钮
    private final JBLabel generateAll = new JBLabel("Generate after filter", AllIcons.Actions.Execute, JLabel.LEFT);
    // 数据库名称显示框
    private final ComboBox<ComboBoxItem> dbNameComboBox = new ComboBox<>();

    // 表名称输入框
    private final JBTextField tableNameFilter = new JBTextField(25); // 设置列数限制

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
        contentPanel.add(buttonPanel);
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // 如果不需要水平滚动条
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        parentPanel.add(scrollPane, BorderLayout.PAGE_START);
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
                    buttonSelected(refresh);
                }
                if (e.getClickCount() == 2) {
                    initStartAsync();
                }
            }
        });
        addComponentToContent(refresh, true);
        // 下拉选择框
        addComponentToContent(dbNameComboBox, true);
        setParentVisible(dbNameComboBox, false);

        // table name filter
        JPanel tableNameFilterPanel = new JPanel(new BorderLayout());
        tableNameFilterPanel.add(new JLabel(AllIcons.General.Filter), BorderLayout.WEST); // 图标在左侧
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
        setParentVisible(tableNameFilter, false);

        generateAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    buttonSelected(generateAll);
                }
                if (e.getClickCount() == 2) {
                    List<String> buttonNameList = new ArrayList<>();
                    for (Component button : buttonPanel.getComponents()) {
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
        addComponentToContent(generateAll, true);
        setParentVisible(generateAll, false);
        // 分割符号
        contentPanel.add(new JPanel());

        logger.info("create content panel");
        if (Config.existLocal(Config.log_path_file_name)) {
            initStartAsync(true);
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

    private void buttonSelected(JComponent childComponent) {
        Color defaultColor = UIManager.getColor("Button.background");
        setParentBackgroundColor(refresh, defaultColor);
        setParentBackgroundColor(generateAll, defaultColor);
        for (Component button : buttonPanel.getComponents()) {
            button.setBackground(defaultColor);
        }
        setParentBackgroundColor(childComponent, SELECTED_COLOR);
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
                dbNameComboBox.setEnabled(true);
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
        buttonPanel.removeAll();

        logger.info("addTableName-selectedDbName: {}", Config.getLocal().selectedDbName);

        for (DbEntity dbEntity : Config.getLocal().dbInfoList) {
            if (!StringUtils.equalsAnyIgnoreCase(Config.getLocal().selectedDbName, dbEntity.dbName)) {
                continue;
            }
            for (String tableName : dbEntity.tableNameList) {
                JBLabel tableNameLabel = new JBLabel(tableName, AllIcons.Nodes.DataTables, JLabel.LEFT);
                tableNameLabel.setName(tableName);
                addComponentToButton(tableNameLabel);
                logger.info("tableName_create: {}", tableName);
                tableNameLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 1) {
                            buttonSelected(tableNameLabel);
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
        for (Component button : buttonPanel.getComponents()) {
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
        for (Component button : buttonPanel.getComponents()) {
            if (tableNameList.contains(button.getName())) {
                ((JBLabel) ((JPanel) button).getComponent(0)).setIcon(loadingIcon);
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
                for (Component button : buttonPanel.getComponents()) {
                    ((JBLabel) ((JPanel) button).getComponent(0)).setIcon(AllIcons.Nodes.DataTables);
                }
                generateAll.setIcon(AllIcons.Actions.Execute);
                // 使用异步刷新机制
                ApplicationManager.getApplication().invokeLater(() -> {
                    VirtualFile root = requireNonNull(ProjectFileIndex.getInstance(ProjectUtil.currentOrDefaultProject(project))
                            .getContentRootForFile(requireNonNull(ProjectUtil.currentOrDefaultProject(project).getProjectFile())));
                    // 异步刷新
                    root.refresh(false, true);
                }, ModalityState.nonModal());
                logger.info("startGenerateAsync.end: {}", String.join(",", tableNameList));
            }
        }).start();
    }

    public void initStartAsync() {
        initStartAsync(false);
    }

    public void initStartAsync(boolean init) {
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
            initTableList(init);
            runFlag.set(false);
            refresh.setIcon(AllIcons.General.InlineRefresh);
            dbNameComboBox.setEnabled(true);
            // 使用异步刷新机制
            ApplicationManager.getApplication().invokeLater(() -> {
                VirtualFile root = requireNonNull(ProjectFileIndex.getInstance(ProjectUtil.currentOrDefaultProject(project))
                        .getContentRootForFile(requireNonNull(ProjectUtil.currentOrDefaultProject(project).getProjectFile())));
                // 异步刷新
                root.refresh(false, true);
            }, ModalityState.nonModal());
        }).start();
    }

    public void initTableList() {
        initTableList(false);
    }

    public void initTableList(boolean init) {
        try {
            logger.info("initTableList");
            generateAll.getParent().setVisible(true);
            setParentVisible(generateAll, true);
            setParentVisible(dbNameComboBox, true);
            setParentVisible(tableNameFilter, true);
            Config.initProject(this.project);
            Config.initFile();
            Config.initLocalData(init);
            addDbName();
            tableNameFilter.setText(Config.getLocal() == null ? "" : Config.getLocal().getFilterTableNameText());
        } catch (Exception ex) {
            logger.info(ex);
        }
    }
}
