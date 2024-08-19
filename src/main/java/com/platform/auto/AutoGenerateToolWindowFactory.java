package com.platform.auto;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

final class AutoGenerateToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        AutoGenerateToolWindowContent toolWindowContent = new AutoGenerateToolWindowContent(toolWindow, project);
        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getParentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/toolWindow/auto-generate.svg");
    }

}
