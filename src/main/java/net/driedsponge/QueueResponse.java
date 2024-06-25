package net.driedsponge;

import net.dv8tion.jda.api.entities.MessageEmbed;

public record QueueResponse(MessageEmbed embed, boolean empty, int firstPage, int lastPage,
                            int currentPage) {
}
