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
                if (!AudioSystem.isLineSupported(dataLineInfo)) {
                    println("Audio format is not supported.")
                    return
                }
                println("Audio format supported")
                if (audioFormat.sampleRate.toInt() != 44100 || audioFormat.sampleSizeInBits % 8 != 0
                    || (audioFormat.channels != 1 && audioFormat.channels != 2)) {
                    println("Incompatible audio format with WAV file extension.")
                    return
                }
                println("Audio format compatible with extension, continuing recording ....")
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
            if (audioData.isEmpty()) {
                println("Audio data is empty.")
                return
            }
            println("Audio data recorded, saving to file ....")
            val file = File(filePath)
            file.createNewFile()

            val inputStream = ByteArrayInputStream(audioData)
            val outputStream = BufferedOutputStream(FileOutputStream(file))

            val buffer = ByteArray(4096)
            var read: Int
            var total: Long = 0

            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
                total += read.toLong()
            }

            outputStream.close()
            inputStream.close()
            verifyAudioCodec(file)
            println("Audio saved to: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun verifyAudioCodec(filePath: String): Boolean {
        try {
            val audioFile = File(filePath)
            val audioInputStream = AudioSystem.getAudioInputStream(audioFile)
            val audioFormat = audioInputStream.format
            val audioCodec = audioFormat.encoding.toString()

            // Check if the audio codec is supported
            val supportedCodecs = listOf("PCM_SIGNED", "PCM_UNSIGNED", "MP3", "FLAC")
            if (!supportedCodecs.contains(audioCodec)) {
                println("Unsupported audio codec: $audioCodec")
                return false
            }

            // Other checks or processing logic if needed

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
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
