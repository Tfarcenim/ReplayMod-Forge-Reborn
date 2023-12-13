package com.replaymod.replay.gui.overlay;

import java.util.function.Consumer;

import com.google.common.base.Strings;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiContainer;
import com.replaymod.lib.de.johni0702.minecraft.gui.container.GuiPanel;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiButton;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiElement;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiLabel;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiNumberField;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiTextField;
import com.replaymod.lib.de.johni0702.minecraft.gui.element.IGuiLabel;
import com.replaymod.lib.de.johni0702.minecraft.gui.function.Typeable;
import com.replaymod.lib.de.johni0702.minecraft.gui.layout.GridLayout;
import com.replaymod.lib.de.johni0702.minecraft.gui.layout.HorizontalLayout;
import com.replaymod.lib.de.johni0702.minecraft.gui.layout.VerticalLayout;
import com.replaymod.lib.de.johni0702.minecraft.gui.popup.AbstractGuiPopup;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.Colors;
import com.replaymod.lib.de.johni0702.minecraft.gui.utils.lwjgl.ReadablePoint;
import com.replaymod.replaystudio.data.Marker;

public class GuiEditMarkerPopup extends AbstractGuiPopup<GuiEditMarkerPopup> implements Typeable {
	private final Consumer<Marker> onSave;
	public final GuiLabel title = (new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.title.marker"
	);
	public final GuiTextField nameField = (new GuiTextField()).setSize(150, 20);
	public final GuiNumberField timeField = newGuiNumberField().setPrecision(0);
	public final GuiNumberField xField = newGuiNumberField().setPrecision(10);
	public final GuiNumberField yField = newGuiNumberField().setPrecision(10);
	public final GuiNumberField zField = newGuiNumberField().setPrecision(10);
	public final GuiNumberField yawField = newGuiNumberField().setPrecision(5);
	public final GuiNumberField pitchField = newGuiNumberField().setPrecision(5);
	public final GuiNumberField rollField = newGuiNumberField().setPrecision(5);
	public final GuiPanel inputs;
	public final GuiButton saveButton;
	public final GuiButton cancelButton;
	public final GuiPanel buttons;

	private static GuiNumberField newGuiNumberField() {
		return (new GuiNumberField()).setSize(150, 20)
				.setValidateOnFocusChange(true);
	}

	public GuiEditMarkerPopup(GuiContainer container, Marker marker, Consumer<Marker> onSave) {
		super(container);
		this.inputs = GuiPanel.builder().layout((new GridLayout()).setColumns(2).setSpacingX(7).setSpacingY(3))
				.with((new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.markername"),
						new GridLayout.Data(0.0D, 0.5D))
				.with(this.nameField, new GridLayout.Data(1.0D, 0.5D))
				.with((new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.timestamp"),
						new GridLayout.Data(0.0D, 0.5D))
				.with(this.timeField, new GridLayout.Data(1.0D, 0.5D))
				.with((new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.xpos"),
						new GridLayout.Data(0.0D, 0.5D))
				.with(this.xField, new GridLayout.Data(1.0D, 0.5D))
				.with((new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.ypos"),
						new GridLayout.Data(0.0D, 0.5D))
				.with(this.yField, new GridLayout.Data(1.0D, 0.5D))
				.with((new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.zpos"),
						new GridLayout.Data(0.0D, 0.5D))
				.with(this.zField, new GridLayout.Data(1.0D, 0.5D))
				.with((new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.camyaw"),
						new GridLayout.Data(0.0D, 0.5D))
				.with(this.yawField, new GridLayout.Data(1.0D, 0.5D))
				.with((new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.campitch"),
						new GridLayout.Data(0.0D, 0.5D))
				.with(this.pitchField, new GridLayout.Data(1.0D, 0.5D))
				.with((new GuiLabel()).setI18nText("replaymod.gui.editkeyframe.camroll"),
						new GridLayout.Data(0.0D, 0.5D))
				.with(this.rollField, new GridLayout.Data(1.0D, 0.5D)).build();
		this.saveButton = (new GuiButton()).onClick(new Runnable() {
			public void run() {
				Marker marker = new Marker();
				marker.setName(Strings.emptyToNull(GuiEditMarkerPopup.this.nameField.getText()));
				marker.setTime(GuiEditMarkerPopup.this.timeField.getInteger());
				marker.setX(GuiEditMarkerPopup.this.xField.getDouble());
				marker.setY(GuiEditMarkerPopup.this.yField.getDouble());
				marker.setZ(GuiEditMarkerPopup.this.zField.getDouble());
				marker.setYaw(GuiEditMarkerPopup.this.yawField.getFloat());
				marker.setPitch(GuiEditMarkerPopup.this.pitchField.getFloat());
				marker.setRoll(GuiEditMarkerPopup.this.rollField.getFloat());
				GuiEditMarkerPopup.this.onSave.accept(marker);
				GuiEditMarkerPopup.this.close();
			}
		}).setSize(150, 20).setI18nLabel("replaymod.gui.save");
		this.cancelButton = (new GuiButton()).onClick(new Runnable() {
			public void run() {
				GuiEditMarkerPopup.this.close();
			}
		}).setSize(150, 20).setI18nLabel("replaymod.gui.cancel");
		this.buttons = (new GuiPanel())
				.setLayout((new HorizontalLayout(HorizontalLayout.Alignment.CENTER)).setSpacing(7))
				.addElements(new HorizontalLayout.Data(0.5D), this.saveButton, this.cancelButton);
		this.onSave = onSave;
		this.setBackgroundColor(Colors.DARK_TRANSPARENT);
		this.popup.setLayout((new VerticalLayout()).setSpacing(5))
				.addElements(new VerticalLayout.Data(0.5D), this.title, this.inputs, this.buttons);
		this.popup.invokeAll(IGuiLabel.class, (e) -> {
			e.setColor(Colors.BLACK);
		});
		this.nameField.setText(Strings.nullToEmpty(marker.getName()));
		this.timeField.setValue(marker.getTime());
		this.xField.setValue(marker.getX());
		this.yField.setValue(marker.getY());
		this.zField.setValue(marker.getZ());
		this.yawField.setValue(marker.getYaw());
		this.pitchField.setValue(marker.getPitch());
		this.rollField.setValue(marker.getRoll());
	}

	public void open() {
		super.open();
	}

	protected GuiEditMarkerPopup getThis() {
		return this;
	}

	public boolean typeKey(ReadablePoint mousePosition, int keyCode, char keyChar, boolean ctrlDown,
			boolean shiftDown) {
		if (keyCode == 256) {
			this.cancelButton.onClick();
			return true;
		} else {
			return false;
		}
	}
}
