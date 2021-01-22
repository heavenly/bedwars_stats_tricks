package net.alfiesmith.bedwarsmod;

import java.io.File;
import net.alfiesmith.bedwarsmod.api.HypixelApi;
import net.alfiesmith.bedwarsmod.command.StarsCommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = BedwarsMod.MOD_ID,
    version = BedwarsMod.MOD_VERSION,
    clientSideOnly = true
)
public class BedwarsMod {

  public static final String MOD_ID = "bedwarsmod";
  public static final String MOD_VERSION = "1.0";

  private HypixelApi api;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    File file = event.getSuggestedConfigurationFile();
    Configuration config = new Configuration(file);
    config.load();

    Property key = config.get(
        Configuration.CATEGORY_CLIENT,
        "api-key",
        "key",
        "Run /api on Hypixel and paste that key here"
    );

    if (config.hasChanged()) {
      config.save();
    }

    this.api = new HypixelApi(key.getString());
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    ClientCommandHandler.instance.registerCommand(new StarsCommand(this.api));
  }
}
