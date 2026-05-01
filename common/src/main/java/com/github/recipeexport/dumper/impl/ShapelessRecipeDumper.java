package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public class ShapelessRecipeDumper implements IRecipeDumper<ShapelessRecipe> {

    @Override
    public void setInputs(ShapelessRecipe recipe, IRecipeInputs inputs) {
        List<Ingredient> ingredients = RecipeIntrospection.readIngredients(recipe);
        for (int i = 0; i < ingredients.size(); i++) {
            inputs.addInput(i + 1, ingredients.get(i));
        }
    }

    @Override
    public String getRecipeTypeName(ShapelessRecipe recipe) {
        return "crafting_shapeless";
    }

    @Override
    public String getRecipeCategoryName(ShapelessRecipe recipe) {
        return "crafting_shapeless";
    }
}
