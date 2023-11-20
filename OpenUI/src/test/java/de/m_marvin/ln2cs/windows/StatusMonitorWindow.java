package de.m_marvin.ln2cs.windows;

import de.m_marvin.ln2cs.ParameterDataSet;
import de.m_marvin.openui.core.components.Compound;
import de.m_marvin.openui.core.layout.BorderLayout;
import de.m_marvin.openui.core.layout.BorderLayout.BorderSection;
import de.m_marvin.openui.core.layout.BorderLayout.CornerStretch;
import de.m_marvin.openui.core.layout.GridLayout;
import de.m_marvin.openui.flatmono.Window;
import de.m_marvin.openui.flatmono.components.BarComponent;
import de.m_marvin.openui.flatmono.components.ButtonComponent;
import de.m_marvin.openui.flatmono.components.GroupBox;
import de.m_marvin.openui.flatmono.components.PointerDisplayComponent;
import de.m_marvin.openui.flatmono.components.ScrollBarComponent;
import de.m_marvin.openui.flatmono.components.ToggleButtonComponent;
import de.m_marvin.renderengine.resources.defimpl.ResourceLocation;
import de.m_marvin.univec.impl.Vec2i;

public class StatusMonitorWindow extends Window {
	
	protected final ParameterDataSet dataSet;
	
	protected PointerDisplayComponent pd_evaporatorPressure;
	protected PointerDisplayComponent pd_evaporatorTemperatur;
	protected PointerDisplayComponent pd_condensatorTemperatur;
	
	protected PointerDisplayComponent pd_condenserFanSpeed;
	protected BarComponent sb_compressorPower;
	protected BarComponent sb_evaporatorTargetTemperatur;
	protected BarComponent sb_condenserMaxTemperature;
	
	protected ToggleButtonComponent sw_condenserFanOverride;
	protected ToggleButtonComponent sw_condenserFanOnOff;
	protected ToggleButtonComponent sw_compressorOverride;
	protected ToggleButtonComponent sw_compressorOnOff;
	protected ToggleButtonComponent sw_expansionValveOverride;
	protected BarComponent sb_expansionValvePosition;
	protected ButtonComponent sw_expansionValveReset;
	protected ScrollBarComponent sc_expansionValvePosition;
	
	
	public StatusMonitorWindow(ParameterDataSet dataSet) {
		super("LN2_CS Status Monitor");
		this.dataSet = dataSet;
	}
	
	public ParameterDataSet getDataSet() {
		return dataSet;
	}
	
	@Override
	protected void initUI() {
		
		Compound<ResourceLocation> bg = this.getRootComponent();
		
		// Left side
		
		GroupBox groupleft = new GroupBox();
		groupleft.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.LEFT));
		bg.addComponent(groupleft);
		
		this.pd_evaporatorTemperatur = new PointerDisplayComponent(-50, 50, (t) -> String.format("%.0fC°", t));
		this.pd_evaporatorTemperatur.setLayoutData(new GridLayout.GridLayoutData(0, 0));
		groupleft.addComponent(pd_evaporatorTemperatur);
		
		this.pd_evaporatorPressure = new PointerDisplayComponent(-1, 10, (p) -> String.format("%.2fbar", p));
		this.pd_evaporatorPressure.setLayoutData(new GridLayout.GridLayoutData(0, 1));
		groupleft.addComponent(pd_evaporatorPressure);
		
		this.pd_condensatorTemperatur = new PointerDisplayComponent(0, 60, (t) -> String.format("%.0fC°", t));
		this.pd_condensatorTemperatur.setLayoutData(new GridLayout.GridLayoutData(0, 2));
		groupleft.addComponent(pd_condensatorTemperatur);
		
		groupleft.setLayout(new GridLayout());
		groupleft.autoSetMinSize();
		groupleft.autoSetMaxSize();
		
		// Right side
		
		GroupBox groupright = new GroupBox();
		
		this.pd_condenserFanSpeed = new PointerDisplayComponent(0, 20, (t) -> String.format("%.0f/min", t));
		this.pd_condenserFanSpeed.setScalaNumberScale(10);
		this.pd_condenserFanSpeed.setLayoutData(new GridLayout.GridLayoutData(0, 0));
		groupright.addComponent(pd_condenserFanSpeed);
		
		Compound<ResourceLocation> statusBarGroup = new Compound<>();
		int barWidth = this.pd_condenserFanSpeed.getSizeMargin().x / 3;
		int barHeight = this.pd_condenserFanSpeed.getSizeMargin().y * 2;
		
		this.sb_compressorPower = new BarComponent(false, 0, 100, (p) -> String.format("COMP_PWR %d%%", p));
		this.sb_compressorPower.setLayoutData(new GridLayout.GridLayoutData(0, 1));
		this.sb_compressorPower.setSizeMargin(new Vec2i(barWidth, barHeight));
		this.sb_compressorPower.fixSize();
		statusBarGroup.addComponent(sb_compressorPower);

		this.sb_condenserMaxTemperature = new BarComponent(false, 0, CONDENSER_MAX_TEMPERATUR, (p) -> String.format("COND_MAX_TEMP %dC°", p));
		this.sb_condenserMaxTemperature.setLayoutData(new GridLayout.GridLayoutData(1, 1));
		this.sb_condenserMaxTemperature.setSizeMargin(new Vec2i(barWidth, barHeight));
		this.sb_condenserMaxTemperature.fixSize();
		statusBarGroup.addComponent(sb_condenserMaxTemperature);

		this.sb_evaporatorTargetTemperatur = new BarComponent(false, 0, -EVAPORATOR_TARGET_TEMPERATUR, (p) -> String.format("EVAP_TRGT_TEMP %dC°", -p));
		this.sb_evaporatorTargetTemperatur.setLayoutData(new GridLayout.GridLayoutData(2, 1));
		this.sb_evaporatorTargetTemperatur.setSizeMargin(new Vec2i(barWidth, barHeight));
		this.sb_evaporatorTargetTemperatur.fixSize();
		statusBarGroup.addComponent(sb_evaporatorTargetTemperatur);
		
		statusBarGroup.setLayout(new GridLayout());
		statusBarGroup.setLayoutData(new GridLayout.GridLayoutData(0, 1));
		statusBarGroup.autoSetMaxAndMinSize();
		groupright.addComponent(statusBarGroup);
		
		groupright.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.RIGHT));
		groupright.setLayout(new GridLayout());
		groupright.autoSetMaxAndMinSize();
		bg.addComponent(groupright);
		
		// Top
		
		GroupBox grouptop = new GroupBox();
		grouptop.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.TOP));
		bg.addComponent(grouptop);
		
		// Bottom
		
		GroupBox groupdown = new GroupBox();
		
		Compound<ResourceLocation> buttonGroup = new Compound<>();
		int buttonWidth = 200;
		int buttonHeight = 24;
		
		this.sw_compressorOverride = new ToggleButtonComponent("COMP_OVRID");
		this.sw_compressorOverride.setLayoutData(new GridLayout.GridLayoutData(0, 0));
		this.sw_compressorOverride.setSize(new Vec2i(buttonWidth, buttonHeight));
		this.sw_compressorOverride.fixSize();
		buttonGroup.addComponent(sw_compressorOverride);

		this.sw_compressorOnOff = new ToggleButtonComponent("COMP_ON");
		this.sw_compressorOnOff.setLayoutData(new GridLayout.GridLayoutData(0, 1));
		this.sw_compressorOnOff.setSize(new Vec2i(buttonWidth, buttonHeight));
		this.sw_compressorOnOff.fixSize();
		buttonGroup.addComponent(sw_compressorOnOff);

		this.sw_condenserFanOverride = new ToggleButtonComponent("CNDF_OVRID");
		this.sw_condenserFanOverride.setLayoutData(new GridLayout.GridLayoutData(1, 0));
		this.sw_condenserFanOverride.setSize(new Vec2i(buttonWidth, buttonHeight));
		this.sw_condenserFanOverride.fixSize();
		buttonGroup.addComponent(sw_condenserFanOverride);

		this.sw_condenserFanOnOff = new ToggleButtonComponent("CNDF_ON");
		this.sw_condenserFanOnOff.setLayoutData(new GridLayout.GridLayoutData(1, 1));
		this.sw_condenserFanOnOff.setSize(new Vec2i(buttonWidth, buttonHeight));
		this.sw_condenserFanOnOff.fixSize();
		buttonGroup.addComponent(sw_condenserFanOnOff);

		this.sw_expansionValveOverride = new ToggleButtonComponent("EXPV_OVRID");
		this.sw_expansionValveOverride.setLayoutData(new GridLayout.GridLayoutData(0, 2));
		this.sw_expansionValveOverride.setSize(new Vec2i(buttonWidth, buttonHeight));
		this.sw_expansionValveOverride.fixSize();
		buttonGroup.addComponent(sw_expansionValveOverride);

		this.sw_expansionValveReset = new ButtonComponent("EXPV_RST");
		this.sw_expansionValveReset.setLayoutData(new GridLayout.GridLayoutData(0, 3));
		this.sw_expansionValveReset.setSize(new Vec2i(buttonWidth, buttonHeight));
		this.sw_expansionValveReset.fixSize();
		buttonGroup.addComponent(sw_expansionValveReset);

		this.sb_expansionValvePosition = new BarComponent(true, 0, 100, (p) -> String.format("EXPV_STATE %d%%", p));
		this.sb_expansionValvePosition.setLayoutData(new GridLayout.GridLayoutData(1, 2));
		this.sb_expansionValvePosition.setSize(new Vec2i(buttonWidth, buttonHeight));
		this.sb_expansionValvePosition.fixSize();
		buttonGroup.addComponent(sb_expansionValvePosition);
		
		this.sc_expansionValvePosition = new ScrollBarComponent(true, 110, 10);
		this.sc_expansionValvePosition.setLayoutData(new GridLayout.GridLayoutData(1, 3));
		this.sc_expansionValvePosition.setSize(new Vec2i(buttonWidth, buttonHeight));
		this.sc_expansionValvePosition.fixSize();
		buttonGroup.addComponent(sc_expansionValvePosition);
		
		buttonGroup.setLayout(new GridLayout());
		buttonGroup.autoSetMaxAndMinSize();
		groupdown.addComponent(buttonGroup);
		
		groupdown.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.BOTTOM));
		groupdown.setLayout(new GridLayout());
		groupdown.autoSetMaxAndMinSize();
		groupdown.setSizeMax(new Vec2i(0, groupdown.getSizeMax().y));
		bg.addComponent(groupdown);
		
		// Center
		
		GroupBox center = new GroupBox();
		center.setLayoutData(new BorderLayout.BorderLayoutData(BorderSection.CENTERED));
		bg.addComponent(center);
		
		bg.setLayout(new BorderLayout(CornerStretch.VERTICAL));
		bg.autoSetMaxAndMinSize();
		
	}
	
	public static final int CONDENSER_MAX_TEMPERATUR = 30;
	public static final int EVAPORATOR_TARGET_TEMPERATUR = -30;
	
	public void update() {
		
		if (this.isInitialized()) {
			this.pd_condensatorTemperatur.setValue(this.dataSet.condesserTemperature);
			this.pd_evaporatorTemperatur.setValue(this.dataSet.evaporatorTemperature);
			this.pd_evaporatorPressure.setValue(this.dataSet.evaportatorPressure / 1000);
			this.sb_compressorPower.setValue((int) this.dataSet.compressorPower);
			this.sb_condenserMaxTemperature.setValue((int) this.dataSet.condesserTemperature);
			this.sb_evaporatorTargetTemperatur.setValue((int) -this.dataSet.evaporatorTemperature);
			this.sb_expansionValvePosition.setValue((int) this.dataSet.expansionValveState);
		}
		
	}
	
}
