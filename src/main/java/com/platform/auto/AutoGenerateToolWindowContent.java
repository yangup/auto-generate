package com.platform.auto;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.platform.auto.config.Config;
import com.platform.auto.config.LocalEntity;
import com.platform.auto.jdbc.Connection;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutoGenerateToolWindowContent {

    private static final Logger logger = AutoLogger.getLogger(Connection.class);

    @Getter
    private final JPanel contentPanel = new JPanel();
//    private final JButton runButton = new JButton("Run");
//    private final JButton cancelButton = new JButton("Cancel");
    private List<JButton> tableNameButtonList;

    public AutoGenerateToolWindowContent(ToolWindow toolWindow, Project project) {
        Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
        init(project);
        initTableList();
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        try {
            JPanel content = createCalendarPanel();
//            content.setBorder(BorderFactory.createLineBorder(Color.RED));
            contentPanel.add(content, BorderLayout.PAGE_START);
            contentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
        } catch (Exception e) {
        }
    }

    @NotNull
    private JPanel createCalendarPanel() throws Exception {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // 垂直排列
        tableNameButtonList = new ArrayList<>();
        int count = 0;
        for (LocalEntity.TableEntity tableEntity : Config.getLocal().tableList) {
            count++;
            JButton button = new JButton(tableEntity.tableSchema);
//            JFXButton button = new JFXButton(tableEntity.tableSchema);
            tableNameButtonList.add(button);
            buttonPanel.add(button);
            if (count > 5) {
                break;
            }
        }
        return buttonPanel;
    }

    @NotNull
    private JPanel createControlsPanel(ToolWindow toolWindow) {
        JPanel controlsPanel = new JPanel();
//        controlsPanel.add(runButton);
//        runButton.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        runButton.addActionListener(e -> {
//            try {
//                Thread.currentThread().setContextClassLoader(AutoGenerateToolWindowFactory.class.getClassLoader());
//                Application.start();
//            } catch (Exception ex) {
//                logger.info(AutoLogger.getExceptionInfo(ex));
//            }
//        });
//        controlsPanel.add(cancelButton);
//        cancelButton.addActionListener(e -> toolWindow.hide(null));
        return controlsPanel;
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
