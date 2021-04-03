package net.alfiesmith.bedwarsmod.api.model;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.util.EnumChatFormatting;

@Getter
public class BedwarsStats implements Comparable<BedwarsStats> {

  private static final BedwarsStats EMPTY = new BedwarsStats(1, 0, 0, 0, 0, 0, 0, 0, 0);

  private final int level;
  private final EnumChatFormatting levelColor;
  private final int gamesPlayed;
  private final int kills;
  private final int deaths;
  private final int finalKills;
  private final int finalDeaths;
  private final int wins;
  private final int losses;
  private final int winstreak;
  private final double winLossRatio;
  private final double killDeathRatio;
  private final double finalKillDeathRatio;
  public final double scary_index;

  public BedwarsStats(int level, int gamesPlayed, int kills, int deaths, int finalKills,
      int finalDeaths, int wins, int losses, int winstreak) {
    this.level = level;
    this.levelColor = BedwarsStats.getLevelColour(level);
    this.gamesPlayed = gamesPlayed;
    this.kills = kills;
    this.deaths = deaths;
    this.finalKills = finalKills;
    this.finalDeaths = finalDeaths;
    this.wins = wins;
    this.losses = losses;
    this.winstreak = winstreak;
    this.winLossRatio = wins / (losses == 0 ? 1 : losses * 1.0);
    this.killDeathRatio = kills / (deaths == 0 ? 1 : deaths * 1.0);
    this.finalKillDeathRatio = finalKills / (finalDeaths == 0 ? 1 : finalDeaths * 1.0);
    this.scary_index = (this.level * this.finalKillDeathRatio * this.finalKillDeathRatio) / 10;
  }

  @Override
  public int compareTo(BedwarsStats o) {
    if (this.level != o.level) {
      return this.level - o.level;
    }

    return this.gamesPlayed - o.gamesPlayed;
  }

  public static BedwarsStats fromStats(JsonObject achievements, JsonObject stats) {
    if (stats == null) {
      return empty();
    }

    int level = achievements.get("bedwars_level").getAsInt();
    if (!stats.has("games_played_bedwars") || stats.get("games_played_bedwars").getAsInt() == 0) {
      return empty();
    }

    int gamesPlayed = stats.get("games_played_bedwars").getAsInt();
    int kills = stats.has("kills_bedwars") ? stats.get("kills_bedwars").getAsInt() : 0;
    int deaths = stats.has("deaths_bedwars") ? stats.get("deaths_bedwars").getAsInt() : 0;
    int finalKills =
        stats.has("final_kills_bedwars") ? stats.get("final_kills_bedwars").getAsInt() : 0;
    int finalDeaths =
        stats.has("final_deaths_bedwars") ? stats.get("final_deaths_bedwars").getAsInt() : 0;
    int wins = stats.has("wins_bedwars") ? stats.get("wins_bedwars").getAsInt() : 0;
    int losses = stats.has("losses_bedwars") ? stats.get("losses_bedwars").getAsInt() : 0;
    int winstreak = stats.has("winstreak") ? stats.get("winstreak").getAsInt() : 0;
    return new BedwarsStats(level, gamesPlayed, kills, deaths, finalKills, finalDeaths, wins,
        losses, winstreak);
  }

  public static EnumChatFormatting getLevelColour(int level) {
    if (level < 100) {
      return EnumChatFormatting.GRAY;
    } else if (level < 200) {
      return EnumChatFormatting.WHITE;
    } else if (level < 300) {
      return EnumChatFormatting.AQUA;
    } else if (level < 400) {
      return EnumChatFormatting.DARK_GREEN;
    } else if (level < 500) {
      return EnumChatFormatting.BLUE;
    } else if (level < 600) {
      return EnumChatFormatting.RED;
    } else if (level < 700) {
      return EnumChatFormatting.LIGHT_PURPLE;
    } else if (level < 800) {
      return EnumChatFormatting.DARK_BLUE;
    } else if (level < 900) {
      return EnumChatFormatting.DARK_PURPLE;
    } else {
      return EnumChatFormatting.YELLOW;
    }
  }

  public static BedwarsStats empty() {
    return EMPTY;
  }
  public static boolean is_empty(BedwarsStats comp) {
    return comp == EMPTY;
  }
}
