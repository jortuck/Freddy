package net.driedsponge.commands;

import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Shuffle extends GuildCommand {
    public Shuffle() {
        super("shuffle");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
    }

    public static void shuffle(Member member, Guild guild, InteractionHook hook){
        if (CommonChecks.listeningMusic(member, guild) && CommonChecks.listeningMusic(member, guild)) {
            VoiceController vc = PlayerStore.get(guild);
            if (vc.getTrackScheduler().shuffle()) {
                hook.sendMessage("The queue has been shuffled!").queue();
            } else {
                hook.sendMessage("There are no songs in the queue to shuffle.").queue();
            }
        } else {
            hook.sendMessage("You need to be in a call listening to music to use this command!").queue();
        }
    }
}
