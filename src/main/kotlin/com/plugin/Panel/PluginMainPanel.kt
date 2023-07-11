package com.plugin.Panel

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.jetbrains.annotations.NotNull
import java.awt.CardLayout
import javax.swing.*

class PluginMainPanel : ToolWindowFactory, DumbAware {
    private val contentPanel = JPanel()
    private val cardLayout = CardLayout()

    override fun createToolWindowContent(
        @NotNull project: Project,
        @NotNull toolWindow: ToolWindow
    ) {
        val contentPanel = JPanel()
        val content = ContentFactory.SERVICE.getInstance().createContent(contentPanel, "", false)
        toolWindow.contentManager.addContent(content)

        val runButton = createTextButton("Run")
        val stopButton = createTextButton("Stop")
        val voiceRecorderButton = createVoiceRecorder("Voice Recorder")
        val settingsButton = createSettingsButton()

//        val mainPanel = createMainPanel()
//        mainPanel.add(runButton)
//        mainPanel.add(stopButton)
//        mainPanel.add(voiceRecorderButton)
//        mainPanel.add(settingsButton)

//        contentPanel.layout = cardLayout
//        contentPanel.add(mainPanel,"main")
//        contentPanel.add(createMainPanel(),"main")

        contentPanel.add(runButton,)
        contentPanel.add(stopButton,)
        contentPanel.add(voiceRecorderButton)
        contentPanel.add(settingsButton)

//        cardLayout.show(contentPanel,"main")
    }

    private fun createTextButton(text: String): JButton {
        val button = JButton(text)
        button.addActionListener {
            // Handle button click event here
        }
        return button
    }

    private fun createVoiceRecorder(text: String): JButton {
        val test : Int = 2
        val button = JButton(text)
        button.addActionListener{
            val dialog = JDialog()
            dialog.contentPane.add(VoiceRecorderPanel())
            dialog.pack()
            dialog.isVisible = true
        }
        return button
    }

    private fun createMainPanel(): JPanel {
        return JPanel()
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

        pathFolderMenuItem.addActionListener {
            val fileChooser = JFileChooser()
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val dialogResult = fileChooser.showDialog(null, "Select Folder")
            if (dialogResult == JFileChooser.APPROVE_OPTION) {
                val selectedFile = fileChooser.selectedFile
                // Use the selected file here
            }
        }

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



