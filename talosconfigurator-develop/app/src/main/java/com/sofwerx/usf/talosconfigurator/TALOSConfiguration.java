package com.sofwerx.usf.talosconfigurator;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Display;
import org.simpleframework.xml.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by dburkhol on 10/31/17.
 */

@Root
public class TALOSConfiguration {

    @Element
    private int version;

    @Attribute(required=false)
    private String noNamespaceSchemaLocation;

    @Element(name="displaydef")
    private DisplayDef displayDef;

    @ElementList(name="elementdef")
    private List<ConfigurableElement> configurableElements;


    @ElementList(name="modedef", required=false)
    private List<Mode> modes;

    @Element(name="TALOSControlsConfig", required=false)
    private TALOSControlsConfiguration controlsConfiguration;

    private List<ConfigurableElement> lookdownElements = new ArrayList<ConfigurableElement>();

    private List<ConfigurableElement> hudElements = new ArrayList<ConfigurableElement>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DisplayDef getDisplayDef() {
        return displayDef;
    }

    public void setDisplayDef(DisplayDef displayDef) {
        this.displayDef = displayDef;
    }


    public List<ConfigurableElement> getConfigurableElements() {
        return configurableElements;
    }

    public void setConfigurableElements(List<ConfigurableElement> configurableElements) {
        this.configurableElements = configurableElements;
    }

    public List<Mode> getModes() {
        return modes;
    }

    public void setModes(List<Mode> modes) {
        this.modes = modes;
    }

    // TODO: Add clears for button configuration as well
    public void clearConfiguration() {
        modes = (List<Mode>) new ArrayList<Mode>();
    }

    public TALOSControlsConfiguration getControlsConfiguration() {
        return controlsConfiguration;
    }

    // Attempt to fetch mode by index. If DNE, create a new mode at the desired index
    public Mode getModeByIndex(int idx) {
        try {
            return modes.get(idx);
        } catch (IndexOutOfBoundsException e) {
            Mode newMode = new Mode();
            newMode.setId(idx);
            newMode.setName(idx + "");
            modes.add(newMode);
            return modes.get(idx);
        }
    }

    public void removeModeByIdx(int idx) {
        try {
            modes.remove(idx);
        } catch (IndexOutOfBoundsException e) {
            // Do nuttin
        }
    }

    public void renameModeByIndex(int idx, String name) {
        Mode m = this.getModeByIndex(idx);
        m.setName(name);
    }

    public HashMap<String, List<String>> getModeMap(TALOSConfiguration currentConfiguration) {
        HashMap<String, List<String>> elements = new HashMap<String, List<String>>();
        for (ConfigurableElement ele : currentConfiguration.getHUDConfigurableElements()) {
            String key = ele.getName();
            if (ele.getFormats() == null) {
                continue;
            }
            List<String> ele2 = new ArrayList<>();
            for (Format ele1 : ele.getFormats()) {
                ele2.add(ele1.getName());

            }
            elements.put(key, ele2);
        }
        return elements;

    }

    public ArrayList<String> getGroupNames(HashMap<String, List<String>> map) {

        ArrayList<String> just = new ArrayList<>();
        Set<String> keys = map.keySet();
        for(String key: keys) {
            just.add(key);
        }


        return just;
    }

    public ConfigurableElement getConfigElement(String elementName) {
        for(ConfigurableElement configurableElement: configurableElements) {
            if(configurableElement.getName().equals(elementName)) {
                return configurableElement;
            }
        }
        return null;
    }

    // Returns a list of only lookdown elements
    public List<ConfigurableElement> getLookdownConfigurableElements() {
        if (this.lookdownElements.isEmpty()) {
            List<ConfigurableElement> cElements = this.getConfigurableElements();
            for (ConfigurableElement c : cElements) {
                for (String location : c.getLocations()) {
                    if (location.contains("ld")) {
                        this.lookdownElements.add(c);
                        break;
                    }
                }
            }
        }
        return this.lookdownElements;
    }

    // Returns a list of only HUD elements
    public List<ConfigurableElement> getHUDConfigurableElements() {
        if (this.hudElements.isEmpty()) {
            List<ConfigurableElement> hElements = this.getConfigurableElements();
            for (ConfigurableElement c : hElements) {
                for (String location : c.getLocations()) {
                    if (location.contains("hud")) {
                        this.hudElements.add(c);
                        break;
                    }
                }
            }
        }
        return this.hudElements;
    }


    public void deleteModeByIdx(int idx) {
        this.modes.remove(idx);
    }

    public Mode createNewMode() {
        //Create mode to translate to ModeDef in the xml
        Mode md = new Mode();
        this.modes.add(md);
        md.setId(this.modes.size() - 1);
        md.setName((this.modes.size() - 1) + "");
        //Create mode to translate to Setting in the xml
        ButtonMode bm = new ButtonMode();
        getControlsConfiguration().getButtonSettings().getButtonModes().add(bm);
        bm.setId(getControlsConfiguration().getButtonSettings().getButtonModes().size()-1);
        bm.setTimeout(30); //TODO: Can the operator change the timeout of button modes?
        bm.setButtonIntents(getControlsConfiguration().getButtonSettings().getButtonModes().get(0).getButtonIntents());
        return md;
    }
}

@Root(name="displaydef")
class DisplayDef {
    @Element
    private Cell cell;

    @ElementList
    private List<Screen> screens;

    public Cell getCell() {
        return cell;
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public void setupScreenScale() {
        for (Screen scr : this.getScreens()) {
            scr.setupCells(cell);
        }
    }
}

@Root
class Cell {
    @Element(name="x_pixel")
    private int xPixel;

    @Element(name="y_pixel")
    private int yPixel;

    public int getxPixel() {
        return xPixel;
    }

    public int getyPixel() {
        return yPixel;
    }
}

@Root(name="display")
class Screen {
    @Element(name="disp_name")
    private String name;

    @Element
    private boolean active;

    @Element(name="x_res")
    private int xRes;

    @Element(name="y_res")
    private int yRes;

    @Element(name="max_x_cell", required=false)
    private int maxXCell;

    @Element(name="max_y_cell", required=false)
    private int maxYCell;

    private int xCells, yCells;

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public int getxRes() {
        return xRes;
    }

    public int getyRes() {
        return yRes;
    }

    public int getMaxXCell() {
        return maxXCell;
    }

    public int getMaxYCell() {
        return maxYCell;
    }

    public int getxCells() {
        return xCells;
    }

    public void setxCells(int xCells) {
        this.xCells = xCells;
    }

    public int getyCells() {
        return yCells;
    }

    public void setyCells(int yCells) {
        this.yCells = yCells;

    }

    // Setups X, Y cell scale
    public void setupCells(Cell cell) {
        int x = xRes/cell.getxPixel();
        int y = yRes/cell.getyPixel();

        if (x <= maxXCell) {
            this.setxCells(x);
        } else {
            this.setxCells(maxXCell);
        }

        if (y <= maxYCell) {
            this.setxCells(y);
        } else {
            this.setxCells(maxYCell);
        }
    }
}

@Root(name="element")
class ConfigurableElement {
    @Element(name="ele_name")
    private String name;

    @ElementList(entry="location", inline=true)
    private List<String> locations;

    @ElementList(entry="format", inline=true, required=false)
    private List<Format> formats;

    @ElementList(entry="subelement", inline=true, required=false)
    private List<String> subelements;

    @ElementList(entry="unit", inline=true, required=false)
    private List<String> units;

    public String getName() {
        return name;
    }

    public List<String> getLocations() {
        return locations;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public List<String> getSubelements() {
        return subelements;
    }

    public List<String> getUnits() {
        return units;
    }
}

@Root(name="format")
class Format implements Parcelable {
    @Element(name="fmt_name")
    private String name;

    @Element(name="x_cell")
    private int xPos;

    @Element(name="y_cell")
    private int yPos;

    @ElementList(entry="style", inline=true, required=false)
    private List<String> style;

    public Format() {

    }

    public Format(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Format> CREATOR = new Parcelable.Creator<Format>() {

        public Format createFromParcel(Parcel in) {
            return new Format(in);
        }


        public Format[] newArray(int size) {
            return new Format[size];
        }
    };

    public void readFromParcel(Parcel in) {
        name = in.readString();
        xPos = in.readInt();
        yPos = in.readInt();
        style = new ArrayList<String>();
        in.readList(style, ClassLoader.getSystemClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(xPos);
        dest.writeInt(yPos);
        dest.writeList(style);
    }

    public String getName() {
        return name;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public List<String> getStyle() {
        return style;
    }
}

@Root(name="mode", strict=false)
class Mode {
    @Element(name="mode_id")
    private int id;

    @Element(name="mode_name")
    private String name;

    @ElementList(entry="element", inline=true, required=false)
    private List<ModeElement> elements;

    @ElementList(entry="test", inline=true, required=false)
    private List<String> tests;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ModeElement> getElements() {
        return elements;
    }

    public void setElements(List<ModeElement> elements) {
        this.elements = elements;
    }
}

@Root(name="element", strict=false)
class ModeElement implements Parcelable {
    @Element(name="ele_name")
    private String name;

    @Element(name="active")
    private boolean active;

    @Element(name="position")
    private Position position;

    @ElementList(entry="test", required=false)
    private List<String> tests;

    @ElementList(entry="subelement", inline=true, required=false)
    private List<String> subelements;

    @ElementList(entry="unit", inline=true, required=false)
    private List<String> unit;

    @Element(name="format", required=false)
    private String format;

    @Element(name="type", required=false)
    private String type;




    public ModeElement() {}

    public ModeElement( String val, Position position, String format) {
        active = true;
        name = val;
        this.position = position;
        this.format = format;
    }

    public ModeElement(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<ModeElement> CREATOR = new Parcelable.Creator<ModeElement>() {
        public ModeElement createFromParcel(Parcel in) {
            return new ModeElement(in);
        }

        public ModeElement[] newArray(int size) {
            return new ModeElement[size];
        }

    };

    public void readFromParcel(Parcel in) {
        name = in.readString();
        List<String> screens = new ArrayList<String>();
        in.readList(screens, ClassLoader.getSystemClassLoader());
        position = new Position(screens,in.readInt(), in.readInt());
        format = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(position.getScreen());
        dest.writeInt(position.getxPos());
        dest.writeInt(position.getyPos());
        dest.writeString(format);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<String> getSubelements() {
        return subelements;
    }

    public void setSubelements(List<String> subelements) {
        this.subelements = subelements;
    }

    public List<String> getUnit() {
        return unit;
    }

    public void setUnit(List<String> unit) {
        this.unit = unit;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}

@Root(name="position", strict=false)
class Position {
    @ElementList(entry="display", inline=true, required=false)
    private List<String> screen;

    @Element(name="x_cell", required=false)
    private int xPos;

    @Element(name="y_cell", required=false)
    private int yPos;

    @Element(name="test", required=false)
    private String test;

    public Position() {}

    public Position(List<String> screens, int x, int y) {
        screen = screens;
        xPos = x;
        yPos = y;
    }

    public List<String> getScreen() {
        return screen;
    }

    public void setScreen(List<String> screen) {
        this.screen = screen;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
}

@Root(name="TALOSControlsConfig")
class TALOSControlsConfiguration {
    @Element(name="functionlist")
    private FunctionList functionlList;

    @ElementList(entry="control", inline=true)
    private List<ControlModule> controlModules;

    @Element(name="setting")
    private ButtonSettings buttonSettings;

    public List<ControlModule> getControlModules() {
        return controlModules;
    }

    public ButtonSettings getButtonSettings() {
        return buttonSettings;
    }

    public List<ButtonFunction> getButtonFunctions() {
        return functionlList.getButtonFunctions();
    }
}

@Root(name="functionlist")
class FunctionList {
    @ElementList(entry="function", inline=true)
    private List<ButtonFunction> buttonFunctions;

    public List<ButtonFunction> getButtonFunctions() {
        return buttonFunctions;
    }
}

@Root(name="function", strict=false)
class ButtonFunction {
    @Element(name="func_name")
    private String name;

    @Element(name="text")
    private String description;

    @Element(name="command", required=false)
    private String command;

    @Element(name="vasAction", required=false)
    private String vasAction;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCommand() {
        return command;
    }

    public String getVasAction() {
        return vasAction;
    }
}

@Root(name="control")
class ControlModule {
    @Element(name="ctl_name")
    private String name;

    @Element(name="btn_cfg_x")
    private int x;

    @Element(name="btn_cfg_y")
    private int y;

    @ElementList(entry="button", inline=true)
    private List<Button> buttons;

    public String getModuleName() {
        return name;
    }

    public int getXSize() {
        return x;
    }

    public int getYSize() {
        return y;
    }

    public List<Button> getModuleButtons() {
        return buttons;
    }

    public int[][] getLayout() { return layout; }

    public void setLayout() {
        List<Button> controlBtns = getModuleButtons();
        int[][] controlLayout = new int[getXSize()][getYSize()];
        for(int i = 0; i < controlBtns.size(); i++){
            controlLayout[controlBtns.get(i).getXPosition()-1][controlBtns.get(i).getYPosition()-1] = 1;
        }
        this.layout = controlLayout;
    }

    private int[][] layout;
}

@Root(name="button")
class Button {
    @Element(name="number")
    private int number;

    @Element(name="pos_x")
    private int x;

    @Element(name="pos_y")
    private int y;

    @Element(name="key")
    private int key;

    public int getButtonNumber() {
        return number;
    }

    public int getXPosition() {
        return x;
    }

    public int getYPosition() {
        return y;
    }

    public int getKeyNumber() {
        return key;
    }
}

@Root(name="setting")
class ButtonSettings {
    @ElementList(name="mode", inline=true)
    private List<ButtonMode> buttonModes;

    public List<ButtonMode> getButtonModes() {
        return buttonModes;
    }
}

@Root(name="mode")
class ButtonMode {
    @Element(name="mode_id")
    private int id;

    // Why is a capital T...
    @Element(name="Timeout")
    private int timeout;

    @ElementList(name="intent", inline=true)
    private List<ButtonIntent> buttonIntents;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<ButtonIntent> getButtonIntents() {
        return buttonIntents;
    }

    public void setButtonIntents(List<ButtonIntent> buttonIntents) {
        this.buttonIntents = buttonIntents;
    }
}

@Root(name="intent")
class ButtonIntent {
    @Element(name="key")
    private int key;

    @Element(name="func_name")
    private String functionName;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}