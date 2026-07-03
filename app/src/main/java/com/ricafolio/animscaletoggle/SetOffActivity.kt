package com.ricafolio.animscaletoggle

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

/** No-UI activity: force animation scale OFF (e.g. before opening a bank app). */
class SetOffActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            AnimScale.setScale(this, AnimScale.getOffScale(this))
            Toast.makeText(this, "Animations OFF", Toast.LENGTH_SHORT).show()
        } catch (e: AnimScale.PermissionMissingException) {
            Toast.makeText(this, "Permission missing — open app for setup", Toast.LENGTH_LONG).show()
        }
        finish()
    }
}
