package dev.primodev.huddleup.feature.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.primodev.huddleup.R
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.extensions.atTime
import dev.primodev.huddleup.extensions.toLocalizedString
import dev.primodev.huddleup.theme.HuddleUpTheme
import kotlinx.datetime.Clock

@Composable
internal fun EventCard(
    event: Event,
    onEndToStartSwiped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val swipeToDismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(onEndToStartSwiped) {
        snapshotFlow { swipeToDismissState.currentValue }
            .collect { currentValue ->
                when (currentValue) {
                    SwipeToDismissBoxValue.Settled,
                    SwipeToDismissBoxValue.StartToEnd,
                        -> Unit

                    SwipeToDismissBoxValue.EndToStart -> onEndToStartSwiped()
                }
            }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = swipeToDismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.error,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                val animation = remember {
                    Animatable(initialValue = 0f)
                }

                LaunchedEffect(true) {
                    snapshotFlow { swipeToDismissState.targetValue }
                        .collect { targetValue ->
                            when (targetValue) {
                                SwipeToDismissBoxValue.Settled,
                                SwipeToDismissBoxValue.StartToEnd,
                                    -> animation.snapTo(0f)

                                SwipeToDismissBoxValue.EndToStart -> animation.animateTo(
                                    targetValue = 0.1f,
                                    animationSpec = keyframes {
                                        durationMillis = 500

                                        0f at 0
                                        30f at 100
                                        (-30f) at 200
                                        15f at 300
                                        (-15f) at 400
                                        0.1f at 500
                                    }
                                )
                            }
                        }
                }

                Icon(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(
                                pivotFractionX = 0.5f,
                                pivotFractionY = 1f
                            )

                            rotationZ = animation.value
                        },
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.event_card_delete),
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }
    ) {
        EventCardContent(
            modifier = Modifier.fillMaxWidth(),
            event = event
        )
    }
}

@Composable
private fun EventCardContent(
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
                when (event.duration) {
                    EventDuration.AllDay -> {
                        Text(
                            text = stringResource(R.string.event_card_all_day),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    EventDuration.Specific -> {
                        val start = event.start.toLocalizedString()
                        val end = event.end.toLocalizedString()

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
                onEndToStartSwiped = {}
            )
        }
    }
}

private class EventProvider : PreviewParameterProvider<Event> {
    private val now = Clock.System.now()

    override val values: Sequence<Event> = sequenceOf(
        Event(
            duration = EventDuration.Specific,
            start = now.atTime(13, 13),
            end = now.atTime(16, 16),
            title = "Specific event one day"
        ),
        Event(
            duration = EventDuration.AllDay,
            start = now,
            end = now,
            title = "All day event"
        ),
    )
}

private enum class SwipeState {
    None,
    EndToStart
}