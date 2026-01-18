package com.wasterec.app.feature.importimage.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Called when the user taps outside the dialog or presses the back button
            onDismiss()
        },
        title = {
            Text(text = "Konfirmasi")
        },
        text = {
            Text(text = "Apakah anda yakin ingin menghapus gambar ini ?")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text("Ya")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
