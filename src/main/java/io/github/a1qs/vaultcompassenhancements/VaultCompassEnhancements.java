package io.github.a1qs.vaultcompassenhancements;

import com.mojang.logging.LogUtils;
import io.github.a1qs.vaultcompassenhancements.client.RenderVaultCompassHud;
import io.github.a1qs.vaultcompassenhancements.network.ModNetwork;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod(VaultCompassEnhancements.MOD_ID)
public class VaultCompassEnhancements {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "vaultcompassenhancements";
    public static KeyMapping set_compass_position;
    public static KeyMapping toggle_hud;

    public VaultCompassEnhancements() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new SetCompassPositionHandler());
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        ModNetwork.register();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new RenderVaultCompassHud());
        set_compass_position = new KeyMapping("key.vaultcompassenhancements.set_compass_position", GLFW.GLFW_KEY_KP_SUBTRACT, "key.category.vaultcompassenhancements");
        toggle_hud = new KeyMapping("key.vaultcompassenhancements.toggle_hud", GLFW.GLFW_KEY_KP_MULTIPLY, "key.category.vaultcompassenhancements");
        ClientRegistry.registerKeyBinding(set_compass_position);
        ClientRegistry.registerKeyBinding(toggle_hud);

    }
    //mod that shows vault compass on hud and keybind to set pos
    //configurable position of the hud?
}
