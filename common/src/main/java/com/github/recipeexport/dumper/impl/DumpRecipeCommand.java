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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class DumpRecipeCommand {

    private static final SuggestionProvider<CommandSourceStack> MOD_SUGGESTIONS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(Services.PLATFORM.getLoadedModIds(), builder);

    private DumpRecipeCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dumprecipe")
                .requires(DumpRecipeCommand::canUseCommand)
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
        DumpResult dumpResult = dumpAllRecipes(recipeManager, modId, registries);

        JsonObject result = new JsonObject();
        result.addProperty("mod", modId);
        result.add("summary", dumpResult.createSummary());
        result.add("recipes", dumpResult.createCategoriesJson());
        result.add("error", dumpResult.createErrorJson());

        File outputDirectory = new File(String.format("export/%s", modId));
        outputJson(new File(outputDirectory, "recipes.json"), result);
        outputJson(new File(String.format("export/dump_recipes_%s.json", modId)), result);
        dumpResult.writeCategoryFiles(outputDirectory);

        int recipesCount = dumpResult.getDumpedCount();
        int skipped = dumpResult.getSkippedCount();
        source.sendSuccess(() -> Component.literal("Dump recipes successfully! See export/" + modId + " directory."), false);
        source.sendSuccess(() -> Component.literal(String.format("%s recipes dumped in %s categories, %s recipes skipped",
                recipesCount,
                dumpResult.categoryRecipes.size(),
                skipped)), false);
        return recipesCount;
    }

    public static DumpResult dumpAllRecipes(RecipeManager recipeManager, String modFilter, HolderLookup.Provider registries) {
        DumpResult result = new DumpResult();
        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            ResourceKey<Recipe<?>> id = holder.id();
            Recipe<?> recipe = holder.value();
            if (!getNamespace(id).equals(modFilter)) {
                continue;
            }
            IRecipeDumper<Recipe<?>> dumper = RecipeDumpers.get(recipe.getClass());
            if (dumper == null) {
                continue;
            }
            try {
                JsonObject dumpedRecipe = dumpRecipe(id, recipe, dumper, registries);
                String category = sanitizeCategory(dumper.getRecipeCategoryName(recipe));
                dumpedRecipe.addProperty("category", category);
                result.addRecipe(category, dumpedRecipe);
            } catch (RecipeDumpException e) {
                Constants.LOG.warn("Failed to dump recipe {}: {}", id, e.getMessage());
                result.errorRecipes.add(getRecipeIdString(id));
            } catch (Throwable t) {
                Constants.LOG.warn("Unexpected error while dumping recipe {}", id, t);
                result.errorRecipes.add(getRecipeIdString(id));
            }
        }
        return result;
    }

    private static JsonObject dumpRecipe(ResourceKey<Recipe<?>> id, Recipe<?> recipe, IRecipeDumper<Recipe<?>> dumper, HolderLookup.Provider registries) throws RecipeDumpException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", dumper.getRecipeTypeName(recipe));
        jsonObject.addProperty("name", getRecipeIdString(id));

        RecipeInputs inputs = new RecipeInputs();
        RecipeOutputs outputs = new RecipeOutputs();
        dumper.setInputs(recipe, inputs);
        dumper.setOutputs(recipe, outputs, registries);
        jsonObject.add("input", inputs.serialize());
        jsonObject.add("output", outputs.serialize(registries));
        dumper.writeExtraInformation(recipe, jsonObject);
        return jsonObject;
    }

    private static boolean canUseCommand(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        if (server != null && server.isSingleplayer()) {
            return true;
        }
        // 兼容不同映射/版本：优先尝试 hasPermission(int)，不存在则尝试 hasPermissionLevel(int)
        return callPermissionMethod(source, "hasPermission", 2) || callPermissionMethod(source, "hasPermissionLevel", 2);
    }

    private static boolean callPermissionMethod(CommandSourceStack source, String methodName, int level) {
        try {
            Method method = CommandSourceStack.class.getMethod(methodName, int.class);
            Object result = method.invoke(source, level);
            return result instanceof Boolean b && b;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static String getNamespace(ResourceKey<Recipe<?>> id) {
        try {
            Method method = id.getClass().getMethod("location");
            Object location = method.invoke(id);
            String namespace = invokeStringNoArg(location, "getNamespace");
            if (namespace != null && !namespace.isEmpty()) {
                return namespace;
            }
            String locationString = String.valueOf(location);
            int colon = locationString.indexOf(':');
            return colon > 0 ? locationString.substring(0, colon) : "";
        } catch (ReflectiveOperationException ignored) {
        }

        String raw = getRecipeIdString(id);
        int colon = raw.indexOf(':');
        return colon > 0 ? raw.substring(0, colon) : "";
    }

    private static String getRecipeIdString(ResourceKey<Recipe<?>> id) {
        try {
            Method method = id.getClass().getMethod("location");
            Object location = method.invoke(id);
            return String.valueOf(location);
        } catch (ReflectiveOperationException ignored) {
        }

        String raw = id.toString();
        int slash = raw.lastIndexOf('/');
        if (slash >= 0 && slash + 1 < raw.length()) {
            return raw.substring(slash + 1).replace("]", "").trim();
        }
        return raw;
    }

    private static String invokeStringNoArg(Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(methodName);
            Object result = method.invoke(target);
            return result == null ? null : result.toString();
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static String sanitizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "unknown";
        }
        return category.replace(':', '_').replaceAll("[^a-zA-Z0-9_.-]", "_");
    }

    private static void outputJson(File file, JsonElement element) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                Constants.LOG.warn("Failed to create directory {}", parent);
            }
            Files.writeString(file.toPath(), gson.toJson(element), StandardCharsets.UTF_8);
        } catch (IOException e) {
            Constants.LOG.error("Failed to write recipe dump file", e);
        }
    }

    public static final class DumpResult {
        private final Map<String, JsonArray> categoryRecipes = new TreeMap<>();
        private final Set<String> errorRecipes = new HashSet<>();

        private void addRecipe(String category, JsonObject recipe) {
            categoryRecipes.computeIfAbsent(category, key -> new JsonArray()).add(recipe);
        }

        public int getDumpedCount() {
            int count = 0;
            for (JsonArray recipes : categoryRecipes.values()) {
                count += recipes.size();
            }
            return count;
        }

        public int getSkippedCount() {
            return errorRecipes.size();
        }

        public JsonObject createCategoriesJson() {
            JsonObject json = new JsonObject();
            categoryRecipes.forEach(json::add);
            return json;
        }

        public JsonObject createErrorJson() {
            JsonObject json = new JsonObject();
            JsonArray recipes = new JsonArray();
            errorRecipes.forEach(recipes::add);
            json.addProperty("count", errorRecipes.size());
            json.add("recipes", recipes);
            return json;
        }

        public JsonObject createSummary() {
            JsonObject summary = new JsonObject();
            JsonObject categoryCounts = new JsonObject();
            categoryRecipes.forEach((category, recipes) -> categoryCounts.addProperty(category, recipes.size()));
            summary.addProperty("dumped", getDumpedCount());
            summary.addProperty("skipped", errorRecipes.size());
            summary.addProperty("categoryCount", categoryRecipes.size());
            summary.add("categories", categoryCounts);
            return summary;
        }

        private void writeCategoryFiles(File outputDirectory) {
            categoryRecipes.forEach((category, recipes) -> {
                JsonObject categoryJson = new JsonObject();
                categoryJson.addProperty("category", category);
                categoryJson.addProperty("count", recipes.size());
                categoryJson.add("recipes", recipes);
                outputJson(new File(outputDirectory, category + ".json"), categoryJson);
            });
        }
    }
}
