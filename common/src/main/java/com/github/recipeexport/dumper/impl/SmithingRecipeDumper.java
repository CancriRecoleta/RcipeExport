package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.world.item.crafting.SmithingRecipe;

public class SmithingRecipeDumper implements IRecipeDumper<SmithingRecipe> {

    @Override
    public void setInputs(SmithingRecipe recipe, IRecipeInputs inputs) {
        if (recipe.templateIngredient().isPresent()) {
            inputs.addInput(1, recipe.templateIngredient().get());
        }
        inputs.addInput(2, recipe.baseIngredient());
        recipe.additionIngredient().ifPresent(ingredient -> inputs.addInput(3, ingredient));
    }
}
