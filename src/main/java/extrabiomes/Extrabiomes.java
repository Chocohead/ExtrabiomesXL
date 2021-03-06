/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes;

import java.io.File;
import java.util.Locale;

import com.google.common.base.Optional;

import extrabiomes.biomes.BiomeManagerImpl;
import extrabiomes.events.ModuleEvent.ModuleInitEvent;
import extrabiomes.events.ModulePreInitEvent;
import extrabiomes.handlers.BiomeHandler;
import extrabiomes.handlers.BlockHandler;
import extrabiomes.handlers.CanMobSpawnHandler;
import extrabiomes.handlers.ConfigurationHandler;
import extrabiomes.handlers.CropHandler;
import extrabiomes.handlers.EBXLCommandHandler;
import extrabiomes.handlers.ItemHandler;
import extrabiomes.handlers.RecipeHandler;
import extrabiomes.helpers.LogHelper;
import extrabiomes.lib.GeneralSettings;
import extrabiomes.lib.Reference;
import extrabiomes.module.fabrica.recipe.RecipeManager;
import extrabiomes.proxy.CommonProxy;
import extrabiomes.utility.CreativeTab;
import extrabiomes.utility.EnhancedConfiguration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = "")
public class Extrabiomes {

  @Instance(Reference.MOD_ID)
  public static Extrabiomes instance;

  @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
  public static CommonProxy proxy;

  public static final CreativeTabs tabsEBXL = new CreativeTab("extrabiomesTab");

  public static final String RESOURCE_PATH = Reference.MOD_ID.toLowerCase(Locale.ENGLISH);
  public static final String TEXTURE_PATH = RESOURCE_PATH + ':';

  private static Optional<EventBus> initBus = Optional.of(new EventBus());

  @Mod.EventHandler
  public static void preInit(FMLPreInitializationEvent event) throws Exception {
    LogHelper.info("Initializing.");

    MinecraftForge.EVENT_BUS.register(CanMobSpawnHandler.INSTANCE);

    // Handle upgrading
    File test = new File(event.getModConfigurationDirectory(), "/extrabiomes/extrabiomes.cfg");
    if (test.exists()) {
      ConfigurationHandler.init(test, true);

      File newFile = new File(event.getModConfigurationDirectory(), "/extrabiomes/oldunusedconfig.cfg");

      if (!newFile.exists()) {
        test.renameTo(newFile);
      }

      LogHelper.info("Upgrading Configfile");
    }

    final EnhancedConfiguration config = ConfigurationHandler.init(new File(event.getModConfigurationDirectory(), "/extrabiomes.cfg"), false);

    BiomeHandler.init();

    // remove after 3.6.0 release
    BiomeManagerImpl.populateAPIBiomes();
    new BiomeManagerImpl();

    Extrabiomes.registerInitEventHandler(new RecipeManager());

    BlockHandler.createBlocks();
    ItemHandler.createItems();
    //CropHandler.createCrops();

    //BiomeHandler.registerWorldGenerators(config);
    BiomeHandler.enableBiomes();
    BiomeManagerImpl.buildWeightedFloraLists();

    //Module.registerModules();
    Module.postEvent(new ModulePreInitEvent());

    // just in case anything else updated config settings
    config.save();
  }
  
  @Mod.EventHandler
  public static void init(FMLInitializationEvent event) throws InstantiationException, IllegalAccessException {
	  Module.postEvent(new ModuleInitEvent());
    proxy.registerRenderInformation();
  }

  @Mod.EventHandler
  public static void postInit(FMLPostInitializationEvent event) {
	  proxy.onPostInit();
    PluginManager.activatePlugins();
    RecipeHandler.init();
    initBus = Optional.absent();
    Module.releaseStaticResources();

    LogHelper.info("Successfully Loaded.");
  }

  public static boolean postInitEvent(Event event) {
    return initBus.isPresent() ? initBus.get().post(event) : false;
  }

  @Mod.EventHandler
  public void serverStart(FMLServerStartingEvent event) {
    if (GeneralSettings.consoleCommandsDisabled)
      return;
    event.registerServerCommand(new EBXLCommandHandler());
  }

  public static void registerInitEventHandler(Object target) {
    if (initBus.isPresent())
      initBus.get().register(target);
  }
}
