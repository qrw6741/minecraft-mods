package com.mahra.ashenrealms.item;

import com.mahra.ashenrealms.client.RpgSwingExtensions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class RpgSwordItem extends SwordItem {
    public enum Ability { NONE, WEAKEN, ROYAL, FROST, ECLIPSE }
    public enum Swing { SLASH, SLAM }

    private final Ability ability;
    private final Swing swing;

    public RpgSwordItem(Tier tier, int damage, float speed, Ability ability, Swing swing, Properties props) {
        super(tier, damage, speed, props);
        this.ability = ability;
        this.swing = swing;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        switch (ability) {
            case WEAKEN -> target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0), attacker);
            case ROYAL -> attacker.heal(2.0F);
            case FROST -> {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1), attacker);
                target.setTicksFrozen(Math.max(target.getTicksFrozen(), 160));
            }
            case ECLIPSE -> {
                target.setSecondsOnFire(4);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1), attacker);
                attacker.heal(2.0F);
            }
            default -> {}
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int cooldown = switch (ability) {
            case ROYAL -> 160;
            case FROST -> 200;
            case ECLIPSE -> 240;
            default -> 0;
        };
        if (cooldown == 0) return super.use(level, player, hand);

        if (level instanceof ServerLevel server) {
            castAbility(server, player);
            stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(hand));
        }
        player.getCooldowns().addCooldown(this, cooldown);
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void castAbility(ServerLevel level, Player player) {
        double x = player.getX(), y = player.getY(), z = player.getZ();
        switch (ability) {
            case ROYAL -> {
                for (LivingEntity e : AreaDamage.around(player, 4.5)) {
                    e.hurt(player.damageSources().playerAttack(player), 8.0F);
                    e.setDeltaMovement(e.getDeltaMovement().add(0, 0.6, 0));
                    e.hurtMarked = true;
                }
                level.sendParticles(ParticleTypes.EXPLOSION, x, y + 0.2, z, 3, 1.5, 0.1, 1.5, 0);
                level.sendParticles(ParticleTypes.SOUL, x, y + 0.3, z, 30, 2.5, 0.2, 2.5, 0.05);
                level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8F, 0.8F);
            }
            case FROST -> {
                for (LivingEntity e : AreaDamage.around(player, 5.5)) {
                    e.hurt(player.damageSources().playerAttack(player), 6.0F);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2), player);
                    e.setTicksFrozen(Math.max(e.getTicksFrozen(), 240));
                }
                ring(level, player.position(), 5.5, ParticleTypes.SNOWFLAKE);
                level.playSound(null, x, y, z, SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 0.6F);
                level.playSound(null, x, y, z, SoundEvents.POWDER_SNOW_STEP, SoundSource.PLAYERS, 1.0F, 0.5F);
            }
            case ECLIPSE -> {
                for (LivingEntity e : AreaDamage.around(player, 6.0)) {
                    e.hurt(player.damageSources().playerAttack(player), 10.0F);
                    e.setSecondsOnFire(5);
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1), player);
                }
                player.heal(4.0F);
                ring(level, player.position(), 6.0, ParticleTypes.SOUL_FIRE_FLAME);
                ring(level, player.position(), 4.0, ParticleTypes.FLAME);
                level.playSound(null, x, y, z, SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.8F, 0.7F);
            }
            default -> {}
        }
    }

    private static void ring(ServerLevel level, Vec3 center, double radius,
                             net.minecraft.core.particles.SimpleParticleType particle) {
        for (int i = 0; i < 48; i++) {
            double a = i * Math.PI * 2 / 48;
            level.sendParticles(particle, center.x + Math.cos(a) * radius, center.y + 0.3,
                    center.z + Math.sin(a) * radius, 1, 0, 0.1, 0, 0.01);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String key = "tooltip.ashen_realms." + net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(this).getPath();
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GOLD));
        if (ability == Ability.ROYAL || ability == Ability.FROST || ability == Ability.ECLIPSE) {
            tooltip.add(Component.translatable(key + ".active").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new RpgSwingExtensions(swing == Swing.SLAM));
    }
}
