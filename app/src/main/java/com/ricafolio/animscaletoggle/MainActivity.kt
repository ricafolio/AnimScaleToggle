package com.ricafolio.animscaletoggle

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Plain-View UI (no Compose dependency needed for something this small).
 * Full black background throughout — no light containers anywhere.
 *
 * Layout order:
 *   Heading -> status -> toggle button
 *   Step 1: grant permission
 *   Customize your "enabled" speed
 *   Step 2: turn Developer Options back off (with live detection)
 *   Ways to trigger the toggle (last)
 */
class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var devOptionsText: TextView

    private val colorBg = Color.BLACK
    private val colorGreen = Color.parseColor("#4CAF50")
    private val colorRed = Color.parseColor("#EF5350")
    private val colorNeutral = Color.parseColor("#B0BEC5")
    private val colorMuted = Color.parseColor("#9E9E9E")
    private val colorWhite = Color.WHITE
    private val colorDivider = Color.parseColor("#333333")
    private val colorCardBg = Color.parseColor("#111111")
    private val colorCardBorder = Color.parseColor("#333333")

    private val sectionGap = 52

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 60, 48, 64)
            setBackgroundColor(colorBg)
        }

        fun heading(text: String) = TextView(this).apply {
            this.text = text
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(colorWhite)
            setPadding(0, 32, 0, 12)
        }

        fun body(text: String, topPad: Int = 0, color: Int = colorMuted) = TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(color)
            setPadding(0, topPad, 0, 0)
        }

        fun sectionTitle(text: String, topPad: Int = 0) = TextView(this).apply {
            this.text = text
            textSize = 15f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(colorWhite)
            setPadding(0, topPad, 0, 4)
        }

        fun divider(yMargin: Int = sectionGap) = View(this).apply {
            setBackgroundColor(colorDivider)
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2).apply {
                this.topMargin = yMargin
                this.bottomMargin = yMargin
            }
        }

        fun circleBadge(number: String): TextView {
            val sizePx = (24 * resources.displayMetrics.density).toInt()
            return TextView(this).apply {
                text = number
                textSize = 12f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                background = android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.OVAL
                    setColor(colorWhite)
                }
                layoutParams = LinearLayout.LayoutParams(sizePx, sizePx)
            }
        }

        fun stepHeaderRow(number: String, title: String): LinearLayout {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 16)
            }
            row.addView(circleBadge(number))
            row.addView(TextView(this).apply {
                text = title
                textSize = 15f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(colorWhite)
                setPadding(20, 0, 0, 0)
            })
            return row
        }

        // ---------- Heading + purpose ----------
        root.addView(heading(getString(R.string.app_name)), lp)
        root.addView(
            body(
                "Turns Android's animation speed off/on with one tap — " +
                    "without touching Developer Options, so it won't reset " +
                    "every time you flip USB debugging on and off."
            ),
            lp.apply { bottomMargin = 6 }
        )

        // ---------- Live status ----------
        statusText = TextView(this).apply {
            textSize = 17f
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
        }
        root.addView(statusText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 32; bottomMargin = 24 })

        val toggleBtn = Button(this).apply {
            text = "Toggle animations now"
            gravity = Gravity.CENTER
            setOnClickListener {
                try {
                    AnimScale.toggle(this@MainActivity)
                    refreshStatus()
                } catch (e: AnimScale.PermissionMissingException) {
                    Toast.makeText(context, "Permission missing — do step 1 below first", Toast.LENGTH_LONG).show()
                }
            }
        }
        root.addView(toggleBtn, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 12 })

        root.addView(divider())

        // ---------- Step 1: grant permission ----------
        root.addView(stepHeaderRow("1", "Grant the permission (one time only)"), lp.apply { topMargin = sectionGap })

        root.addView(
            body("This grant sticks even after Developer Options is turned off. You only need to do this once, unless you reinstall the app.", topPad = 8),
            lp
        )

        root.addView(
            body("Connect via USB with debugging on, then run this on your computer:", topPad = 8),
            lp
        )

        val adbCommand = "adb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"

        val adbText = TextView(this).apply {
            text = adbCommand
            textSize = 13f
            setPadding(24, 24, 24, 24)
            setTextColor(colorWhite)
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(colorCardBg)
                setStroke(2, colorCardBorder)
                cornerRadius = 12f
            }
        }
        root.addView(adbText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 18 })

        val copyBtn = Button(this).apply {
            text = "Copy command"
            setOnClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("adb command", adbCommand))
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            }
        }
        root.addView(copyBtn, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.START
            leftMargin = 0
        })

        root.addView(divider())

        // ---------- Step 2: customize speed ----------
        root.addView(stepHeaderRow("2", "Customize your \"enabled\" speed"), lp.apply { topMargin = sectionGap })
        root.addView(
            body("One value applies to all three animation settings. Default is 0.5x — most people find anything much lower starts to feel choppy.", topPad = 8),
            lp
        )

        root.addView(
            body("Applies immediately to the button above, tile, widget, and shortcuts.", topPad = 8),
            lp
        )

        val scaleInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "e.g. 0.5"
            setHintTextColor(colorMuted)
            setTextColor(colorWhite)
            setPadding(24, 24, 24, 24)
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(colorCardBg)
                setStroke(2, colorCardBorder)
                cornerRadius = 12f
            }
            setText(AnimScale.getOffScale(this@MainActivity).toString())
        }
        root.addView(scaleInput, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 18 })

        val saveScaleBtn = Button(this).apply {
            text = "Save value"
            setOnClickListener {
                val value = scaleInput.text.toString().toFloatOrNull()
                if (value == null || value <= 0f || value > 10f) {
                    Toast.makeText(context, "Enter a number between 0 and 10", Toast.LENGTH_SHORT).show()
                } else {
                    AnimScale.setOffScale(this@MainActivity, value)
                    Toast.makeText(context, "Saved — ${value}x is now your enabled speed", Toast.LENGTH_SHORT).show()
                    refreshStatus()
                }
            }
        }
        root.addView(saveScaleBtn, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.START
            leftMargin = 0
        })

        root.addView(divider())

        // ---------- Step 3: turn dev options back off ----------
        root.addView(stepHeaderRow("3", "Turn Developer Options back off"), lp.apply { topMargin = sectionGap })

        devOptionsText = TextView(this).apply {
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
            setPadding(0, 12, 0, 0)
        }
        root.addView(devOptionsText, lp)
        root.addView(body("Safe to turn off right away — this app's permission isn't tied to that toggle.", topPad = 6), lp)

        root.addView(divider())

        // ---------- Ways to trigger the toggle (last) ----------
        root.addView(heading("That's it!"), lp.apply { topMargin = sectionGap })

        root.addView(sectionTitle("You can now use these ways to toggle speed").apply {
            gravity = Gravity.CENTER
        }, lp.apply {
            topMargin = sectionGap
            bottomMargin = 16
        })

        val waysCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 32, 24, 32)
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(colorCardBg)
                setStroke(2, colorCardBorder)
                cornerRadius = 20f
            }
        }
        fun wayRow(title: String, detail: String) {
            waysCard.addView(TextView(this).apply {
                text = title
                textSize = 14f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(colorWhite)
                setPadding(12, if (waysCard.childCount == 0) 0 else 16, 12, 0)
            })
            waysCard.addView(TextView(this).apply {
                text = detail
                textSize = 13f
                setTextColor(colorMuted)
                setPadding(12, 2, 12, 0)
            })
        }
        wayRow("Home screen widget", "· Long-press your home screen → Widgets → \"${getString(R.string.app_name)}\". Place the button anywhere convenient in your home screen.")
        wayRow("Pinned app shortcuts", "· Long-press this app's launcher icon → pin Off / On / Toggle individually.")
        wayRow("Quick Settings tile", "· Swipe down twice → edit tiles → add \"${getString(R.string.tile_label)}\". Shows current state in the tile itself.")
        wayRow("This screen", "· Just come back here anytime and tap the button above!")
        root.addView(waysCard, lp.apply { topMargin = 8 })

        val scroll = ScrollView(this).apply { setBackgroundColor(colorBg) }
        scroll.addView(root)
        setContentView(scroll)
        refreshStatus()
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun isDevOptionsOn(): Boolean {
        return Settings.Global.getInt(
            contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) == 1
    }

    private fun refreshStatus() {
        val off = try {
            AnimScale.isOff(this)
        } catch (e: Exception) {
            null
        }
        val offScale = AnimScale.getOffScale(this)

        val (label, textColor) = when (off) {
            true -> "Enabled — ${offScale}x speed" to colorGreen
            false -> "Disabled — 1x (default)" to colorNeutral
            null -> "Unknown — permission missing" to colorRed
        }
        statusText.text = label
        statusText.setTextColor(textColor)

        val devOn = isDevOptionsOn()
        devOptionsText.text = if (devOn) "Your Developer Options is currently ON" else "Your Developer Options is currently OFF"
        devOptionsText.setTextColor(if (devOn) colorRed else colorGreen)
    }
}
