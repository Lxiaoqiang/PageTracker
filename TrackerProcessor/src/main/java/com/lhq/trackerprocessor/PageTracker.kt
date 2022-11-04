package com.lhq.trackerprocessor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PageTracker {

    private var mLogPath: String? = null

    private const val TAG = "Tracker"

    fun init(path: String) {
        mLogPath = path
        if (!File(mLogPath).exists()) {

        }
        writeLog(
            String.format(
                "==========================%s==========================\n",
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
            )
        )
    }

    fun writeLog(message: String) {
        if (mLogPath.isNullOrEmpty()) {
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val fs = FileOutputStream(mLogPath, true)
                fs.write(message.toByteArray())
                fs.flush()
                fs.close()
            } catch (e: Exception) {

            }
        }
    }

    /**
     * when app crash can use this function
     *
     * you need use UncaughtExceptionHandler
     */
    fun forceWrite() {

    }
}