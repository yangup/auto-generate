package com.platform.auto.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.platform.auto.config.Config;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.platform.auto.util.UiUtil.addComponentToPanel;
import static com.platform.auto.util.UiUtil.setParentVisible;


public class RadioButtonWithText {

    List<String> configJsonNameList = List.of("config.json", "config_add_column.json", "config_front.json");
    JPanel contentPanel;
    ButtonGroup buttonGroup = new ButtonGroup();
    List<Pair> pairs = new ArrayList<>();
    List<JPanel> panelList = new ArrayList<>();
    private final JPanel thisPanel = new JPanel();

    public void init(JPanel contentPanel) {
        this.contentPanel = contentPanel;
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
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));  // 使用 BoxLayout 来设置横向布局
        JBRadioButton radioButton = new JBRadioButton();
        JLabel jLabel = new JLabel(AllIcons.FileTypes.Config);
        JBLabel label = new JBLabel(name);
        label.setName(name);
        // 点击文本框时，自动选中对应的单选按钮
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                radioButton.setSelected(true);
                Config.getLocal().selectedJsonName = label.getName();
                Config.refreshLocal();
            }
        });
        radioButton.addActionListener(e -> {
            if (radioButton.isSelected()) {
                Config.getLocal().selectedJsonName = label.getName();
                Config.refreshLocal();
            }
        });

        buttonGroup.add(radioButton);
        panel.add(radioButton);
//        panel.add(Box.createHorizontalStrut(1));  // 添加小的间隔
        panel.add(jLabel);
//        panel.add(Box.createHorizontalStrut(1));  // 添加小的间隔
        panel.add(label);
        addComponentToContent(panel, true);
        panelList.add(panel);
        pairs.add(new Pair(radioButton, label));
        if (StringUtils.isBlank(Config.getLocal().selectedJsonName)) {
            if (pairs.size() == 1) {
                radioButton.setSelected(true);
            }
        } else {
            if (Config.getLocal().selectedJsonName.equals(label.getName())) {
                radioButton.setSelected(true);
            }
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
