package net.driedsponge.buttons;

import net.driedsponge.commands.util.Owner;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Entertaining extends ButtonListener{
    public static final Button ENTERTAINING_BUTTON = Button.primary("Entertaining","Current Calls");

    public Entertaining(){
        super("Entertaining");
    }
    @Override
    public void execute(ButtonInteractionEvent event){
        event.replyEmbeds(Owner.callList(event.getJDA()).build()).queue();
    }
}
