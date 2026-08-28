package com.arsla.liteapksclone.install

import android.content.Context
import android.content.Intent
import android.net.Uri

object ApkUninstaller {

    fun uninstall(context: Context, packageName: String) {
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
