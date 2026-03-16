package com.example.psyche.client.mixin;

import com.example.psyche.client.NameHallucinationRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.client.render.entity.EntityRenderer")
public class EntityRendererMixin {
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Text psychdisturb$alterNameLabel(Text original, Entity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return original;
        }
        if (!NameHallucinationRenderer.shouldHallucinate(player.getUuid())) {
            return original;
        }
        return Text.translatable(NameHallucinationRenderer.getRandomHallucinationKey()).formatted(Formatting.DARK_RED, Formatting.BOLD);
    }
}
