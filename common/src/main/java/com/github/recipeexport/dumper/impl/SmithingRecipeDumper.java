package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;

/**
 * 锻造台配方导出器（兼容 SmithingTransformRecipe / SmithingTrimRecipe）。
 * 1.21.1 中 {@link SmithingRecipe#getIngredients()} 返回 [template, base, addition]。
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

