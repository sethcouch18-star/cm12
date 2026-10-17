package com.example.colormons;

import net.minecraft.resources.ResourceLocation;

/** One place that knows where a species texture lives. */
public class ResourceLocationHelper {
    public static ResourceLocation speciesTexture(String species) {
        return ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/pokemon/" + species + ".png");
    }
}
