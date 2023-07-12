package com.plugin.Panel

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.jetbrains.annotations.NotNull
import java.io.*
import javax.sound.sampled.*
import javax.swing.JButton
import javax.swing.JPanel

class VoiceRecorderPanel : JPanel(), ToolWindowFactory, DumbAware {
    private val audioFormat: AudioFormat = AudioFormat(44100f, 16, 2, true, true)
    private lateinit var targetDataLine: TargetDataLine
    private var isRecording: Boolean = false

    init {
        val startButton = createTextButton("Start")
        val pauseButton = createTextButton("Pause")
        val endButton = createTextButton("End")

        startButton.addActionListener {
            println("FIRST PRINT")
            startRecording()
        }

        pauseButton.addActionListener {
            pauseRecording()
        }

        endButton.addActionListener {
            stopRecording()
        }
        add(startButton)
        add(pauseButton)
        add(endButton)
    }

    private fun startRecording() {
        if (!isRecording) {
            try {
                val dataLineInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)
                targetDataLine = AudioSystem.getLine(dataLineInfo) as TargetDataLine
                targetDataLine.open(audioFormat)
                targetDataLine.start()

                val outerThis = this
                Thread {
                    println("INSIDE THREADS")
                   isRecording = true
                    val byteArrayOutputStream = ByteArrayOutputStream()

                    val buffer = ByteArray(4096)
                    var bytesRead: Int

                    while (isRecording) {
                        bytesRead = targetDataLine.read(buffer, 0, buffer.size)
                        byteArrayOutputStream.write(buffer, 0, bytesRead)
                    }

                    byteArrayOutputStream.close()

                    // Save the recorded audio to a file
                    saveToFile(byteArrayOutputStream.toByteArray(), "audio.wav")
                }.start()
            } catch (e: LineUnavailableException) {
                e.printStackTrace()
            }
        }
    }


    private fun pauseRecording() {
        // Pause recording logic here
        // You can add functionality to pause the recording if desired
    }

    private fun stopRecording() {
        if (isRecording) {
            isRecording = false
            targetDataLine.stop()
            targetDataLine.close()
        }
    }
    private fun saveToFile(audioData: ByteArray, filePath: String) {
        try {
            val file = File(filePath)
            file.createNewFile()

            val outputStream = BufferedOutputStream(FileOutputStream(file))
            outputStream.write(audioData)
            outputStream.close()

            println("Audio saved to: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
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
        }
        return button
    }
}
