package com.example.examplemod.mixin.dedicated;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {

    @Inject(at = @At("HEAD"), method = "init")
    private void init(CallbackInfoReturnable<Boolean> info) {
        System.out.println("DedicatedServer: This line is printed by an example mod mixin!");
    }

}
