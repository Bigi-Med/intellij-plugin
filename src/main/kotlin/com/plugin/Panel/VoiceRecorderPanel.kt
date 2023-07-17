package com.plugin.Panel

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.jetbrains.annotations.NotNull
import java.io.*
import java.util.*
import javafx.*
import javax.sound.sampled.*
import javax.swing.JButton
import javax.swing.JFileChooser
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
        val playButton = createTextButton("Play")
        val updateButton = createTextButton("Update")
        startButton.addActionListener {
            startRecording()
        }

        playButton.addActionListener {
            playAudioClip()
        }

        updateButton.addActionListener {
            updateAudioFile()
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
        add(playButton)
        add(updateButton)
    }

    private fun updateAudioFile() {
        val fileChooser = JFileChooser()
        val result = fileChooser.showOpenDialog(this)

        if (result == JFileChooser.APPROVE_OPTION) {
            audioFile = fileChooser.selectedFile
            println("Updated audio file: $audioFile")
        } else {
            println("No audio file selected")
        }
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

    private fun playAudioClip() {
        try {
            val audioStream = AudioSystem.getAudioInputStream(audioFile)
            val format = audioStream.format
            val supportedFormats = arrayOf("WAV", "AIFF", "AU") // Add more supported formats as needed

            if (!supportedFormats.contains(format.encoding.toString().uppercase(Locale.getDefault()))) {
                println("Unsupported audio format: ${format.encoding}")
                return
            }


            val info = DataLine.Info(Clip::class.java, format)
            val audioClip = AudioSystem.getLine(info) as Clip
            audioClip.open(audioStream)
            audioClip.start()
        } catch (ex: UnsupportedAudioFileException) {
            println("The specified audio file is not supported.")
            ex.printStackTrace()
        } catch (ex: LineUnavailableException) {
            println("Audio line for playing back is unavailable.")
            ex.printStackTrace()
        } catch (ex: IOException) {
            println("Error playing the audio file.")
            ex.printStackTrace()
        }
    }


    private fun verifyAudioCodec(filePath: String): Boolean {
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
