package net.driedsponge.commands.music;

import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public final class Clear extends SlashCommand {

    public static final Clear INSTANCE = new Clear();

    private Clear(){
        super("clear");
    }
    @Override

    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState state = member.getVoiceState();
        if (Player.contains(event.getGuild().getId())) {
            Player player = Player.get(event.getGuild().getId());
            if (state.inAudioChannel() && state.getChannel() == player.getVoiceChannel()) {
                player.clearQueue();
                event.reply("The queue has been cleared!").queue();
            } else {
                event.reply("You must be in the same channel as me to clear the queue.").setEphemeral(true).queue();
            }
        } else {
            event.reply("I am not connected to any voice channel").setEphemeral(true).queue();
        }
    }
}
