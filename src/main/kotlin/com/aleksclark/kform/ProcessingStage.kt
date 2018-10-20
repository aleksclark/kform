package com.aleksclark.kform

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.*


interface ProcessingStage: ReportableStage, CoroutineScope {
    val srcChan: Channel<RowData>
    val destChan: Channel<RowData>


    suspend fun start(): Unit {
        while (!srcChan.isClosedForReceive) {
            val row = srcChan.poll()
            if (row == null) {
                delay(100)
            } else {

                launch {
                    destChan.send(process(row))
                    reportProgress()
                }
            }
        }
        reportDone()
    }

    suspend fun process(data: RowData): RowData

}