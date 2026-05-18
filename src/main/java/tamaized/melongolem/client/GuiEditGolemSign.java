package tamaized.melongolem.client;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.IMEPreeditOverlay;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.joml.Vector2f;
import org.joml.Vector3f;
import tamaized.melongolem.ISignHolder;
import tamaized.melongolem.network.server.ServerPacketMelonSign;

import javax.annotation.Nullable;

public class GuiEditGolemSign extends Screen {

	private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);

	private final ISignHolder golem;
	private long cursorBlinkStartTime;
	private int editLine;
	private boolean canSend = true;

	@Nullable
	private Model.Simple signModel;

	@Nullable
	private TextFieldHelper textInputUtil;
	private @Nullable IMEPreeditOverlay preeditOverlay;
	private final Vector2f cursorPosScratch;

	public GuiEditGolemSign(ISignHolder golem) {
		super(Component.translatable("sign.edit"));
		this.golem = golem;
		this.cursorPosScratch = new Vector2f();
	}

	@Override
	protected void init() {
		this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), (_) -> this.onClose()).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
		this.cursorBlinkStartTime = Util.getMillis();
		this.textInputUtil = new TextFieldHelper(

				() -> golem.getSignText(this.editLine).getString(),

				text -> golem.setSignText(editLine, Component.literal(text)),

				TextFieldHelper.createClipboardGetter(this.minecraft),

				TextFieldHelper.createClipboardSetter(this.minecraft),

				(string) -> this.minecraft.font.width(string) <= 90

		);
		signModel = StandingSignRenderer.createSignModel(minecraft.getEntityModels(), ((SignBlock)golem.getSignTileEntity().getBlockState().getBlock()).type(), PlainSignBlock.Attachment.WALL);
	}

	public boolean preeditUpdated(@org.jspecify.annotations.Nullable PreeditEvent event) {
		this.preeditOverlay = event != null ? new IMEPreeditOverlay(event, this.font, getTextLineHeight()) : null;
		return true;
	}

	@Override
	public void onClose() {
		if (canSend)
			ClientPacketDistributor.sendToServer(new ServerPacketMelonSign(golem));
		this.minecraft.setScreen(null);
	}

	@Override
	public void tick() {
		if (minecraft.player == null || golem.distanceTo(minecraft.player) > 6) {
			canSend = false;
			onClose();
		}
	}

	@Override
	public boolean charTyped(CharacterEvent typedChar) {
		if (textInputUtil == null)
			return false;

		textInputUtil.charTyped(typedChar);
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (textInputUtil == null)
			return false;

		if (event.key() == 265) {
			this.editLine = this.editLine - 1 & 3;
			this.textInputUtil.setCursorToEnd();
			return true;
		} else if (event.key() != 264 && event.key() != 257 && event.key() != 335) {
			return this.textInputUtil.keyPressed(event) || super.keyPressed(event);
		} else {
			this.editLine = this.editLine + 1 & 3;
			this.textInputUtil.setCursorToEnd();
			return true;
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		graphics.centeredText(this.font, this.title, this.width / 2, 40, -1);
		this.extractSign(graphics);
	}

	private float getSignYOffset() {
		return 90.0F;
	}

	private int getTextLineHeight() {
		return 10;
	}

	private void extractSign(GuiGraphicsExtractor graphics) {
		graphics.pose().pushMatrix();
		float offsetX = (float)this.width / 2.0F;
		float offsetY = this.getSignYOffset();
		graphics.pose().translate(offsetX, offsetY);
		graphics.pose().pushMatrix();
		this.extractSignBackground(graphics);
		graphics.pose().popMatrix();
		graphics.pose().scale(TEXT_SCALE.x(), TEXT_SCALE.y());
		this.cursorPosScratch.zero();
		this.extractSignText(graphics, this.cursorPosScratch);
		graphics.pose().popMatrix();
		if (this.preeditOverlay != null) {
			this.cursorPosScratch.mul(TEXT_SCALE.x(), TEXT_SCALE.y()).add(offsetX, offsetY);
			this.preeditOverlay.updateInputPosition((int)this.cursorPosScratch.x, (int)this.cursorPosScratch.y);
			graphics.setPreeditOverlay(this.preeditOverlay);
		}
	}

	public void extractSignBackground(GuiGraphicsExtractor graphics) {
		if (this.signModel != null) {
			int centerX = this.width / 2;
			int x0 = centerX - 48;
			int x1 = centerX + 48;
			graphics.sign(
				this.signModel,
				62.500004F,
				((SignBlock)golem.getSignTileEntity().getBlockState().getBlock()).type(),
				x0, 66, x1, 168);
		}
	}

	private int getDarkColor() {
		int color = golem.getTextColor().getTextColor();
		return color == DyeColor.BLACK.getTextColor() && golem.glowingText() ? -988212 : ARGB.scaleRGB(color, 0.4F);
	}

	private void extractSignText(GuiGraphicsExtractor graphics, Vector2f cursorPosOutput) {
		if (textInputUtil == null)
			return;

		int color = golem.glowingText() ? golem.getTextColor().getTextColor() : getDarkColor();
		boolean showCursor = TextCursorUtils.isCursorVisible(Util.getMillis() - this.cursorBlinkStartTime);
		boolean needsValidCursorPos = this.preeditOverlay != null;
		int cursorPos = this.textInputUtil.getCursorPos();
		int selectionPos = this.textInputUtil.getSelectionPos();
		int signMidpoint = 4 * getTextLineHeight() / 2;
		int cursorY = this.editLine * getTextLineHeight() - signMidpoint;

		for(int i = 0; i < golem.getSignTextList().size(); ++i) {
			String line = golem.getSignText(i).getString();
			if (this.font.isBidirectional()) {
				line = this.font.bidirectionalShaping(line);
			}

			int x1 = -this.font.width(line) / 2;
			graphics.text(this.font, line, x1, i * getTextLineHeight() - signMidpoint, color, false);
			if (i == editLine && cursorPos >= 0 && (showCursor || needsValidCursorPos)) {
				int cursorPosition = this.font.width(line.substring(0, Math.min(cursorPos, line.length())));
				int cursorX = cursorPosition - this.font.width(line) / 2;
				if (cursorPos >= line.length()) {
					if (showCursor) {
						TextCursorUtils.extractAppendCursor(graphics, this.font, cursorX, cursorY, color, false);
					}

					cursorPosOutput.set((float) cursorX, (float) cursorY);
				}
			}
		}

		for(int ix = 0; ix < golem.getSignTextList().size(); ++ix) {
			String line = golem.getSignText(ix).getString();
			if (ix == this.editLine && cursorPos >= 0) {
				int cursorPosition = this.font.width(line.substring(0, Math.min(cursorPos, line.length())));
				int cursorX = cursorPosition - this.font.width(line) / 2;
				if (cursorPos < line.length()) {
					if (showCursor) {
						TextCursorUtils.extractInsertCursor(graphics, cursorX, cursorY, ARGB.opaque(color), getTextLineHeight());
					}

					cursorPosOutput.set((float)cursorX, (float)cursorY);
				}

				if (selectionPos != cursorPos) {
					int startIndex = Math.min(cursorPos, selectionPos);
					int endIndex = Math.max(cursorPos, selectionPos);
					int startPosX = this.font.width(line.substring(0, startIndex)) - this.font.width(line) / 2;
					int endPosX = this.font.width(line.substring(0, endIndex)) - this.font.width(line) / 2;
					int fromX = Math.min(startPosX, endPosX);
					int toX = Math.max(startPosX, endPosX);
					graphics.textHighlight(fromX, cursorY, toX, cursorY + getTextLineHeight(), true);
				}
			}
		}
	}
}
