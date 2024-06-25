package net.driedsponge.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public final class NextPageButton extends ButtonCommand {
    public static final Button NEXT_PAGE_BUTTON = Button.primary("NP","Next Page");
    public NextPageButton() {
        super("NP");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        PreviousPageButton.changePage(event,1);

    }

}
