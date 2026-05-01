package com.github.recipeexport.dumper.impl;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.api.IRecipeInputs;
import com.github.recipeexport.dumper.api.IRecipeOutputs;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShapedRecipeDumper implements IRecipeDumper<ShapedRecipe> {

    @Override
    public void setInputs(ShapedRecipe recipe, IRecipeInputs inputs) {
        int width = recipe.getWidth();
        List<Optional<Ingredient>> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            Optional<Ingredient> ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) {
                continue;
            }
            int x = i % width;
            int y = i / width;
            inputs.addInput(y * 3 + x + 1, ingredient.get());
        }
    }

    @Override
    public void setOutputs(ShapedRecipe recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        List<ItemStack> items = new ArrayList<>(recipe.getIngredients().size());
        for (Optional<Ingredient> ingredient : recipe.getIngredients()) {
            items.add(ingredient.map(this::sampleStack).orElse(ItemStack.EMPTY));
        }
        outputs.addOutput(1, recipe.assemble(CraftingInput.of(recipe.getWidth(), recipe.getHeight(), items), registries));
    }

    @Override
    public String getRecipeTypeName(ShapedRecipe recipe) {
        return "crafting_shaped";
    }

    @Override
    public String getRecipeCategoryName(ShapedRecipe recipe) {
        return "crafting_shaped";
    }
}
