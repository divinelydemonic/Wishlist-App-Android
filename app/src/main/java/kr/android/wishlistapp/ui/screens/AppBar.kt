@file:OptIn(ExperimentalMaterial3Api::class)

package kr.android.wishlistapp.ui.screens

import android.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppBarView(
    title : String,
    onBackNavClicked : () -> Unit = {}
){

    val navigationIcon : (@Composable () -> Unit) = {
        if (!title.contains("WishNest")) {
            IconButton(
                onClick = { onBackNavClicked() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "back",
                    tint = Color.Black
                )
            }
        }
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                style = MaterialTheme.typography.displayLarge,
                color = colorResource(id = R.color.black),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 29.dp)
            )
        },
        windowInsets = WindowInsets(top = 30.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id = kr.android.wishlistapp.R.color.pink)
        ),
        navigationIcon = navigationIcon
    )
}