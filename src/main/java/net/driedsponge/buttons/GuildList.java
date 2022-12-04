package net.driedsponge.buttons;

import net.driedsponge.commands.util.Owner;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class GuildList extends  ButtonListener{
    public static final Button GUILD_LIST_BUTTON = Button.primary("GuildList","Servers");

    public GuildList(){
        super("GuildList");
    }

    @Override
    public void execute(ButtonInteractionEvent event){
        event.replyEmbeds(Owner.guildList(event.getJDA()).build())
                .addActionRow(GUILD_LIST_BUTTON)
                .queue();
    }


}
