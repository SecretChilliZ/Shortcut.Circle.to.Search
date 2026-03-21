package com.chilliz.hardwaremacro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import rikka.shizuku.Shizuku

class MacroReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            Thread {
                try {
                    val command = "input keyevent --longpress 3"
                    val newProcessMethod = Shizuku::class.java.getDeclaredMethod(
                        "newProcess",
                        Array<String>::class.java,
                        Array<String>::class.java,
                        String::class.java
                    )
                    newProcessMethod.isAccessible = true
                    newProcessMethod.invoke(null, arrayOf("sh", "-c", command), null, null)
                } catch (e: Exception) {
                    // Fail silently
                }
            }.start()
        }
    }
}
