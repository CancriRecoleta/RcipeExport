package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;

public class ShapedRecipeDumper implements IRecipeDumper<ShapedRecipe> {

    @Override
    public void setInputs(ShapedRecipe recipe, IRecipeInputs inputs) {
        int width = recipe.getWidth();
        List<net.minecraft.world.item.crafting.Ingredient> ingredients = RecipeIntrospection.readIngredients(recipe);
        for (int i = 0; i < ingredients.size(); i++) {
            int x = i % width;
            int y = i / width;
            int slot = y * 3 + x + 1;
            inputs.addInput(slot, ingredients.get(i));
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
