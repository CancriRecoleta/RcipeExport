package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.List;

public class StoneCuttingRecipeDumper implements IRecipeDumper<StonecutterRecipe> {

    @Override
    public void setInputs(StonecutterRecipe recipe, IRecipeInputs inputs) {
        List<net.minecraft.world.item.crafting.Ingredient> ingredients = RecipeIntrospection.readIngredients(recipe);
        if (!ingredients.isEmpty()) {
            inputs.addInput(1, ingredients.get(0));
        }
    }
}
