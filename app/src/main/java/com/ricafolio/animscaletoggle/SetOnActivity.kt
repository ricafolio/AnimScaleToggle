package com.ricafolio.animscaletoggle

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

/** No-UI activity: restore animation scale to normal (1x). */
class SetOnActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            AnimScale.setScale(this, AnimScale.SCALE_ON)
            Toast.makeText(this, "Animations ON", Toast.LENGTH_SHORT).show()
        } catch (e: AnimScale.PermissionMissingException) {
            Toast.makeText(this, "Permission missing — open app for setup", Toast.LENGTH_LONG).show()
        }
        finish()
    }
}
