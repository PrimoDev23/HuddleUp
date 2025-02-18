package dev.primodev.huddleup.feature.eventcreation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.ExperimentalAnimatableApi
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.primodev.huddleup.domain.entity.event.EventDuration
import dev.primodev.huddleup.theme.HuddleUpTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimatableApi::class)
@Composable
fun <T> SliderButton(
    items: List<T>,
    selectedItem: T,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    border: BorderStroke? = null,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    item: @Composable BoxScope.(T) -> Unit,
) {
    Surface(
        modifier = modifier.requiredHeight(48.dp),
        shape = shape,
        color = backgroundColor,
        border = border
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val itemWidth = constraints.maxWidth.toFloat() / items.size
            val animation = remember {
                val initialValue = items.indexOf(selectedItem) * itemWidth

                Animatable(
                    initialValue = initialValue.roundToInt(),
                    typeConverter = Int.VectorConverter
                )
            }

            LaunchedEffect(selectedItem) {
                val target = items.indexOf(selectedItem) * itemWidth

                animation.animateTo(
                    targetValue = target.roundToInt()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(1f / items.size)
                    .fillMaxHeight()
                    .offset {
                        IntOffset(
                            x = animation.value,
                            y = 0
                        )
                    }
                    .background(
                        color = indicatorColor,
                        shape = shape
                    )
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                items.forEach { entry ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        item(entry)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SliderButtonPreview() {
    HuddleUpTheme {
        SliderButton(
            modifier = Modifier.fillMaxWidth(),
            items = EventDuration.entries,
            selectedItem = EventDuration.AllDay
        ) { entry ->
            Box(
                modifier = Modifier.matchParentSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = entry.toString())
            }
        }
    }
}