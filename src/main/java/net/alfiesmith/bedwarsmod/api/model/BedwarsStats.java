package net.alfiesmith.bedwarsmod.api.model;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.util.EnumChatFormatting;

@Getter
public class BedwarsStats implements Comparable<BedwarsStats> {

  private static final BedwarsStats EMPTY = new BedwarsStats(0, 0, 0, 0, 0, 0, 0, 0, 0);

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

    int level = achievements == null ? 1 : achievements.get("bedwars_level").getAsInt();
    int gamesPlayed = stats.get("games_played_bedwars").getAsInt();
    int kills = stats.get("kills_bedwars").getAsInt();
    int deaths = stats.get("deaths_bedwars").getAsInt();
    int finalKills = stats.get("final_kills_bedwars").getAsInt();
    int finalDeaths = stats.get("final_deaths_bedwars").getAsInt();
    int wins = stats.get("wins_bedwars").getAsInt();
    int losses = stats.get("losses_bedwars").getAsInt();
    int winstreak = stats.get("winstreak").getAsInt();
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

}
