package net.driedsponge;

import ch.qos.logback.core.Appender;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidWithThumbnail;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import dev.lavalink.youtube.clients.skeleton.Client;
import net.driedsponge.buttons.*;
import net.driedsponge.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static final String OWNER_ID = System.getenv("OWNER_ID");
    public static final Color PRIMARY_COLOR = Color.MAGENTA;
    public static AudioPlayerManager PLAYER_MANAGER;
    public static final int QUEUE_LIMIT = 500;
    private static final List<String> allowedHosts = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws LoginException, IOException, ParseException, SpotifyWebApiException {

        logger.info("Starting bot...");

        // Register player manager
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true,
                new MusicWithThumbnail(), new AndroidWithThumbnail(), new WebWithThumbnail());
        playerManager.registerSourceManager(youtube);
        PLAYER_MANAGER = playerManager;
        logger.info("Registered player manager with YouTube source.");

        allowedHosts.add("youtube.com");
        allowedHosts.add("www.youtube.com");
        allowedHosts.add("youtu.be");
        allowedHosts.add("music.youtube.com");
        allowedHosts.add("open.spotify.com");

        String token = System.getenv("DISCORD_TOKEN");

        JDABuilder builder = JDABuilder.createDefault(token);
        // Manage parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.enableCache(CacheFlag.VOICE_STATE);
        // Disable compression (not recommended)
        builder.setCompression(Compression.NONE);

        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES);

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

    public static List<String> getAllowedHosts(){
        return Collections.unmodifiableList(allowedHosts);
    }
}
