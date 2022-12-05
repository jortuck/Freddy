package net.driedsponge;

import net.driedsponge.buttons.*;
import net.driedsponge.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static final String OWNER_ID = System.getenv("OWNER_ID");

    public static void main(String[] args) throws LoginException, IOException, ParseException, SpotifyWebApiException {

        SpotifyLookup.clientCredentials_Sync();

        String token = System.getenv("DISCORD_TOKEN");

        JDABuilder builder = JDABuilder.createDefault(token);
        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.enableCache(CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Disable compression (not recommended)
        builder.setCompression(Compression.NONE);

        builder.setActivity(Activity.watching("for /help"));

        //Voice
        builder.addEventListeners(new UserVoiceEvents());

        //Commands
        builder.addEventListeners(new CommandListener());

        // Messages
        builder.addEventListeners(new MessageListener());

        // Buttons
        builder.addEventListeners(new ButtonListener());

        JDA jda = builder.build();

        Interactions.initialize(jda.updateCommands());
    }

}
