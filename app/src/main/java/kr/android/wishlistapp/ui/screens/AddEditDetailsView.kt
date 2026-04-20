@file:OptIn(ExperimentalMaterial3Api::class)

package kr.android.wishlistapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kr.android.wishlistapp.R
import kr.android.wishlistapp.WishViewModel
import kr.android.wishlistapp.data.Wish

@Composable
fun AddEditDetailsView(
    id : Long,
    viewModel: WishViewModel,
    navController: NavController
){

    val snackMessage = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (id != 0L){
        val wish = viewModel.getWishById(id).collectAsState(
            initial = Wish(
                id = 0L,
                title = "",
                description = ""
            )
        )
        viewModel.wishTitleState = wish.value.title
        viewModel.wishDescriptionState = wish.value.description
    } else {
        viewModel.wishTitleState = ""
        viewModel.wishDescriptionState = ""
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp) // moves snackbar upward
            ) { data ->

                Snackbar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = colorResource(id = R.color.dark_pink)
                        ),
                    shape = RoundedCornerShape(16.dp), // rounded corners
                    containerColor = colorResource(id = R.color.pink),
                    contentColor = Color.Black
                ) {

                    Text(
                        text = data.visuals.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        topBar = {
            AppBarView(
                title =
                    if (id != 0L) stringResource(id = R.string.update_wish)
                    else stringResource(id = R.string.add_wish),
                onBackNavClicked = { navController.navigateUp() }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(Modifier.height(20.dp))

            WishTextField(
                label = "Title",
                value = viewModel.wishTitleState,
                onValueChange = { viewModel.onWishTitleChanged(it) }
            )

            Spacer(Modifier.height(16.dp))

            WishTextField(
                label = "Description",
                value = viewModel.wishDescriptionState,
                onValueChange = { viewModel.onWishDescriptionChanged(it) }
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {

                    if(viewModel.wishTitleState.isNotEmpty() && viewModel.wishDescriptionState.isNotEmpty()){
                        if (id != 0L){
                            //update wish
                            viewModel.updateWish(
                                Wish(
                                    id = id,
                                    title = viewModel.wishTitleState.trim(),
                                    description = viewModel.wishDescriptionState.trim()
                                )
                            )
                            snackMessage.value = "The wish has been updated!"
                        } else {
                            //add wish
                            viewModel.addWish(
                                Wish(
                                    title = viewModel.wishTitleState.trim(),
                                    description = viewModel.wishDescriptionState.trim()
                                )
                            )
                            snackMessage.value = "A new wish has been created!"
                        }
                    } else {
                        snackMessage.value = "Please fill the fields to create a wish!"
                    }
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = snackMessage.value,
                            duration = SnackbarDuration.Short
                        )
                        navController.navigateUp()
                    }

                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 4.dp
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = colorResource(id = R.color.pink)
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.light_pink),
                    contentColor = Color.Black,
                    disabledContentColor = Color.Black.copy(alpha = 0.6f),
                    disabledContainerColor = colorResource(id = R.color.light_pink).copy(alpha = 0.8f)
                ),
                enabled = viewModel.wishTitleState.isNotBlank() && viewModel.wishDescriptionState.isNotBlank()
            ) {
                Text(
                    text =
                        if (id != 0L) stringResource(id = R.string.update_wish)
                        else stringResource(id = R.string.add_wish)
                )
            }
        }
    }
}

@Composable
fun WishTextField(
    label : String,
    value : String,
    onValueChange : (String) -> Unit
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange ,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
                },
        textStyle = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.inverseSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = colorResource(id = R.color.dark_pink),
            focusedLabelColor = colorResource(id = R.color.dark_pink),
            unfocusedLabelColor = colorResource(id = R.color.pink),
            unfocusedBorderColor = colorResource(id = R.color.pink),
            focusedBorderColor = colorResource(id = R.color.dark_pink)
        )
    )
}
