package net.alfiesmith.bedwarsmod.api.model;

import com.google.gson.JsonObject;
import lombok.Getter;

@Getter
public class HypixelPlayer {

  private static final HypixelPlayer EMPTY = new HypixelPlayer(null, Rank.NONE,
      0, BedwarsStats.empty());

  private final String displayName;
  private final Rank rank;
  private final int networkLevel;
  private final BedwarsStats bedwarsStats;

  private HypixelPlayer(String displayName, Rank rank, int networkLevel,
      BedwarsStats bedwarsStats) {
    this.displayName = displayName;
    this.rank = rank;
    this.networkLevel = networkLevel;
    this.bedwarsStats = bedwarsStats;
  }

  public static HypixelPlayer fromPlayerObject(JsonObject player) {
    String displayName = player.get("displayname").getAsString();
    Rank rank = Rank.fromApiRank(player);
    int networkLevel = getLevelFromExp(player.get("networkExp").getAsInt());

    JsonObject stats = player.get("stats").getAsJsonObject();
    JsonObject achievements = player.get("achievements").getAsJsonObject();

    BedwarsStats bedwarsStats;
    if (stats.has("Bedwars")) {
       bedwarsStats = BedwarsStats.fromStats(achievements, stats.get("Bedwars").getAsJsonObject());
    } else {
      bedwarsStats = BedwarsStats.empty();
    }

    return new HypixelPlayer(displayName, rank, networkLevel, bedwarsStats);
  }

  public static HypixelPlayer empty() {
    return EMPTY;
  }

  public static boolean isEmpty(HypixelPlayer player) {
    return player == EMPTY;
  }

  // Taken from:
  // https://github.com/HypixelDev/PublicAPI/blob/master/Java/src/main/java/net/hypixel/api/util/ILeveling.java
  private static final double BASE = 10_000;
  private static final double REVERSE_PQ_PREFIX = -(BASE - 0.5 * 2_500) / 2_500;
  private static final double REVERSE_CONST = REVERSE_PQ_PREFIX * REVERSE_PQ_PREFIX;
  private static final double GROWTH_DIVIDES_2 = 1 / 1_250.0;

  public static int getLevelFromExp(int exp) {
    return exp < 0 ? 1 :
        (int) Math.floor(1 + REVERSE_PQ_PREFIX + Math.sqrt(REVERSE_CONST + GROWTH_DIVIDES_2 * exp));
  }

}
