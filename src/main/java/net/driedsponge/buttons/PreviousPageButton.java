package net.driedsponge.buttons;

import net.driedsponge.QueueResponse;
import net.driedsponge.commands.music.Queue;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public final class PreviousPageButton extends ButtonCommand {
    public static final Button PREVIOUS_PAGE_BUTTON = Button.primary("PP","Previous Page");
    public PreviousPageButton() {
        super("PP");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        changePage(event,-1);
    }
    public static void changePage(ButtonInteractionEvent event,int dir){
        try {
            int currentPage = event.getComponent().getId().charAt(2) - '0';
            int page = currentPage + dir;
            QueueResponse queue = Queue.qResponse(event.getGuild(), page);
            event.getMessage().editMessageEmbeds(queue.embed()).queue();
            if(!queue.empty()){
                event.getMessage().editMessageComponents(ActionRow.of(ShuffleButton.SHUFFLE_BUTTON,
                        PreviousPageButton.PREVIOUS_PAGE_BUTTON.withId("PP"+page)
                                .withDisabled(page==queue.firstPage()),
                        NextPageButton.NEXT_PAGE_BUTTON.withId("NP"+page)
                                .withDisabled(page==queue.lastPage() || queue.lastPage() == 0))).queue();
                event.reply("Going to page "+page+"!").setEphemeral(true).queue();
            }else {
                event.reply("The queue is empty.").setEphemeral(true).queue();
                event.getMessage().editMessageComponents().queue();
            }
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }
}
