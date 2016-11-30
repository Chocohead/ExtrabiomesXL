/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.blocks;

import java.util.List;
import java.util.Locale;

import extrabiomes.Extrabiomes;
import extrabiomes.lib.IMetaSerializable;
import extrabiomes.lib.ITextureRegisterer;
import extrabiomes.lib.PropertyEnum;
import extrabiomes.utility.ModelUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GenericTerrainBlock extends Block implements ITextureRegisterer {
	public enum TerrainBlockType implements IMetaSerializable {
		RED_ROCK(0, 1.5F), RED_COBBLE(1, 2F), RED_ROCK_BRICK(2, 1.F), CRACKED_SAND(3, 1.2F);

		private final String name;
		private final int metadata;
		private final float hardness;

		private TerrainBlockType(int metadata, float hardness) {
			this.name = name().toLowerCase(Locale.ENGLISH);
			this.metadata = metadata;
			this.hardness = hardness;
		}
		
		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return metadata;
		}
		
		public float getHardness() {
			return hardness;
		}
	}
	
	public static final PropertyEnum<TerrainBlockType> TYPE = new PropertyEnum<TerrainBlockType>(TerrainBlockType.class, TerrainBlockType.RED_ROCK, 16);

	public GenericTerrainBlock() {
		super(Material.ROCK);
		
		setResistance(2F);
		setSoundType(SoundType.STONE);
		setCreativeTab(Extrabiomes.tabsEBXL);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, TYPE.getForMeta(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(TYPE).getMetadata();
	}

	@Override
    @SideOnly(Side.CLIENT)
	public void registerTexture() {
		ModelUtil.registerTextures(this, TYPE.getTypeStates(getDefaultState()));
    }
	
	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
		return state.getValue(TYPE).hardness;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (TerrainBlockType type : TerrainBlockType.values()) {
			list.add(new ItemStack(item, 1, type.getMetadata()));
		}
	}
}
