package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

import java.util.List;

public class CookingRecipeDumper implements IRecipeDumper<AbstractCookingRecipe> {

    @Override
    public void setInputs(AbstractCookingRecipe recipe, IRecipeInputs inputs) {
        List<net.minecraft.world.item.crafting.Ingredient> ingredients = RecipeIntrospection.readIngredients(recipe);
        if (!ingredients.isEmpty()) {
            inputs.addInput(1, ingredients.get(0));
        }
    }

    @Override
    public void writeExtraInformation(AbstractCookingRecipe recipe, JsonObject jsonObject) {
        jsonObject.addProperty("experience", RecipeIntrospection.readFloat(recipe, "getExperience", "experience", 0.0F));
        jsonObject.addProperty("cookTime", RecipeIntrospection.readInt(recipe, "getCookingTime", "cookingTime", 0));
    }
}
