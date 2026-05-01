package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class ShapedRecipeDumper implements IRecipeDumper<ShapedRecipe> {

    @Override
    public void setInputs(ShapedRecipe recipe, IRecipeInputs inputs) {
        int width = recipe.getWidth();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            int x = i % width;
            int y = i / width;
            inputs.addInput(y * 3 + x + 1, ingredient);
        }
    }

    @Override
    public String getRecipeTypeName(ShapedRecipe recipe) {
        return "crafting_shaped";
    }

    @Override
    public String getRecipeCategoryName(ShapedRecipe recipe) {
        return "crafting_shaped";
    }
}
