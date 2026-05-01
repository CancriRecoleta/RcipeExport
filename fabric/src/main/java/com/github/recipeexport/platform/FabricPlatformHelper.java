package com.github.recipeexport.platform;

import com.github.recipeexport.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Collection;
import java.util.stream.Collectors;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Collection<String> getLoadedModIds() {
        return FabricLoader.getInstance().getAllMods().stream()
                .map(mc -> mc.getMetadata().getId())
                .collect(Collectors.toList());
    }
}
