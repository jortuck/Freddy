package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;import net.dv8tion.jda.api.managers.AudioManager;

public final class Pause extends SlashCommand {

    public static final Pause INSTANCE = new Pause();

    private Pause() {
        super(new String[]{"pause", "resume"});
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState state = member.getVoiceState();
        Player player = Player.get(event.getGuild().getId());
        if (player!=null && player.getNowPlaying() != null) {
            if (state.getChannel() == player.getVoiceChannel()) {
                try{
                    player.setPaused(!event.getName().equals("resume"));
                    event.replyEmbeds(
                            Embeds.basic(
                                    event.getName().equals("resume") ? "Resuming playback!" : "Pausing playback!"
                            ).build()
                    ).queue();
                }catch (IllegalArgumentException e){
                    event.reply(e.getMessage()).setEphemeral(true).queue();
                }
            } else {
                event.reply("You must be in the same channel as me to pause/resume.").setEphemeral(true).queue();
            }
        } else {
            event.reply("I am not playing anything.").setEphemeral(true).queue();
        }
    }
}
