package com.github.recipeexport.dumper.api;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public interface IRecipeOutputs {
    void addOutput(int slot, ItemStack stack);

    JsonObject serialize(HolderLookup.Provider registries) throws RecipeDumpException;
}
