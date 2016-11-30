package extrabiomes.blocks;

import java.util.ArrayList;
import java.util.List;

import extrabiomes.lib.ITextureRegisterer;
import extrabiomes.utility.ModelUtil;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustomVine extends BlockVine implements ITextureRegisterer {

	public enum BlockType {
		GLORIOSA, SPANISH_MOSS;
	}

	public final BlockType type;

	public BlockCustomVine(BlockType type) {
		super();
		
		this.type = type;
		setHardness(0.2F);
		setSoundType(SoundType.GROUND);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerTexture() {
    	//Extrabiomes.TEXTURE_PATH + "vine_" + type.toString().toLowerCase();
		ModelUtil.registerTextures(this, getVarientStates());
    }

	protected List<IBlockState> getVarientStates() {
		List<IBlockState> states = new ArrayList<IBlockState>(16); 
		
		for (byte meta = 0; meta < 16; meta++) {
			states.add(getStateFromMeta(meta));
		}

		return states;
	}
}