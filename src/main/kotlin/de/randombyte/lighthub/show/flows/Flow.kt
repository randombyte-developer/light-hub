package de.randombyte.lighthub.show.flows

import de.randombyte.lighthub.show.ThatShow
import kotlin.time.ExperimentalTime

@ExperimentalTime
open class Flow(val show: ThatShow) : Tickable {
    override fun onTick() {}
}