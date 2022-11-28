package net.driedsponge.commands;

import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Skip extends GuildCommand {
    public Skip() {
        super("skip");
    }

    @Override
    public void execute(SlashCommandEvent event) {

        if(!CommonChecks.listeningMusic(event.getMember(),event.getGuild())){
            event.reply("You need to be in a voice channel with the bot to skip.").setEphemeral(true).queue();
            return;
        }
        if(CommonChecks.playingMusic(event.getGuild())){
            VoiceController vc = PlayerStore.get(event.getGuild());
            event.reply(":fast_forward: Skipping **"+vc.getNowPlaying().getInfo().title+"**").complete();
            vc.skip();
        }else{
            event.reply("Nothing to skip.").setEphemeral(true).queue();
        }
    }
}
