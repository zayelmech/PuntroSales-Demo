package com.imecatro.products.ui.list.views

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerListItem(
    modifier: Modifier
) {
    val transition = rememberInfiniteTransition(label = "product-shimmer")
    val progress = transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 500)),
        label = "product-shimmer-progress"
    )
    ElevatedCard(
        modifier = Modifier
            .padding(2.dp, 2.dp)
            .fillMaxWidth(0.9f),
        elevation = CardDefaults.cardElevation(0.5.dp)
    ) {
        Row(modifier = modifier) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp)
                    .shimmerEffect(progress)
            )
            //Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .shimmerEffect(progress)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .shimmerEffect(progress)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .shimmerEffect(progress)
                )
            }
        }
    }
}

private fun Modifier.shimmerEffect(progress: State<Float>): Modifier =
    this then ShimmerElement(progress)

private data class ShimmerElement(val progress: State<Float>) : ModifierNodeElement<ShimmerNode>() {
    override fun create() = ShimmerNode(progress)

    override fun update(node: ShimmerNode) {
        node.progress = progress
    }
}

private val shimmerColors = listOf(Color(0xFFB8B5B5), Color(0xFF8F8B8B), Color(0xFFB8B5B5))

private class ShimmerNode(var progress: State<Float>) : Modifier.Node(), DrawModifierNode {
    private var brushSize = Size.Unspecified
    private var brush: Brush? = null

    override fun ContentDrawScope.draw() {
        if (size.width > 0f && size.height > 0f) {
            if (brushSize != size) {
                brushSize = size
                brush = Brush.linearGradient(shimmerColors, Offset.Zero, Offset(size.width, size.height))
            }
            // Only drawing observes the animation. Keep the shader and translate it each frame.
            val offsetX = progress.value * size.width
            val bounds = size
            withTransform({ translate(left = offsetX) }) {
                drawRect(brush = requireNotNull(brush), topLeft = Offset(-offsetX, 0f), size = bounds)
            }
        }
        drawContent()
    }
}

