package net.driedsponge;

import io.sentry.Sentry;
import net.driedsponge.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Main {
    public static final String OWNER_ID = "283710670409826304";

    public static void main(String[] args) throws LoginException {

        intializeSentry();

        String token = System.getenv("DISCORD_TOKEN");

        JDABuilder builder = JDABuilder.createDefault(token);
        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.enableCache(CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Disable compression (not recommended)
        builder.setCompression(Compression.NONE);

        builder.setActivity(Activity.watching("Fergre"));

        builder.addEventListeners(new Ping());
        builder.addEventListeners(new JoinLeave());
        builder.addEventListeners(new UserVoiceEvents());
        builder.addEventListeners(new Play());
        builder.addEventListeners(new Pause());
        builder.addEventListeners(new Owner());
        builder.addEventListeners(new NowPlaying());
        builder.addEventListeners(new Queue());
        builder.addEventListeners(new Skip());
        builder.addEventListeners(new Help());
        builder.addEventListeners(new Bug());
        JDA jda = builder.build();

    }
    private static void intializeSentry(){
        Sentry.init(options -> {
            options.setDsn(System.getenv("SENTRY_DSN"));
            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.setTracesSampleRate(1.0);

        });

    }
}
