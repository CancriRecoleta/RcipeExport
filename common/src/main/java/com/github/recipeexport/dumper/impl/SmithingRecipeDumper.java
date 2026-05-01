package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import com.github.recipeexport.dumper.api.IRecipeOutputs;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

/**
 * Smithing recipe dumper for both transform and trim recipes.
 */
public class SmithingRecipeDumper implements IRecipeDumper<SmithingRecipe> {

    @Override
    public void setInputs(SmithingRecipe recipe, IRecipeInputs inputs) {
        recipe.templateIngredient().ifPresent(ingredient -> inputs.addInput(1, ingredient));
        recipe.baseIngredient().ifPresent(ingredient -> inputs.addInput(2, ingredient));
        recipe.additionIngredient().ifPresent(ingredient -> inputs.addInput(3, ingredient));
    }

    @Override
    public void setOutputs(SmithingRecipe recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        ItemStack template = sampleStack(recipe.templateIngredient());
        ItemStack base = sampleStack(recipe.baseIngredient());
        ItemStack addition = sampleStack(recipe.additionIngredient());
        outputs.addOutput(1, recipe.assemble(new SmithingRecipeInput(template, base, addition), registries));
    }
}
