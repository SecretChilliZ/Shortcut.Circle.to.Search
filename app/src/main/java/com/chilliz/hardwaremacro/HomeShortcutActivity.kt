package com.chilliz.hardwaremacro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class HomeShortcutActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (intent.action == Intent.ACTION_CREATE_SHORTCUT) {
            val shortcutIntent = Intent(this, HomeShortcutActivity::class.java).apply {
                action = "com.chilliz.hardwaremacro.RUN_MACRO"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            @Suppress("DEPRECATION")
            val icon = Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher)
            val resultIntent = Intent().apply {
                @Suppress("DEPRECATION")
                putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent)
                @Suppress("DEPRECATION")
                putExtra(Intent.EXTRA_SHORTCUT_NAME, "Long Home")
                @Suppress("DEPRECATION")
                putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
            return
        }

        // 1. Try Native Accessibility Assist (Most stable on Samsung)
        val service = KeyInterceptorService.instance
        if (service != null) {
            service.triggerAssist()
        } else {
            // 2. Fallback to Broadcast for Shizuku
            sendBroadcast(Intent(this, MacroReceiver::class.java))
        }

        // 3. SAMSUNG GHOST TRICK:
        // Move to back immediately but don't finish for 200ms
        moveTaskToBack(true)
        
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }, 200)
    }
}
