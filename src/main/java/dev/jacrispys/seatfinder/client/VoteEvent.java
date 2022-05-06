package dev.jacrispys.seatfinder.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VoteEvent {

    private boolean isVotePeriod = false;
    private final Map<String, String> voteMap = new HashMap<>();
    private Set<String> waitingPlayers = new HashSet<>();
    private boolean cooldown;
    private long cooldownTime;

    private final MinecraftClient client = MinecraftClient.getInstance();


    public VoteEvent() {
        ClientTickEvents.END_WORLD_TICK.register((world) -> {
            if (isVotePeriod) {
                world.getPlayers().forEach(this::accept);
                if (cooldown) {
                    if ((System.currentTimeMillis() - cooldownTime) < 500) {
                        return;
                    }
                    cooldown = false;
                    return;
                }
                if (!waitingPlayers.isEmpty()) {
                    queueWaitlist();
                    cooldownTime = System.currentTimeMillis();
                    cooldown = true;
                }
            }
        });
    }

    private void queueWaitlist() {
        if(cooldown) return;
        List<String> waitList = new ArrayList<>(waitingPlayers.stream().toList());
        SeatFinderClient.getSign(waitList.get(0));
        waitList.remove(waitList.get(0));
        waitingPlayers = new HashSet<>(waitList);
    }

    public void startVote() {
        isVotePeriod = true;
    }

    public void endVote() {
        isVotePeriod = false;
        pollVotes();
    }

    private void pollVotes() {
        AtomicInteger vote1 = new AtomicInteger(0);
        AtomicInteger vote2 = new AtomicInteger(0);
        AtomicInteger vote3 = new AtomicInteger(0);
        AtomicInteger vote4 = new AtomicInteger(0);
        AtomicInteger vote5 = new AtomicInteger(0);
        AtomicInteger vote6 = new AtomicInteger(0);
        AtomicInteger vote7 = new AtomicInteger(0);
        AtomicInteger vote8 = new AtomicInteger(0);

        client.world.getPlayers().forEach(player -> {
            if (!voteMap.containsKey(player.getEntityName().toLowerCase()) || voteMap.get(player.getEntityName().toLowerCase()) == null) {
                return;
            }
            System.out.println(voteMap.get(player.getEntityName().toLowerCase()));
            switch (voteMap.get(player.getEntityName().toLowerCase())) {
                case ("vote1") -> vote1.getAndIncrement();
                case ("vote2") -> vote2.getAndIncrement();
                case ("vote3") -> vote3.getAndIncrement();
                case ("vote4") -> vote4.getAndIncrement();
                case ("vote5") -> vote5.getAndIncrement();
                case ("vote6") -> vote6.getAndIncrement();
                case ("vote7") -> vote7.getAndIncrement();
                case ("vote8") -> vote8.getAndIncrement();

                default -> {
                    System.out.println(voteMap.get(player.getEntityName().toLowerCase()));
                }
            }
        });
        StringBuilder builder = new StringBuilder();
        builder.append("Vote1 -> ").append(vote1.get());
        builder.append(" Vote2 -> ").append(vote2.get());
        builder.append(" Vote3 -> ").append(vote3.get());
        builder.append(" Vote4 -> ").append(vote4.get());
        builder.append(" Vote5 -> ").append(vote5.get());
        builder.append(" Vote6 -> ").append(vote6.get());
        builder.append(" Vote7 -> ").append(vote7.get());
        builder.append(" Vote8 -> ").append(vote8.get());
        client.player.sendChatMessage(builder.toString());
        voteMap.clear();
    }

    @SuppressWarnings("All")
    private void accept(AbstractClientPlayerEntity player) {
        BlockPos loc = player.getBlockPos().down(1);
        Map<BlockPos, String> posMap = new HashMap<>();
        posMap.put(new BlockPos(-7, 125, 12), "vote1");
        posMap.put(new BlockPos(-5, 125, 12), "vote2");
        posMap.put(new BlockPos(-3, 125, 12), "vote3");
        posMap.put(new BlockPos(-1, 125, 12), "vote4");
        posMap.put(new BlockPos(1, 125, 12), "vote5");
        posMap.put(new BlockPos(-5, 125, 15), "vote6");
        posMap.put(new BlockPos(-3, 125, 15), "vote7");
        posMap.put(new BlockPos(-1, 125, 15), "vote8");

        String voteNumber = posMap.getOrDefault(loc, null);

        if(posMap.get(loc) != null) {
            voteMap.put(player.getEntityName().toLowerCase(), voteNumber);
            if(!waitingPlayers.contains(player.getEntityName().toLowerCase())) {
                waitingPlayers.add(player.getEntityName().toLowerCase());
                return;
            }
        }
    }
}
