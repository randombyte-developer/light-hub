package de.randombyte.lighthub.midi.akai

import kotlin.math.absoluteValue

sealed class Control(val type: Int, val number: Int) {

    var value = 0
    var lastValue = 0

    open fun onUpdate() {}

    fun update(newValue: Int) {
        lastValue = value
        value = newValue
        onUpdate()
    }

    sealed class Button(type: Int, number: Int) : Control(type, number) {

        private val Int.isPressed: Boolean get() = this > 0

        override fun onUpdate() {
            if (value.isPressed && !lastValue.isPressed) {
                onDown()
            } else if (!value.isPressed && lastValue.isPressed) {
                onUp()
            }
        }

        open fun onUp() {}

        open fun onDown() {}

        abstract class SimpleButton(number: Int) : Button(SYSEX_TYPE, number) {
            companion object {
                const val SYSEX_TYPE = 0x40
            }
        }

        abstract class TouchButton(number: Int) : Button(SYSEX_TYPE, number)
        {
            companion object {
                const val SYSEX_TYPE = 0x43
            }
        }
    }

    abstract class Potentiometer(number: Int) : Control(SYSEX_TYPE, number) {

        companion object {
            const val SYSEX_TYPE = 0x41

            private const val FULL_WAY = 127
            private const val FULL_WAY_PLUS_ONE = FULL_WAY + 1
            private const val HALF_WAY = FULL_WAY / 2
        }

        val direction: Int
            get() {
                val directDistance = (value - lastValue).absoluteValue
                return if (directDistance < HALF_WAY) {
                    // small direct distance
                    // movement directly between the brackets, no wrap-around
                    // .......[---].......
                    value - lastValue
                } else {
                    // big direct distance
                    // movement with a wrap-around
                    // --].............[--
                    val delta = value - lastValue
                    if (value > lastValue) delta - FULL_WAY_PLUS_ONE else delta + FULL_WAY_PLUS_ONE
                }
            }

    }

}