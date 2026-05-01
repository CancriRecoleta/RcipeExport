package com.github.recipeexport.dumper.api;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public interface IRecipeDumper<T extends Recipe<?>> {

    void setInputs(T recipe, IRecipeInputs inputs);

    default void setOutputs(T recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
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

    default ItemStack sampleStack(Ingredient ingredient) {
        return ingredient.items()
                .findFirst()
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
    }

    default ItemStack sampleStack(Optional<Ingredient> ingredient) {
        return ingredient.map(this::sampleStack).orElse(ItemStack.EMPTY);
    }
}
