package dev.primodev.huddleup.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.primodev.huddleup.R
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.extensions.nowAsDateTime
import dev.primodev.huddleup.extensions.toLocalizedString
import dev.primodev.huddleup.theme.HuddleUpTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atDate

@Composable
internal fun EventCard(
    event: Event,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = event.title,
                style = MaterialTheme.typography.titleMedium
            )

            Column {
                when (val duration = event.duration) {
                    is EventDuration.AllDay -> {
                        Text(
                            text = stringResource(R.string.event_card_all_day),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    is EventDuration.Specific -> {
                        val start = duration.start.toLocalizedString()
                        val end = duration.end.toLocalizedString()

                        Text(
                            text = start,
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = end,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EventCardPreview(
    @PreviewParameter(EventProvider::class) event: Event,
) {
    HuddleUpTheme {
        Surface {
            EventCard(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                event = event,
            )
        }
    }
}

private class EventProvider : PreviewParameterProvider<Event> {
    private val now = Clock.System.nowAsDateTime()

    override val values: Sequence<Event> = sequenceOf(
        Event(
            duration = EventDuration.Specific(
                start = LocalTime(13, 13).atDate(now.date),
                end = LocalTime(16, 16).atDate(now.date)
            ),
            title = "Specific event one day"
        ),
        Event(
            duration = EventDuration.AllDay(
                start = now.date,
                end = now.date
            ),
            title = "All day event"
        ),
    )

}