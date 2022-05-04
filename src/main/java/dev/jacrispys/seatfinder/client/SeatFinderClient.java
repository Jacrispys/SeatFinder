package dev.jacrispys.seatfinder.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReferenceArray;


@Environment(EnvType.CLIENT)
public class SeatFinderClient implements ClientModInitializer {

    private boolean cooldown;
    private long cooldownTime = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_WORLD_TICK.register((world) -> {
            if (cooldown) {
                if ((System.currentTimeMillis() - cooldownTime) < 300) {
                    return;
                }
                cooldown = false;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;
            world.getPlayers().forEach(player -> {
                if (player.getBlockX() == 0 && player.getBlockZ() == 0 && (player.getBlockY() < 151 && player.getBlockY() > 149)) {
                    getSign(player.getEntityName().toLowerCase());
                    cooldownTime = System.currentTimeMillis();
                    cooldown = true;
                }
            });

        });

    }

    private void getSign(String ign) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        try {
            ClientChunkManager chunkManager = client.world.getChunkManager();
            Field chunkMap = chunkManager.getClass().getDeclaredField("field_16246");
            chunkMap.setAccessible(true);
            Object chunkMapValue = chunkMap.get(chunkManager);
            Field getChunk = chunkMapValue.getClass().getDeclaredField("field_16251");
            getChunk.setAccessible(true);
            AtomicReferenceArray<WorldChunk> chunks = (AtomicReferenceArray<WorldChunk>) getChunk.get(chunkMapValue);
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
                            client.player.sendChatMessage("/hypixelcommand:tp " + ign + " " + pos.getX() + " " + (pos.getY() + 1) + " " + pos.getZ());
                            teleported = true;

                        }
                    }
                }

            }
            if(!teleported) {
                client.player.sendChatMessage("/hypixelcommand:tp " + ign + " -2.5 150 26.5");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
