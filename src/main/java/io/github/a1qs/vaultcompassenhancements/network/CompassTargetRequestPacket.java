package io.github.a1qs.vaultcompassenhancements.network;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CompassTargetRequestPacket {
    private final UUID playerUUID;

    public CompassTargetRequestPacket(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public static void encode(CompassTargetRequestPacket msg, FriendlyByteBuf buffer) {
        buffer.writeUUID(msg.playerUUID);
    }

    public static CompassTargetRequestPacket decode(FriendlyByteBuf buffer) {
        return new CompassTargetRequestPacket(buffer.readUUID());
    }

    public static void handle(CompassTargetRequestPacket msg, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            context.get().enqueueWork(() -> {
                ServerLevel level = player.getLevel();
                BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos targetPos = blockHitResult.getBlockPos();
                    setCompassTarget(player, level, targetPos);
                }
            });
        }
        context.get().setPacketHandled(true);
    }

    private static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluidMode) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, fluidMode, player));
    }

    private static void setCompassTarget(Player player, ServerLevel level, BlockPos pos) {
        ServerVaults.get(level).ifPresent((vault) -> {
            Listeners listeners = vault.get(Vault.LISTENERS);
            if (listeners.contains(player.getUUID())) {
                listeners.get(player.getUUID()).set(Listener.COMPASS_TARGET, pos);
                player.displayClientMessage(new TextComponent("New compass target set"), true);
            }
        });
    }

//    private static void setCompassTarget(Player player, ServerLevel level, BlockPos pos) {
//        System.out.println("Setting the Compass Target for level: " + level);
//        Optional<ServerVault> serverVaultOptional = ServerVaults.get(level);
//        if (serverVaultOptional.isPresent()) {
//            ServerVault serverVault = serverVaultOptional.get();
//            System.out.println("ServerVault found: " + serverVault);
//            Listeners listeners = serverVault.get(Vault.LISTENERS);
//            System.out.println("Listeners found: " + listeners);
//            if (listeners.contains(player.getUUID())) {
//                listeners.get(player.getUUID()).set(Listener.COMPASS_TARGET, pos);
//                System.out.println("Set The Compass Target to: " + pos);
//            } else {
//                System.out.println("Listeners do not contain player UUID: " + player.getUUID());
//            }
//        } else {
//            System.out.println("No ServerVault found for level: " + level);
//        }
//    }
}
