package com.example.data

import com.example.model.HudButtonInfo
import java.util.Locale

object LayoutJsonGenerator {

    /**
     * Generates an authentic, 100% playable PojavLauncher / Mojo Launcher / Zalith Launcher
     * controlmap JSON (version 9, scaledAt 100.0) with mControlDataList, mDrawerDataList,
     * and mJoystickDataList.
     */
    fun generatePojavControlJson(
        layoutName: String,
        fileName: String,
        category: String,
        buttons: List<HudButtonInfo>,
        version: String = "1.21.x",
        macroInfo: String = ""
    ): String {
        val controlButtons = buttons.filter { !it.isJoystick }
        val joystickButtons = buttons.filter { it.isJoystick }

        val controlsJson = controlButtons.joinToString(",\n") { btn ->
            val k0 = btn.keycodes.getOrElse(0) { 0 }
            val k1 = btn.keycodes.getOrElse(1) { 0 }
            val k2 = btn.keycodes.getOrElse(2) { 0 }
            val k3 = btn.keycodes.getOrElse(3) { 0 }

            val widthPx = btn.rawWidthPx ?: (btn.widthPercent * 760f).coerceIn(32f, 145f)
            val heightPx = btn.rawHeightPx ?: (btn.heightPercent * 440f).coerceIn(20f, 110f)

            val dynX = btn.rawDynamicX ?: String.format(
                Locale.US,
                "%.7f * \${screen_width}",
                btn.xPercent.coerceIn(0.001f, 0.94f)
            )
            val dynY = btn.rawDynamicY ?: String.format(
                Locale.US,
                "%.7f * \${screen_height}",
                btn.yPercent.coerceIn(0.002f, 0.92f)
            )

            val safeName = btn.name.replace("\\", "\\\\").replace("\"", "\\\"")

            """    {
      "bgColor": 1291845632,
      "cornerRadius": ${String.format(Locale.US, "%.1f", btn.cornerRadius)},
      "displayInGame": ${btn.displayInGame},
      "displayInMenu": ${btn.displayInMenu},
      "dynamicX": "$dynX",
      "dynamicY": "$dynY",
      "height": ${String.format(Locale.US, "%.6f", heightPx).trimEnd('0').let { if (it.endsWith(".")) "${it}0" else it }},
      "isSwipeable": ${btn.isSwipe},
      "isToggle": ${btn.isToggle},
      "keycodes": [
        $k0,
        $k1,
        $k2,
        $k3
      ],
      "name": "$safeName",
      "opacity": ${String.format(Locale.US, "%.2f", btn.opacity).trimEnd('0').let { if (it.endsWith(".")) "${it}0" else it }},
      "passThruEnabled": false,
      "strokeColor": ${btn.strokeColor},
      "strokeWidth": ${String.format(Locale.US, "%.1f", btn.strokeWidth)},
      "width": ${String.format(Locale.US, "%.6f", widthPx).trimEnd('0').let { if (it.endsWith(".")) "${it}0" else it }}
    }"""
        }

        val joysticksJson = if (joystickButtons.isNotEmpty()) {
            joystickButtons.joinToString(",\n") { joy ->
                val widthPx = joy.rawWidthPx ?: 108.24964f
                val heightPx = joy.rawHeightPx ?: 88.49928f
                val dynX = joy.rawDynamicX ?: String.format(
                    Locale.US,
                    "%.8f * \${screen_width}",
                    joy.xPercent.coerceIn(0.02f, 0.25f)
                )
                val dynY = joy.rawDynamicY ?: String.format(
                    Locale.US,
                    "%.8f * \${screen_height} - \${height}",
                    (joy.yPercent + joy.heightPercent).coerceIn(0.65f, 0.96f)
                )
                """    {
      "absolute": true,
      "forwardLock": true,
      "bgColor": 1291845632,
      "cornerRadius": ${String.format(Locale.US, "%.1f", joy.cornerRadius)},
      "displayInGame": true,
      "displayInMenu": false,
      "dynamicX": "$dynX",
      "dynamicY": "$dynY",
      "height": ${String.format(Locale.US, "%.5f", heightPx)},
      "isSwipeable": false,
      "isToggle": false,
      "keycodes": [
        0,
        0,
        0,
        0
      ],
      "name": "button",
      "opacity": 1.0,
      "passThruEnabled": false,
      "strokeColor": ${joy.strokeColor},
      "strokeWidth": ${String.format(Locale.US, "%.1f", joy.strokeWidth)},
      "width": ${String.format(Locale.US, "%.5f", widthPx)}
    }"""
            }
        } else {
            ""
        }

        val joystickSection = if (joysticksJson.isNotEmpty()) {
            "[\n$joysticksJson\n  ]"
        } else {
            "[]"
        }

        return """{
  "mControlDataList": [
$controlsJson
  ],
  "mDrawerDataList": [],
  "mJoystickDataList": $joystickSection,
  "scaledAt": 100.0,
  "version": 9
}"""
    }
}
