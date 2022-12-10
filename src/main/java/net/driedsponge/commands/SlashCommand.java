package net.driedsponge.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class SlashCommand {

    private final String name;
    private String[] alias;


    public SlashCommand(String name) {
        this.name = name;
        this.alias = new String[]{};
    }
    public SlashCommand(String[] names) {
        this.name = names[0];
        this.alias = names;
    }


    public String getName() {
        return this.name;
    }
    public String[] getAlias() {
        return this.alias;
    }


    public abstract void execute(SlashCommandInteractionEvent event);
}
