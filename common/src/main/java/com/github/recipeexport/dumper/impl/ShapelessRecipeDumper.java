package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import com.github.recipeexport.dumper.api.IRecipeOutputs;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipeDumper implements IRecipeDumper<ShapelessRecipe> {

    @Override
    public void setInputs(ShapelessRecipe recipe, IRecipeInputs inputs) {
        List<Ingredient> ingredients = recipe.placementInfo().ingredients();
        for (int i = 0; i < ingredients.size(); i++) {
            inputs.addInput(i + 1, ingredients.get(i));
        }
    }

    @Override
    public void setOutputs(ShapelessRecipe recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        List<Ingredient> ingredients = recipe.placementInfo().ingredients();
        List<ItemStack> items = new ArrayList<>(ingredients.size());
        for (Ingredient ingredient : ingredients) {
            items.add(sampleStack(ingredient));
        }
        outputs.addOutput(1, recipe.assemble(CraftingInput.of(Math.max(1, items.size()), 1, items), registries));
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
