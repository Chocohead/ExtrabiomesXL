/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.handlers;

import java.util.Collection;
import java.util.Locale;

import extrabiomes.Extrabiomes;
import extrabiomes.blocks.BlockCatTail;
import extrabiomes.blocks.BlockCustomFlower;
import extrabiomes.blocks.BlockCustomFlower.BlockType;
import extrabiomes.blocks.BlockCustomSapling;
import extrabiomes.blocks.BlockCustomTallGrass;
import extrabiomes.blocks.BlockCustomTallGrass.TallGrassType;
import extrabiomes.blocks.BlockCustomVine;
import extrabiomes.blocks.BlockCustomVine.VineType;
import extrabiomes.blocks.BlockEBXLLeaves;
import extrabiomes.blocks.BlockEBXLLog;
import extrabiomes.blocks.BlockKneeLog;
import extrabiomes.blocks.BlockLeafPile;
import extrabiomes.blocks.BlockMiniLog;
import extrabiomes.blocks.BlockNewQuarterLog;
import extrabiomes.blocks.BlockNewSapling;
import extrabiomes.blocks.BlockQuarterLog;
import extrabiomes.blocks.BlockWaterPlant;
import extrabiomes.blocks.GenericTerrainBlock;
import extrabiomes.blocks.GenericTerrainBlock.TerrainBlockType;
import extrabiomes.events.BlockActiveEvent.RedRockActiveEvent;
import extrabiomes.helpers.BiomeHelper;
import extrabiomes.helpers.ForestryModHelper;
import extrabiomes.helpers.LogHelper;
import extrabiomes.items.ItemEBXLLeaves;
import extrabiomes.items.ItemGrass;
import extrabiomes.items.ItemKneeLog;
import extrabiomes.items.ItemNewQuarterLog;
import extrabiomes.items.ItemTerrainBlock;
import extrabiomes.lib.BiomeSettings;
import extrabiomes.lib.BlockSettings;
import extrabiomes.lib.Element;
import extrabiomes.lib.GeneralSettings;
import extrabiomes.lib.ILeafSerializable;
import extrabiomes.lib.IMetaSerializable;
import extrabiomes.lib.IQuarterSerializable;
import extrabiomes.lib.ModuleControlSettings;
import extrabiomes.module.summa.worldgen.CatTailGenerator;
import extrabiomes.module.summa.worldgen.EelGrassGenerator;
import extrabiomes.module.summa.worldgen.FlowerGenerator;
import extrabiomes.module.summa.worldgen.LeafPileGenerator;
import extrabiomes.module.summa.worldgen.VineGenerator;
import extrabiomes.module.summa.worldgen.WorldGenCustomTallGrass;
import extrabiomes.proxy.CommonProxy;
import extrabiomes.renderers.RenderKneeLog;
import extrabiomes.renderers.RenderMiniLog;
import extrabiomes.renderers.RenderNewQuarterLog;
import extrabiomes.renderers.RenderQuarterLog;
import extrabiomes.subblocks.SubBlockWaterPlant;
import extrabiomes.utility.MultiItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.feature.WorldGenTallGrass;

public abstract class BlockHandler {
	public static void createBlocks() throws Exception {
		LogHandler.createLogs();
		LeafHandler.createLeaves();

		//createCattail();
		createTerrainBlocks();
		//createFlowers();
		createGrass();
		createVines();
		//createWaterPlants();
	}

	private static void createWaterPlants() throws Exception {
		if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.WATERPLANT.getEnabled())
			return;

		final BlockWaterPlant waterPlantBlock = new BlockWaterPlant(BlockSettings.WATERPLANT, "waterPlant");

		waterPlantBlock.setBlockName("extrabiomes.waterplant").setHardness(0.01F).setStepSound(Block.soundTypeGrass)
				.setCreativeTab(Extrabiomes.tabsEBXL);

		// Add the subblocks
		waterPlantBlock.registerSubBlock(new SubBlockWaterPlant("eelgrass").addPlaceableBlock(Blocks.GRASS)
				.addPlaceableBlock(Blocks.SAND).addPlaceableBlock(Blocks.GRAVEL).addPlaceableBlock(Blocks.CLAY), 0);

		final CommonProxy proxy = Extrabiomes.proxy;
		proxy.registerBlock(waterPlantBlock, extrabiomes.items.ItemBlockWaterPlant.class, "waterplant1");

		Element.WATERPLANT.set(new ItemStack(waterPlantBlock, 1, 0));

		proxy.registerWorldGenerator(new EelGrassGenerator(waterPlantBlock, 0));
	}

	private static void createCattail() {
		if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.CATTAIL.getEnabled())
			return;

		final BlockCatTail block = new BlockCatTail(79, Material.plants);
		block.setBlockName("extrabiomes.cattail").setHardness(0.0F).setStepSound(Block.soundTypeGrass)
				.setCreativeTab(Extrabiomes.tabsEBXL);

		final CommonProxy proxy = Extrabiomes.proxy;
		proxy.registerBlock(block, extrabiomes.items.ItemCatTail.class, "plants4");
		proxy.registerOre("reedTypha", block);

		Element.CATTAIL.set(new ItemStack(block));

		proxy.registerWorldGenerator(new CatTailGenerator(block));
	}

	private static void createTerrainBlocks() {
		if (ModuleControlSettings.SUMMA.isEnabled()) {
			boolean crackedSand = BlockSettings.CRACKEDSAND.getEnabled();
			boolean redrock = BlockSettings.REDROCK.getEnabled();

			if (!crackedSand && !redrock) return;

			final GenericTerrainBlock block = new GenericTerrainBlock();
			block.setUnlocalizedName("extrabiomes.terrain");

			final CommonProxy proxy = Extrabiomes.proxy;
			proxy.setBlockHarvestLevel(block, "pickaxe", 0);
			proxy.registerBlock(block, ItemTerrainBlock.class, "terrain_blocks");

			if (crackedSand) {
				ItemStack stack = new ItemStack(block, 1, TerrainBlockType.CRACKED_SAND.getMetadata());

				proxy.registerOre("sandCracked", stack);
				Element.CRACKEDSAND.set(stack);

				IBlockState state = block.getStateFromMeta(TerrainBlockType.CRACKED_SAND.getMetadata());
				BiomeHelper.addTerrainBlockstoBiome(BiomeSettings.WASTELAND, state, state);
			}

			if (redrock) {
				Element.RED_ROCK.set(new ItemStack(block, 1, TerrainBlockType.RED_ROCK.getMetadata()));
			    Element.RED_COBBLE.set(new ItemStack(block, 1, TerrainBlockType.RED_COBBLE.getMetadata()));
			    Element.RED_ROCK_BRICK.set(new ItemStack(block, 1, TerrainBlockType.RED_ROCK_BRICK.getMetadata()));
			
			    Extrabiomes.postInitEvent(new RedRockActiveEvent(block));
			    
			    IBlockState state = block.getStateFromMeta(TerrainBlockType.RED_ROCK.getMetadata());
			    BiomeHelper.addTerrainBlockstoBiome(BiomeSettings.MOUNTAINRIDGE, state, state);
			}

			ForestryModHelper.addToDiggerBackpack(new ItemStack(block, 1, Short.MAX_VALUE));
			for (final TerrainBlockType type : TerrainBlockType.values()) {
				// FacadeHelper.addBuildcraftFacade(block, type.metadata());
				LogHelper.fine("Successfully built block for %s", type.getName());
			}
		}
	}

	private static void createFlowers() {
		if (!ModuleControlSettings.SUMMA.isEnabled())
			return;

		final boolean enableds[] = { BlockSettings.FLOWER.getEnabled(), BlockSettings.FLOWER2.getEnabled(),
				BlockSettings.FLOWER3.getEnabled() };

		final CommonProxy proxy = Extrabiomes.proxy;
		final FlowerGenerator generator = FlowerGenerator.getInstance();

		for (int group = 0; group < enableds.length; ++group) {
			if (!enableds[group])
				continue;

			final BlockCustomFlower block = new BlockCustomFlower(group, Material.plants);
			block.setBlockName("extrabiomes.flower").setTickRandomly(true).setHardness(0.0F)
					.setStepSound(Block.soundTypeGrass).setCreativeTab(Extrabiomes.tabsEBXL);
			proxy.registerBlock(block, extrabiomes.items.ItemFlower.class, "flower" + (group + 1));

			Collection<BlockType> types = block.getGroupTypes();
			for (BlockType type : types) {
				final Element element;
				try {
					element = Element.valueOf(type.name());
				} catch (Exception e) {
					LogHelper.warning("No element found for flower " + type);
					continue;
				}
				type.setBlock(block);
				ItemStack item = new ItemStack(block, 1, type.metadata());
				element.set(item);
				ForestryModHelper.registerBasicFlower(item);
			}

			generator.registerBlock(block, types);
			ForestryModHelper.addToForesterBackpack(new ItemStack(block, 1, Short.MAX_VALUE));
		}

		proxy.registerWorldGenerator(generator);
	}

	private static void createVines() {
		if (!ModuleControlSettings.SUMMA.isEnabled())
			return;

		final CommonProxy proxy = Extrabiomes.proxy;

		// BlockCustomVine.BlockType[] vines =
		// BlockCustomVine.BlockType.values();
		VineType[] vines = { VineType.GLORIOSA };

		for (VineType blockType : vines) {
			final BlockSettings settings;
			try {
				settings = BlockSettings.valueOf(blockType.name());
			} catch (Exception e) {
				LogHelper.severe("Unable to find settings for " + blockType);
				continue;
			}

			if (!settings.getEnabled())
				continue;

			final BlockCustomVine block = new BlockCustomVine(blockType);
			block.setUnlocalizedName("extrabiomes.vine." + blockType.toString());
			proxy.registerBlock(block, "vines");

			final Element element;
			try {
				element = Element.valueOf("VINE_" + blockType.name());
			} catch (Exception e) {
				LogHelper.warning("No element found for vine " + blockType);
				continue;
			}
			element.set(new ItemStack(block));

			ForestryModHelper.addToForesterBackpack(new ItemStack(block, 1, Short.MAX_VALUE));

			final VineGenerator generator;
			// gloriosa gets a biome list override
			if (blockType == VineType.GLORIOSA) {
				final BiomeSettings[] biomeList = { BiomeSettings.EXTREMEJUNGLE, BiomeSettings.MINIJUNGLE,
						BiomeSettings.RAINFOREST };
				generator = new VineGenerator(block, biomeList);
			} else {
				generator = new VineGenerator(block);
			}
			proxy.registerWorldGenerator(generator);
		}
	}

	private static void createGrass() {
		if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.GRASS.getEnabled())
			return;

		final BlockCustomTallGrass block = new BlockCustomTallGrass();
		block.setUnlocalizedName("extrabiomes.tallgrass");

		final CommonProxy proxy = Extrabiomes.proxy;
		proxy.registerBlock(block, ItemGrass.class, "grass");
		Blocks.FIRE.setFireInfo(block, 60, 100);

		Element.GRASS_BROWN.set(new ItemStack(block, 1, TallGrassType.BROWN.getMetadata()));
		Element.GRASS_DEAD.set(new ItemStack(block, 1, TallGrassType.DEAD.getMetadata()));
		Element.GRASS_BROWN_SHORT.set(new ItemStack(block, 1, TallGrassType.SHORT_BROWN.getMetadata()));
		Element.GRASS_DEAD_TALL.set(new ItemStack(block, 1, TallGrassType.DEAD_TALL.getMetadata()));
		Element.GRASS_DEAD_YELLOW.set(new ItemStack(block, 1, TallGrassType.DEAD_YELLOW.getMetadata()));

		BiomeHelper.addWeightedGrassGen(BiomeSettings.MOUNTAINRIDGE.getBiome(),
				new WorldGenCustomTallGrass(block.withType(TallGrassType.BROWN)), 100);
		
		BiomeHelper.addWeightedGrassGen(BiomeSettings.MOUNTAINRIDGE.getBiome(),
				new WorldGenCustomTallGrass(block.withType(TallGrassType.SHORT_BROWN)), 100);

		BiomeHelper.addWeightedGrassGen(BiomeSettings.WASTELAND.getBiome(),
				new WorldGenCustomTallGrass(block.withType(TallGrassType.DEAD)), 90);
		
		BiomeHelper.addWeightedGrassGen(BiomeSettings.WASTELAND.getBiome(),
				new WorldGenCustomTallGrass(block.withType(TallGrassType.DEAD_YELLOW)), 90);
		
		BiomeHelper.addWeightedGrassGen(BiomeSettings.WASTELAND.getBiome(),
				new WorldGenCustomTallGrass(block.withType(TallGrassType.DEAD_TALL)), 35);
	}

	public static final class LeafHandler {
		public static enum Autumn_Leaf_Types implements ILeafSerializable {
			UMBER(0), GOLDENROD(1), VERMILLION(2), CITRINE(3);

			private final String name;
			private final int metadata;
			private ItemStack sapling = new ItemStack(Blocks.SAPLING);

			private Autumn_Leaf_Types(int metadata) {
				this.name = name().toLowerCase(Locale.ENGLISH);
				this.metadata = metadata;
			}
			
			@Override
			public String getName() {
				return name;
			}

			@Override
			public int getMetadata() {
				return metadata;
			}
			
			@Override
			public Block getSaplingBlock() {
				return Block.getBlockFromItem(sapling.getItem());
			}

			@Override
			public int getSaplingMetadata() {
				return sapling.getItemDamage();
			}
			
			@Override
			public int getSaplingDropChance() {
				return 20;
			}
			
			@Override
			public boolean canDropApples() {
				return true;
			}
		}
		
		public static enum New_Leaf_Types implements ILeafSerializable {
			BALD_CYPRESS(0) {
				@Override
				public int getSaplingDropChance() {
					if (GeneralSettings.bigTreeSaplingDropModifier) {
						return 90;
					} else {
						return super.getSaplingDropChance();
					}
				}
			}, JAPANESE_MAPLE(1), JAPANESE_MAPLE_SHRUB(2), RAINBOW_EUCALYPTUS(3) {
				@Override
				public int getSaplingDropChance() {
					if (GeneralSettings.bigTreeSaplingDropModifier) {
						return 90;
					} else {
						return super.getSaplingDropChance();
					}
				}
			};

			private final String name;
			private final int metadata;
			private ItemStack sapling = new ItemStack(Blocks.SAPLING);

			private New_Leaf_Types(int metadata) {
				this.name = name().toLowerCase(Locale.ENGLISH);
				this.metadata = metadata;
			}

			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public int getMetadata() {
				return metadata;
			}
			
			@Override
			public Block getSaplingBlock() {
				return Block.getBlockFromItem(sapling.getItem());
			}

			@Override
			public int getSaplingMetadata() {
				return sapling.getItemDamage();
			}
			
			@Override
			public int getSaplingDropChance() {
				return 20;
			}

			@Override
			public boolean canDropApples() {
				return false;
			}
		}

		public static enum More_Leaf_Types implements ILeafSerializable {
			SAKURA_BLOSSOM(0);

			private final String name;
			private final int metadata;
			private ItemStack sapling = new ItemStack(Blocks.SAPLING);

			private More_Leaf_Types(int metadata) {
				this.name = name().toLowerCase(Locale.ENGLISH);
				this.metadata = metadata;
			}
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public int getMetadata() {
				return metadata;
			}
			
			@Override
			public Block getSaplingBlock() {
				return Block.getBlockFromItem(sapling.getItem());
			}

			@Override
			public int getSaplingMetadata() {
				return sapling.getItemDamage();
			}

			@Override
			public int getSaplingDropChance() {
				return 20;
			}
			
			@Override
			public boolean canDropApples() {
				return false;
			}
		}

		public static enum Green_Leaf_Types implements ILeafSerializable {
			FIR(0) {
				@Override
				public int getSaplingDropChance() {
					if (GeneralSettings.bigTreeSaplingDropModifier) {
						return 90;
					} else {
						return super.getSaplingDropChance();
					}
				}
			}, REDWOOD(1) {
				@Override
				public int getSaplingDropChance() {
					if (GeneralSettings.bigTreeSaplingDropModifier) {
						return 90;
					} else {
						return super.getSaplingDropChance();
					}
				}
			}, ACACIA(2), CYPRESS(3);

			private final String name;
			private final int metadata;
			private ItemStack sapling = new ItemStack(Blocks.SAPLING);

			private Green_Leaf_Types(int metadata) {
				this.name = name().toLowerCase(Locale.ENGLISH);
				this.metadata = metadata;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public int getMetadata() {
				return metadata;
			}
			
			@Override
			public Block getSaplingBlock() {
				return Block.getBlockFromItem(sapling.getItem());
			}

			@Override
			public int getSaplingMetadata() {
				return sapling.getItemDamage();
			}

			@Override
			public int getSaplingDropChance() {
				return 20;
			}
			
			@Override
			public boolean canDropApples() {
				return false;
			}
		}

		private static void createLeaves() {
			if (ModuleControlSettings.SUMMA.isEnabled()) {
				createAutumnLeaves();
				createNewLeaves();
				createMoreLeaves();
				createGreenLeaves();
				
				createLeafPile();
			}
		}
		
		private static <T extends Enum<T> & ILeafSerializable> BlockEBXLLeaves<T> createLeaves(Class<T> type, T defaultType, String name) {
			final BlockEBXLLeaves<T> block = BlockEBXLLeaves.<T>create(type, defaultType);
			block.setUnlocalizedName("extrabiomes.leaves");

			final CommonProxy proxy = Extrabiomes.proxy;
			proxy.registerBlock(block, ItemEBXLLeaves.class, name);
			proxy.registerOreInAllSubblocks("treeLeaves", block);

			Blocks.FIRE.setFireInfo(block, 30, 60);

			final ItemStack stack = new ItemStack(block, 1, Short.MAX_VALUE);
			ForestryModHelper.registerLeaves(stack);
			ForestryModHelper.addToForesterBackpack(stack);
			
			return block;
		}
		
		private static void createAutumnLeaves() {
			if (!BlockSettings.AUTUMNLEAVES.getEnabled())
				return;

			final BlockEBXLLeaves<Autumn_Leaf_Types> block = createLeaves(Autumn_Leaf_Types.class, Autumn_Leaf_Types.UMBER, "leaves_1");

			Element.LEAVES_AUTUMN_BROWN.set(Autumn_Leaf_Types.UMBER.sapling = new ItemStack(block, 1, Autumn_Leaf_Types.UMBER.getMetadata()));
			Element.LEAVES_AUTUMN_ORANGE.set(Autumn_Leaf_Types.GOLDENROD.sapling = new ItemStack(block, 1, Autumn_Leaf_Types.GOLDENROD.getMetadata()));
			Element.LEAVES_AUTUMN_PURPLE.set(Autumn_Leaf_Types.VERMILLION.sapling = new ItemStack(block, 1, Autumn_Leaf_Types.VERMILLION.getMetadata()));
			Element.LEAVES_AUTUMN_YELLOW.set(Autumn_Leaf_Types.CITRINE.sapling = new ItemStack(block, 1, Autumn_Leaf_Types.CITRINE.getMetadata()));
		}
	  
		private static void createNewLeaves() {
			if (!BlockSettings.NEWLEAVES.getEnabled())
				return;

			final BlockEBXLLeaves<New_Leaf_Types> block = createLeaves(New_Leaf_Types.class, New_Leaf_Types.BALD_CYPRESS, "leaves_2");

			Element.LEAVES_BALD_CYPRESS.set(New_Leaf_Types.BALD_CYPRESS.sapling =
					new ItemStack(block, 1, New_Leaf_Types.BALD_CYPRESS.getMetadata()));
			
			Element.LEAVES_JAPANESE_MAPLE.set(New_Leaf_Types.JAPANESE_MAPLE.sapling =
					new ItemStack(block, 1, New_Leaf_Types.JAPANESE_MAPLE.getMetadata()));
			
			Element.LEAVES_JAPANESE_MAPLE_SHRUB.set(New_Leaf_Types.JAPANESE_MAPLE_SHRUB.sapling =
					new ItemStack(block, 1, New_Leaf_Types.JAPANESE_MAPLE_SHRUB.getMetadata()));
			
			Element.LEAVES_RAINBOW_EUCALYPTUS.set(New_Leaf_Types.RAINBOW_EUCALYPTUS.sapling =
					new ItemStack(block, 1, New_Leaf_Types.RAINBOW_EUCALYPTUS.getMetadata()));
		}

		private static void createMoreLeaves() {
			if (!BlockSettings.MORELEAVES.getEnabled())
				return;

			final BlockEBXLLeaves<More_Leaf_Types> block = createLeaves(More_Leaf_Types.class, More_Leaf_Types.SAKURA_BLOSSOM, "leaves_3");

			Element.LEAVES_SAKURA_BLOSSOM.set(More_Leaf_Types.SAKURA_BLOSSOM.sapling = new ItemStack(block, 1, More_Leaf_Types.SAKURA_BLOSSOM.getMetadata()));
		}

		private static void createGreenLeaves() {
			if (!BlockSettings.GREENLEAVES.getEnabled())
				return;

			final BlockEBXLLeaves<Green_Leaf_Types> block = createLeaves(Green_Leaf_Types.class, Green_Leaf_Types.FIR, "leaves_4");

			Element.LEAVES_ACACIA.set(Green_Leaf_Types.ACACIA.sapling = new ItemStack(block, 1, Green_Leaf_Types.ACACIA.getMetadata()));
			Element.LEAVES_FIR.set(Green_Leaf_Types.FIR.sapling = new ItemStack(block, 1, Green_Leaf_Types.FIR.getMetadata()));
			Element.LEAVES_REDWOOD.set(Green_Leaf_Types.REDWOOD.sapling = new ItemStack(block, 1, Green_Leaf_Types.REDWOOD.getMetadata()));
			Element.LEAVES_CYPRESS.set(Green_Leaf_Types.CYPRESS.sapling = new ItemStack(block, 1, Green_Leaf_Types.CYPRESS.getMetadata()));
		}

		private static void createLeafPile() {
			if (!BlockSettings.LEAFPILE.getEnabled())
				return;

			final BlockLeafPile block = new BlockLeafPile();
			block.setUnlocalizedName("extrabiomes.leafpile");

			final CommonProxy proxy = Extrabiomes.proxy;
			proxy.registerBlock(block, "leaf_pile");
			proxy.registerWorldGenerator(new LeafPileGenerator(block));
			
			Blocks.FIRE.setFireInfo(block, 30, 60);

			Element.LEAFPILE.set(new ItemStack(block));
		}
	  }
  
  public static final class LogHandler {
		/** 4 log types, name open to change **/
		public static enum Log_A_Type implements IMetaSerializable {
			FIR(0), ACACIA(1), CYPRESS(2), JAPANESE_MAPLE(3);

			private final String name;
			private final int metadata;

			Log_A_Type(int metadata) {
				this.name = name().toLowerCase(Locale.ENGLISH);
				this.metadata = metadata;
			}

			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public int getMetadata() {
				return metadata;
			}
		}
		
		/** 4 new log types, name open to change **/
		public static enum Log_B_Type implements IMetaSerializable {
			RAINBOW_EUCALYPTUS(0), AUTUMN(1), BALD_CYPRESS(2), REDWOOD(3);

			private final String name;
			private final int metadata;

			Log_B_Type(int metadata) {
				this.name = name().toLowerCase(Locale.ENGLISH);
				this.metadata = metadata;
			}
			
			@Override
			public String getName() {
				return name;
			}

			@Override
			public int getMetadata() {
				return metadata;
			}
		}
		
		public enum QuarterLogs_A_Type implements IQuarterSerializable {
			REDWOOD(0, BlockSettings.CUSTOMLOG.getItem(), Log_B_Type.REDWOOD.getMetadata()),
			FIR(1, BlockSettings.NEWLOG.getItem(), Log_A_Type.FIR.getMetadata()),
			OAK(2, ItemBlock.getItemFromBlock(Blocks.LOG), 0);

			private final Item item;
			private final String name;
			private final int metadata, damage;

			QuarterLogs_A_Type(int metadata, Item item, int damage) {
				this.name = name().toLowerCase(Locale.ENGLISH);
				this.metadata = metadata;

				this.item = item;
				this.damage = damage;
			}

			public String getName() {
				return name;
			}

			@Override
			public int getMetadata() {
				return metadata;
			}

			@Override
			public Item getItem() {
				return item;
			}

			@Override
			public int getMeta() {
				return damage;
			}

	        /* FIXME: Add these textures back
	        textureArray[0] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "logredwoodsideleft");
	        textureArray[1] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "logredwoodsideright");
	        textureArray[2] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "redwoodtopleft");
	        textureArray[3] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "redwoodtopright");
	        textureArray[4] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "redwoodbottomleft");
	        textureArray[5] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "redwoodbottomright");
	        textureArray[6] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "redwoodsideleft");
	        textureArray[7] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "redwoodsideright");
	        
	        textureArray[8] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "logfirsideleft");
	        textureArray[9] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "logfirsideright");
	        textureArray[10] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "firtopleft");
	        textureArray[11] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "firtopright");
	        textureArray[12] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "firbottomleft");
	        textureArray[13] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "firbottomright");
	        textureArray[14] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "firsideleft");
	        textureArray[15] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "firsideright");
	        
	        textureArray[16] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "logoaksideleft");
	        textureArray[17] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "logoaksideright");
	        textureArray[18] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "oaktopleft");
	        textureArray[19] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "oaktopright");
	        textureArray[20] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "oakbottomleft");
	        textureArray[21] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "oakbottomright");
	        textureArray[22] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "oaksideleft");
	        textureArray[23] = iconRegister.registerIcon(Extrabiomes.TEXTURE_PATH + "oaksideright");
	         */
	    }
	  
		private static void createLogs() {
			if (ModuleControlSettings.SUMMA.isEnabled()) {
				createWood();
				/*createMiniLogs();
				createQuarterLogs();
				createNewQuarterLogs();
				createKneeLogs();*/
			}
		}

	  private static void createMiniLogs() {
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.MINILOG.getEnabled())
	      return;
	
	    final BlockMiniLog block = new BlockMiniLog(BlockSettings.MINILOG);
	    extrabiomes.lib.Blocks.BLOCK_LOG_SAKURA_GROVE.set(block);
	    block.setBlockName("extrabiomes.log").setStepSound(Block.soundTypeWood).setHardness(2.0F).setResistance(Blocks.log.getExplosionResistance(null) * 5.0F).setCreativeTab(Extrabiomes.tabsEBXL);
	
	    final CommonProxy proxy = Extrabiomes.proxy;
	    proxy.setBlockHarvestLevel(block, "axe", 0);
	    proxy.registerBlock(block, extrabiomes.items.ItemCustomMiniLog.class, "mini_log_1");
	    proxy.registerOreInAllSubblocks("logWood", block);
	    proxy.registerEventHandler(block);
	    Blocks.FIRE.setFireInfo(block, 5, 5);
	
	    Element.LOG_SAKURA_BLOSSOM.set(new ItemStack(block, 1, BlockMiniLog.BlockType.SAKURA_BLOSSOM.metadata()));
	
	    ForestryModHelper.addToForesterBackpack(new ItemStack(block, 1, Short.MAX_VALUE));
	
	    BlockMiniLog.setRenderId(Extrabiomes.proxy.registerBlockHandler(new RenderMiniLog()));
	  }
	
	  private static void createKneeLogs() {
	    final BlockKneeLog block = new BlockKneeLog(BlockSettings.KNEELOG, "baldcypress");
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.KNEELOG.getEnabled())
	      return;
	
	    block.setBlockName("extrabiomes.cypresskneelog");
	    ((BlockKneeLog) block).setDroppedItemStack(Element.LOG_BALD_CYPRESS.get());
	
	    final CommonProxy proxy = Extrabiomes.proxy;
	    proxy.setBlockHarvestLevel(block, "axe", 0);
	    // proxy.registerBlock(block, extrabiomes.utility.MultiItemBlock.class);
	    proxy.registerBlock(block, ItemKneeLog.class, "log_elbow_baldcypress");
	    proxy.registerOreInAllSubblocks("logWood", block);
	    proxy.registerEventHandler(block);
	    Blocks.FIRE.setFireInfo(block, 5, 5);
	
	    final BlockKneeLog block2 = new BlockKneeLog(BlockSettings.RAINBOWKNEELOG, "rainboweucalyptus");
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.RAINBOWKNEELOG.getEnabled())
	      return;
	
	    block2.setBlockName("extrabiomes.rainbowkneelog");
	    ((BlockKneeLog) block2).setDroppedItemStack(Element.LOG_RAINBOW_EUCALYPTUS.get());
	
	    proxy.setBlockHarvestLevel(block2, "axe", 0);
	    proxy.registerBlock(block2, ItemKneeLog.class, "log_elbow_rainbow_eucalyptus");
	    proxy.registerOreInAllSubblocks("logWood", block2);
	    proxy.registerEventHandler(block2);
	    Blocks.FIRE.setFireInfo(block2, 5, 5);
	
	    Element.LOG_KNEE_BALD_CYPRESS.set(new ItemStack(block, 1, Short.MAX_VALUE));
	    Element.LOG_KNEE_RAINBOW_EUCALYPTUS.set(new ItemStack(block2, 1, Short.MAX_VALUE));
	
	    BlockKneeLog.setRenderId(Extrabiomes.proxy.registerBlockHandler(new RenderKneeLog()));
	
	    ForestryModHelper.addToForesterBackpack(new ItemStack(block, 1, Short.MAX_VALUE));
	    ForestryModHelper.addToForesterBackpack(new ItemStack(block2, 1, Short.MAX_VALUE));
	
	    FacadeHelper.addBuildcraftFacade(block);
	    FacadeHelper.addBuildcraftFacade(block2);
	
	  }
	
	  private static void createNewQuarterLogs() {
	    final CommonProxy proxy = Extrabiomes.proxy;
	    BlockNewQuarterLog.setRenderId(Extrabiomes.proxy.registerBlockHandler(new RenderNewQuarterLog()));
	
	    final BlockNewQuarterLog block = new BlockNewQuarterLog(BlockSettings.NEWQUARTERLOG, "baldcypress");
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.NEWQUARTERLOG.getEnabled())
	      return;
	
	    block.setBlockName("extrabiomes.baldcypressquarter").setStepSound(Block.soundTypeWood).setHardness(2.0F)
	        .setResistance(Blocks.log.getExplosionResistance(null) * 5.0F).setCreativeTab(Extrabiomes.tabsEBXL);
	    ((BlockNewQuarterLog) block).setDroppedItemStack(Element.LOG_BALD_CYPRESS.get());
	
	    proxy.setBlockHarvestLevel(block, "axe", 0);
	    proxy.registerBlock(block, ItemNewQuarterLog.class, "cornerlog_baldcypress");
	    proxy.registerOreInAllSubblocks("logWood", block);
	    proxy.registerEventHandler(block);
	    Blocks.FIRE.setFireInfo(block, 5, 5);
	
	    final BlockNewQuarterLog block2 = new BlockNewQuarterLog(BlockSettings.RAINBOWQUARTERLOG, "rainboweucalyptus");
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.RAINBOWQUARTERLOG.getEnabled())
	      return;
	
	    block2.setBlockName("extrabiomes.rainboweucalyptusquarter").setStepSound(Block.soundTypeWood).setHardness(2.0F).setResistance(Blocks.log.getExplosionResistance(null) * 5.0F).setCreativeTab(Extrabiomes.tabsEBXL);
	    ((BlockNewQuarterLog) block2).setDroppedItemStack(Element.LOG_RAINBOW_EUCALYPTUS.get());
	
	    proxy.setBlockHarvestLevel(block2, "axe", 0);
	    proxy.registerBlock(block2, ItemNewQuarterLog.class, "cornerlog_rainboweucalyptus");
	    proxy.registerOreInAllSubblocks("logWood", block2);
	    proxy.registerEventHandler(block2);
	    Blocks.FIRE.setFireInfo(block2, 5, 5);
	
	    final BlockNewQuarterLog block3 = new BlockNewQuarterLog(BlockSettings.OAKQUARTERLOG, "oak");
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.OAKQUARTERLOG.getEnabled())
	      return;
	
	    block3.setBlockName("extrabiomes.oakquarter").setStepSound(Block.soundTypeWood).setHardness(2.0F).setResistance(Blocks.log.getExplosionResistance(null) * 5.0F).setCreativeTab(Extrabiomes.tabsEBXL);
	    ((BlockNewQuarterLog) block3).setDroppedItemStack(new ItemStack(Item.getItemFromBlock(Blocks.log), 1, 0));
	
	    proxy.setBlockHarvestLevel(block3, "axe", 0);
	    proxy.registerBlock(block3, ItemNewQuarterLog.class, "cornerlog_oak");
	    proxy.registerOreInAllSubblocks("logWood", block3);
	    proxy.registerEventHandler(block3);
	    Blocks.FIRE.setFireInfo(block3, 5, 5);
	
	    final BlockNewQuarterLog block4 = new BlockNewQuarterLog(BlockSettings.FIRQUARTERLOG, "fir");
	    if (!ModuleControlSettings.SUMMA.isEnabled() && !BlockSettings.FIRQUARTERLOG.getEnabled())
	      return;
	
	    block4.setBlockName("extrabiomes.firquarter").setStepSound(Block.soundTypeWood).setHardness(2.0F).setResistance(Blocks.log.getExplosionResistance(null) * 5.0F).setCreativeTab(Extrabiomes.tabsEBXL);
	    ((BlockNewQuarterLog) block4).setDroppedItemStack(Element.LOG_FIR.get());
	
	    proxy.setBlockHarvestLevel(block4, "axe", 0);
	    proxy.registerBlock(block4, ItemNewQuarterLog.class, "cornerlog_fir");
	    proxy.registerOreInAllSubblocks("logWood", block4);
	    proxy.registerEventHandler(block4);
	    Blocks.FIRE.setFireInfo(block4, 5, 5);
	
	    final BlockNewQuarterLog block5 = new BlockNewQuarterLog(BlockSettings.REDWOODQUARTERLOG, "redwood");
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.REDWOODQUARTERLOG.getEnabled())
	      return;
	
	    block5.setBlockName("extrabiomes.redwoodquarter").setStepSound(Block.soundTypeWood).setHardness(2.0F).setResistance(Blocks.log.getExplosionResistance(null) * 5.0F).setCreativeTab(Extrabiomes.tabsEBXL);
	    ((BlockNewQuarterLog) block5).setDroppedItemStack(Element.LOG_REDWOOD.get());
	    // block5.setRenderId(renderId);
	
	    proxy.setBlockHarvestLevel(block5, "axe", 0);
	    proxy.registerBlock(block5, ItemNewQuarterLog.class, "cornerlog_redwood");
	    proxy.registerOreInAllSubblocks("logWood", block5);
	    proxy.registerEventHandler(block5);
	    Blocks.FIRE.setFireInfo(block5, 5, 5);
	
	    Element.LOG_QUARTER_BALD_CYPRESS.set(new ItemStack(block, 1, Short.MAX_VALUE));
	    Element.LOG_QUARTER_RAINBOW_EUCALYPTUS.set(new ItemStack(block2, 1, Short.MAX_VALUE));
	    Element.LOG_QUARTER_OAK.set(new ItemStack(block3, 1, Short.MAX_VALUE));
	    Element.LOG_QUARTER_FIR.set(new ItemStack(block4, 1, Short.MAX_VALUE));
	    Element.LOG_QUARTER_REDWOOD.set(new ItemStack(block5, 1, Short.MAX_VALUE));
	
	    // BlockNewQuarterLog.setRenderId(Extrabiomes.proxy.registerBlockHandler(new
	    // RenderNewQuarterLog()));
	
	    ForestryModHelper.addToForesterBackpack(new ItemStack(block, 1, Short.MAX_VALUE));
	    ForestryModHelper.addToForesterBackpack(new ItemStack(block2, 1, Short.MAX_VALUE));
	    ForestryModHelper.addToForesterBackpack(new ItemStack(block3, 1, Short.MAX_VALUE));
	    ForestryModHelper.addToForesterBackpack(new ItemStack(block4, 1, Short.MAX_VALUE));
	    ForestryModHelper.addToForesterBackpack(new ItemStack(block5, 1, Short.MAX_VALUE));
	    //FacadeHelper.addBuildcraftFacade(block, i);
	    //FacadeHelper.addBuildcraftFacade(block2, i);
	    //FacadeHelper.addBuildcraftFacade(block3, i);
	    //FacadeHelper.addBuildcraftFacade(block4, i);
	    //FacadeHelper.addBuildcraftFacade(block5, i);
	
	  }
	
	  private static void createQuarterLogs() {
	    final boolean blockIDNW = BlockSettings.QUARTERLOG0.getEnabled();
	    final boolean blockIDNE = BlockSettings.QUARTERLOG1.getEnabled();
	    final boolean blockIDSW = BlockSettings.QUARTERLOG2.getEnabled();
	    final boolean blockIDSE = BlockSettings.QUARTERLOG3.getEnabled();
	    if (!blockIDNE || !blockIDNW || !blockIDSE || !blockIDSW)
	      return;
	
	    final BlockQuarterLog blockNW = new BlockQuarterLog(BlockSettings.QUARTERLOG0, 144, BlockQuarterLog.BarkOn.NW);
	    final BlockQuarterLog blockNE = new BlockQuarterLog(BlockSettings.QUARTERLOG1, 144, BlockQuarterLog.BarkOn.NE);
	    final BlockQuarterLog blockSW = new BlockQuarterLog(BlockSettings.QUARTERLOG2, 144, BlockQuarterLog.BarkOn.SW);
	    final BlockQuarterLog blockSE = new BlockQuarterLog(BlockSettings.QUARTERLOG3, 144, BlockQuarterLog.BarkOn.SE);
	
	    for (final BlockQuarterLog block : new BlockQuarterLog[] { blockNW, blockNE, blockSW, blockSE }) {
	      block.setUnlocalizedName("extrabiomes.log.quarter");
	
	      alterLog(block, "log_old_quarter");
	    }
	
	    Element.LOG_HUGE_FIR_NW.set(new ItemStack(blockNW, 1, BlockQuarterLog.BlockType.FIR.getMetadata()));
	    Element.LOG_HUGE_FIR_NE.set(new ItemStack(blockNE, 1, BlockQuarterLog.BlockType.FIR.getMetadata()));
	    Element.LOG_HUGE_FIR_SW.set(new ItemStack(blockSW, 1, BlockQuarterLog.BlockType.FIR.getMetadata()));
	    Element.LOG_HUGE_FIR_SE.set(new ItemStack(blockSE, 1, BlockQuarterLog.BlockType.FIR.getMetadata()));
	    Element.LOG_HUGE_OAK_NW.set(new ItemStack(blockNW, 1, BlockQuarterLog.BlockType.OAK.getMetadata()));
	    Element.LOG_HUGE_OAK_NE.set(new ItemStack(blockNE, 1, BlockQuarterLog.BlockType.OAK.getMetadata()));
	    Element.LOG_HUGE_OAK_SW.set(new ItemStack(blockSW, 1, BlockQuarterLog.BlockType.OAK.getMetadata()));
	    Element.LOG_HUGE_OAK_SE.set(new ItemStack(blockSE, 1, BlockQuarterLog.BlockType.OAK.getMetadata()));
	    Element.LOG_HUGE_REDWOOD_NW.set(new ItemStack(blockNW, 1, BlockQuarterLog.BlockType.REDWOOD.getMetadata()));
	    Element.LOG_HUGE_REDWOOD_NE.set(new ItemStack(blockNE, 1, BlockQuarterLog.BlockType.REDWOOD.getMetadata()));
	    Element.LOG_HUGE_REDWOOD_SW.set(new ItemStack(blockSW, 1, BlockQuarterLog.BlockType.REDWOOD.getMetadata()));
	    Element.LOG_HUGE_REDWOOD_SE.set(new ItemStack(blockSE, 1, BlockQuarterLog.BlockType.REDWOOD.getMetadata()));
	
	    // Create the recipies to update logs
	
	    BlockQuarterLog.setRenderId(Extrabiomes.proxy.registerBlockHandler(new RenderQuarterLog()));
	
	    for (final BlockQuarterLog.TerrainBlockType type : BlockQuarterLog.BlockType.values()) {
	      FacadeHelper.addBuildcraftFacade(blockSE, type.getMetadata());
	    }
	  }
	
	  private static void createSapling() {
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.SAPLING.getEnabled())
	      return;
	
	    final BlockCustomSapling block = new BlockCustomSapling(16);
	    block.setUnlocalizedName("extrabiomes.sapling").setHardness(0.0F).setSoundType(SoundType.GROUND).setCreativeTab(Extrabiomes.tabsEBXL);
	
	    final CommonProxy proxy = Extrabiomes.proxy;
	    proxy.registerBlock(block, extrabiomes.items.ItemSapling.class, "saplings_1");
	    proxy.registerOreInAllSubblocks("treeSapling", block);
	
	    Element.SAPLING_ACACIA.set(new ItemStack(block, 1, BlockCustomSapling.BlockType.ACACIA.metadata()));
	    Element.SAPLING_AUTUMN_BROWN.set(new ItemStack(block, 1, BlockCustomSapling.BlockType.UMBER.metadata()));
	    Element.SAPLING_AUTUMN_ORANGE.set(new ItemStack(block, 1, BlockCustomSapling.BlockType.GOLDENROD.metadata()));
	    Element.SAPLING_AUTUMN_PURPLE.set(new ItemStack(block, 1, BlockCustomSapling.BlockType.VERMILLION.metadata()));
	    Element.SAPLING_AUTUMN_YELLOW.set(new ItemStack(block, 1, BlockCustomSapling.BlockType.CITRINE.metadata()));
	    Element.SAPLING_FIR.set(new ItemStack(block, 1, BlockCustomSapling.BlockType.FIR.metadata()));
	    Element.SAPLING_REDWOOD.set(new ItemStack(block, 1, BlockCustomSapling.BlockType.REDWOOD.metadata()));
	    Element.SAPLING_CYPRESS.set(new ItemStack(block, 1, BlockCustomSapling.BlockType.CYPRESS.metadata()));
	
	    final ItemStack stack = new ItemStack(block, 1, Short.MAX_VALUE);
	
	    // Temp fix so that NEI shows the fermenter recipies when you try to view
	    // uses of saplings.
	    // ForestryModHelper.registerSapling(stack);
	    ForestryModHelper.registerSapling(Element.SAPLING_ACACIA.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_AUTUMN_BROWN.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_AUTUMN_ORANGE.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_AUTUMN_PURPLE.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_AUTUMN_YELLOW.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_FIR.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_REDWOOD.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_CYPRESS.get());
	    ForestryModHelper.addToForesterBackpack(stack);
	
	    // all but redwood
	    final Element[] forestrySaplings = { Element.SAPLING_ACACIA, Element.SAPLING_AUTUMN_BROWN, Element.SAPLING_AUTUMN_ORANGE, Element.SAPLING_AUTUMN_PURPLE,
	        Element.SAPLING_AUTUMN_YELLOW, Element.SAPLING_FIR, Element.SAPLING_CYPRESS };
	    for (final Element sapling : forestrySaplings) {
	      ForestryModHelper.registerGermling(sapling.get());
	    }
	
	    proxy.registerEventHandler(new SaplingBonemealEventHandler(block));
	    proxy.registerFuelHandler(new SaplingFuelHandler(block));
	  }
	
	  private static void createNewSapling() {
	    if (!ModuleControlSettings.SUMMA.isEnabled() || !BlockSettings.NEWSAPLING.getEnabled())
	      return;
	
	    final BlockNewSapling block = new BlockNewSapling();
	    block.setBlockName("extrabiomes.sapling").setHardness(0.0F).setStepSound(Block.soundTypeGrass).setCreativeTab(Extrabiomes.tabsEBXL);
	
	    final CommonProxy proxy = Extrabiomes.proxy;
	    proxy.registerBlock(block, extrabiomes.items.ItemNewSapling.class, "saplings_2");
	    proxy.registerOreInAllSubblocks("treeSapling", block);
	
	    Element.SAPLING_BALD_CYPRESS.set(new ItemStack(block, 1, BlockNewSapling.BlockType.BALD_CYPRESS.metadata()));
	    Element.SAPLING_JAPANESE_MAPLE.set(new ItemStack(block, 1, BlockNewSapling.BlockType.JAPANESE_MAPLE.metadata()));
	    Element.SAPLING_JAPANESE_MAPLE_SHRUB.set(new ItemStack(block, 1, BlockNewSapling.BlockType.JAPANESE_MAPLE_SHRUB.metadata()));
	    Element.SAPLING_RAINBOW_EUCALYPTUS.set(new ItemStack(block, 1, BlockNewSapling.BlockType.RAINBOW_EUCALYPTUS.metadata()));
	    Element.SAPLING_SAKURA_BLOSSOM.set(new ItemStack(block, 1, BlockNewSapling.BlockType.SAKURA_BLOSSOM.metadata()));
	
	    final ItemStack stack = new ItemStack(block, 1, Short.MAX_VALUE);
	
	    // Temp fix so that NEI shows the fermenter recipies when you try to view
	    // uses of saplings.
	    // ForestryModHelper.registerSapling(stack);
	    ForestryModHelper.registerSapling(Element.SAPLING_BALD_CYPRESS.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_JAPANESE_MAPLE.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_JAPANESE_MAPLE_SHRUB.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_RAINBOW_EUCALYPTUS.get());
	    ForestryModHelper.registerSapling(Element.SAPLING_SAKURA_BLOSSOM.get());
	    ForestryModHelper.addToForesterBackpack(stack);
	
	    // all but redwood
	    final Element[] forestrySaplings = { Element.SAPLING_JAPANESE_MAPLE, Element.SAPLING_JAPANESE_MAPLE_SHRUB, Element.SAPLING_SAKURA_BLOSSOM };
	    for (final Element sapling : forestrySaplings) {
	      ForestryModHelper.registerGermling(sapling.get());
	    }
	
	    proxy.registerEventHandler(new SaplingBonemealNewEventHandler(block));
	    proxy.registerFuelHandler(new SaplingFuelHandler(block));
	  }
	
		private static void createWood() {
			if (BlockSettings.CUSTOMLOG.getEnabled()) {
				final BlockEBXLLog<Log_A_Type> log = BlockEBXLLog.create(Log_A_Type.class, Log_A_Type.FIR);
				log.setUnlocalizedName("extrabiomes.log");

				alterLog(log, "log1");

				Element.LOG_ACACIA.set(new ItemStack(log, 1, Log_A_Type.ACACIA.getMetadata()));
				Element.LOG_FIR.set(new ItemStack(log, 1, Log_A_Type.FIR.getMetadata()));
				Element.LOG_CYPRESS.set(new ItemStack(log, 1, Log_A_Type.CYPRESS.getMetadata()));
				Element.LOG_JAPANESE_MAPLE.set(new ItemStack(log, 1, Log_A_Type.JAPANESE_MAPLE.getMetadata()));

				ForestryModHelper.addToForesterBackpack(new ItemStack(log, 1, Short.MAX_VALUE));
				for (final Log_A_Type type : Log_A_Type.values()) {
					//FacadeHelper.addBuildcraftFacade(block, type.getMetadata());
					LogHelper.fine("Successfully built log for %s", type.getName());
				}
			}

			if (BlockSettings.NEWLOG.getEnabled()) {
				final BlockEBXLLog<Log_B_Type> log = BlockEBXLLog.create(Log_B_Type.class, Log_B_Type.AUTUMN);
				log.setUnlocalizedName("extrabiomes.newlog");

				alterLog(log, "log2");

				Element.LOG_RAINBOW_EUCALYPTUS.set(new ItemStack(log, 1, Log_B_Type.RAINBOW_EUCALYPTUS.getMetadata()));
				Element.LOG_AUTUMN.set(new ItemStack(log, 1, Log_B_Type.AUTUMN.getMetadata()));
				Element.LOG_BALD_CYPRESS.set(new ItemStack(log, 1, Log_B_Type.BALD_CYPRESS.getMetadata()));
				Element.LOG_REDWOOD.set(new ItemStack(log, 1, Log_B_Type.REDWOOD.getMetadata()));

				ForestryModHelper.addToForesterBackpack(new ItemStack(log, 1, Short.MAX_VALUE));
				for (final Log_B_Type type : Log_B_Type.values()) {
					//FacadeHelper.addBuildcraftFacade(log, type.getMetadata());
					LogHelper.fine("Successfully built log for %s", type.getName());
				}
			}
		}
		
		private static void alterLog(Block log, String name) {
			final CommonProxy proxy = Extrabiomes.proxy;
			
			proxy.setBlockHarvestLevel(log, "axe", 0);
			proxy.registerBlock(log, MultiItemBlock.class, name);
			proxy.registerOreInAllSubblocks("logWood", log);
			proxy.registerEventHandler(log);
			
			Blocks.FIRE.setFireInfo(log, 5, 5);
		}
  }
}