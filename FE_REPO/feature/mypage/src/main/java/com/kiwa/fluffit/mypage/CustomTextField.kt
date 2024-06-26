package com.kiwa.fluffit.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiwa.fluffit.designsystem.theme.fluffitTypography

@Composable
internal fun CustomTextField(
    textState: MutableState<String>,
    modifier: Modifier = Modifier,
    isSingleLine: Boolean,
    maxLength: Int
) {
    val focusManager: FocusManager = LocalFocusManager.current
    BasicTextField(
        modifier = modifier.then(
            Modifier
                .background(Color.Transparent)
        ),
        value = textState.value,
        onValueChange = { newText ->
            if (newText.length <= maxLength) {
                textState.value = newText
            }
        },
        textStyle = fluffitTypography.bodyLarge.merge(
            TextStyle(
                fontSize = 24.sp,
                color = Color.Black
            )
        ),
        singleLine = isSingleLine,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                innerTextField()
            }
        }
    )
}
