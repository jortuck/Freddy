package net.driedsponge.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// import javax.annotation.Nonnull;

public class ButtonListener extends ListenerAdapter {
    public String name;

    public ButtonListener(String name){
        this.name = name;
    }
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getComponent().getId().equals(this.name)) {
            this.execute(event);
        }
    }

    public void execute(ButtonInteractionEvent event){}
}
