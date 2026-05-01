package com.github.recipeexport.dumper.impl;

import net.minecraft.world.item.crafting.Ingredient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class RecipeIntrospection {

    private RecipeIntrospection() {
    }

    static List<Ingredient> readIngredients(Object recipe) {
        List<Ingredient> result = new ArrayList<>();

        addFromListLike(result, invokeNoArg(recipe, "getIngredients"));
        if (!result.isEmpty()) {
            return result;
        }

        addFromListLike(result, invokeNoArg(recipe, "ingredients"));
        if (!result.isEmpty()) {
            return result;
        }

        // Fallback for recipes that expose direct fields (e.g. smithing/single-item in some mappings)
        addIfIngredient(result, readField(recipe, "template"));
        addIfIngredient(result, readField(recipe, "base"));
        addIfIngredient(result, readField(recipe, "addition"));
        addIfIngredient(result, readField(recipe, "ingredient"));

        return result;
    }

    static float readFloat(Object recipe, String getterName, String fieldName, float defaultValue) {
        Object value = invokeNoArg(recipe, getterName);
        if (value instanceof Number number) {
            return number.floatValue();
        }

        Object fieldValue = readField(recipe, fieldName);
        if (fieldValue instanceof Number number) {
            return number.floatValue();
        }

        return defaultValue;
    }

    static int readInt(Object recipe, String getterName, String fieldName, int defaultValue) {
        Object value = invokeNoArg(recipe, getterName);
        if (value instanceof Number number) {
            return number.intValue();
        }

        Object fieldValue = readField(recipe, fieldName);
        if (fieldValue instanceof Number number) {
            return number.intValue();
        }

        return defaultValue;
    }

    private static void addFromListLike(List<Ingredient> out, Object value) {
        if (!(value instanceof List<?> list)) {
            return;
        }

        for (Object element : list) {
            if (element instanceof Ingredient ingredient) {
                out.add(ingredient);
            } else if (element instanceof Optional<?> optional && optional.orElse(null) instanceof Ingredient ingredient) {
                out.add(ingredient);
            }
        }
    }

    private static void addIfIngredient(List<Ingredient> out, Object value) {
        if (value instanceof Ingredient ingredient) {
            out.add(ingredient);
        } else if (value instanceof Optional<?> optional && optional.orElse(null) instanceof Ingredient ingredient) {
            out.add(ingredient);
        }
    }

    private static Object invokeNoArg(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Object readField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}

