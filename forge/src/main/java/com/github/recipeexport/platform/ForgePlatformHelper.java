package com.github.recipeexport.platform;

import com.github.recipeexport.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Collection;
import java.util.stream.Collectors;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Collection<String> getLoadedModIds() {
        return ModList.get().getMods().stream()
                .map(IModInfo::getModId)
                .collect(Collectors.toList());
    }
}
