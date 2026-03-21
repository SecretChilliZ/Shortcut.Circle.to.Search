package com.chilliz.hardwaremacro

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import rikka.shizuku.Shizuku

class MacroService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
                    val process = newProcessMethod.invoke(null, arrayOf("sh", "-c", command), null, null) as Process
                    process.waitFor()
                } catch (e: Exception) {
                    // Fail silently in background
                } finally {
                    stopSelf()
                }
            }.start()
        } else {
            stopSelf()
        }
        return START_NOT_STICKY
    }
}
