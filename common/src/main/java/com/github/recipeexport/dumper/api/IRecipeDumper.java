package com.github.recipeexport.dumper.api;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.lang.reflect.Method;

public interface IRecipeDumper<T extends Recipe<?>> {

    void setInputs(T recipe, IRecipeInputs inputs);

    default void setOutputs(T recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        ItemStack result = readResultItem(recipe, registries);
        if (!result.isEmpty()) {
            outputs.addOutput(1, result);
        }
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

    private static ItemStack readResultItem(Recipe<?> recipe, HolderLookup.Provider registries) {
        try {
            Method method = recipe.getClass().getMethod("getResultItem", HolderLookup.Provider.class);
            Object result = method.invoke(recipe, registries);
            if (result instanceof ItemStack stack) {
                return stack;
            }
        } catch (ReflectiveOperationException ignored) {
        }

        try {
            Method method = recipe.getClass().getMethod("resultItem", HolderLookup.Provider.class);
            Object result = method.invoke(recipe, registries);
            if (result instanceof ItemStack stack) {
                return stack;
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return ItemStack.EMPTY;
    }
}
