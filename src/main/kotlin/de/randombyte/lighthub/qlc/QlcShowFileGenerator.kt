package de.randombyte.lighthub.qlc

import de.randombyte.lighthub.dmx.Device
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.xml
import java.nio.file.Files
import java.nio.file.Path

object QlcShowFileGenerator {
    fun generate(path: Path, deviceTypes: List<Device.Type>) {
        Files.createDirectories(path.parent)
        val writer = path.toFile().bufferedWriter()

        writer.write("""
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE Workspace>

        """.trimIndent())

        xml("Workspace") {
            xmlns = "http://www.qlcplus.org/Workspace"
            "Creator" {
                "Name" { -"Q Light Controller Plus" }
                "Version" { -"4.12.1" }
                "Author" { -"LightHub" }
            }
            "Engine" {
                "InputOutputMap" {
                    "Universe"("Name" to "Universe 1", "ID" to 0) {
                        "Input"("Plugin" to "OSC", "Line" to 0)
                        "Output"("Plugin" to "DMX USB", "Line" to 0)
                    }
                }

                var currentId = 0
                deviceTypes.forEach { device ->
                    val config = device.config.get()
                    val meta = config.meta
                    config.addresses.forEach { address ->
                        "Fixture" {
                            "Manufacturer" { -meta.manufacturer }
                            "Model" { -meta.model }
                            "Name" { -meta.name }
                            "Mode" { -meta.mode }
                            "Universe" { -"0" }
                            "ID" { -currentId.toString() }
                            "Address" { -address.toString() }
                            "Channels" { -device.channels.toString() }
                        }
                        currentId++
                    }
                }
            }

            "VirtualConsole" {
                "Frame" {
                    "Appearance" {
                        "FrameStyle" { -"None" }
                        "ForegroundColor" { -"Default" }
                        "BackgroundColor" { -"Default" }
                        "BackgroundImage" { -"None" }
                        "Font" { -"Default" }
                    }
                }
                "Properties" {
                    "Size"("Width" to 1920, "Height" to 1080)
                    "GrandMaster"("ChannelMode" to "Intensity", "ValueMode" to "Reduce", "SliderMode" to "Normal")
                }
            }
        }.writeTo(writer, PrintOptions(singleLineTextElements = true))

        writer.close()
    }
}