package net.driedsponge.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.Array;
import java.util.Arrays;

public class GuildCommand extends ListenerAdapter {
    private String name;
    private String[] alias;

    public GuildCommand(String name){
        this.name = name;
        this.alias = new String[]{};
    }
    public GuildCommand(String[] strings){
        this.alias = strings;
    }
    public void onSlashCommand(SlashCommandEvent event) {
        if(event.getName().equals(this.name) || Arrays.asList(this.alias).contains(event.getName())){
            if(event.isFromGuild()){
                execute(event);
            }else{
                event.reply("Please use this command in a server. It does not work in direct messages.").setEphemeral(true).queue();
            }
        }

    }

    public void execute(SlashCommandEvent event){}
}
