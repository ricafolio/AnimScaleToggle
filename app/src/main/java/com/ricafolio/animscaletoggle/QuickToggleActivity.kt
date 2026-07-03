package com.ricafolio.animscaletoggle

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

/**
 * No-UI activity. Launch via home screen shortcut / app shortcut / Tasker intent
 * to flip animation scale between off and on in one tap, then closes itself.
 */
class QuickToggleActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val nowOff = AnimScale.toggle(this)
            Toast.makeText(
                this,
                if (nowOff) "Animations OFF" else "Animations ON",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: AnimScale.PermissionMissingException) {
            Toast.makeText(this, "Permission missing — open app for setup", Toast.LENGTH_LONG).show()
        }
        finish()
    }
}
