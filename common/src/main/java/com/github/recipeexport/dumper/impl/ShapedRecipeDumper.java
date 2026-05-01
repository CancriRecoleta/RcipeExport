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
            // 沿用原项目惯例：按 3 列网格槽位编号（1..9）
            inputs.addInput(y * 3 + x + 1, ingredient);
        }
    }

    @Override
    public String getRecipeTypeName(ShapedRecipe recipe) {
        return "crafting_shaped";
    }
}

