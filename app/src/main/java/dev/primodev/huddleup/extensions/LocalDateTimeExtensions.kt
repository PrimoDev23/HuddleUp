package dev.primodev.huddleup.extensions

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.primodev.huddleup.R
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

private val MILLISECONDS_PER_DAY = 1.days.inWholeMilliseconds

@Composable
internal fun LocalTime.toLocalizedString(): String {
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

        this.format(formatter)
    }
}

fun LocalDateTime.plus(duration: Duration): LocalDateTime {
    // Get milliseconds not filling a whole day
    val milliseconds = duration.inWholeMilliseconds - duration.inWholeDays.days.inWholeMilliseconds
    val millisecondsOfDay = time.toMillisecondOfDay() + milliseconds.toInt()

    // Milliseconds is bigger than a single day
    return if (millisecondsOfDay > MILLISECONDS_PER_DAY) {
        // Add one more day
        val days = duration.inWholeDays + 1

        // Subtract one day
        val timeMilliseconds = millisecondsOfDay - MILLISECONDS_PER_DAY

        val date = date.plus(
            value = days,
            unit = DateTimeUnit.DAY
        )
        val time = LocalTime.fromMillisecondOfDay(timeMilliseconds.toInt())

        LocalDateTime(
            date = date,
            time = time
        )
    } else {
        val date = date.plus(
            value = duration.inWholeDays,
            unit = DateTimeUnit.DAY
        )

        val time = LocalTime.fromMillisecondOfDay(millisecondsOfDay)

        LocalDateTime(
            date = date,
            time = time
        )
    }
}

fun LocalDateTime.minus(duration: Duration): LocalDateTime {
    // Get milliseconds not filling a whole day
    val milliseconds = duration.inWholeMilliseconds - duration.inWholeDays.days.inWholeMilliseconds
    val millisecondsOfDay = time.toMillisecondOfDay() - milliseconds.toInt()

    // Milliseconds is bigger than a single day
    return if (millisecondsOfDay < 0) {
        // Add one more day
        val days = duration.inWholeDays + 1

        // Subtract one day
        val timeMilliseconds = millisecondsOfDay + MILLISECONDS_PER_DAY

        val date = date.minus(
            value = days,
            unit = DateTimeUnit.DAY
        )
        val time = LocalTime.fromMillisecondOfDay(timeMilliseconds.toInt())

        LocalDateTime(
            date = date,
            time = time
        )
    } else {
        val date = date.minus(
            value = duration.inWholeDays,
            unit = DateTimeUnit.DAY
        )

        val time = LocalTime.fromMillisecondOfDay(millisecondsOfDay)

        LocalDateTime(
            date = date,
            time = time
        )
    }
}

fun LocalDateTime.atTime(
    hour: Int,
    minute: Int,
    second: Int = 0,
    nanosecond: Int = 0,
) = this.date.atTime(
    hour = hour,
    minute = minute,
    second = second,
    nanosecond = nanosecond
)

fun LocalDateTime.atTime(
    time: LocalTime,
) = this.date.atTime(time)