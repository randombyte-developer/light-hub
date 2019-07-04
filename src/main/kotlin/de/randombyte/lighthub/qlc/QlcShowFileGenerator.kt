package de.randombyte.lighthub.qlc

import de.randombyte.lighthub.osc.dmx.Device
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.xml
import java.nio.file.Files
import java.nio.file.Path

object QlcShowFileGenerator {

    const val VC_WIDTH = 1920
    const val VC_HEIGHT = 1080

    const val SLIDER_WIDTH = 60
    const val SLIDER_HEIGHT = 200

    fun generate(path: Path, devices: List<Device>) {
        Files.createDirectories(path.parent)
        val writer = path.toFile().bufferedWriter()

        val indexedDevices = devices.mapIndexed { index, device -> index to device }.toMap()

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

                indexedDevices.forEach { index, device ->
                    val type = device.type
                    val config = type.configHolder.config
                    val meta = config.meta
                    config.addresses.forEach { address ->
                        "Fixture" {
                            "Manufacturer" { -meta.manufacturer }
                            "Model" { -meta.model }
                            "Name" { -meta.name }
                            "Mode" { -meta.mode }
                            "Universe" { -"0" }
                            "ID" { -index.toString() }
                            "Address" { -address.toString() }
                            "Channels" { -type.channels.toString() }
                        }
                    }
                }
            }

            "VirtualConsole" {
                "Frame" {
                    WidgetAppearance.DEFAULT.toXml(this)

                    var currentSliderIndex = 0

                    indexedDevices.forEach { deviceIndex, device ->
                        device.oscChannelMapping.channels.forEach { dmxDeviceChannel, oscChannel ->
                            "Slider"(
                                "Caption" to "Slider $currentSliderIndex",
                                "ID" to deviceIndex,
                                "WidgetStyle" to "Slider",
                                "InvertedAppearance" to false
                            ) {
                                "WindowState"(
                                    "Visible" to "True",
                                    "X" to SLIDER_WIDTH * currentSliderIndex,
                                    "Y" to 0,
                                    "Width" to SLIDER_WIDTH,
                                    "Height" to SLIDER_HEIGHT
                                )

                                WidgetAppearance.DEFAULT.toXml(this)

                                "SliderMode"(
                                    "ValueDisplayStyle" to "Exact",
                                    "ClickAndGoType" to "None",
                                    "Monitor" to false
                                ) {
                                    -"Level"
                                }
                                "Level"("LowLimit" to 0, "HighLimit" to 255, "Value" to 0) {
                                    "Channel"("Fixture" to deviceIndex) {
                                        -dmxDeviceChannel.toString()
                                    }
                                }

                                "Input"("Universe" to 0, "Channel" to oscChannel.qlcChannel)
                            }

                            currentSliderIndex++
                        }
                    }
                }
                "Properties" {
                    "Size"("Width" to VC_WIDTH, "Height" to VC_HEIGHT)
                    "GrandMaster"("ChannelMode" to "Intensity", "ValueMode" to "Reduce", "SliderMode" to "Normal")
                }
            }
        }.writeTo(writer, PrintOptions(singleLineTextElements = true))

        writer.close()
    }
}