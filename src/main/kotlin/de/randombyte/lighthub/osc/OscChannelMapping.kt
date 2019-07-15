package de.randombyte.lighthub.osc

/**
* The Int is the relative device dmx channel.
* For a simple RGB light it would be 0->red, 1->green, 2->blue.
* This could be simply done by the index of a list, but it might lead to bugs
* because the index is not easily readable.
*/
class OscChannelMapping(vararg val channels: Pair<Int, OscChannel>)