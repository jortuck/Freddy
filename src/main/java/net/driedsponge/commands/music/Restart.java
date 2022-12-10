package net.driedsponge.commands.music;

import net.driedsponge.PlayerStore;
import net.driedsponge.Song;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.CommonChecks;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
public class Restart extends SlashCommand {
    public Restart() {
        super("restart");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (CommonChecks.listeningMusic(event.getMember(), event.getGuild()) && CommonChecks.listeningMusic(event.getMember(), event.getGuild())) {
            VoiceController vc = PlayerStore.get(event.getGuild());
            Song np = vc.getNowPlaying();
            np.getTrack().setPosition(0L);
            event.reply(":arrows_counterclockwise: Now playing **"+np.getInfo().title+"** from the beginning!").queue();
        } else {
            event.reply("You need to be in a call listening to music to use this command!").setEphemeral(true).queue();
        }
    }
}
