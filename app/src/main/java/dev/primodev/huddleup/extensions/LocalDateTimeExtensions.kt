package dev.primodev.huddleup.extensions

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.primodev.huddleup.R
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char

@Composable
internal fun LocalDateTime.toLocalizedString(): String {
    val context = LocalContext.current
    val amMarker = stringResource(R.string.local_time_am_marker)
    val pmMarker = stringResource(R.string.local_time_pm_marker)

    return remember(this) {
        val formatter = LocalTime.Format {
            val is24HourFormat = DateFormat.is24HourFormat(context)

            if (is24HourFormat) {
                hour()
                char(':')
                minute()
            } else {
                amPmHour()
                char(':')
                minute()
                char(' ')
                amPmMarker(
                    am = amMarker,
                    pm = pmMarker
                )
            }
        }

        this.time.format(formatter)
    }
}