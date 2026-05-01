package com.github.recipeexport;

import com.github.recipeexport.dumper.impl.DumpRecipeCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class RecipeExport implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) ->
                DumpRecipeCommand.register(dispatcher));
    }
}
