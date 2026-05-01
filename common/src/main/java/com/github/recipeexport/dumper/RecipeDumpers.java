package com.github.recipeexport.dumper;

import com.github.recipeexport.dumper.api.IRecipeDumper;
import com.github.recipeexport.dumper.impl.CookingRecipeDumper;
import com.github.recipeexport.dumper.impl.ShapedRecipeDumper;
import com.github.recipeexport.dumper.impl.ShapelessRecipeDumper;
import com.github.recipeexport.dumper.impl.SmithingRecipeDumper;
import com.github.recipeexport.dumper.impl.StoneCuttingRecipeDumper;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局 RecipeDumper 注册表。其它模组可调用 {@link #register(Class, IRecipeDumper)} 注册自定义导出器。
 */
public final class RecipeDumpers {

    private static final Map<Class<? extends Recipe<?>>, IRecipeDumper<?>> DUMPERS = new HashMap<>();
    private static boolean bootstrapped = false;

    private RecipeDumpers() {
    }

    /**
     * 注册一个配方导出器。dumper 的泛型 T 可以是 recipeClass 的父类（例如 AbstractCookingRecipe 的导出器
     * 可以注册给 SmeltingRecipe / BlastingRecipe 等子类）。
     */
    public static <T extends Recipe<?>> void register(Class<T> recipeClass, IRecipeDumper<? super T> dumper) {
        DUMPERS.put(recipeClass, dumper);
    }

    @SuppressWarnings("unchecked")
    public static IRecipeDumper<Recipe<?>> get(Class<?> recipeClass) {
        return (IRecipeDumper<Recipe<?>>) DUMPERS.get(recipeClass);
    }

    public static boolean has(Class<?> recipeClass) {
        return DUMPERS.containsKey(recipeClass);
    }

    /**
     * 注册全部内置导出器。会被入口在加载阶段调用一次。
     */
    public static synchronized void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;

        register(ShapedRecipe.class, new ShapedRecipeDumper());
        register(ShapelessRecipe.class, new ShapelessRecipeDumper());

        CookingRecipeDumper cooking = new CookingRecipeDumper();
        register(SmeltingRecipe.class, cooking);
        register(BlastingRecipe.class, cooking);
        register(SmokingRecipe.class, cooking);
        register(CampfireCookingRecipe.class, cooking);

        SmithingRecipeDumper smithing = new SmithingRecipeDumper();
        register(SmithingTransformRecipe.class, smithing);
        register(SmithingTrimRecipe.class, smithing);

        register(StonecutterRecipe.class, new StoneCuttingRecipeDumper());
    }
}

