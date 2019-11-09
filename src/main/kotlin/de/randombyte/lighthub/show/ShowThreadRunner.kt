package de.randombyte.lighthub.show

import java.util.concurrent.ConcurrentLinkedQueue

object ShowThreadRunner {
    val runnables = ConcurrentLinkedQueue<Runnable>()

    fun runLater(action: () -> Unit) {
        runnables += Runnable { action() }
    }
}