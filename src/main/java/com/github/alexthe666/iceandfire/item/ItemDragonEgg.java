package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDragonEgg extends Item {
	public EnumDragonEgg type;

	public ItemDragonEgg(String name, EnumDragonEgg type) {
		this.setHasSubtypes(true);
		this.setCreativeTab(IceAndFire.TAB);
		this.type = type;
		this.setTranslationKey("iceandfire.dragonegg");
		this.maxStackSize = 1;
		this.setRegistryName(IceAndFire.MODID, name);
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
		itemStack.setTagCompound(new NBTTagCompound());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(type.color + StatCollector.translateToLocal("dragon." + type.toString().toLowerCase()));
	}


	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing != EnumFacing.UP) {
			return EnumActionResult.PASS;
		} else {
			EntityDragonEgg egg = new EntityDragonEgg(worldIn);
			egg.setType(type);
			egg.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			egg.onPlayerPlace(player);
			if (!worldIn.isRemote) {
				worldIn.spawnEntity(egg);
			}
			ItemStack itemstack = player.getHeldItem(hand);

			if (!player.capabilities.isCreativeMode) {
				itemstack.shrink(1);
				if (itemstack.getCount() <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
				}
			}
			return EnumActionResult.SUCCESS;

		}
	}
}
