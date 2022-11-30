package net.driedsponge.commands;

import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
public class Shuffle extends GuildCommand {
    public Shuffle() {
        super("shuffle");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        if (CommonChecks.listeningMusic(event.getMember(), event.getGuild()) && CommonChecks.listeningMusic(event.getMember(), event.getGuild())) {
            VoiceController vc = PlayerStore.get(event.getGuild());
            if (vc.getTrackScheduler().shuffle()) {
                event.getHook().sendMessage("The queue has been shuffled!").queue();
            } else {
                event.getHook().sendMessage("There are no songs in the queue to shuffle.").queue();
            }
        } else {
            event.getHook().sendMessage("You need to be in a call listening to music to use this command!").queue();
        }
    }
}
