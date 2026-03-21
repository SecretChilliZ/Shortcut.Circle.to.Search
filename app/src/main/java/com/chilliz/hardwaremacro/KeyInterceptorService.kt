package com.chilliz.hardwaremacro

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class KeyInterceptorService : AccessibilityService() {

    companion object {
        var instance: KeyInterceptorService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    fun triggerAssist() {
        // GLOBAL_ACTION_ASSIST (INT 16) triggers Circle to Search / Assistant
        performGlobalAction(16)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean = false
}
