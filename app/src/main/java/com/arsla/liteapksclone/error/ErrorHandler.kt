package com.arsla.liteapksclone.error

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ErrorHandler {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
    private lateinit var appContext: Context

    private val logDir: File
        get() = File(appContext.filesDir, "errors").apply { mkdirs() }

    private val appLog: File
        get() = File(logDir, "app.log")

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun handle(throwable: Throwable, tag: String = "LiteapksClone") {
        try {
            Log.e(tag, "Handled error", throwable)
            val timestamp = dateFormat.format(Date())
            appLog.appendText("$timestamp [$tag]\n${throwable.stackTraceToString()}\n\n")
        } catch (_: Exception) {
            // If logging fails, fall back to Logcat only.
        }
    }

    fun logCrash(throwable: Throwable) {
        try {
            Log.e("LiteapksClone", "FATAL", throwable)
            val timestamp = dateFormat.format(Date())
            val file = File(logDir, "crash_$timestamp.log")
            file.writeText(throwable.stackTraceToString())
        } catch (_: Exception) {
            // If crash logging fails, continue with the default handler.
        }
    }

    fun getLogFile(): File = appLog
}
