package com.example

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.widget.HeatmapWidgetProvider
import com.example.widget.SparkFlameWidgetProvider
import com.example.widget.StreaksWidgetProvider
import com.example.widget.TasksWidgetProvider
import com.example.widget.WidgetUpdater
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WidgetRobolectricTest {

    @Test
    fun testWidgetProvidersRegistered() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        assertNotNull(appWidgetManager)

        val streaksComp = ComponentName(context, StreaksWidgetProvider::class.java)
        val tasksComp = ComponentName(context, TasksWidgetProvider::class.java)
        val flameComp = ComponentName(context, SparkFlameWidgetProvider::class.java)
        val heatmapComp = ComponentName(context, HeatmapWidgetProvider::class.java)

        assertNotNull(streaksComp)
        assertNotNull(tasksComp)
        assertNotNull(flameComp)
        assertNotNull(heatmapComp)
    }

    @Test
    fun testWidgetUpdaterExecution() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Verify executing WidgetUpdater does not crash
        WidgetUpdater.updateAllWidgets(context)
    }
}
