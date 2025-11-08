# 1.4.0

- Add Tool Trims

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