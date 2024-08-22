package com.platform.auto.ui;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.*;

@Data
@AllArgsConstructor
public class ComboBoxItem {


    public String text;
    public Icon icon;

    public ComboBoxItem(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        return (anObject instanceof ComboBoxItem aString)
                && (aString.text.equals(text));
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

}
