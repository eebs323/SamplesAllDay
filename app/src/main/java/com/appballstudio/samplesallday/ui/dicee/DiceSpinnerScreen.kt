package com.appballstudio.samplesallday.ui.dicee

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DiceRoller() {
    var dieValue by remember { mutableStateOf(1) }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Die(value = dieValue)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { dieValue = (1..6).random() }) {
            Text("Roll")
        }
    }
}

@Composable
fun Die(value: Int) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(Color.White, shape = RoundedCornerShape(4.dp)).padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        when (value) {
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