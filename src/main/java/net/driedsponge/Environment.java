package net.driedsponge;

public record Environment(String ownerID, String discordBotToken,
                          int queueLimit, String spotifyClientID,
                          String spotifyClientSecret)  {
}
