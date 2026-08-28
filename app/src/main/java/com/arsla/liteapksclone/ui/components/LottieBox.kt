package com.arsla.liteapksclone.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

const val DEFAULT_LOTTIE_URL =
    "https://lottie.host/960c9da5-cadc-4a97-993e-2f4321bffb57/aowBzkxVWs.json"

@Composable
fun LottieBox(
    url: String,
    modifier: Modifier = Modifier,
    loop: Boolean = true,
    autoPlay: Boolean = true
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Url(url))
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (composition == null) {
            CircularProgressIndicator()
        } else {
            LottieAnimation(
                composition = composition,
                iterations = if (loop) LottieConstants.IterateForever else 1,
                isPlaying = autoPlay,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
