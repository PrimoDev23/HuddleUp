package dev.primodev.huddleup.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dev.primodev.huddleup.domain.entity.event.Event
import dev.primodev.huddleup.domain.entity.event.EventDuration
import kotlinx.datetime.Clock

@Composable
internal fun CalendarEventBubbleRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit,
) {
    val horizontalSpacing = with(LocalDensity.current) {
        horizontalArrangement.spacing.roundToPx()
    }

    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val width = constraints.maxWidth

        var currentX = 0
        val placeableConstraints = Constraints()
        val spacedBubbles = buildList {
            for (index in measurables.indices) {
                val measurable = measurables[index]
                val placeable = measurable.measure(placeableConstraints)
                val newX = if (index == 0 || index == measurables.lastIndex) {
                    currentX + placeable.width
                } else {
                    currentX + horizontalSpacing + placeable.width
                }

                if (newX > width) {
                    break
                } else {
                    add(
                        Bubble(
                            placeable = placeable,
                            x = newX
                        )
                    )
                }

                currentX = newX
            }
        }

        val outPositions = IntArray(spacedBubbles.size)
        val sizes = IntArray(spacedBubbles.size) { index ->
            spacedBubbles[index].placeable.width
        }

        with(horizontalArrangement) {
            arrange(
                totalSize = width,
                sizes = sizes,
                layoutDirection = layoutDirection,
                outPositions = outPositions
            )
        }

        val arrangedBubbles = spacedBubbles.mapIndexed { index, bubble ->
            bubble.copy(
                x = outPositions[index]
            )
        }

        layout(width, constraints.minHeight) {
            arrangedBubbles.forEach { bubble ->
                bubble.placeable.place(
                    x = bubble.x,
                    y = 0
                )
            }
        }
    }
}

private data class Bubble(
    val placeable: Placeable,
    val x: Int,
)

@Preview
@Composable
private fun CalendarEventBubbleRowPreview(
    @PreviewParameter(BubbleRowPreviewDataProvider::class) data: BubbleRowPreviewData,
) {
    Surface {
        CalendarEventBubbleRow(
            modifier = Modifier
                .width(22.dp)
                .height(4.dp),
            horizontalArrangement = data.arrangement
        ) {
            data.events.forEach { _ ->
                Canvas(modifier = Modifier.size(4.dp)) {
                    drawCircle(color = Color.Black)
                }
            }
        }
    }
}

private data class BubbleRowPreviewData(
    val events: List<Event>,
    val arrangement: Arrangement.Horizontal,
)

private class BubbleRowPreviewDataProvider : PreviewParameterProvider<BubbleRowPreviewData> {
    private val now = Clock.System.now()

    override val values: Sequence<BubbleRowPreviewData> = sequenceOf(
        BubbleRowPreviewData(
            events = listOf(
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 1"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 2"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 3"
                ),
            ),
            arrangement = Arrangement.Start
        ),
        BubbleRowPreviewData(
            events = listOf(
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 1"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 2"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 3"
                ),
            ),
            arrangement = Arrangement.End
        ),
        BubbleRowPreviewData(
            events = listOf(
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 1"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 2"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 3"
                ),
            ),
            arrangement = Arrangement.Center
        ),
        BubbleRowPreviewData(
            events = listOf(
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 1"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 2"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 3"
                ),
            ),
            arrangement = Arrangement.spacedBy(
                space = 2.dp,
                alignment = Alignment.CenterHorizontally
            )
        ),
        BubbleRowPreviewData(
            events = listOf(
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 1"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 2"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 3"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 4"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 5"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 6"
                ),
                Event(
                    duration = EventDuration.AllDay,
                    start = now,
                    end = now,
                    title = "Event 7"
                ),
            ),
            arrangement = Arrangement.Center
        ),
    )
}