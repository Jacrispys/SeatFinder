package dev.jacrispys.seatfinder.mixins;

import dev.jacrispys.seatfinder.client.VoteEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(ChatHud.class)
public class MixinChatHud {


    private final VoteEvent voteEvent = new VoteEvent();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("TAIL"))
    public void onChatMessage(Text message, CallbackInfo ci) {
        StringBuilder builder = new StringBuilder();
        message.getSiblings().stream().map(Text::asString).toList().forEach(builder::append);
        String rawText = builder.toString().toLowerCase();
        if(rawText.contains("§9party §8> ") && rawText.contains("!startvote")) {
            voteEvent.startVote();
            return;
        }
        if(rawText.contains("§9party §8> ") && rawText.contains("!endvote")) {
            voteEvent.endVote();
        }

    }
}
