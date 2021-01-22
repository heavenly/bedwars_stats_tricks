package net.alfiesmith.bedwarsmod.api.model;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.EnumChatFormatting;

@RequiredArgsConstructor
public enum Rank {

  NONE(EnumChatFormatting.GRAY),
  VIP(EnumChatFormatting.GREEN),
  VIP_PLUS(EnumChatFormatting.GREEN),
  MVP(EnumChatFormatting.AQUA),
  MVP_PLUS(EnumChatFormatting.AQUA),
  MVP_PLUS_PLUS(EnumChatFormatting.GOLD),
  OTHER(EnumChatFormatting.RED);

  @Getter
  private final EnumChatFormatting color;

  public static Rank fromApiRank(JsonObject player) {
    if (player.has("monthlyPackageRank") && player.get("monthlyPackageRank").getAsString()
        .equals("SUPERSTAR")) {
      return MVP_PLUS_PLUS;
    }

    if (!player.has("newPackageRank")) {
      return NONE;
    } else {
      switch (player.get("newPackageRank").getAsString()) {
        case "NORMAL":
        case "NONE":
          return NONE;
        case "VIP":
          return VIP;
        case "VIP_PLUS":
          return VIP_PLUS;
        case "MVP":
          return MVP;
        case "MVP_PLUS":
          return MVP_PLUS;
        default:
          return OTHER;
      }
    }
  }
}
