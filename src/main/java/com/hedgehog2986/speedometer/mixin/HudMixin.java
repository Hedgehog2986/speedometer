package com.hedgehog2986.speedometer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

import static java.lang.Double.isNaN;

@Mixin(InGameHud.class)
public abstract class HudMixin {

    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V")
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        double dx = client.player.getPos().x - client.player.prevX;
        double dy = client.player.getPos().y - client.player.prevY;
        double dz = client.player.getPos().z - client.player.prevZ;
        double speed = MathHelper.sqrt((float) (dx*dx + dy*dy + dz*dz))*20;
        TextRenderer textRenderer = this.getTextRenderer();
        String speedString = String.format(Locale.ROOT, "%.2f m/s", speed);
        int textX = client.getWindow().getScaledWidth() - textRenderer.getWidth(speedString) - 5;
        int textY = client.getWindow().getScaledHeight() - textRenderer.fontHeight - 15;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        textRenderer.draw(matrices, speedString, textX, textY, 0xFFFFFF | 0xFF000000);

        RenderSystem.disableBlend();
    }
}
