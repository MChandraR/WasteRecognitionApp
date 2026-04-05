package com.wasterec.app.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun getDateTimeFromTimestamp(timestamp: Long): LocalDateTime? {
    return Instant.ofEpochSecond(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}