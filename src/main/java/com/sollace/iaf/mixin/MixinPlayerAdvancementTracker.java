package com.sollace.iaf.mixin;

import java.util.Set;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.sollace.iaf.DistinctStack;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;

@Mixin(PlayerAdvancementTracker.class)
abstract class MixinPlayerAdvancementTracker {

    @Shadow
    private @Final Set<Advancement> visibleAdvancements;

    @Shadow
    abstract boolean canSee(Advancement advancement);

    @Shadow
    abstract void updateDisplay(Advancement advancement);

    private boolean debuggify_nestingCheck;

    @Inject(method = "updateDisplay", at = @At("RETURN"))
    private void afterUpdateDisplay(Advancement advancement, CallbackInfo info) {
        if (debuggify_nestingCheck) {
            return;
        }
        try {
            debuggify_nestingCheck = true;
            final DistinctStack<Advancement> stack = new DistinctStack<>();

            stack.push(advancement.getParent());
            for (Advancement child : advancement.getChildren()) {
                stack.push(child);
            }

            while ((advancement = stack.poll()) != null) {
                boolean visibilityChanged = canSee(advancement) != visibleAdvancements.contains(advancement);

                updateDisplay(advancement);

                if (visibilityChanged) {
                    stack.push(advancement.getParent());
                }

                for (Advancement child : advancement.getChildren()) {
                    stack.push(child);
                }

            }
        } finally {
            debuggify_nestingCheck = false;
        }
    }

    @Redirect(method = "updateDisplay", at = @At(value = "INVOKE", target = "net/minecraft/advancement/PlayerAdvancementTracker.updateDisplay(Lnet/minecraft/advancement/Advancement;)V"))
    private void onNestedUpdateDisplayInvokation(PlayerAdvancementTracker self, Advancement advancement) {
        // we void any nested invokations since the recursive call has been unwound into a stack
    }
}
