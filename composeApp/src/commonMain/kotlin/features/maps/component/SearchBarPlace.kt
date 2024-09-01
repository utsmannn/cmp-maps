package features.maps.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import isKeyboardOpen
import kotlincomposemultiplatform1.composeapp.generated.resources.Res
import kotlincomposemultiplatform1.composeapp.generated.resources.ic_arrow_back
import kotlincomposemultiplatform1.composeapp.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchBarPlace(
    value: String,
    onEditValue: (String) -> Unit,
    onDoneEdit: () -> Unit = {},
    isShowSearch: Boolean = false,
    isPlaceNotEmpty: Boolean = false,
    photoUrl: String? = null,
    onBackButtonClick: () -> Unit = {},
    onIconProfileClick: () -> Unit = {},
    content: @Composable ColumnScope.(SoftwareKeyboardController?) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val modifierColumn by remember(isShowSearch) {
        derivedStateOf {
            if (isShowSearch) {
                Modifier.fillMaxSize()
            } else {
                Modifier.wrapContentSize()
            }
        }
    }

    val backgroundColorColumn by remember(isShowSearch) {
        derivedStateOf {
            if (isShowSearch) {
                Color.White
            } else {
                Color.Transparent
            }
        }
    }

    val isKeyboardOpen by isKeyboardOpen()

    Column(
        modifier = modifierColumn
            .background(
                color = backgroundColorColumn
            )
            .padding(
                horizontal = 12.dp
            )
            .padding(
                paddingValues = PaddingValues(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
            )
    ) {

        // search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isKeyboardOpen || isPlaceNotEmpty) {
                IconButton(
                    onClick = {
                        when {
                            isKeyboardOpen -> {
                                keyboardController?.hide()
                            }
                            isPlaceNotEmpty -> {
                                onBackButtonClick.invoke()
                            }
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_back),
                        contentDescription = null
                    )
                }

                Spacer(Modifier.width(12.dp))
            }

            BasicTextField(
                value = value,
                onValueChange = onEditValue,
                decorationBox = { decoration ->
                    if (value.isEmpty()) {
                        Text(
                            text = "Search",
                            color = Color.Black.copy(
                                alpha = 0.4f
                            )
                        )
                    }

                    decoration.invoke()
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    onDoneEdit.invoke()
                }),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )

            if (photoUrl == null) {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                // display image profile
                val painter = rememberImagePainter(url = photoUrl)
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable {
                           onIconProfileClick.invoke()
                        }
                )
            }
        }

        if (isShowSearch) {
            // content
            val scrollVertical = rememberScrollState()

            LaunchedEffect(scrollVertical.isScrollInProgress) {
                if (scrollVertical.isScrollInProgress) {
                    keyboardController?.hide()
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(scrollVertical)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            keyboardController?.hide()
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                content.invoke(this, keyboardController)
            }
        }
    }
}