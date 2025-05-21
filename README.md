# Trimica

:warning: Not Published Yet :warning:

All in one mod for your item trimming needs.

## Features
- Almost any item can be a trim material - Even modded
- Most equipment can be trimmed - Even modded
- Custom Shield Trims
- Animated Trims
- Per-trim-pattern item textures
  - The item texture overlay now has a custom pattern for each trim pattern rather than a single overlay for all trims.
  - This falls back to the old overlay if the trim pattern does not have a provided item texture. [See Below](#custom-patterns)

### Items
- Animator
  - Adding the "Animator" item to a trimmed item in a smithing table (without a pattern) will make the trim animated
- Rainbowifier
  - Using the "Rainbowifier" as a trim material will make the trim an animated rainbow

## API
Trimica provides a resource API and an in-code API for modders to provide additional functionality or 
override the default behaviour of the mod.

### Providing Overlay Texutres
For overlay textures the lightness of a pixel will be used to determine the colour index on the palette. 
For example if there are 8 distinct colours in the overlay texture, the lightest pixel will be the
first colour in the palette, the second lightest will be the second colour in the palette, and so on.
If there are less than 8 colours in the texture, then only the first N colours will be used.
If there are more than 8 colours in the texture, then only the darkest 8 colours will be used, the rest will be
made transparent.

#### Custom Patterns
To provide a custom overlay texture for a trim pattern, create a new resource pack and add the following to your pack:
```
assets/trimica/textures/trims/items/<slot_name>/<pattern_namespace>/<pattern_path>.png
```
For example:
```
assets/trimica/textures/trims/items/boots/minecraft/bolt.png
```
This texture will need to be a 16x16 texture. 
### Shields
To provide a custom shield trim, create a new resource pack and add the following to your pack:
```
assets/trimica/textures/trims/items/shield/<pattern_namespace>/<pattern_path>.png
```
For example:
```
assets/trimica/textures/trims/items/shield/minecraft/bolt.png
```
This texture will need to be a 64x64 texture. 

### Making A Material Animated By Default
To make a material animated by default add the entry:
```
"trimica$animated": true
```
to the material definition.
For example to make diamond animated by default you would define the material as:

```json5
// assets/minecraft/trim_material.diamond.json
{
  "asset_name": "diamond",
  "description": {
    "color": "#6EECD2",
    "translate": "trim_material.minecraft.diamond"
  },
  "override_armor_assets": {
    "minecraft:diamond": "diamond_darker"
  },
  "trimica$animated": true
}
```

## Development

### Depending on Trimica
View the latest version of Trimica [here](https://maven.bawnorton.com/#/releases/com/bawnorton/trimica)
```kotlin
repositories {
    maven {
        url = "https://maven.bawnorton.com"
    }
}

dependencies {
    // Loom
    modImplementation("com.bawnorton.trimica:trimica-<loader>:<version>")
    // MDG
    implementation("com.bawnorton.trimica:trimica-<loader>:<version>")
}
```

### Developing Trimica

Trimica uses Modstitch and Stonecutter for development.

See the [Stonecutter](https://stonecutter.kikugie.dev/) documentation for how to write pre-processor comments
when developing.

#### Setting Project Version
Once cloned you can set the active project version to the one you want to work on by opening the gradle tab, and running
`Tasks/stonecutter/"Set active project to <version>"` where `<version>` is the version you want to set.

#### Running
To run the mod, set the project version to the one you want to run and run the matchting generated run config.
For example for `1.21.5-fabric` you would run `Fabric Client (:1.21.5-fabric)`. If you attempt to run a different version,
it will fail to load the mod and crash.

#### Testing
Fabric provides client gametests as a part of their API, NeoForge does not, thus, the tests are only avaliable for Fabric.
Set your active project version to a version of Fabric and run the `Gametest Minecraft Client Game Test (:<version>-fabric)`
task. The tests are located at `versions/<version>/src/gametest/`.

#### Building
To build the mod, run the `Tasks/project/chiseledBuildAndCollect` task. This will build the mod for all versions
and move the built jars to the root `build/libs` directory. 