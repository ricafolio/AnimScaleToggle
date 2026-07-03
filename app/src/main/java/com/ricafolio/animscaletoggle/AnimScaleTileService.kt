package com.ricafolio.animscaletoggle

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

/**
 * Swipe-down Quick Settings tile. Tap to toggle animation scale on/off.
 * No activity launch needed, so it's the fastest path day-to-day.
 */
class AnimScaleTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        refresh()
    }

    override fun onClick() {
        super.onClick()
        try {
            AnimScale.toggle(this)
        } catch (e: AnimScale.PermissionMissingException) {
            Toast.makeText(this, "Permission missing — open app for setup", Toast.LENGTH_LONG).show()
        }
        refresh()
    }

    private fun refresh() {
        val tile = qsTile ?: return
        val off = try {
            AnimScale.isOff(this)
        } catch (e: Exception) {
            false
        }
        tile.state = if (off) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = if (off) "Animations: Off" else "Animations: On"
        tile.updateTile()
    }
}
