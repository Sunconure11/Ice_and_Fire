package com.github.alexthe666.iceandfire.loot;

import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.item.ItemDragonEgg;
import com.github.alexthe666.iceandfire.item.ItemDragonFlesh;
import com.github.alexthe666.iceandfire.item.ItemDragonScales;
import com.github.alexthe666.iceandfire.item.ItemDragonSkull;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.*;

import java.util.Random;

public class CustomizeToDragon extends LootFunction {

    public CustomizeToDragon(LootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        if (!stack.isEmpty()  && context.getLootedEntity() instanceof EntityDragonBase) {
            Random random = new Random();
            EntityDragonBase dragon = (EntityDragonBase)context.getLootedEntity();
            if(stack.getItem() == ModItems.dragonbone){
                stack.setCount(1 + random.nextInt(1 + (dragon.getAgeInDays() / 25)));
                return stack;
            }
            if(stack.getItem() instanceof ItemDragonScales){
                stack.setCount(1 + random.nextInt(1 + (dragon.getAgeInDays() / 5)));
                return new ItemStack(dragon.getVariantScale(dragon.getVariant()), stack.getCount(), stack.getMetadata());
            }
            if(stack.getItem() instanceof ItemDragonEgg){
                if(dragon.isAdult()){
                    return new ItemStack(dragon.getVariantEgg(dragon.getVariant()), stack.getCount(), stack.getMetadata());
                }else{
                    stack.setCount(1 + random.nextInt(1 + (dragon.getAgeInDays() / 5)));
                    return new ItemStack(dragon.getVariantScale(dragon.getVariant()), stack.getCount(), stack.getMetadata());
                }
            }
            if(stack.getItem() instanceof ItemDragonFlesh){
                stack.setCount(1 + random.nextInt(1 + (dragon.getAgeInDays() / 25)));
                return new ItemStack(dragon.isFire ? ModItems.fire_dragon_flesh : ModItems.ice_dragon_flesh, stack.getCount(), stack.getMetadata());
            }
            if(stack.getItem() instanceof ItemDragonSkull){
                ItemStack stack1 = new ItemStack(dragon.isFire ? ModItems.dragon_skull : ModItems.dragon_skull, stack.getCount(), stack.getMetadata());
                stack1.setTagCompound(stack.getTagCompound());
                return stack1;
            }
            if(stack.getItem() == ModItems.fire_dragon_blood || stack.getItem() == ModItems.ice_dragon_blood){
                return new ItemStack(dragon.isFire ? ModItems.fire_dragon_blood : ModItems.ice_dragon_blood, stack.getCount(), stack.getMetadata());
            }
            if(stack.getItem() == ModItems.fire_dragon_heart || stack.getItem() == ModItems.ice_dragon_heart){
                return new ItemStack(dragon.isFire ? ModItems.fire_dragon_heart : ModItems.ice_dragon_heart, stack.getCount(), stack.getMetadata());
            }
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<CustomizeToDragon> {
        public Serializer() {
            super(new ResourceLocation("iceandfire:customize_to_dragon"), CustomizeToDragon.class);
        }

        public void serialize(JsonObject object, CustomizeToDragon functionClazz, JsonSerializationContext serializationContext) {
        }

        public CustomizeToDragon deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
            return new CustomizeToDragon(conditionsIn);
        }
    }
}