package com.plugin.Panel

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.jetbrains.annotations.NotNull
import javax.swing.JButton
import javax.swing.JPanel

class VoiceRecorderPanel : JPanel(), ToolWindowFactory, DumbAware {

    init {
        val runButton = createTextButton("Run")
        val stopButton = createTextButton("Stop")
        val voiceRecorderPlugin = createTextButton("Voice Recorder")

        add(runButton)
        add(stopButton)
        add(voiceRecorderPlugin)
    }

    override fun createToolWindowContent(
        @NotNull project: Project,
        @NotNull toolWindow: ToolWindow
    ) {
        val content = ContentFactory.SERVICE.getInstance().createContent(this, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createTextButton(text: String): JButton {
        val button = JButton(text)
        button.addActionListener {
            // Handle button click event here
        }
        return button
    }
}
