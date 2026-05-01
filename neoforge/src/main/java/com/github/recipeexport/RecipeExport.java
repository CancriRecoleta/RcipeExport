package com.github.recipeexport;

import com.github.recipeexport.dumper.impl.DumpRecipeCommand;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Constants.MOD_ID)
public class RecipeExport {

    public RecipeExport(IEventBus eventBus) {
        CommonClass.init();
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    public void onRegisterCommands(RegisterCommandsEvent event) {
        DumpRecipeCommand.register(event.getDispatcher());
    }
}
