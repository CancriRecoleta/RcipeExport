package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class StoneCuttingRecipeDumper implements IRecipeDumper<StonecutterRecipe> {

    @Override
    public void setInputs(StonecutterRecipe recipe, IRecipeInputs inputs) {
        inputs.addInput(1, recipe.input());
    }
}

