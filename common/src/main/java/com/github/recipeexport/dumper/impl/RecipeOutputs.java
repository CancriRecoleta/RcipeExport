package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeOutputs;
import com.github.recipeexport.dumper.api.RecipeDumpException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class RecipeOutputs implements IRecipeOutputs {
    private final Int2ObjectMap<ItemStack> outputs = new Int2ObjectArrayMap<>();

    @Override
    public void addOutput(int slot, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        outputs.put(slot, stack);
    }

    @Override
    public JsonObject serialize(HolderLookup.Provider registries) throws RecipeDumpException {
        JsonObject json = new JsonObject();
        try {
            HolderLookup.Provider provider = registries != null ? registries : RegistryAccess.EMPTY;
            for (Int2ObjectMap.Entry<ItemStack> entry : outputs.int2ObjectEntrySet()) {
                JsonObject stackJson = new JsonObject();
                ItemStack stack = entry.getValue();
                stackJson.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                stackJson.addProperty("count", stack.getCount());
                DataComponentPatch patch = stack.getComponentsPatch();
                if (!patch.isEmpty()) {
                    JsonElement componentsJson = DataComponentPatch.CODEC
                            .encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), patch)
                            .getOrThrow(msg -> new IllegalStateException("Components encode failed: " + msg));
                    stackJson.add("components", componentsJson);
                }
                json.add(String.valueOf(entry.getIntKey()), stackJson);
            }
        } catch (Throwable throwable) {
            throw new RecipeDumpException("Failed to serialize outputs", throwable);
        }
        return json;
    }
}

