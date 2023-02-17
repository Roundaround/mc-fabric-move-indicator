package me.roundaround.moveindicator.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import me.roundaround.moveindicator.client.MovementState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
  @Shadow
  private MinecraftClient client;
  @Shadow
  private int scaledWidth;
  @Shadow
  private int scaledHeight;

  @Shadow
  protected abstract PlayerEntity getCameraPlayer();

  @Shadow
  public abstract TextRenderer getTextRenderer();

  @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"))
  private void afterRenderHotbar(float partialTicks, MatrixStack matrixStack, CallbackInfo info) {
    PlayerEntity basePlayer = getCameraPlayer();
    if (!(basePlayer instanceof ClientPlayerEntity)) {
      return;
    }

    ClientPlayerEntity player = (ClientPlayerEntity) basePlayer;
    Arm arm = player.getMainArm().getOpposite();

    int x = arm == Arm.LEFT ? (this.scaledWidth + 182) / 2 + 6 : (this.scaledWidth - 182) / 2 - 24 - 6;
    int y = this.scaledHeight - 24;

    // The order that we check these is important, because some of the states
    // require that the player not be in the other previously checked states
    MovementState state = MovementState.STANDING;

    if (player.getAbilities().flying) {
      // If have the flying ability, is creative/spectator flying
      state = MovementState.CREATIVE_FLYING;

    } else if (player.hasVehicle()) {
      // Check this early, because could also be "sprinting" or "swimming"
      state = MovementState.RIDING;

    } else if (player.isInSwimmingPose()) {
      // Simple, just check if in swimming pose
      state = MovementState.SWIMMING;

    } else if (player.isFallFlying()) {
      // If fallFlying, then is flying? Needs more testing
      state = MovementState.FLYING;

    } else if (player.isInPose(EntityPose.SLEEPING)) {
      // Simple, just check if in sleeping pose
      state = MovementState.SLEEPING;

    } else if (player.isClimbing()) {
      // Simple again, just check isClimbing
      state = MovementState.CLIMBING;

    } else if (player.isTouchingWater() || player.isInLava()) {
      // If touching fluid at all, then is in fluid. Check might need tweaked
      state = MovementState.IN_FLUID;

    } else if (player.isInSneakingPose()) {
      // Only actually sneaking if in crouching pose and not in any other state
      state = MovementState.SNEAKING;

    } else if (player.isSprinting()) {
      // Only actually sprinting if "sprinting" and not in any other state
      state = MovementState.SPRINTING;

    } else if (player.input.getMovementInput().lengthSquared() > 1.0E-5f) {
      // If moving but not in any other state, then is walking
      state = MovementState.WALKING;

    } else if (!player.isOnGround() && player.fallDistance <= 0) {
      // Jumping if not on ground and not falling
      state = MovementState.JUMPING;

    } else if (!player.isOnGround() && player.fallDistance > 0) {
      // Only falling if not on ground and not in any other state
      state = MovementState.FALLING;
    }

    state.render(matrixStack, x, y);
  }

  // @formatter:off
  @ModifyArgs(
    method = "renderHotbar",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
      ordinal = 0
    ),
    slice = @Slice(
      from = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;"
      ),
      to = @At(
        value = "INVOKE",
        target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"
      )
    )
  )
  // @formatter:on
  private void modifyArgsToFirstDrawTexture(Args args) {
    PlayerEntity player = getCameraPlayer();
    Arm arm = player.getMainArm().getOpposite();

    if (arm == Arm.LEFT) {
      args.set(1, (int) args.get(1) + 26);
    } else {
      args.set(1, (int) args.get(1) - 26);
    }
  }

  // @formatter:off
  @ModifyArgs(
    method = "renderHotbar",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
      ordinal = 1
    ),
    slice = @Slice(
      from = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;"
      ),
      to = @At(
        value = "INVOKE",
        target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"
      )
    )
  )
  // @formatter:on
  private void modifyArgsToSecondDrawTexture(Args args) {
    PlayerEntity player = getCameraPlayer();
    Arm arm = player.getMainArm().getOpposite();

    if (arm == Arm.LEFT) {
      args.set(1, (int) args.get(1) + 26);
    } else {
      args.set(1, (int) args.get(1) - 26);
    }
  }
}
