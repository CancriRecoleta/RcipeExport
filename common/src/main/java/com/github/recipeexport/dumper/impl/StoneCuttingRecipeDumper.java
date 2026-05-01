package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import com.github.recipeexport.dumper.api.IRecipeOutputs;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class StoneCuttingRecipeDumper implements IRecipeDumper<StonecutterRecipe> {

    @Override
    public void setInputs(StonecutterRecipe recipe, IRecipeInputs inputs) {
        inputs.addInput(1, recipe.input());
    }

    @Override
    public void setOutputs(StonecutterRecipe recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        outputs.addOutput(1, recipe.assemble(new SingleRecipeInput(sampleStack(recipe.input())), registries));
    }
}

