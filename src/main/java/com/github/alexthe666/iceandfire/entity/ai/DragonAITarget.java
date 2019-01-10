package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class DragonAITarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
	private EntityDragonBase dragon;

	public DragonAITarget(EntityDragonBase entityIn, Class<T> classTarget, boolean checkSight, Predicate<? super T> targetSelector) {
		super(entityIn, classTarget, 0, checkSight, false, targetSelector);
		this.dragon = entityIn;
	}

	@Override
	public boolean shouldExecute() {
		if (super.shouldExecute() && this.targetEntity != null && !this.targetEntity.getClass().equals(this.dragon.getClass())) {
			float dragonSize = Math.max(this.dragon.width, this.dragon.width * (dragon.getRenderSize() / 3));
			if (dragonSize >= this.targetEntity.width) {
				if(this.targetEntity instanceof EntityDragonBase){
					EntityDragonBase dragon = (EntityDragonBase)this.targetEntity;
					if(dragon.getOwner() != null && this.dragon.getOwner() != null && this.dragon.isOwner(dragon.getOwner())){
						return false;
					}
					return !dragon.isModelDead();
				}
				if (this.targetEntity instanceof EntityPlayer && dragon.isTamed()) {
					return false;
				} else {

					if (!dragon.isOwner(this.targetEntity) && FoodUtils.getFoodPoints(this.targetEntity) > 0 && dragon.canMove() && (dragon.getHunger() < 90 || !dragon.isTamed() && this.targetEntity instanceof EntityPlayer)) {
						if(dragon.isTamed()){
							return DragonUtils.canTameDragonAttack(dragon, this.targetEntity);
						}else{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return this.dragon.getEntityBoundingBox().grow(targetDistance, targetDistance, targetDistance);
	}
}