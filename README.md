# RecipeExport

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

