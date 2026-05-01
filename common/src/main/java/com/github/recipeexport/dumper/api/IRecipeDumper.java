package com.github.recipeexport.dumper.api;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;

/**
 * 自定义配方导出器接口。注册到 {@link com.github.recipeexport.dumper.RecipeDumpers}。
 *
 * @param <T> 配方类型
 */
public interface IRecipeDumper<T extends Recipe<?>> {

    /**
     * 收集配方的输入。
     */
    void setInputs(T recipe, IRecipeInputs inputs);

    /**
     * 收集配方的输出。默认实现使用 {@link Recipe#getResultItem(HolderLookup.Provider)}。
     */
    default void setOutputs(T recipe, IRecipeOutputs outputs, HolderLookup.Provider registries) {
        outputs.addOutput(1, recipe.getResultItem(registries));
    }

    /**
     * 写入额外信息（如经验、烧炼时间）。
     */
    default void writeExtraInformation(T recipe, JsonObject jsonObject) {
    }

    /**
     * 配方类型名（默认从 RecipeType 注册表取出）。
     */
    default String getRecipeTypeName(T recipe) {
        var key = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        return key == null ? recipe.getType().toString() : key.toString();
    }
}

