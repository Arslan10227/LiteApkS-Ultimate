package com.arsla.liteapksclone.error

object CrashHandler {

    fun initialize() {
        val default = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            ErrorHandler.logCrash(throwable)
            default?.uncaughtException(thread, throwable)
        }
    }
}
