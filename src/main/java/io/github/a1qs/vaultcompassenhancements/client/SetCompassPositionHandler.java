package io.github.a1qs.vaultcompassenhancements.client;

import io.github.a1qs.vaultcompassenhancements.VaultCompassEnhancements;
import io.github.a1qs.vaultcompassenhancements.network.CompassTargetRequestPacket;
import io.github.a1qs.vaultcompassenhancements.network.ModNetwork;
import iskallia.vault.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class SetCompassPositionHandler {
    private static boolean isKeyPressed = false;
    private static long keyPressStartTime = 0;
    private static boolean targetSet = false;
    private static long lastSoundTime = 0;
    private static int pitch = 0;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (VaultCompassEnhancements.set_compass_position.isDown()) {
            if (!isKeyPressed) {
                isKeyPressed = true;
                targetSet = false;
                keyPressStartTime = System.currentTimeMillis();
                pitch = 0;
                lastSoundTime = 0;
            }
        } else {
            if (isKeyPressed) {
                isKeyPressed = false;
                keyPressStartTime = 0;
                targetSet = false;
                lastSoundTime = 0;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (isKeyPressed && !targetSet) {
            long currentTime = System.currentTimeMillis();
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null && hasVaultCompass(player)) {
                if (player.getLevel().dimension().location() != ClientLevel.OVERWORLD.location() && player.getLevel().dimension().location() != ClientLevel.NETHER.location() && player.getLevel().dimension().location() != ClientLevel.END.location()) {
                    playSoundWhileUsing(minecraft.player);
                }
            }
            if (currentTime - keyPressStartTime >= 2000) {
                if (minecraft.player != null) {
                    ModNetwork.CHANNEL.sendToServer(new CompassTargetRequestPacket(minecraft.player.getUUID()));
                }
                targetSet = true;
            }
        }
    }



    private void playSoundWhileUsing(Player player) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSoundTime >= 250) {
            player.playSound(SoundEvents.NOTE_BLOCK_BELL, 1.0F, (float) pitch / 40.0F);
            lastSoundTime = currentTime;
            pitch += 5;
        }
    }

    private boolean hasVaultCompass(Player player) {
        return CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.VAULT_COMPASS).isPresent();
    }
}
