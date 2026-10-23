package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import com.example.data.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppsRepository(
    private val context: Context,
    private val preferences: LauncherPreferences
) {
    private val recentAppsList = mutableListOf<String>()

    suspend fun getInstalledApps(): List<AppItem> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val pinnedPackages = preferences.getPinnedAppPackages()

        val apps = mutableListOf<AppItem>()

        for (ri in resolveInfos) {
            val pkg = ri.activityInfo.packageName
            // Skip launcher itself to avoid recursive launcher-in-launcher confusion
            if (pkg == context.packageName) continue

            val label = ri.loadLabel(pm).toString()
            val icon = try {
                ri.loadIcon(pm)
            } catch (_: Exception) {
                null
            }
            val activityName = ri.activityInfo.name
            val isSystem = (ri.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            val category = when {
                isSystem -> "System"
                label.contains("Chrome", true) || label.contains("Browser", true) || label.contains("Web", true) -> "Internet"
                label.contains("Music", true) || label.contains("YouTube", true) || label.contains("Media", true) || label.contains("Video", true) -> "Media"
                label.contains("Map", true) || label.contains("Clock", true) || label.contains("Calc", true) || label.contains("File", true) -> "Utilities"
                label.contains("Mail", true) || label.contains("Chat", true) || label.contains("Message", true) -> "Communication"
                else -> "Apps"
            }

            apps.add(
                AppItem(
                    id = "$pkg/$activityName",
                    label = label,
                    packageName = pkg,
                    activityName = activityName,
                    icon = icon,
                    isPinned = pinnedPackages.contains(pkg),
                    category = category
                )
            )
        }

        // Add essential companion launcher shortcuts if list has very few apps (e.g. testing emulator)
        if (apps.size < 4) {
            apps.addAll(getEssentialCompanionShortcuts(pinnedPackages))
        }

        apps.sortedBy { it.label.lowercase() }
    }

    private fun getEssentialCompanionShortcuts(pinnedPackages: Set<String>): List<AppItem> {
        val companions = listOf(
            AppItem("sys_settings", "Settings", "com.android.settings", "com.android.settings.Settings", null, pinnedPackages.contains("com.android.settings"), "System"),
            AppItem("sys_dialer", "Phone Dialer", "com.android.dialer", "com.android.dialer.DialtactsActivity", null, pinnedPackages.contains("com.android.dialer"), "Communication"),
            AppItem("sys_browser", "Web Browser", "com.android.browser", "com.android.browser.BrowserActivity", null, pinnedPackages.contains("com.android.browser"), "Internet"),
            AppItem("sys_camera", "Camera", "com.android.camera2", "com.android.camera.CameraLauncher", null, pinnedPackages.contains("com.android.camera2"), "Media"),
            AppItem("sys_gallery", "Gallery Photos", "com.android.gallery3d", "com.android.gallery3d.app.GalleryActivity", null, pinnedPackages.contains("com.android.gallery3d"), "Media"),
            AppItem("sys_calc", "Calculator", "com.android.calculator2", "com.android.calculator2.Calculator", null, pinnedPackages.contains("com.android.calculator2"), "Utilities"),
            AppItem("sys_clock", "Clock & Alarm", "com.android.deskclock", "com.android.deskclock.DeskClock", null, pinnedPackages.contains("com.android.deskclock"), "Utilities"),
            AppItem("sys_files", "Files Explorer", "com.android.documentsui", "com.android.documentsui.files.FilesActivity", null, pinnedPackages.contains("com.android.documentsui"), "Utilities")
        )
        return companions
    }

    fun launchApp(app: AppItem): Boolean {
        // Track recents
        recentAppsList.remove(app.packageName)
        recentAppsList.add(0, app.packageName)
        if (recentAppsList.size > 12) {
            recentAppsList.removeAt(recentAppsList.lastIndex)
        }

        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)
                true
            } catch (_: Exception) {
                false
            }
        } else {
            // Handle fallback system shortcuts
            try {
                when (app.id) {
                    "sys_settings" -> {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        true
                    }
                    "sys_dialer" -> {
                        context.startActivity(Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        true
                    }
                    else -> false
                }
            } catch (_: Exception) {
                false
            }
        }
    }

    fun openAppInfo(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun getRecentPackageNames(): List<String> = recentAppsList.toList()
}
