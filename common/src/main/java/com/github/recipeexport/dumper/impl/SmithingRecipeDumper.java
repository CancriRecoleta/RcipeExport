package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;

/**
 * Smithing recipe dumper for both transform and trim recipes.
 * In 1.21.x, {@link SmithingRecipe#getIngredients()} returns
 * [template, base, addition].
 */
public class SmithingRecipeDumper implements IRecipeDumper<SmithingRecipe> {

    @Override
    public void setInputs(SmithingRecipe recipe, IRecipeInputs inputs) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            inputs.addInput(i + 1, ingredients.get(i));
        }
    }
}
