package com.ricafolio.animscaletoggle

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast

/**
 * 1x1 home screen widget that looks/behaves like an app icon button.
 * Tap = toggle animation scale, shows a toast, updates the widget icon/label
 * to reflect current state.
 */
class AnimScaleWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE = "com.ricafolio.animscaletoggle.WIDGET_TOGGLE"

        fun buildViews(context: Context, widgetId: Int): RemoteViews {
            val off = try {
                AnimScale.isOff(context)
            } catch (e: Exception) {
                false
            }
            val offScale = AnimScale.getOffScale(context)
            fun fmt(v: Float) = if (v == v.toInt().toFloat()) "${v.toInt()}" else v.toString()

            val views = RemoteViews(context.packageName, R.layout.widget_toggle)
            views.setTextViewText(R.id.widget_label, if (off) "Anim: ${fmt(offScale)}x" else "Anim: ${fmt(AnimScale.SCALE_ON)}x")
            views.setInt(
                R.id.widget_root,
                "setBackgroundColor",
                if (off) 0xFF1B5E20.toInt() else 0xFF37474F.toInt()
            )

            val clickIntent = Intent(context, AnimScaleWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE
            }
            val pendingIntent = android.app.PendingIntent.getBroadcast(
                context,
                widgetId,
                clickIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            return views
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            appWidgetManager.updateAppWidget(id, buildViews(context, id))
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) {
            try {
                val nowOff = AnimScale.toggle(context)
                Toast.makeText(
                    context,
                    if (nowOff) "Animations OFF" else "Animations ON",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: AnimScale.PermissionMissingException) {
                Toast.makeText(context, "Permission missing — open app for setup", Toast.LENGTH_LONG).show()
            }

            // Refresh every instance of this widget on screen
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, AnimScaleWidgetProvider::class.java)
            )
            for (id in ids) {
                manager.updateAppWidget(id, buildViews(context, id))
            }
        }
    }
}
