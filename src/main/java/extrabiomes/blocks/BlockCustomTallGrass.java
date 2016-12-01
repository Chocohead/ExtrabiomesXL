/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.common.base.Optional;

import extrabiomes.Extrabiomes;
import extrabiomes.lib.BiomeSettings;
import extrabiomes.lib.IMetaSerializable;
import extrabiomes.lib.ITextureRegisterer;
import extrabiomes.lib.PropertyEnum;
import extrabiomes.utility.ModelUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustomTallGrass extends BlockBush implements IShearable, ITextureRegisterer
{
    public enum TallGrassType implements IMetaSerializable
    {
        BROWN(0), SHORT_BROWN(1), DEAD(2, true), DEAD_TALL(3, true), DEAD_YELLOW(4, true);
        
    	private final String name;
        private final int metadata;
        private final boolean dead;
        
        private TallGrassType(int metadata) {
        	this(metadata, false);
        }
        
        private TallGrassType(int metadata, boolean dead)
        {
        	this.name = name().toLowerCase(Locale.ENGLISH);
            this.metadata = metadata;
            this.dead = dead;
        }
        
        @Override
        public String getName() {
        	return name;
        }
        
        @Override
        public int getMetadata()
        {
            return metadata;
        }
        
        public boolean isDead() {
        	return dead;
        }
    }
    
    private static final float SIZE = 0.4F;
	protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.5F - SIZE, 0.0F, 0.5F - SIZE, 0.5F + SIZE, 0.8F, 0.5F + SIZE);
	public static final PropertyEnum<TallGrassType> TYPE = new PropertyEnum<TallGrassType>(TallGrassType.class, TallGrassType.BROWN, 16);
     
    public BlockCustomTallGrass()
    {
        super(Material.VINE);

        setHardness(0.0F);
        setSoundType(SoundType.GROUND);
		setCreativeTab(Extrabiomes.tabsEBXL);
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    	return TALL_GRASS_AABB;
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
    public void registerTexture()
    {
    	ModelUtil.registerTextures(this, TYPE.getTypeStates(getDefaultState()));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public EnumOffsetType getOffsetType() {
    	return EnumOffsetType.XYZ;
    }
    
    public IBlockState withType(TallGrassType type) {
    	return getDefaultState().withProperty(TYPE, type);
    }
    
    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        Block block = world.getBlockState(pos).getBlock();
        return (BiomeSettings.MOUNTAINRIDGE.getBiome().isPresent() && block.equals(BiomeSettings.MOUNTAINRIDGE.getBiome().get().topBlock))
                || (BiomeSettings.WASTELAND.getBiome().isPresent() && block.equals(BiomeSettings.WASTELAND.getBiome().get().topBlock))
                || super.canPlaceBlockAt(world, pos);
    }
    
    @Override
    protected boolean canSustainBush(IBlockState state) {
    	Block block = state.getBlock();
    	return (BiomeSettings.MOUNTAINRIDGE.getBiome().isPresent() && block.equals(BiomeSettings.MOUNTAINRIDGE.getBiome().get().topBlock))
                || (BiomeSettings.WASTELAND.getBiome().isPresent() && block.equals(BiomeSettings.WASTELAND.getBiome().get().topBlock))
                || super.canSustainBush(state);
    }
    
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        
        int rarity = 8;
        if (state.getValue(TYPE).isDead())
            rarity *= 2;

        if (RANDOM.nextInt(rarity) != 0)
            return ret;
        
        final Optional<ItemStack> item = Extrabiomes.proxy.getGrassSeed(RANDOM, fortune);
        
        if (item.isPresent())
            ret.add(item.get());
        return ret;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item id, CreativeTabs tab, List<ItemStack> itemList)
    {
        for (final TallGrassType type : TallGrassType.values())
            itemList.add(new ItemStack(this, 1, type.getMetadata()));
    }
    
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
    	return new ItemStack(this, 1, getMetaFromState(state));
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }
    
    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }
    
    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }
    
    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }
    
    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        
        ret.add(new ItemStack(this, 1, getMetaFromState(world.getBlockState(pos))));
        
        return ret;
    }
    
    @Override
    public int quantityDroppedWithBonus(int i, Random rand)
    {
        return 1 + rand.nextInt(i * 2 + 1);
    }
}
