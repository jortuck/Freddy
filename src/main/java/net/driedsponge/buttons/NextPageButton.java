package net.driedsponge.buttons;

import net.driedsponge.QueueResponse;
import net.driedsponge.commands.music.Queue;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

public class NextPageButton extends ButtonCommand {
    public static final Button NEXT_PAGE_BUTTON = Button.primary("NP","Next Page");
    public NextPageButton() {
        super("NP");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        PreviousPageButton.changePage(event,1);

    }

}
