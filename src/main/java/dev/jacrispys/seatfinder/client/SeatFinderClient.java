package dev.jacrispys.seatfinder.client;

import dev.jacrispys.seatfinder.mixins.ClientChunkMapAccessor;
import dev.jacrispys.seatfinder.mixins.MixinGetClientChunks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.atomic.AtomicReferenceArray;


@Environment(EnvType.CLIENT)
public class SeatFinderClient implements ClientModInitializer {

    private boolean cooldown;
    private long cooldownTime = 0;


    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register((world) -> {
            if (cooldown) {
                if ((System.currentTimeMillis() - cooldownTime) < 3001) {
                    return;
                }
                cooldown = false;
                return;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;
            world.getPlayers().forEach(player -> {
                if (player.getBlockX() == 0 && player.getBlockZ() == 0 && (player.getBlockY() < 151 && player.getBlockY() > 149)) {
                    cooldownTime = System.currentTimeMillis();
                    cooldown = true;
                    getSign(player.getEntityName().toLowerCase());
                }
            });

        });

    }

    private void getSign(String ign) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;
            try {
                ClientWorld world = client.world;
                ClientChunkMapAccessor chunkManager = (ClientChunkMapAccessor) world.getChunkManager();
                IChunkProvider chunkMap = (IChunkProvider) chunkManager.getChunks();
                AtomicReferenceArray<WorldChunk> chunks = chunkMap.getLoadedChunks();
                boolean teleported = false;
                for (int i = 0; i < chunks.length(); i++) {
                    Chunk chunk = chunks.get(i);
                    if (chunk == null) {
                        continue;
                    }
                    for (BlockPos pos : chunk.getBlockEntityPositions()) {
                        if (chunk.getBlockEntity(pos) instanceof SignBlockEntity sign) {
                            String text = sign.getTextOnRow(1, true).getString().toLowerCase();
                            if (text.contains(ign)) {
                                client.player.sendChatMessage("/hypixelcommand:tp " + ign + " " + (pos.getX() + 0.5) + " " + (pos.getY() + 1) + " " + (pos.getZ() + 0.5));
                                teleported = true;

                            }
                        }
                    }

                }
                if (!teleported) {
                    client.player.sendChatMessage("/hypixelcommand:tp " + ign + " -5 150 15");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
    }
}
