package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import com.github.recipeexport.dumper.api.IRecipeOutputs;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

public class CookingRecipeDumper implements IRecipeDumper<AbstractCookingRecipe> {

    @Override
    public void setInputs(AbstractCookingRecipe recipe, IRecipeInputs inputs) {
        inputs.addInput(1, recipe.input());
    }

    @Override
    public void setOutputs(AbstractCookingRecipe recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        outputs.addOutput(1, recipe.assemble(new SingleRecipeInput(sampleStack(recipe.input())), registries));
    }

    @Override
    public void writeExtraInformation(AbstractCookingRecipe recipe, JsonObject jsonObject) {
        jsonObject.addProperty("experience", recipe.experience());
        jsonObject.addProperty("cookTime", recipe.cookingTime());
    }
}

