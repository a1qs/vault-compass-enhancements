package io.github.a1qs.vaultcompassenhancements.network;

import io.github.a1qs.vaultcompassenhancements.VaultCompassEnhancements;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    public static final String NETWORK_VERSION = "0.1.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(VaultCompassEnhancements.MOD_ID, "network"), () -> NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, CompassTargetRequestPacket.class, CompassTargetRequestPacket::encode, CompassTargetRequestPacket::decode, CompassTargetRequestPacket::handle);
    }
}
