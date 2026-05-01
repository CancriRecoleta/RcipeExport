package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeInputs;
import com.github.recipeexport.dumper.api.RecipeDumpException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.item.crafting.Ingredient;

public class RecipeInputs implements IRecipeInputs {
    private final Int2ObjectMap<Ingredient> inputs = new Int2ObjectArrayMap<>();
    private final Int2IntMap counts = new Int2IntArrayMap();

    @Override
    public void addInput(int slot, Ingredient ingredient, int count) {
        if (ingredient == null || ingredient.isEmpty()) {
            return;
        }
        inputs.put(slot, ingredient);
        counts.put(slot, count);
    }

    @Override
    public JsonObject serialize() throws RecipeDumpException {
        JsonObject json = new JsonObject();
        if (inputs.size() != counts.size()) {
            throw new RecipeDumpException("inputs/counts size mismatch");
        }
        try {
            for (Int2ObjectMap.Entry<Ingredient> entry : inputs.int2ObjectEntrySet()) {
                JsonElement ingredientJson = Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue())
                        .getOrThrow(msg -> new IllegalStateException("Ingredient encode failed: " + msg));
                JsonObject wrapper;
                if (ingredientJson.isJsonObject()) {
                    wrapper = ingredientJson.getAsJsonObject();
                } else {
                    wrapper = new JsonObject();
                    wrapper.add("value", ingredientJson);
                }
                wrapper.addProperty("count", counts.get(entry.getIntKey()));
                json.add(String.valueOf(entry.getIntKey()), wrapper);
            }
        } catch (Throwable throwable) {
            throw new RecipeDumpException("Failed to serialize inputs", throwable);
        }
        return json;
    }
}

