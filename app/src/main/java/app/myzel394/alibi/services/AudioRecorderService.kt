package app.myzel394.alibi.services

import android.media.MediaRecorder
import android.os.Build

class AudioRecorderService: IntervalRecorderService() {
    var recorder: MediaRecorder? = null
        private set

    val filePath: String
        get() = "$folder/$counter.${settings.fileExtension}"

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFile(filePath)
            setOutputFormat(settings.outputFormat)
            setAudioEncoder(settings.encoder)
            setAudioEncodingBitRate(settings.bitRate)
            setAudioSamplingRate(settings.samplingRate)
        }
    }

    private fun resetRecorder() {
        runCatching {
            recorder?.let {
                it.stop()
                it.release()
            }
        }
    }

    override fun startNewCycle() {
        super.startNewCycle()

        val newRecorder = createRecorder().also {
            it.prepare()
        }

        resetRecorder()

        newRecorder.start()
        recorder = newRecorder
    }

    override fun getAmplitudeAmount(): Int {
        return 100
    }

    override fun getAmplitude(): Int = recorder?.maxAmplitude ?: 0
}