package com.example.colormons;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Mod(value = "colormons", dist = Dist.CLIENT)
public class ColormonsClient {
    public static final KeyMapping OPEN = new KeyMapping(
            "key.colormons.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.colormons");

    /** 0 = yours, 1 = wild, 2 = someone else's */
    private static int status = 0;

    public ColormonsClient(IEventBus modBus) {
        ColorStore.load();
        modBus.addListener(RegisterKeyMappingsEvent.class, e -> e.register(OPEN));
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, e -> {
            Minecraft mc = Minecraft.getInstance();
            while (OPEN.consumeClick()) {
                if (mc.player == null) continue;
                if (!(mc.crosshairPickEntity instanceof PokemonEntity mon)) continue;

                status = ownership(mc, mon);
                if (!ColorStore.allowOthers && status != 0) {
                    String msg = status == 1
                            ? "Colormons: this Pokemon is wild - catch it first to paint it"
                            : "Colormons: this Pokemon doesn't belong to you";
                    mc.player.displayClientMessage(Component.literal(msg), true);
                    continue;
                }
                ScreenData.reset();
                mc.setScreen(new ColorWheelScreen(mon));
            }
        });
    }

    /** Returns 0 if the Pokemon is yours, 1 if wild, 2 if it belongs to another player. */
    public static int ownership(Minecraft mc, PokemonEntity mon) {
        try {
            UUID owner = mon.getPokemon().getOwnerUUID();
            if (owner == null) return 1;
            return owner.equals(mc.player.getUUID()) ? 0 : 2;
        } catch (Throwable t) {
            return 2;
        }
    }
}
