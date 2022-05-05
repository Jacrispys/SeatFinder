package dev.jacrispys.seatfinder.mixins;

import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(ClientChunkManager.class)
public interface ClientChunkMapAccessor {

    @Accessor
    ClientChunkManager.ClientChunkMap getChunks();
}
