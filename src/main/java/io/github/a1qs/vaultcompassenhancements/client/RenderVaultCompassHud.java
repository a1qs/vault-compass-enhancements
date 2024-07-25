package io.github.a1qs.vaultcompassenhancements.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.a1qs.vaultcompassenhancements.VaultCompassEnhancements;
import iskallia.vault.init.ModItems;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = VaultCompassEnhancements.MOD_ID, value = Dist.CLIENT)
public class RenderVaultCompassHud {
    private static boolean showHud = true;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (VaultCompassEnhancements.toggle_hud.consumeClick()) {
            showHud = !showHud;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL && showHud) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null && hasVaultCompass(player)) {
                renderVaultCompassOnHud(event.getMatrixStack());
            }
        }
    }

    private void renderVaultCompassOnHud(PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        Font font = minecraft.font;

        ItemStack compass = new ItemStack(ModItems.VAULT_COMPASS);

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int x = screenWidth/2 - 8;
        int y = screenHeight - 52;

        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, itemRenderer.getItemModelShaper().getItemModel(compass).getParticleIcon().getName());
        itemRenderer.renderAndDecorateItem(compass, x, y);
        itemRenderer.renderGuiItemDecorations(font, compass, x, y);
        poseStack.popPose();
    }

    private boolean hasVaultCompass(Player player) {
        return CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.VAULT_COMPASS).isPresent();
    }
}
