package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.message.MessageMultipartInteract;
import net.ilexiconn.llibrary.server.entity.multipart.PartEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;

public class EntityMutlipartPart extends PartEntity {

    public EntityMutlipartPart(EntityLiving parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(parent, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if(world.isRemote){
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageMultipartInteract(this.parent.getEntityId(), 0));
        }
        return this.parent.processInitialInteract(player, hand);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if(world.isRemote && source.getTrueSource() instanceof EntityPlayer){
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageMultipartInteract(this.parent.getEntityId(), damage));
        }
        return this.parent.attackEntityFrom(source, damage * this.damageMultiplier);
    }

    public void resize(float width, float height){
        this.setSize(width, height);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }
}
