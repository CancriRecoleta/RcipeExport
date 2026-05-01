package com.github.recipeexport.dumper.api;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;

public interface IRecipeDumper<T extends Recipe<?>> {

    void setInputs(T recipe, IRecipeInputs inputs);

    default void setOutputs(T recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        outputs.addOutput(1, recipe.getResultItem(registries));
    }

    default void writeExtraInformation(T recipe, JsonObject jsonObject) {
    }

    default String getRecipeCategoryName(T recipe) {
        String type = getRecipeTypeName(recipe);
        int namespaceSeparator = type.indexOf(':');
        return namespaceSeparator >= 0 ? type.substring(namespaceSeparator + 1) : type;
    }

    default String getRecipeTypeName(T recipe) {
        var key = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        return key == null ? recipe.getType().toString() : key.toString();
    }
}
