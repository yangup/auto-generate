package com.platform.auto.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.platform.auto.config.Config;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.platform.auto.util.UiUtil.addComponentToPanel;
import static com.platform.auto.util.UiUtil.setParentVisible;


public class RadioButtonWithTextField {

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
        Map<String, String> configMap = null;
        if (Config.getLocal().configMap == null) {
            configMap = configJsonNameList.stream().collect(Collectors.toMap(name -> name, name -> name));
            Config.getLocal().configMap = configMap;
            Config.refreshLocal();
        } else {
            configMap = Config.getLocal().configMap;
        }
        this.addRadioButtonWithTextField(configMap);
    }

    public void addRadioButtonWithTextField(Map<String, String> defaultText) {
        defaultText.forEach(this::addRadioButtonWithTextField);
    }

    public void addRadioButtonWithTextField(String key, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));  // 使用 BoxLayout 来设置横向布局
        JBRadioButton radioButton = new JBRadioButton();
        JBTextField textField = new JBTextField(value);
        textField.setColumns(35); // 设置输入框宽度
        textField.setName(key);
        // 点击文本框时，自动选中对应的单选按钮
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                radioButton.setSelected(true);
                Config.getLocal().selectedJsonName = textField.getName();
                Config.refreshLocal();
            }
        });
        radioButton.addActionListener(e -> {
            if (radioButton.isSelected()) {
                Config.getLocal().selectedJsonName = textField.getName();
                Config.refreshLocal();
            }
        });
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }

            private void textChanged() {
                Config.getLocal().configMap.put(textField.getName(), textField.getText());
                Config.refreshLocal();
            }
        });

        buttonGroup.add(radioButton);
        panel.add(radioButton);
        panel.add(Box.createHorizontalStrut(1));  // 添加小的间隔
        panel.add(new JLabel(AllIcons.FileTypes.Config));
        panel.add(Box.createHorizontalStrut(1));  // 添加小的间隔
        panel.add(textField);
        addComponentToContent(panel, true);
        panelList.add(panel);
        pairs.add(new Pair(radioButton, textField));
        if (StringUtils.isBlank(Config.getLocal().selectedJsonName)) {
            if (pairs.size() == 1) {
                radioButton.setSelected(true);
            }
        } else {
            if (Config.getLocal().selectedJsonName.equals(textField.getName())) {
                radioButton.setSelected(true);
            }
        }

    }

    // 一个简单的内部类，用来配对 RadioButton 和 TextField
    private static class Pair {
        JBRadioButton radioButton;
        JBTextField textField;

        Pair(JBRadioButton radioButton, JBTextField textField) {
            this.radioButton = radioButton;
            this.textField = textField;
        }
    }

    // 获取当前选中的文本
    public String getSelectedText() {
        for (Pair pair : pairs) {
            if (pair.radioButton.isSelected()) {
                return Config.getLocal().configMap.get(pair.textField.getName());
            }
        }
        return null;
    }

    public JPanel addComponentToContent(JComponent component, boolean needCursor) {
        return addComponentToPanel(component, contentPanel, needCursor);
    }

    public void setVisible(boolean visible) {
        for (JPanel panel : panelList) {
            setParentVisible(panel, visible);
        }
    }

}
