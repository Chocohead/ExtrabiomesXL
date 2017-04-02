package extrabiomes.blocks;

import java.util.Locale;
import java.util.Map;

import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import extrabiomes.Extrabiomes;
import extrabiomes.lib.ITextureRegisterer;
import extrabiomes.utility.ModelUtil.CustomStateMapper;

public class BlockCustomVine extends BlockVine implements ITextureRegisterer {
	@SideOnly(Side.CLIENT)
	private class VineMapper extends CustomStateMapper {
		public final PropertyBool[] allFaces = new PropertyBool[] {EAST, NORTH, SOUTH, UP, WEST}; //Alphabetical to match the JSON

		@Override
		protected ModelResourceLocation getModelLocation(IBlockState state) {
			assert state.getBlock() == BlockCustomVine.this;
			return new ModelResourceLocation(new ResourceLocation(Extrabiomes.RESOURCE_PATH, "vines"), getPropertyString(state.getProperties()));
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return new ModelResourceLocation(new ResourceLocation(Extrabiomes.RESOURCE_PATH, type.toString() + "_vine"), null);
		}

		@Override
		public String getPropertyString(Map<IProperty<?>, Comparable<?>> values) {
			return "facing=" + getFacingString(values) + ",type=" + type.toString();
		}

		protected String getFacingString(Map<IProperty<?>, Comparable<?>> values) {
			StringBuilder out = new StringBuilder();

			for (IProperty<Boolean> facing : allFaces) {
				if ((boolean) values.get(facing)) {
					if (out.length() > 0) {
						out.append('|');
					}
					out.append(facing.getName());
				}
			}

			return out.length() < 1 ? "none" : out.toString();
		}
	}

	public static enum VineType {
		GLORIOSA, SPANISH_MOSS;

		@Override
		public String toString() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	public final VineType type;

	public BlockCustomVine(VineType type) {
		super();

		this.type = type;
		setHardness(0.2F);
		setCreativeTab(Extrabiomes.tabsEBXL);
		setSoundType(SoundType.PLANT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerTexture() {
		VineMapper mapper = new VineMapper();
		Item item = Item.getItemFromBlock(this);

		ModelLoader.setCustomStateMapper(this, mapper);
		ModelLoader.setCustomModelResourceLocation(item, 0, mapper.getModelLocation(new ItemStack(item)));
	}
}