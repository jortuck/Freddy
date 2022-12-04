package net.driedsponge.commands;

import net.driedsponge.PlayerStore;
import net.driedsponge.Song;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
public class Restart extends SlashCommand {
    public Restart() {
        super("restart");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        if (CommonChecks.listeningMusic(event.getMember(), event.getGuild()) && CommonChecks.listeningMusic(event.getMember(), event.getGuild())) {
            VoiceController vc = PlayerStore.get(event.getGuild());
            Song np = vc.getNowPlaying();
            np.getTrack().setPosition(0L);
            event.getHook().sendMessage(":arrows_counterclockwise: Now playing **"+np.getInfo().title+"** from the beginning!").queue();
        } else {
            event.getHook().sendMessage("You need to be in a call listening to music to use this command!").queue();
        }
    }
}
