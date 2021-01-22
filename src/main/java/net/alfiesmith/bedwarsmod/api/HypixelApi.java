package net.alfiesmith.bedwarsmod.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import net.alfiesmith.bedwarsmod.api.model.HypixelPlayer;

public final class HypixelApi {

  private static final String API_BASE = "https://api.hypixel.net/";

  private final JsonParser parser;
  private final String apiKey;

  public HypixelApi(String apiKey) {
    this.parser = new JsonParser();
    this.apiKey = apiKey;
  }

  public HypixelPlayer getPlayer(UUID uuid) {
    String endpoint = API_BASE + "player";
    JsonObject response = query(endpoint, "uuid", formatUuid(uuid));
    if (response != null && response.get("success").getAsBoolean()
        && response.has("player") && response.get("player").isJsonObject()) {
      return HypixelPlayer.fromPlayerObject(response.get("player").getAsJsonObject());
    } else {
      return HypixelPlayer.empty();
    }
  }

  private JsonObject query(String endpoint, String... params) {
    StringBuilder endpointBuilder = new StringBuilder(endpoint);

    endpointBuilder.append("?key=").append(this.apiKey);

    for (int i = 0; i < params.length; i += 2) {
      endpointBuilder.append("&").append(params[i]).append("=").append(params[i + 1]);
    }

    try {
      URL url = new URL(endpointBuilder.toString());
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(1000);

      try (InputStream inputStream = connection.getInputStream();
          InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
        return this.parser.parse(inputStreamReader).getAsJsonObject();
      }
    } catch (IOException err) {
      err.printStackTrace();
    }

    return null;
  }

  private static String formatUuid(UUID uuid) {
    return uuid.toString().replace("-", "");
  }
}
