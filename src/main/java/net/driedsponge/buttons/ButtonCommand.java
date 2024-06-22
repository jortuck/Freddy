package net.driedsponge.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

// TODO: Take a look a buttons, find a maybe better way to do them!
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
