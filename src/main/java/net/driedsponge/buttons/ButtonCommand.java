package net.driedsponge.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class ButtonCommand {
    private final String name;


    public ButtonCommand(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }


    public abstract void execute(ButtonInteractionEvent event);
}
