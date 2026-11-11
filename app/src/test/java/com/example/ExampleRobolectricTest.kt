package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DefaultCatalog
import com.example.model.AppCategory
import com.example.model.MediaApp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("OmniStore TV", appName)
    }

    @Test
    fun `default catalog loads media applications`() {
        val apps = DefaultCatalog.sampleApps
        assertTrue("Catalog should contain sample apps", apps.isNotEmpty())
        assertTrue("Catalog should contain at least 20 apps", apps.size >= 20)

        val kodi = apps.find { it.id == "kodi" }
        assertNotNull("Kodi should be in catalog", kodi)
        assertTrue("Kodi should be marked as featured", kodi!!.isFeatured)
        assertEquals(AppCategory.MEDIA_PLAYERS, kodi.category)
    }

    @Test
    fun `sports apps are present in catalog`() {
        val apps = DefaultCatalog.sampleApps
        val sportsApps = apps.filter { it.category == AppCategory.SPORTS }
        assertTrue("Catalog should contain sports apps", sportsApps.size >= 8)

        val espn = sportsApps.find { it.id == "espn_tv" }
        assertNotNull("ESPN for Android TV should be in catalog", espn)
        assertEquals("ESPN for Android TV", espn!!.name)

        val f1tv = sportsApps.find { it.id == "f1_tv" }
        assertNotNull("F1 TV should be in catalog", f1tv)
        assertEquals("F1 TV for Android TV", f1tv!!.name)
        assertEquals("Formula One Digital", f1tv.developer)
    }

    @Test
    fun `hdstreamz and live streaming community apps are present in catalog`() {
        val apps = DefaultCatalog.sampleApps
        val hdstreamz = apps.find { it.id == "hdstreamz_tv" }
        assertNotNull("HD Streamz should be in catalog", hdstreamz)
        assertEquals("HD Streamz Live TV", hdstreamz!!.name)
        assertEquals("HD Streamz Team", hdstreamz.developer)
        assertEquals(AppCategory.IPTV_LIVE, hdstreamz.category)
        assertTrue("HD Streamz should be featured", hdstreamz.isFeatured)

        val liveNetTv = apps.find { it.id == "live_nettv" }
        assertNotNull("Live NetTV should be in catalog", liveNetTv)
        assertEquals("Live NetTV Companion", liveNetTv!!.name)

        val rapidStreamz = apps.find { it.id == "rapid_streamz" }
        assertNotNull("Rapid Streamz should be in catalog", rapidStreamz)
        assertEquals("Rapid Streamz TV", rapidStreamz!!.name)

        val cinemaHd = apps.find { it.id == "cinema_hd" }
        assertNotNull("Cinema HD should be in catalog", cinemaHd)

        assertTrue("Total catalog should now have at least 40 apps", apps.size >= 40)
    }

    @Test
    fun `media app update detection works correctly`() {
        val appWithUpdate = MediaApp(
            id = "test_app",
            name = "Test Player",
            packageName = "com.test.player",
            versionName = "2.0.0",
            versionCode = 200,
            installedVersionName = "1.0.0",
            installedVersionCode = 100,
            category = AppCategory.MEDIA_PLAYERS,
            description = "Test description",
            changelog = "New features",
            downloadUrl = "https://example.com/app.apk",
            iconUrl = "",
            fileSizeBytes = 1024L * 1024L * 10L,
            developer = "Test Dev"
        )

        assertTrue("App should have update available", appWithUpdate.hasUpdate)
        assertTrue("App should be installed", appWithUpdate.isInstalled)

        val appUpToDate = appWithUpdate.copy(
            versionCode = 200,
            installedVersionCode = 200,
            installedVersionName = "2.0.0"
        )
        assertFalse("App up to date should not show pending update", appUpToDate.hasUpdate)
    }

    @Test
    fun `package installer receiver processes success intent`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val receiver = com.example.service.PackageInstallerStatusReceiver()

        val intent = android.content.Intent(com.example.service.PackageInstallerStatusReceiver.ACTION_INSTALL_STATUS).apply {
            putExtra(android.content.pm.PackageInstaller.EXTRA_STATUS, android.content.pm.PackageInstaller.STATUS_SUCCESS)
            putExtra(com.example.service.PackageInstallerStatusReceiver.EXTRA_PACKAGE_NAME, "org.xbmc.kodi")
            putExtra(com.example.service.PackageInstallerStatusReceiver.EXTRA_APP_NAME, "Kodi Media Center")
        }

        // Execute onReceive - verifies no crashes and proper handling
        receiver.onReceive(context, intent)
    }
}
