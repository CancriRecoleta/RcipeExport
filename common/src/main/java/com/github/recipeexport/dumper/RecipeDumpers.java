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

import java.util.LinkedHashMap;
import java.util.Map;

public final class RecipeDumpers {

    private static final Map<Class<? extends Recipe<?>>, IRecipeDumper<?>> DUMPERS = new LinkedHashMap<>();
    private static boolean bootstrapped = false;

    private RecipeDumpers() {
    }

    public static <T extends Recipe<?>> void register(Class<T> recipeClass, IRecipeDumper<? super T> dumper) {
        DUMPERS.put(recipeClass, dumper);
    }

    @SuppressWarnings("unchecked")
    public static IRecipeDumper<Recipe<?>> get(Class<?> recipeClass) {
        IRecipeDumper<?> exact = DUMPERS.get(recipeClass);
        if (exact != null) {
            return (IRecipeDumper<Recipe<?>>) exact;
        }
        for (Map.Entry<Class<? extends Recipe<?>>, IRecipeDumper<?>> entry : DUMPERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(recipeClass)) {
                return (IRecipeDumper<Recipe<?>>) entry.getValue();
            }
        }
        return null;
    }

    public static boolean has(Class<?> recipeClass) {
        return get(recipeClass) != null;
    }

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
