package net.driedsponge.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommonChecks {
    /**
     * Check if music is playing in a guild
     * @param guild The guild to check
     * @return Whether the bot is playing music in the guild or not
     */
    public static boolean playingMusic(Guild guild){
        return guild.getAudioManager().isConnected() && Play.PLAYERS.get(guild) != null;
    }

    /**
     * Check if the member is in the same call as the bot
     * @param member The member to check
     * @param guild The server to check
     * @return Whether the member is listening to music or not
     */
    public static boolean listeningMusic(Member member, Guild guild){
        return (member.getVoiceState().inVoiceChannel() && member.getVoiceState().getChannel() == guild.getAudioManager().getConnectedChannel());
    }
}
