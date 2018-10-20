package com.aleksclark.kform

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay

class StatsReporter(
    val inChan: Channel<StatsData>
) {
    suspend fun monitor(): Unit {
        while (!inChan.isClosedForReceive) {
            val stats = inChan.poll()
            if (stats == null) {
                delay(100)
            } else {

                println(stats.msg)

            }

        }
        println("Stats channel closed, we're done here")

    }
}