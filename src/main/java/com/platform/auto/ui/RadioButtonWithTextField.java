package com.platform.auto.ui;

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
import java.util.List;
import java.util.stream.Collectors;

import static com.platform.auto.util.UiUtil.addComponentToPanel;
import static com.platform.auto.util.UiUtil.setParentVisible;


public class RadioButtonWithTextField {

    List<String> configJsonNameList = List.of(
            "config.json",
            "config_add_column.json",
            "config_front.json"
    );
    JPanel contentPanel;
    ButtonGroup buttonGroup = new ButtonGroup();
    List<Pair> pairs = new ArrayList<>();
    List<JPanel> panelList = new ArrayList<>();

    public void init(JPanel contentPanel) {
        this.contentPanel = contentPanel;
        if (Config.getLocal().configMap == null) {
            Config.getLocal().configMap = configJsonNameList.stream()
                    .collect(Collectors.toMap(name -> name, name -> name));
            Config.refreshLocal();
        }
        this.addRadioButtonWithTextField(configJsonNameList);
    }

    public void addRadioButtonWithTextField(List<String> defaultText) {
        // 添加单选框, 确定单元框文本
        for (String configJsonName : defaultText) {
            addRadioButtonWithTextField(configJsonName);
        }

    }

    public void addRadioButtonWithTextField(String defaultText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JBRadioButton radioButton = new JBRadioButton();
        JBTextField textField = new JBTextField(defaultText);
        textField.setColumns(35); // 设置输入框宽度
        textField.setName(defaultText);
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
