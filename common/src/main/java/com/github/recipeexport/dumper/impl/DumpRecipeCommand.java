package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.Constants;
import com.github.recipeexport.dumper.RecipeDumpers;
import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.RecipeDumpException;
import com.github.recipeexport.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

/**
 * /dumprecipe &lt;mod&gt; 命令的实现。
 */
public final class
DumpRecipeCommand {

    private static final Set<ResourceLocation> ERROR_RECIPES = new HashSet<>();

    private static final SuggestionProvider<CommandSourceStack> MOD_SUGGESTIONS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(Services.PLATFORM.getLoadedModIds(), builder);

    private DumpRecipeCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dumprecipe")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("mod", StringArgumentType.word())
                        .suggests(MOD_SUGGESTIONS)
                        .executes(DumpRecipeCommand::executeCommand)
                )
        );
    }

    public static int executeCommand(CommandContext<CommandSourceStack> context) {
        String modId = StringArgumentType.getString(context, "mod");
        CommandSourceStack source = context.getSource();
        if (!Services.PLATFORM.isModLoaded(modId)) {
            source.sendFailure(Component.literal("No such a mod: " + modId));
            return 0;
        }
        RecipeManager recipeManager = source.getServer().getRecipeManager();
        HolderLookup.Provider registries = source.registryAccess();

        JsonArray recipesArray = dumpAllRecipes(recipeManager, modId, registries);
        JsonObject result = new JsonObject();
        result.add("recipes", recipesArray);
        JsonArray errorArray = new JsonArray();
        ERROR_RECIPES.forEach(id -> errorArray.add(id.toString()));
        result.add("error", errorArray);

        File file = new File(String.format("export/dump_recipes_%s.json", modId));
        outputJson(file, result);

        int recipesCount = recipesArray.size();
        int skipped = ERROR_RECIPES.size();
        source.sendSuccess(() -> Component.literal("Dump recipes successfully! See export directory."), false);
        source.sendSuccess(() -> Component.literal(String.format("%s recipes dumped, %s recipes skipped", recipesCount, skipped)), false);
        ERROR_RECIPES.clear();
        return recipesCount;
    }

    public static JsonArray dumpAllRecipes(RecipeManager recipeManager, String modFilter, HolderLookup.Provider registries) {
        JsonArray array = new JsonArray();
        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            ResourceLocation id = holder.id();
            Recipe<?> recipe = holder.value();
            if (!id.getNamespace().equals(modFilter)) {
                continue;
            }
            if (!RecipeDumpers.has(recipe.getClass())) {
                continue;
            }
            try {
                array.add(dumpRecipe(id, recipe, registries));
            } catch (RecipeDumpException e) {
                Constants.LOG.warn("Failed to dump recipe {}: {}", id, e.getMessage());
                ERROR_RECIPES.add(id);
            } catch (Throwable t) {
                Constants.LOG.warn("Unexpected error while dumping recipe {}", id, t);
                ERROR_RECIPES.add(id);
            }
        }
        return array;
    }

    private static JsonObject dumpRecipe(ResourceLocation id, Recipe<?> recipe, HolderLookup.Provider registries) throws RecipeDumpException {
        JsonObject jsonObject = new JsonObject();
        IRecipeDumper<Recipe<?>> dumper = RecipeDumpers.get(recipe.getClass());
        jsonObject.addProperty("type", dumper.getRecipeTypeName(recipe));
        jsonObject.addProperty("name", id.toString());
        RecipeInputs inputs = new RecipeInputs();
        RecipeOutputs outputs = new RecipeOutputs();
        dumper.setInputs(recipe, inputs);
        dumper.setOutputs(recipe, outputs, registries);
        jsonObject.add("input", inputs.serialize());
        jsonObject.add("output", outputs.serialize(registries));
        dumper.writeExtraInformation(recipe, jsonObject);
        return jsonObject;
    }

    private static void outputJson(File file, JsonElement element) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                Constants.LOG.warn("Failed to create directory {}", parent);
            }
            Files.write(file.toPath(), gson.toJson(element).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Constants.LOG.error("Failed to write recipe dump file", e);
        }
    }
}

