package dev.jacrispys.seatfinder.client;

import net.minecraft.world.chunk.WorldChunk;

import java.util.concurrent.atomic.AtomicReferenceArray;

public interface IChunkProvider {

    AtomicReferenceArray<WorldChunk> getLoadedChunks();
}
