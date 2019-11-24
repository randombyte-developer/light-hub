package de.randombyte.lighthub.show

import de.randombyte.lighthub.show.events.SelectedColorSet
import tornadofx.FX

object ColorSetSelector {
    var selectedColorSetId = "set-1"
        set(value) {
            field = value

            FX.eventbus.fire(SelectedColorSet(value))
        }
}