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
    private val  audioFormat = AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100f, 16, 2, 4, 44100f, true)
    private lateinit var targetDataLine: TargetDataLine
    private var isRecording: Boolean = false
    private lateinit var audioFile: File
    init {
        val startButton = createTextButton("Start")
        val pauseButton = createTextButton("Pause")
        val endButton = createTextButton("End")
        startButton.addActionListener {
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
                val audioFormat = AudioFormat(44100f, 16, 2, true, true)
                val dataLineInfo = DataLine.Info(TargetDataLine::class.java, audioFormat)
                if (!AudioSystem.isLineSupported(dataLineInfo)) {
                    println("Audio format is not supported.")
                    return
                }

                println("recording ...")

                targetDataLine = AudioSystem.getLine(dataLineInfo) as TargetDataLine
                targetDataLine.open(audioFormat)
                targetDataLine.start()

                Thread {
                    isRecording = true
                    val byteArrayOutputStream = ByteArrayOutputStream()

                    val buffer = ByteArray(4096)
                    var bytesRead: Int

                    while (isRecording) {
                        bytesRead = targetDataLine.read(buffer, 0, buffer.size)
                        byteArrayOutputStream.write(buffer, 0, bytesRead)
                    }

                    byteArrayOutputStream.close()

                    audioFile = File.createTempFile("audio", ".wav")
                    val audioData = byteArrayOutputStream.toByteArray()
                    saveToFile(audioData, audioFile)

                    println("Recording stopped. Audio file saved: ${audioFile.absolutePath}")
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
    private fun saveToFile(audioData: ByteArray, file: File) {
        try {
            val audioFormat = AudioFormat(44100f, 16, 2, true, true)
            val audioInputStream = AudioInputStream(
                ByteArrayInputStream(audioData),
                audioFormat,
                audioData.size.toLong() / audioFormat.frameSize
            )

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file)
        } catch (e: Exception) {
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
