package extrabiomes.items;

import java.util.List;
import java.util.Locale;

import extrabiomes.Extrabiomes;
import extrabiomes.lib.ITextureRegisterer;
import extrabiomes.utility.ModelUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCustomFood extends ItemFood implements ITextureRegisterer {

	public static final int		DEFAULT_HUNGER		= 2;	// 1.0 meat
	public static final float	DEFAULT_SATURATION	= 0.6f;

	public enum FoodType {
		// @formatter:off
		//						 idx   hung  sat   text
		CHOCOLATE			 	(0,    3,    0.5f, "chocolate" ),
		CHOCOLATE_STRAWBERRY	(1,    7,    1.0f, "chocolate_strawberry" );
		// @formatter:on
		
		public final int	 meta;
		public final int	 hunger;
		public final float	 saturation;
		public final String  texture;
		public final boolean alwaysEdible;
		
		private FoodType(int meta, Integer hunger, Float saturation, String texture ) {
			this(meta, hunger, saturation, texture, false);
		}
		
		private FoodType(int meta, Integer hunger, Float saturation, String texture, boolean alwaysEdible) {
			this.meta = meta;
			this.hunger = (hunger == null ? DEFAULT_HUNGER : hunger);
			this.saturation = (saturation == null ? DEFAULT_SATURATION : saturation);
			this.texture = texture;
			this.alwaysEdible = alwaysEdible;
		}
		
		public static final FoodType[] VALUES = values();
	}

	public ItemCustomFood() {
		super(DEFAULT_HUNGER, DEFAULT_SATURATION, false);
		
		setMaxDamage(0);
		setHasSubtypes(true);
		setUnlocalizedName("extrabiomes.food");
		setCreativeTab(Extrabiomes.tabsEBXL);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerTexture() {
		for (FoodType type : FoodType.values()) {
			ModelUtil.registerTexture(this, type.meta, type.texture);
		}
	}

	@Override
	public int getMetadata(int meta) {
		return MathHelper.clamp_int(meta, 0, FoodType.VALUES.length);
	}

	public FoodType getFoodType(int meta) {
		return FoodType.VALUES[getMetadata(meta)];
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		final FoodType type = getFoodType(itemStack.getItemDamage());
		return super.getUnlocalizedName() + '.' + type.name().toLowerCase(Locale.ENGLISH);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
		for (FoodType type : FoodType.values()) {
			list.add(new ItemStack(item, 1, type.meta));
		}
	}

	@Override
	public int getHealAmount(ItemStack itemStack)
	{
		final FoodType type = getFoodType(itemStack.getItemDamage());
		return type.hunger;
	}

	@Override
	public float getSaturationModifier(ItemStack itemStack)
	{
		final FoodType type = getFoodType(itemStack.getItemDamage());
		return type.saturation;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		final FoodType type = getFoodType(stack.getItemDamage());
		
		if (player.canEat(type.alwaysEdible)) {
			player.setActiveHand(hand);
		    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
    }
}