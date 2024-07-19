package com.appballstudio.foody.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun DiceRoller() {
    val die1Value = remember { mutableIntStateOf(1) }
    val die2Value = remember { mutableIntStateOf(1) }
    val isRolling = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row( // Use a Row to place dice horizontally
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Die(die1Value, isRolling)
            Spacer(modifier = Modifier.width(16.dp)) // Space between dice
            Die(die2Value, isRolling)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            isRolling.value = true
            die1Value.intValue = (1..6).random()
            die2Value.intValue = (1..6).random()
        }) {
            Text("Roll")
        }
    }
}

@Composable
fun Die(dieValue: MutableIntState, isRolling: MutableState<Boolean>) {
    val angle by animateFloatAsState(
        targetValue = if (isRolling.value) 360f * 5 else 0f, // Rotate 5 times
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = ""
    )

    DieBox(dieValue = dieValue, angle = angle)

    // Trigger side effect when angle changes
    LaunchedEffect(key1 = angle) {
        if (isRolling.value && angle < 360f * 5) { // Only when spinning
            dieValue.intValue = (1..6).random()
        }
        if (angle == 360f * 5) { // Reset isRolling when animation ends
            isRolling.value = false
        }
    }
}

@Composable
fun DieBox(dieValue: MutableIntState, angle: Float) {
    Box(
        modifier = Modifier
            .graphicsLayer { rotationZ = angle }
            .size(64.dp)
            .background(Color.Red, shape = RoundedCornerShape(4.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        when (dieValue.intValue) {
            1 -> Dot(Modifier.align(Alignment.Center))
            2 -> {
                Dot(Modifier.align(Alignment.TopStart))
                Dot(Modifier.align(Alignment.BottomEnd))
            }

            3 -> {
                Dot(Modifier.align(Alignment.TopStart))
                Dot(Modifier.align(Alignment.Center))
                Dot(Modifier.align(Alignment.BottomEnd))
            }

            4 -> {
                Dot(Modifier.align(Alignment.TopStart))
                Dot(Modifier.align(Alignment.TopEnd))
                Dot(Modifier.align(Alignment.BottomStart))
                Dot(Modifier.align(Alignment.BottomEnd))
            }

            5 -> {
                Dot(Modifier.align(Alignment.TopStart))
                Dot(Modifier.align(Alignment.TopEnd))
                Dot(Modifier.align(Alignment.Center))
                Dot(Modifier.align(Alignment.BottomStart))
                Dot(Modifier.align(Alignment.BottomEnd))
            }

            6 -> {
                Dot(Modifier.align(Alignment.TopStart))
                Dot(Modifier.align(Alignment.TopCenter))
                Dot(Modifier.align(Alignment.TopEnd))
                Dot(Modifier.align(Alignment.BottomStart))
                Dot(Modifier.align(Alignment.BottomCenter))
                Dot(Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}

@Composable
fun Dot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(Color.Black)
    )
}