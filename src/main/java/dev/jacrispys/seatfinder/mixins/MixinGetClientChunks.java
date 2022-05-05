package dev.jacrispys.seatfinder.mixins;

import dev.jacrispys.seatfinder.client.IChunkProvider;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(ClientChunkManager.ClientChunkMap.class)
public class MixinGetClientChunks implements IChunkProvider {


    @Shadow @Final
    AtomicReferenceArray<WorldChunk> chunks;

    @Override
    public AtomicReferenceArray<WorldChunk> getLoadedChunks() {
        return this.chunks;
    }

}


