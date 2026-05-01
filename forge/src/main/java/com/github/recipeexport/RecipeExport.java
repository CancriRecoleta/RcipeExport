package com.github.recipeexport;

import com.github.recipeexport.dumper.impl.DumpRecipeCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class RecipeExport {

    public RecipeExport() {
        CommonClass.init();
        RegisterCommandsEvent.BUS.addListener(this::onRegisterCommands);
    }

    public void onRegisterCommands(RegisterCommandsEvent event) {
        DumpRecipeCommand.register(event.getDispatcher());
    }
}
