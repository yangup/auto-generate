package com.platform.auto.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.platform.auto.config.Config;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.platform.auto.util.UiUtil.addComponentToPanel;
import static com.platform.auto.util.UiUtil.setParentVisible;


public class RadioButtonWithText {

    List<String> configJsonNameList = List.of("config.json", "config_add_column.json", "config_front.json");
    JPanel contentPanel;
    AutoGenerateToolWindowContent autoGenerateToolWindowContent;
    ButtonGroup buttonGroup = new ButtonGroup();
    List<Pair> pairs = new ArrayList<>();
    List<JPanel> panelList = new ArrayList<>();
    private final JPanel thisPanel = new JPanel();

    public void init(JPanel contentPanel, AutoGenerateToolWindowContent autoGenerateToolWindowContent) {
        this.contentPanel = contentPanel;
        this.autoGenerateToolWindowContent = autoGenerateToolWindowContent;
        thisPanel.setLayout(new BoxLayout(thisPanel, BoxLayout.Y_AXIS)); // 垂直排列
        thisPanel.setBorder(null);
        this.contentPanel.add(thisPanel);
        this.refresh();
    }

    public void refresh() {
        // 清理所有的按钮
        List<AbstractButton> buttonList = Collections.list(buttonGroup.getElements());
        for (AbstractButton button : buttonList) {
            buttonGroup.remove(button);
        }
        this.thisPanel.removeAll();
        this.pairs = new ArrayList<>();
        this.panelList = new ArrayList<>();
        List<String> configList = null;
        if (Config.getLocal().configList == null) {
            configList = configJsonNameList;
            Config.getLocal().configList = configList;
            Config.refreshLocal();
        } else {
            configList = Config.getLocal().configList;
        }
        this.addRadioButtonWithTextField(configList);
    }

    public void addRadioButtonWithTextField(List<String> defaultText) {
        defaultText.forEach(this::addRadioButtonWithTextField);
    }

    public void addRadioButtonWithTextField(String name) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));  // 横向布局

        JBRadioButton radioButton = new JBRadioButton();
        JLabel jLabel = new JLabel(AllIcons.FileTypes.Config);
        JBLabel label = new JBLabel(name);
        label.setName(name);

// 统一处理选中逻辑
        Runnable selectAction = () -> {
            radioButton.setSelected(true);
            Config.getLocal().selectedJsonName = label.getName();
            Config.refreshLocal();
            Config.refreshConfig();
            Config.refreshConfigDataBaseData(true);
            autoGenerateToolWindowContent.addTableName();
        };

// 公共 MouseListener（对所有组件统一行为）
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectAction.run();
            }
        };

// 绑定监听器
        label.addMouseListener(mouseListener);
        panel.addMouseListener(mouseListener);
        radioButton.addActionListener(e -> selectAction.run());
        jLabel.addMouseListener(mouseListener);

        buttonGroup.add(radioButton);
        panel.add(radioButton);
        panel.add(jLabel);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(label);

// 添加到 UI
        addComponentToContent(panel, true);
        panelList.add(panel);
        pairs.add(new Pair(radioButton, label));

// 初始化选中逻辑
        String selected = Config.getLocal().selectedJsonName;
        if ((StringUtils.isBlank(selected) && pairs.size() == 1) ||
                (selected != null && selected.equals(label.getName()))) {
            radioButton.setSelected(true);
        }

    }

    // 一个简单的内部类，用来配对 RadioButton 和 TextField
    private static class Pair {
        JBRadioButton radioButton;
        JBLabel label;

        Pair(JBRadioButton radioButton, JBLabel label) {
            this.radioButton = radioButton;
            this.label = label;
        }
    }

    public JPanel addComponentToContent(JComponent component, boolean needCursor) {
        return addComponentToPanel(component, this.thisPanel, needCursor);
    }

    public void setVisible(boolean visible) {
        for (JPanel panel : panelList) {
            setParentVisible(panel, visible);
        }
    }

}
