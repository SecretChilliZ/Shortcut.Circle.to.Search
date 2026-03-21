package com.chilliz.hardwaremacro

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import rikka.shizuku.Shizuku
import kotlin.random.Random
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    private var shizukuListener: Shizuku.OnRequestPermissionResultListener? = null
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        shizukuListener = Shizuku.OnRequestPermissionResultListener { requestCode, _ ->
            if (requestCode == 1) { /* Re-compose handled by onResume */ }
        }
        Shizuku.addRequestPermissionResultListener(shizukuListener!!)

        setContent {
            MatrixTheme {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    MatrixRainBackground()
                    Scaffold(containerColor = Color.Transparent) { padding ->
                        MacroUI(Modifier.padding(padding))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        shizukuListener?.let { Shizuku.removeRequestPermissionResultListener(it) }
    }
}

@Composable
fun MatrixRainBackground() {
    // Increased density from 15 to 45 columns
    val columnCount = 45
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(columnCount) { index ->
            MatrixColumn(index * (1f / columnCount))
        }
    }
}

@Composable
fun MatrixColumn(xPositionFactor: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "MatrixRain")
    
    // Varying speeds for better depth effect
    val duration = remember { Random.nextInt(2000, 5000) }
    val delay = remember { Random.nextInt(0, 3000) }
    
    val yOffset by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                delayMillis = delay,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "yOffset"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val xPos = maxWidth * xPositionFactor
        val yPos = maxHeight * yOffset
        
        // Randomly changing character to feel more like "code"
        var char by remember { mutableStateOf(if (Random.nextBoolean()) "0" else "1") }
        LaunchedEffect(yOffset) {
            if (Random.nextFloat() > 0.8f) {
                char = if (Random.nextBoolean()) "0" else "1"
            }
        }

        Text(
            text = char,
            color = Color(0xFF00FF41).copy(alpha = 0.35f), // Slightly more transparent for high density
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            modifier = Modifier.offset(x = xPos, y = yPos)
        )
    }
}

@Composable
fun MatrixTheme(content: @Composable () -> Unit) {
    val matrixGreen = Color(0xFF00FF41)
    val colors = darkColorScheme(
        primary = matrixGreen,
        background = Color.Black,
        onBackground = matrixGreen,
    )
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace, color = matrixGreen),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = matrixGreen),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace, color = matrixGreen),
        ),
        content = content
    )
}

@SuppressLint("BatteryLife")
@Composable
fun MacroUI(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isServiceEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    var isBatteryOptimized by remember {
        mutableStateOf(!powerManager.isIgnoringBatteryOptimizations(context.packageName))
    }
    
    var isNotificationGranted by remember { 
        mutableStateOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true) 
    }
    
    var isShizukuRunning by remember { mutableStateOf(false) }
    var isShizukuGranted by remember { mutableStateOf(false) }
    
    val checkShizukuState = {
        try {
            isShizukuRunning = Shizuku.pingBinder()
            if (isShizukuRunning) {
                isShizukuGranted = (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED)
            }
        } catch (e: Exception) {
            isShizukuRunning = false
        }
    }

    DisposableEffect(Unit) {
        val binderReceivedListener = Shizuku.OnBinderReceivedListener { checkShizukuState() }
        val binderDeadListener = Shizuku.OnBinderDeadListener { isShizukuRunning = false }
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
        onDispose {
            Shizuku.removeBinderReceivedListener(binderReceivedListener)
            Shizuku.removeBinderDeadListener(binderDeadListener)
        }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isServiceEnabled = isAccessibilityServiceEnabled(context)
                isBatteryOptimized = !powerManager.isIgnoringBatteryOptimizations(context.packageName)
                checkShizukuState()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isNotificationGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "S H O R T C U T", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00FF41))
        Text(text = "CLICK TO SEARCH", fontSize = 16.sp, color = Color(0xFF00FF41))
        
        Spacer(modifier = Modifier.height(40.dp))

        StatusItem(
            label = "SHIZUKU STATUS",
            status = if (!isShizukuRunning) "NOT RUNNING (TAP TO RETRY)" else if (!isShizukuGranted) "PERMISSION DENIED (TAP TO GRANT)" else "CONNECTED & GRANTED",
            isError = !isShizukuRunning || !isShizukuGranted,
            onClick = {
                if (!isShizukuRunning) checkShizukuState()
                else if (!isShizukuGranted) Shizuku.requestPermission(1)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        StatusItem(
            label = "ACCESSIBILITY SERVICE",
            status = if (isServiceEnabled) "ACTIVE" else "INACTIVE (TAP TO TOGGLE)",
            isError = !isServiceEnabled,
            onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        StatusItem(
            label = "BATTERY OPTIMIZATION",
            status = if (isBatteryOptimized) "OPTIMIZED (TAP TO DISABLE)" else "OPTIMIZATION DISABLED",
            isError = isBatteryOptimized,
            onClick = {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        StatusItem(
            label = "NOTIFICATIONS",
            status = if (!isNotificationGranted) "BLOCKED (TAP TO ALLOW)" else "ALLOWED",
            isError = !isNotificationGranted,
            onClick = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        )
        
        if (!isShizukuRunning) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "[ FORCE RESTART APP ]",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { exitProcess(0) }
            )
        }
    }
}

@Composable
fun StatusItem(label: String, status: String, isError: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "--- $label ---", fontSize = 12.sp, color = Color(0xFF00FF41).copy(alpha = 0.7f))
        Text(
            text = status,
            color = if (isError) Color.Red else Color(0xFF00FF41),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = if (isError) Modifier.padding(vertical = 4.dp).clickable { onClick() } else Modifier.padding(vertical = 4.dp)
        )
    }
}

private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    val expectedService = "${context.packageName}/${KeyInterceptorService::class.java.canonicalName}"
    return enabledServices.contains(expectedService)
}
