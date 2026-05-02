package com.github.recipeexport.dumper.api;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface IRecipeDumper<T extends Recipe<?>> {

    void setInputs(T recipe, IRecipeInputs inputs);

    default void setOutputs(T recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        outputs.addOutput(1, assembleResult(recipe, registries));
    }

    default void writeExtraInformation(T recipe, JsonObject jsonObject) {
    }

    default String getRecipeCategoryName(T recipe) {
        String type = getRecipeTypeName(recipe);
        int namespaceSeparator = type.indexOf(':');
        return namespaceSeparator >= 0 ? type.substring(namespaceSeparator + 1) : type;
    }

    default String getRecipeTypeName(T recipe) {
        var key = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        return key == null ? recipe.getType().toString() : key.toString();
    }

    private static ItemStack assembleResult(Recipe<?> recipe, HolderLookup.Provider registries) {
        if (recipe instanceof SingleItemRecipe singleItemRecipe) {
            return singleItemRecipe.assemble(new SingleRecipeInput(sampleStack(singleItemRecipe.input())), registries);
        }
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            List<ItemStack> items = new ArrayList<>(shapedRecipe.getWidth() * shapedRecipe.getHeight());
            for (Optional<Ingredient> ingredient : shapedRecipe.getIngredients()) {
                items.add(ingredient.map(IRecipeDumper::sampleStack).orElse(ItemStack.EMPTY));
            }
            return shapedRecipe.assemble(CraftingInput.of(shapedRecipe.getWidth(), shapedRecipe.getHeight(), items), registries);
        }
        if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            List<Ingredient> ingredients = shapelessRecipe.placementInfo().ingredients();
            List<ItemStack> items = new ArrayList<>(ingredients.size());
            for (Ingredient ingredient : ingredients) {
                items.add(sampleStack(ingredient));
            }
            return shapelessRecipe.assemble(CraftingInput.of(Math.max(1, items.size()), 1, items), registries);
        }
        if (recipe instanceof SmithingRecipe smithingRecipe) {
            return smithingRecipe.assemble(new SmithingRecipeInput(
                    smithingRecipe.templateIngredient().map(IRecipeDumper::sampleStack).orElse(ItemStack.EMPTY),
                    sampleStack(smithingRecipe.baseIngredient()),
                    smithingRecipe.additionIngredient().map(IRecipeDumper::sampleStack).orElse(ItemStack.EMPTY)
            ), registries);
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack sampleStack(Ingredient ingredient) {
        return ingredient.items()
                .findFirst()
                .map(holder -> holder.value().getDefaultInstance())
                .orElse(ItemStack.EMPTY);
    }
}
