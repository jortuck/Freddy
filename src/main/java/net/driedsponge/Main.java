package net.driedsponge;

import net.driedsponge.commands.JoinLeaveCommand;
import net.driedsponge.commands.PingCommand;
import net.driedsponge.commands.PlayCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(args[0]);

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.enableCache(CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Disable compression (not recommended)
        builder.setCompression(Compression.NONE);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.watching("Fergre"));

        builder.addEventListeners(new PingCommand());
        builder.addEventListeners(new JoinLeaveCommand());
        builder.addEventListeners(new UserVoiceEvents());
        builder.addEventListeners(new PlayCommand());

        JDA jda = builder.build();

        /*
        jda.upsertCommand("ping","Check the bots ping.").queue();
        jda.upsertCommand("join","Tells the bot to join your current voice channel.").queue();
        jda.upsertCommand("leave","Tells the bot to leave the current voice channel.").queue();
        jda.upsertCommand("play","Tell the bot to play a song.")
                .addOption(OptionType.STRING,"song","The name of the song to play.",true).queue();
        */



    }
}
