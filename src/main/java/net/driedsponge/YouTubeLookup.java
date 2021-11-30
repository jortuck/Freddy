package net.driedsponge;


import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.gson.Gson;
import net.driedsponge.commands.VideoObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class YouTubeLookup {
    private static final String DEVELOPER_KEY = System.getenv("YOUTUBE_API_KEY");

    private static final String APPLICATION_NAME = "DriedSponge";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Gets a url for a search query
     * @return The url for the video
     */
    public static String GetAUrl(String query) throws IOException, GeneralSecurityException, NoSuchFieldException {
        System.out.println("Searching YouTube for "+query);

        YouTube yt = getService();
        // Define and execute the API request
        List<String> list = new ArrayList<>();
        YouTube.Search.List request = yt.search().list( list);

        SearchListResponse response = request.setKey(DEVELOPER_KEY)
                .setQ(query)
                .setType(List.of("video"))
                .execute();

        Gson json = new Gson();
        VideoObject o = json.fromJson(response.getItems().get(0).toString(), VideoObject.class);
        String videoId = o.getId().videoId;
        return "https://www.youtube.com/watch?v="+videoId;
    }
    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
