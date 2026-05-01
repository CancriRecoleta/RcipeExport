package com.github.recipeexport;

import com.github.recipeexport.dumper.RecipeDumpers;
import com.github.recipeexport.platform.Services;

public class CommonClass {

    public static void init() {
        RecipeDumpers.bootstrap();
        Constants.LOG.info("Initialized {} on {} ({})",
                Constants.MOD_NAME,
                Services.PLATFORM.getPlatformName(),
                Services.PLATFORM.getEnvironmentName());
    }
}
