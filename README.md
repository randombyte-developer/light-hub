# light-hub

# Build requirements
```
sudo apt install openjdk-11-jdk openjfx
```

# Runtime requirements
```
sudo apt install openjdk-11-jre openjfx
```

# Structure

Every `Device` is a `Receiver`. A `Receiver` receives OSC messages from LightHub.
A `MasterFlow` controls other `Flow`s. Only one `MasterFlow` should be active at a time. Multiple `Flow`s can be active at the same time.

# Time / Beats

There's a constant 120 BPM going, unrelated to any music. The time measurement in the configs
is done in half notes, so basically a 240 BPM beat.

# Pads

|                   |           |               |               |
|---                |---        |---            |---            |
|warm               |cold       |red            |green          |
|colorful-1         |colorful-2 |uv             |blue           |
|colorful-black     |quads      |scanners       |X              |
|blackout           |X          |slow-strobe    |fast-strobe    |