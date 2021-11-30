package net.driedsponge.commands;

import net.dv8tion.jda.api.entities.Guild;
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
}
