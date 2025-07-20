# Trimica

:warning: Not Published Yet :warning:

All in one mod for your item trimming needs.

## Features
- Almost any item can be a trim material - Even modded
- Most equipment can be trimmed - Even modded
- Custom Shield Trims
- Trim Material Additions
  - Animated Trims (Animator)
  - Emissive Trims (Glow Ink Sacs)
- Per-trim-pattern item textures
  - The item texture overlay now has a custom pattern for each trim pattern rather than a single overlay for all trims.
  - This falls back to the old overlay if the trim pattern does not have a provided item texture. [See Below](#custom-patterns)

### Items
- Animator
  - Adding the "Animator" item to a trimmed item in a smithing table (without a pattern) will make the trim animated
- Rainbowifier
  - Using the "Rainbowifier" as a trim material will make the trim an animated rainbow

## Resource API
Trimica provides a resource API for modders / modpack makers to provide additional textures or modify existing ones.

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
#### Shields
To provide a custom shield trim, create a new resource pack and add the following to your pack:
```
assets/trimica/textures/trims/items/shield/<pattern_namespace>/<pattern_path>.png
```
For example:
```
assets/trimica/textures/trims/items/shield/minecraft/bolt.png
```
This texture will need to be a 64x64 texture. 

### Trim Material Addditions
Trimica comes with 2 default material additions:
- `trimica:animator` - This will make the trim animated.
- `minecraft:glow_ink_sac` - This will make the trim emissive.

You can make any item a trim material addition by adding it to the `trimica:material_additions` tag.
See [Palette Interceptor](#endpoints) for providing a custom palette for a given addition.

To give a material default additions, you can add the following to the material definition:
```
"trimica$additions": [
    <addition_1>,
    <addition_2>,
    ...
]
```
For example to make diamond animated and emissive by default you would define the material as:

```json5
// minecraft/trim_material/diamond.json
{
  "asset_name": "diamond",
  "description": {
    "color": "#6EECD2",
    "translate": "trim_material.minecraft.diamond"
  },
  "override_armor_assets": {
    "minecraft:diamond": "diamond_darker"
  },
  "trimica$additions": [
    "trimica:animator",
    "minecraft:glow_ink_sac"
  ]
}
```

## In-Code API
Trimica provides an API for modders to interact with the mod and add their own features.

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

### Endpoints:
See each of the endpoints below for more information on how to use them.
- [BaseTextureInterceptor](src/main/java/com/bawnorton/trimica/api/BaseTextureInterceptor.java)
  - Where Trimica can find the base overlay textures provided by [Custom Patterns](#custom-patterns) if they are not in
  an expected location, or you want to provide your own schema. i.e. per-item-per-pattern overlays.
- [CraftingRecipeInterceptor](src/main/java/com/bawnorton/trimica/api/CraftingRecipeInterceptor.java)
  - How Trimica determines what items can be used to trim an item or what items can be trimmed.
- [PaletteInterceptor](src/main/java/com/bawnorton/trimica/api/PaletteInterceptor.java)
  - How Trimica determines the palette for a given item. This can be used in conjuction with 
  [Custom Material Additions](#trim-material-addditions) to provide a custom palette for a given
  addition.

## Developing Trimica

Trimica uses Stonecutter for development.

See the [Stonecutter](https://stonecutter.kikugie.dev/) documentation for how to write pre-processor comments
when developing.

#### Setting Project Version
Once cloned you can set the active project version to the one you want to work on by opening the gradle tab, and running
`Tasks/stonecutter/"Set active project to <version>"` where `<version>` is the version you want to set.

#### Running
To run the mod, set the project version to the one you want to run and run the matchting generated run config.
For example for `1.21.5-fabric` you would run `Fabric Client 1.21.5`. If you attempt to run a different version,
it may fail to load the mod and crash.

#### Testing
Fabric provides client gametests as a part of their API, NeoForge does not, thus, the tests are only avaliable for Fabric.
Set your active project version to a version of Fabric and run the `Fabric Client Game Test <version>`
task. The tests are located at `src/main/java/com/bawnorton/trimica/platform/fabric/test`.

#### Building
To build the mod, run the `Tasks/project/buildAndCollect` task. This will build the mod for all versions which can 
then be found in the root `build/libs/` directory. 