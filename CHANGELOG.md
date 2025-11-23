# 1.4.3

- Fix overlay rendering using cached UVs when atlas resizes before the next frame can be drawn (#8)

# 1.4.2

- Fix missing trim data file in neo distribution

# 1.4.1

- Fix crash on neo related to accessing unloaded tags

# 1.4.0

- Use `trimica:all_trimmables` tag to determine when an item can be trimmed in the default crafting
- Migrate shield trim rendering to api to ease compat with mods that add custom shields
- Migrate client-side api endpoints to new client api
  - Old endpoints are now deprecated but will still work until 2.0.0
- Consolidate PalleteInterceptor methods into a single method that provides the material and additions
  - Old methods are now deprecated but will still work until 2.0.0

# 1.3.2

- Whoops I broke the material addition recipe (its fixed now)

# 1.3.1

- Capture models during resolution to allow item definition derived item models to work correctly with trims
- Alphabetically sort generated trim material tags

# 1.3.0

- Update to 1.21.10
- Switch runtime atlases to be per-material instead of per-pattern to improve performance most of the time
  - Players are more likely to use the same material across multiple patterns than the same pattern across multiple materials
- Add runtime tag generation for generated trim materials for compat with mods or datapacks that wish to reference trims by id
- Drop 1.21.5/6 support

# 1.2.2

- Update configurable dep
- Include icon in neo dist

# 1.2.1

- Fix crash when loading into world
- Fix intrinsic additional materials not applying to additional trims

# 1.2.0

- Add support for additional trims on already trimmed items
  - Disabled by default but can be enabled in the config or via `/trimica toggle additionalTrims true`

# 1.1.0

- Add Configurable
- Add modularisation support to allow users to disable features they don't need

# 1.0.1

- Use new elytra trims api and fix issues with compat with elytra trims
- Use mixin config plugin for optional mixins
- Fix textures failing to load
- Fix tests

# 1.0.0

Intial Release