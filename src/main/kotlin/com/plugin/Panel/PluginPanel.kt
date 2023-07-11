package com.plugin.newplugin

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import org.jetbrains.annotations.NotNull
import javax.swing.*

class OrderFoodAction : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(
        @NotNull project: Project,
        @NotNull toolWindow: ToolWindow
    ) {
        val contentPanel = JPanel()
        val content = ContentFactory.SERVICE.getInstance().createContent(contentPanel, "", false)
        toolWindow.contentManager.addContent(content)

        val runButton = createTextButton("Run")
        val stopButton = createTextButton("Stop")
        val settingsButton = createSettingsButton()

        contentPanel.add(runButton)
        contentPanel.add(stopButton)
        contentPanel.add(settingsButton)
    }

    private fun createTextButton(text: String): JButton {
        val button = JButton(text)
        button.addActionListener {
            // Handle button click event here
        }
        return button
    }


    private fun createSettingsButton(): JButton {
        val settingsButton = JButton("Settings")
        val popupMenu = JPopupMenu()

        val pathFolderMenuItem = JMenuItem("Path Folder")
        val tokenSubMenu = JMenu("Token")
        val tokenValueMenuItem = JMenuItem("tst token")
        tokenSubMenu.add(tokenValueMenuItem)

        popupMenu.add(pathFolderMenuItem)
        popupMenu.add(tokenSubMenu)

        settingsButton.addActionListener {
            val parentFrame = SwingUtilities.getWindowAncestor(settingsButton)
            if (parentFrame is java.awt.Frame) {
                val buttonLocation = settingsButton.locationOnScreen
                val popupX = buttonLocation.x
                val popupY = buttonLocation.y + settingsButton.height
                popupMenu.show(parentFrame, popupX, popupY)
            }
        }

        return settingsButton
    }
}



