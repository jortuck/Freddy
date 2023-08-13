package net.driedsponge.commands.music;

import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.CommonChecks;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Skip extends SlashCommand {
    public Skip() {
        super("skip");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            String skip = skip(event.getMember(),event.getGuild());
            event.reply(skip).setEphemeral(true).queue();
        }catch (Exception e){
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    public static String skip(Member member, Guild guild) throws Exception{
        if(!CommonChecks.listeningMusic(member,guild)){
            throw new Exception("You need to be in a voice channel with the bot to skip.");
        }
        if(CommonChecks.playingMusic(guild)){
            VoiceController vc = PlayerStore.get(guild);
            if(vc.getTrackScheduler().getQueue().size() > 0){
                vc.skip(member);
                return "Skipping...";
            }else{
                vc.skip(member);
                return "No more songs left in the queue!";
            }
        }else{
            throw new Exception("Nothing to skip.");
        }

    }
}
