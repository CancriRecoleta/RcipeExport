package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.List;

/**
 * Smithing recipe dumper for both transform and trim recipes.
 * In 1.21.x, smithing ingredients are ordered as [template, base, addition].
 */
public class SmithingRecipeDumper implements IRecipeDumper<SmithingRecipe> {

    @Override
    public void setInputs(SmithingRecipe recipe, IRecipeInputs inputs) {
        List<net.minecraft.world.item.crafting.Ingredient> ingredients = RecipeIntrospection.readIngredients(recipe);
        for (int i = 0; i < ingredients.size(); i++) {
            inputs.addInput(i + 1, ingredients.get(i));
        }
    }
}
