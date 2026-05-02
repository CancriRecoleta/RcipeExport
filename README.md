# RecipeExport
[![](https://badges.moddingx.org/curseforge/downloads/1531142)](https://www.curseforge.com/minecraft/mc-mods/recipeexport)
[![](https://badges.moddingx.org/modrinth/downloads/recipeexport)](https://modrinth.com/mod/recipeexport)
[![](https://img.shields.io/github/downloads/CancriRecoleta/RcipeExport/total?style=flat&logo=github&label=Github%20Downloads&args=14)](https://github.com/CancriRecoleta/RcipeExport/releases)

[![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/recipeexport)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/recipeexport)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/CancriRecoleta/RcipeExport/releases)

`RecipeExport` is a multi-loader Minecraft mod for exporting recipe data to JSON files.

## Features

- Exports recipes for a specified mod with the `/dumprecipe <modid>` command.
- Groups exported recipes by category.
- Writes a summary file and separate files for each recipe category.
- Keeps a compatibility export file at the old path.

## Supported recipe categories

- `crafting_shaped`
- `crafting_shapeless`
- `smelting`
- `blasting`
- `smoking`
- `campfire_cooking`
- `smithing`
- `stonecutting`

## Usage

Run the command in a world with operator permission:

```text
/dumprecipe <modid>
```

Exported files are written to:

- `export/<modid>/recipes.json`
- `export/<modid>/<category>.json`
- `export/dump_recipes_<modid>.json`

