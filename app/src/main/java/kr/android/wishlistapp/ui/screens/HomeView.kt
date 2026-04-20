package kr.android.wishlistapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.android.wishlistapp.R
import kr.android.wishlistapp.navigation.Screen
import kr.android.wishlistapp.WishViewModel
import kr.android.wishlistapp.data.Wish

@Composable
fun HomeView(
    viewModel: WishViewModel,
    navController: NavController
){

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            ) { data ->

                Snackbar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = colorResource(id = R.color.dark_pink)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    containerColor = colorResource(id = R.color.pink),
                    contentColor = Color.Black,

                    // UNDO action button
                    action = {
                        data.visuals.actionLabel?.let { label ->
                            TextButton(
                                onClick = { data.performAction() }
                            ) {
                                Text(
                                    text = label,
                                    color = colorResource(id = R.color.dark_pink)
                                )
                            }
                        }

                    }
                ) {
                    Text(
                        text = data.visuals.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        topBar = {
            AppBarView(title = "Wishlist")
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(24.dp)
                    .size(64.dp),
                shape = CircleShape,
                contentColor = Color.Black,
                containerColor = colorResource(id = R.color.pink),
                onClick = {
                    navController.navigate(route = Screen.AddScreen.route + "/0L")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PostAdd,
                    contentDescription = "add",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) {

        val wishList = viewModel.getAllWishes.collectAsState(initial = listOf())    //initially empty list

        val scope = rememberCoroutineScope()

        if (wishList.value.isEmpty()) {
            // Empty State UI
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.empty_wishlist),
                        contentDescription = "Empty wishlist",
                        modifier = Modifier.size(300.dp)
                    )

                    Text(
                        text = "No wishes yet",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Tap + to make one!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                items(
                    wishList.value,
                    key = { it.id }
                ) { wish ->
                    val recentlyDeletedWish by viewModel.recentlyDeletedWish.collectAsState()

                    // Check if THIS specific wish is currently in the "Undo" phase
                    val isBeingDeleted = recentlyDeletedWish?.id == wish.id

                    // Only show the item if it's NOT currently being held for deletion
                    if (!isBeingDeleted) {
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.markWishForDeletion(wish)
                                    true // Allow the swipe to complete
                                } else false
                            }
                        )

                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 12.dp)
                                .padding(horizontal = 8.dp)
                        ) {
                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = true, //snap false swipe
                                enableDismissFromEndToStart = true,
                                backgroundContent = {

                                    val direction = dismissState.dismissDirection
                                    val bgColor =
                                        if (direction == SwipeToDismissBoxValue.StartToEnd)
                                            Color.LightGray.copy(0.5f)
                                        else Color(0xFFFF5050)

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(bgColor),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteSweep,
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier
                                                .padding(end = 16.dp)
                                                .size(35.dp)
                                        )
                                    }
                                }
                            ) {
                                WishItem(
                                    wish = wish,
                                    onClick = {
                                        navController.navigate(
                                            route = Screen.AddScreen.route + "/${wish.id}"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        val recentlyDeletedWish by viewModel.recentlyDeletedWish.collectAsState()

        LaunchedEffect(recentlyDeletedWish) {
            recentlyDeletedWish?.let {
                // We launch the snackbar
                val snackBarJob = scope.launch {
                    val result = snackBarHostState.showSnackbar(
                        message = "Wish deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short // Start with Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.restoreDeletedWish()
                    }
                }

                // CUSTOM TIMER: Wait 2 seconds, then confirm delete if not undone
                delay(2000L)

                // If the wish is STILL marked for deletion after 2 seconds
                if (viewModel.recentlyDeletedWish.value != null) {
                    snackBarJob.cancel() // Hide snackbar
                    viewModel.confirmDelete() // Delete from DB
                }
            }
        }
    }

}

@Composable
fun WishItem(
    wish : Wish,
    onClick : () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                onClick()
            },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.light_pink),
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = wish.title,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = wish.description,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}