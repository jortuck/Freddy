package net.driedsponge.buttons;

import net.driedsponge.commands.util.Owner;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public final class Entertaining extends ButtonCommand{
    public static final Button ENTERTAINING_BUTTON = Button.primary("EN","Current Calls");

    public Entertaining(){
        super("EN");
    }
    @Override
    public void execute(ButtonInteractionEvent event){
        event.replyEmbeds(Owner.callList(event.getJDA()).build()).queue();
    }
}
