package com.wasterec.app.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

fun taskRepeater( interval : Int = 1, action:()->Unit) = runBlocking {
    val job = launch {
        while (isActive) {
            // Place your repeating task code here
            println("Task repeated at ${System.currentTimeMillis()}")
            action()
            // Wait for the specified interval
            delay(interval.seconds)
        }
    }

    // The rest of your main function can run concurrently.
    // Use delay() or other mechanisms to run the program long enough
    // to see the repeating task in action, or to cancel it.

//    // Example: let the task run for 5 seconds
//    delay(5.seconds)
//    job.cancel() // Stop the repeating task
//    println("Task cancelled")
}