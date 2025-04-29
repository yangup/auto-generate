package com.platform.auto.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.platform.auto.config.Config;
import com.platform.auto.sys.log.AutoLogger;
import com.platform.auto.sys.log.Logger;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.platform.auto.util.UiUtil.addComponentToPanel;
import static com.platform.auto.util.UiUtil.setParentVisible;


public class RadioButtonDataBase {

    private static final Logger logger = AutoLogger.getLogger(RadioButtonDataBase.class);

    List<String> configList = List.of("test1", "test2");
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
        logger.info("refreshRadioButtonDataBase");
        // 清理所有的按钮
        List<AbstractButton> buttonList = Collections.list(buttonGroup.getElements());
        for (AbstractButton button : buttonList) {
            buttonGroup.remove(button);
        }
        this.thisPanel.removeAll();
        this.pairs = new ArrayList<>();
        this.panelList = new ArrayList<>();

        Map<String, String> dbMap = null;
        if (Config.getLocal().dbMap == null) {
            dbMap = configList.stream().collect(Collectors.toMap(name -> name, name -> name));
            Config.getLocal().dbMap = dbMap;
            Config.refreshLocal();
        } else {
            dbMap = Config.getLocal().dbMap;
        }
        logger.info(dbMap.toString());
        this.addRadioButtonWithTextField(dbMap);
    }

    public void addRadioButtonWithTextField(Map<String, String> defaultText) {
        defaultText.forEach(this::addRadioButtonWithTextField);
    }

    public void addRadioButtonWithTextField(String key, String value) {
        JBRadioButton radioButton = new JBRadioButton();
        JBTextField textField = new JBTextField(value);
        textField.setColumns(35); // 设置输入框宽度
        textField.setName(key);
        textField.setText(value);
        // 点击文本框时，自动选中对应的单选按钮
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                radioButton.setSelected(true);
                Config.getLocal().selectedDbKey = textField.getName();
                Config.getLocal().selectedDbName = Config.getLocal().dbMap.get(Config.getLocal().selectedDbKey);
                Config.refreshLocal();
                autoGenerateToolWindowContent.addTableName();
            }
        });
        radioButton.addActionListener(e -> {
            if (radioButton.isSelected()) {
                Config.getLocal().selectedDbKey = textField.getName();
                Config.getLocal().selectedDbName = Config.getLocal().dbMap.get(Config.getLocal().selectedDbKey);
                Config.refreshLocal();
                autoGenerateToolWindowContent.addTableName();
            }
        });
        textField.addActionListener(e -> {
            Config.getLocal().dbMap.put(textField.getName(), textField.getText());
            Config.refreshLocal();
        });

        buttonGroup.add(radioButton);
        addOne(radioButton, textField);
        pairs.add(new Pair(radioButton, textField));
        if (StringUtils.isBlank(Config.getLocal().selectedDbKey)) {
            if (pairs.size() == 1) {
                radioButton.setSelected(true);
                textField.setText(Config.getConfig().jdbc.database);
                Config.getLocal().dbMap.put(textField.getName(), textField.getText());
                Config.getLocal().selectedDbKey = textField.getName();
                Config.getLocal().selectedDbName = textField.getText();
                Config.refreshLocal();
                autoGenerateToolWindowContent.addTableName();
            }
        } else {
            if (Config.getLocal().selectedDbKey.equals(textField.getName())) {
                radioButton.setSelected(true);
            }
        }
    }

    public void addOne(JBRadioButton radioButton, JBTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));  // 使用 BoxLayout 来设置横向布局
        panel.add(radioButton);
        panel.add(Box.createHorizontalStrut(1));  // 添加小的间隔
        panel.add(new JLabel(AllIcons.Nodes.DataSchema));
        panel.add(Box.createHorizontalStrut(1));  // 添加小的间隔
        panel.add(textField);
        addComponentToContent(panel, true);
        panelList.add(panel);
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

    public JPanel addComponentToContent(JComponent component, boolean needCursor) {
        return addComponentToPanel(component, this.thisPanel, needCursor);
    }

    public void setVisible(boolean visible) {
        for (JPanel panel : panelList) {
            setParentVisible(panel, visible);
        }
    }

}
