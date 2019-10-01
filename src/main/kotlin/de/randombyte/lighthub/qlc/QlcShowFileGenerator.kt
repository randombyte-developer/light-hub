package de.randombyte.lighthub.qlc

import de.randombyte.lighthub.osc.Device
import de.randombyte.lighthub.osc.devices.QlcPlus
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.xml
import java.nio.file.Files
import java.nio.file.Path

object QlcShowFileGenerator {

    const val VC_WIDTH = 1920
    const val VC_HEIGHT = 2080

    fun generate(path: Path, devices: List<Device>) {
        Files.createDirectories(path.parent)
        val writer = path.toFile().bufferedWriter()

        val indexedDevices = devices.mapIndexed { index, device -> index to device }.toMap()

        writer.write(
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE Workspace>

        """.trimIndent()
        )

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

                indexedDevices.forEach { (index, device) ->
                    val qlcMeta = device.type.metaConfig.config.qlcMeta
                    "Fixture" {
                        "Manufacturer" { -qlcMeta.manufacturer }
                        "Model" { -qlcMeta.model }
                        "Name" { -qlcMeta.name }
                        "Mode" { -qlcMeta.mode }
                        "Universe" { -"0" }
                        "ID" { -index.toString() }
                        "Address" { -(device.dmxAddress - 1).toString() }
                        "Channels" { -device.type.channelsCount.toString() }
                    }
                }
            }

            "VirtualConsole" {
                "Frame" {
                    WidgetAppearance.DEFAULT.toXml(this)

                    var currentWidgetIndex = 0

                    indexedDevices.forEach { (deviceIndex, device) ->
                        device.oscChannelList.allNestedChannels.forEach { oscChannel ->
                            "Slider"(
                                "Caption" to "Slider $currentWidgetIndex",
                                "ID" to currentWidgetIndex,
                                "WidgetStyle" to "Slider",
                                "InvertedAppearance" to false
                            ) {
                                WindowState.SLIDER.copy(
                                    x = WindowState.SLIDER_WIDTH * (currentWidgetIndex % 20),
                                    y = WindowState.SLIDER_HEIGHT * (currentWidgetIndex / 20)
                                ).toXml(this)

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
                                        -oscChannel.relativeDmxAddress.toString()
                                    }
                                }

                                "Input"("Universe" to 0, "Channel" to oscChannel.qlcChannel)
                            }

                            currentWidgetIndex++
                        }
                    }
                }
                "Properties" {
                    "Size"("Width" to VC_WIDTH, "Height" to VC_HEIGHT)
                    "GrandMaster"("ChannelMode" to "Intensity", "ValueMode" to "Reduce", "SliderMode" to "Normal") {
                        "Input"("Universe" to 0, "Channel" to QlcPlus.oscMasterDimmer.qlcChannel)
                    }
                }
            }
        }.writeTo(writer, PrintOptions(singleLineTextElements = true))

        writer.close()
    }
}