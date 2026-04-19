package com.wasterec.app.feature.main.components

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Rect

val SmoothBottomBarShape = GenericShape { size, _ ->
    val fabRadius = 100f       // Radius untuk lubang FAB
    val cornerRadius = 40f    // Kehalusan sudut di bibir cekungan
    val center = size.width / 2

    moveTo(0f, 0f)

    // 1. Garis lurus sampai sebelum cekungan
    lineTo(center - fabRadius - cornerRadius, 0f)

    // 2. Sudut melengkung pertama (Masuk ke cekungan)
    quadraticBezierTo(
        x1 = center - fabRadius, y1 = 0f,
        x2 = center - fabRadius, y2 = cornerRadius
    )

    // 3. Lengkungan utama cekungan (Tempat FAB berada)
    arcTo(
        rect = Rect(
            left = center - fabRadius,
            top = -fabRadius + cornerRadius,
            right = center + fabRadius,
            bottom = fabRadius + cornerRadius
        ),
        startAngleDegrees = 180f,
        sweepAngleDegrees = -180f,
        forceMoveTo = false
    )

    // 4. Sudut melengkung kedua (Keluar dari cekungan)
    quadraticBezierTo(
        x1 = center + fabRadius, y1 = 0f,
        x2 = center + fabRadius + cornerRadius, y2 = 0f
    )

    // 5. Sisa garis ke kanan dan menutup shape
    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

val WideBottomBarShape = GenericShape { size, _ ->
    val horizontalRadius = 120f   // Diperlebar ke samping (sebelumnya 110f)
    val verticalDepth = 100f      // Kedalaman tetap (atur sesuai selera)
    val cornerRadius = 50f       // Transisi sudut diperhalus
    val center = size.width / 2

    moveTo(0f, 0f)

    // 1. Garis lurus sampai sebelum lengkungan
    lineTo(center - horizontalRadius - cornerRadius, 0f)

    // 2. Sudut masuk (Bezier)
    quadraticBezierTo(
        x1 = center - horizontalRadius, y1 = 0f,
        x2 = center - horizontalRadius, y2 = cornerRadius
    )

    // 3. Cekungan utama (Oval Rect agar bisa lebar ke samping tapi kedalaman tetap)
    arcTo(
        rect = Rect(
            left = center - horizontalRadius,
            top = -verticalDepth + cornerRadius,
            right = center + horizontalRadius,
            bottom = verticalDepth + cornerRadius
        ),
        startAngleDegrees = 180f,
        sweepAngleDegrees = -180f,
        forceMoveTo = false
    )

    // 4. Sudut keluar (Bezier)
    quadraticBezierTo(
        x1 = center + horizontalRadius, y1 = 0f,
        x2 = center + horizontalRadius + cornerRadius, y2 = 0f
    )

    // 5. Tutup shape
    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}

val BottomBarShape = GenericShape { size, _ ->
    val radius = 120f // Sesuaikan lebar cekungan
    val center = size.width / 2

    moveTo(0f, 0f)
    lineTo(center - radius, 0f)
    // Membuat lengkungan cekungan
    arcTo(
        rect = Rect(center - radius, -radius, center + radius, radius),
        startAngleDegrees = 180f,
        sweepAngleDegrees = -180f,
        forceMoveTo = false
    )
    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
}