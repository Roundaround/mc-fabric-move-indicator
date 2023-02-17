package me.roundaround.moveindicator.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public enum MovementState {
  STANDING(0),
  WALKING(1),
  SPRINTING(2),
  SNEAKING(3),
  JUMPING(4),
  FALLING(5),
  SWIMMING(6),
  CLIMBING(7),
  RIDING(8),
  FLYING(9),
  SLEEPING(10),
  IN_FLUID(11),
  CREATIVE_FLYING(12);

  private static final Identifier TEXTURE = new Identifier("moveindicator", "textures/gui/indicators.png");
  private static final int TEXTURE_WIDTH = 256;
  private static final int TEXTURE_HEIGHT = 256;
  private static final int ICON_WIDTH = 18;
  private static final int ICON_HEIGHT = 18;
  private static final int ICONS_PER_ROW = MathHelper.floor((float) TEXTURE_WIDTH / ICON_WIDTH);
  private static final int BACKGROUND_WIDTH = 24;
  private static final int BACKGROUND_HEIGHT = 24;

  private int id;

  private MovementState(int id) {
    this.id = id;
  }

  private int getUPos() {
    return (id % ICONS_PER_ROW) * ICON_WIDTH;
  }

  private int getVPos() {
    return (id / ICONS_PER_ROW) * ICON_HEIGHT;
  }

  public void render(MatrixStack matrixStack, int x, int y) {
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    RenderSystem.setShaderTexture(0, HandledScreen.BACKGROUND_TEXTURE);
    DrawableHelper.drawTexture(
        matrixStack,
        x,
        y,
        141,
        166,
        BACKGROUND_WIDTH,
        BACKGROUND_HEIGHT,
        TEXTURE_WIDTH,
        TEXTURE_HEIGHT);

    RenderSystem.setShaderTexture(0, TEXTURE);
    DrawableHelper.drawTexture(
        matrixStack,
        x + (BACKGROUND_WIDTH - ICON_WIDTH) / 2,
        y + (BACKGROUND_HEIGHT - ICON_HEIGHT) / 2,
        getUPos(),
        getVPos(),
        ICON_WIDTH,
        ICON_HEIGHT,
        TEXTURE_WIDTH,
        TEXTURE_HEIGHT);
  }
}
