package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

public class CookingRecipeDumper implements IRecipeDumper<AbstractCookingRecipe> {

    @Override
    public void setInputs(AbstractCookingRecipe recipe, IRecipeInputs inputs) {
        inputs.addInput(1, recipe.getIngredients().get(0));
    }

    @Override
    public void writeExtraInformation(AbstractCookingRecipe recipe, JsonObject jsonObject) {
        jsonObject.addProperty("experience", recipe.getExperience());
        jsonObject.addProperty("cookTime", recipe.getCookingTime());
    }
}

