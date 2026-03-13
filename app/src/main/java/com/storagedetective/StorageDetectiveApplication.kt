package com.storagedetective

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.storagedetective.core.constants.AppConstants
import com.storagedetective.core.utils.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StorageDetectiveApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var logger: Logger

    override fun onCreate() {
        super.onCreate()
        
        // Initialize logger
        Logger.getInstance().d("Application starting...")
        
        // Create notification channels
        createNotificationChannels()
        
        // Initialize preferences
        initPreferences()
        
        // Setup crash handler
        setupCrashHandler()
        
        // Start background workers
        scheduleBackgroundWorkers()
        
        Logger.getInstance().d("Application initialized successfully")
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Scan channel
            val scanChannel = NotificationChannel(
                AppConstants.CHANNEL_ID_SCAN,
                "Storage Scans",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications about storage scans"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(scanChannel)

            // Clean channel
            val cleanChannel = NotificationChannel(
                AppConstants.CHANNEL_ID_CLEAN,
                "Storage Cleanup",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about cleanup operations"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(cleanChannel)

            // Warning channel
            val warningChannel = NotificationChannel(
                AppConstants.CHANNEL_ID_WARNING,
                "Storage Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Warnings about storage issues"
                enableVibration(true)
                setSound(
                    android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                    null
                )
            }
            notificationManager.createNotificationChannel(warningChannel)

            // Backup channel
            val backupChannel = NotificationChannel(
                AppConstants.CHANNEL_ID_BACKUP,
                "Backup & Sync",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about backups"
            }
            notificationManager.createNotificationChannel(backupChannel)

            // Game channel
            val gameChannel = NotificationChannel(
                AppConstants.CHANNEL_ID_GAME,
                "Achievements",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Achievement notifications"
            }
            notificationManager.createNotificationChannel(gameChannel)
        }
    }

    private fun initPreferences() {
        // Initialize default preferences
        val prefs = getSharedPreferences("storage_detective_prefs", MODE_PRIVATE)
        
        if (!prefs.contains("first_launch")) {
            prefs.edit().apply {
                putBoolean("first_launch", true)
                putBoolean("analytics_enabled", true)
                putBoolean("crash_reporting", true)
                putString("theme", "system")
                apply()
            }
        }
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Logger.getInstance().e("Uncaught exception", throwable)
            
            // Log to file
            logCrashToFile(throwable)
            
            // Call default handler
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun logCrashToFile(throwable: Throwable) {
        try {
            val crashLog = File(filesDir, "crash_log.txt")
            crashLog.appendText(
                """
                === Crash at ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                    .format(java.util.Date())} ===
                ${android.util.Log.getStackTraceString(throwable)}
                
                """.trimIndent()
            )
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun scheduleBackgroundWorkers() {
        // Schedule periodic scan
        workers.StorageScanWorker.schedulePeriodicScan(
            this,
            intervalHours = 24,
            scanType = "incremental"
        )

        // Schedule auto clean
        workers.AutoCleanWorker.schedulePeriodicAutoClean(this)

        // Schedule analytics
        workers.AnalyticsWorker.scheduleDailyAnalytics(this)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        
        when (level) {
            TRIM_MEMORY_UI_HIDDEN -> {
                Logger.getInstance().d("UI hidden - releasing resources")
            }
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                Logger.getInstance().w("Memory critical - clearing caches")
                clearCaches()
            }
        }
    }

    private fun clearCaches() {
        // Clear image caches
        coil.ImageLoader(this).diskCache?.clear()
        
        // Clear database caches
        // database.query("PRAGMA shrink_memory", null)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Logger.getInstance().w("Low memory warning")
        clearCaches()
    }
}
