package com.wasterec.app.feature.login.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wasterec.app.R
import com.wasterec.app.ui.color.ColorAsset

@Composable
fun CustomTextInputField(
    modifier: Modifier = Modifier,
    text : String,
    onChange : (value : String)->Unit,
    leadingResId : Int? = null,
    trailingResId : Int? = null,
    leadingColor : Color = TextFieldDefaults.colors().focusedTrailingIconColor,
    trailingColor : Color = TextFieldDefaults.colors().focusedTrailingIconColor,
    onLeadingClick : (()->Unit)? = null,
    onTrailingClick : (()->Unit)? = null,
    backgroundColor : Color = TextFieldDefaults.colors().unfocusedContainerColor,
    visualTransformation : VisualTransformation = VisualTransformation.None,
    maxLines : Int = Int.MAX_VALUE,
    placeholder : (@Composable () -> Unit)? = null,
    ){

    @Composable
    fun getIconFromResId(resId : Int?, modifier: Modifier = Modifier, colorFilter : ColorFilter? = null ) : @Composable (() -> Unit)? {
        resId?.let{
            return {
                Image(
                    painter = painterResource(it),
                    "",
                    modifier = modifier.background(color = Color.Transparent),
                    colorFilter = colorFilter,
                    )
            }
        }
        return null
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = backgroundColor)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {


        TextField(
            text,
            onValueChange = {
                onChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = getIconFromResId(leadingResId,
                modifier = modifier.clickable(true){
                    onLeadingClick?.invoke()
                },
                colorFilter = ColorFilter.tint(leadingColor)
            ),
            trailingIcon = getIconFromResId(trailingResId,
                modifier = modifier.clickable(true){
                    onTrailingClick?.invoke()
                },
                colorFilter = ColorFilter.tint(trailingColor)
            ),
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = trailingColor,
                unfocusedTrailingIconColor = trailingColor,
                focusedLeadingIconColor = leadingColor,
                disabledLeadingIconColor = leadingColor,
                unfocusedLeadingIconColor = leadingColor,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            visualTransformation = visualTransformation,
            maxLines = maxLines,
            placeholder = placeholder,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrailingTextInputFieldPreview(){
    Box(modifier = Modifier.fillMaxSize()){
        CustomTextInputField(
            text = "",
            onChange = {},
            trailingResId = R.drawable.baseline_add_location_24,
            trailingColor = ColorAsset.primaryBlue,
            visualTransformation = PasswordVisualTransformation(),
        )
    }
}
