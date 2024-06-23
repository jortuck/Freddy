package net.driedsponge.commands.music;

import net.driedsponge.*;
import net.driedsponge.commands.CommonChecks;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
public final class Restart extends SlashCommand {
    public Restart() {
        super("restart");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Player player = Player.get(event.getGuild().getId());
        if(player!=null){
            if(player.getVoiceChannel().asVoiceChannel() == event.getMember().getVoiceState().getChannel()){
                player.seek(0L);
                event.replyEmbeds(Embeds.basic(
                        ":arrows_counterclockwise: Now playing "+player.getNowPlaying().getInfo().title+" from the beginning!"
                ).build()).queue();
            }else{
                event.reply("You must be in the same channel as me to restart the song!").setEphemeral(true).queue();
            }
        }else {
            event.reply("There is nothing playing right now.").setEphemeral(true).queue();
        }
    }
}
