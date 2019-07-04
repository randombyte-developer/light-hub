package de.randombyte.lighthub.midi.akai

sealed class Control(val type: Int, val number: Int) {

    var value: Int = 0
    var lastValue: Int = 0

    protected abstract fun onUpdate()

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

        abstract fun onUp()

        abstract fun onDown()

        abstract class SimpleButton(number: Int) : Button(0x40, number)
    }

    /*class TouchButton(number: Int, listener: (Change) -> Unit) : Control<TouchButton.Change>(0x43, number, listener) {
        sealed class Change {
            object Up
            object Down
        }
    }
    class Fader(number: Int, listener: (Change) -> Unit) : Control<Fader.Change>(0x41, number, listener) {
        sealed class Change {
        }
    }
    class Knob(number: Int, listener: (Change) -> Unit) : Control<Knob.Change>(0x41, number, listener) {
        sealed class Change {
        }
    }*/
}