package dev.jacrispys.seatfinder.client;

import dev.jacrispys.seatfinder.mixins.ClientChunkMapAccessor;
import dev.jacrispys.seatfinder.mixins.MixinGetClientChunks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;


@Environment(EnvType.CLIENT)
public class SeatFinderClient implements ClientModInitializer {

    private boolean cooldown;
    private long cooldownTime = 0;

    private static Set<String> ignList = new HashSet<>();
    private static Map<String, String> teleportMap = new HashMap<>();
    private final List<BlockPos> seatList = new ArrayList<>();
    private final BlockPos seatPos = new BlockPos(0, 148, 0);
    private final BlockPos seatPos2 = new BlockPos(-1, 148, 0);
    private final BlockPos seatPos3 = new BlockPos(-1, 148, -1);
    private final BlockPos seatPos4 = new BlockPos(0, 148, -1);


    @Override
    public void onInitializeClient() {
        seatList.add(seatPos);
        seatList.add(seatPos2);
        seatList.add(seatPos3);
        seatList.add(seatPos4);
        ClientTickEvents.END_WORLD_TICK.register((world) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            assert client.player != null;
            world.getPlayers().forEach(player -> {
                if (seatList.contains(player.getBlockPos().down(1))) {
                    cooldownTime = System.currentTimeMillis();
                    cooldown = true;
                    getSign(player.getEntityName().toLowerCase());
                }
            });
            if (cooldown) {
                if ((System.currentTimeMillis() - cooldownTime) < 750) {
                    return;
                }
                cooldown = false;
                return;
            }
            if(!ignList.isEmpty()) {
                cooldownTime = System.currentTimeMillis();
                cooldown = true;
                client.player.sendChatMessage(teleportMap.get(ignList.stream().toList().get(0)));
                ignList.remove(ignList.stream().toList().get(0));
                return;
            }

        });

    }

    public static void getSign(String ign) {
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
                    if(teleported) continue;
                    Chunk chunk = chunks.get(i);
                    if (chunk == null) {
                        continue;
                    }
                    for (BlockPos pos : chunk.getBlockEntityPositions()) {
                        if (chunk.getBlockEntity(pos) instanceof SignBlockEntity sign) {
                            String text = sign.getTextOnRow(1, true).getString().toLowerCase();
                            if (text.contains(ign)) {
                                teleportMap.put(ign, "/hypixelcommand:tp " + ign + " " + (pos.getX() + 0.5) + " " + (pos.getY() + 1) + " " + (pos.getZ() + 0.5));
                                ignList.add(ign);
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
