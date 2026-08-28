package com.arsla.liteapksclone.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.sdsmdg.tastytoast.TastyToast

object TastyToaster {

    fun show(
        context: Context,
        message: String,
        type: Type,
        duration: Int = TastyToast.LENGTH_LONG
    ) {
        val style = when (type) {
            Type.SUCCESS -> TastyToast.SUCCESS
            Type.ERROR -> TastyToast.ERROR
            Type.WARNING -> TastyToast.WARNING
            Type.INFO -> TastyToast.INFO
            Type.DEFAULT -> TastyToast.DEFAULT
            Type.CONFUSING -> TastyToast.CONFUSING
        }

        val appContext = context.applicationContext
        Handler(Looper.getMainLooper()).post {
            TastyToast.makeText(appContext, message, duration, style).show()
        }
    }

    enum class Type {
        SUCCESS, ERROR, WARNING, INFO, DEFAULT, CONFUSING
    }
}
