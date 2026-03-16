package com.example.psyche.client.mixin;

import com.example.psyche.client.NameHallucinationRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.client.render.entity.EntityRenderer")
public class EntityRendererMixin {
    /**
     * 1.21.11+ renderLabelIfPresent signature changed to include render-state arguments.
     * Keep a broad handler signature to avoid hard coupling to unstable mapped types.
     */
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text psychdisturb$alterNameLabel(
            Text original,
            Object renderState,
            Object matrices,
            Object vertexConsumers,
            Object textLayer
    ) {
        if (!NameHallucinationRenderer.shouldHallucinateAny()) {
            return original;
        }
        return Text.translatable(NameHallucinationRenderer.getRandomHallucinationKey())
                .formatted(Formatting.DARK_RED, Formatting.BOLD);
    }
}
