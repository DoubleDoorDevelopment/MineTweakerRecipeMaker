package net.doubledoordev.mtrm.gui;

import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.network.MessageResponse;
import net.doubledoordev.mtrm.network.MessageSend;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.doubledoordev.mtrm.gui.MTRMContainer.RETURN_SLOT_ID;

/**
 * @author Dries007
 */
public class MTRMGui extends GuiContainer
{
    private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("mtrm:gui/mtrm.png");

    private static final Pattern MATCH_ALL = Pattern.compile("<\\*>");
    private static final Pattern META_WILDCARD = Pattern.compile(":\\*>");
    private static final Pattern ORE_DICT = Pattern.compile("<ore:.*>");
    private static final Pattern REUSE = Pattern.compile("\\.reuse\\(\\)");
    private static final Pattern NO_RETURN = Pattern.compile("\\.noReturn\\(\\)");
    private static final Pattern ANY_DAMAGE = Pattern.compile("\\.anyDamage\\(\\)");
    private static final Pattern ONLY_DAMAGE = Pattern.compile("\\.onlyDamaged\\(\\)");
    private static final Pattern ONLY_DAMAGE_AT_MOST = Pattern.compile("\\.onlyDamageAtMost\\((\\d+)\\)");
    private static final Pattern ONLY_DAMAGE_AT_LEAST = Pattern.compile("\\.onlyDamageAtLeast\\((\\d+)\\)");
    private static final Pattern ONLY_DAMAGE_BETWEEN = Pattern.compile("\\.onlyDamageBetween\\((\\d+), ?(\\d+)\\)");
    private static final Pattern WITH_DAMAGE = Pattern.compile("\\.withDamage\\((\\d+)\\)");
    private static final Pattern TRANSFORM_DAMAGE = Pattern.compile("\\.transformDamage\\((\\d+)\\)");
    private static final Pattern GIVE_BACK = Pattern.compile("\\.giveBack\\(<(.*):(.*)(:\\d+)?>(?: ?\\* ?(\\d+))\\)");
    private static final Pattern TRANSFORM_REPLACE = Pattern.compile("\\.transformReplace\\(<(.*):(.*)(:\\d+)?>(?: ?\\* ?(\\d+))\\)");

    private static final int ID_SEND = 10;
    private static final int ID_REMOVE = 11;
    private static final int ID_SHAPELESS = 12;
    private static final int ID_MIRRORED = 13;
    private static final int ID_OPTION_OK = 14;
    private static final int ID_OPTION_MATCHALL = 15;
    private static final int ID_OPTION_OREDICT = 16;
    private static final int ID_OPTION_META_WILDCARD = 17;
    private static final int ID_OPTION_NEXT_OREDICT = 18;
    private static final int ID_OPTION_ANY_DAMAGE = 19;
    private static final int ID_OPTION_WITH_DAMAGE = 20;
    private static final int ID_OPTION_ONLY_DAMAGED = 21;
    private static final int ID_CLOSE = 22;
    private static final int ID_OPTION_ONLY_DAMAGE_AT_LEAST = 23;
    private static final int ID_OPTION_ONLY_DAMAGE_AT_MOST = 24;
    private static final int ID_OPTION_ONLY_DAMAGE_BETWEEN = 25;
    private static final int ID_OPTION_REUSE = 26;
    private static final int ID_OPTION_ID_OPTION_TRANSFORM_DAMAGE = 27;
    private static final int ID_OPTION_NO_RETURN = 28;
    private static final int ID_OPTION_RETURN_OK = 29;
    private static final int ID_OPTION_ID_OPTION_TRANSFORM_REPLACE = 30;
    private static final int ID_OPTION_GIVE_BACK = 31;

    private final GuiSlider.ISlider iSlider = new GuiSlider.ISlider()
    {
        @Override
        public void onChangeSliderValue(GuiSlider slider)
        {
            tokenTxt.setText(getStackToken(false, inventorySlots.getSlot(editing).getStack()));
        }
    };

    private final MTRMContainer container;
    private GuiCheckBox remove;
    private GuiCheckBox shapeless;
    private GuiCheckBox mirrored;
    private GuiCheckBox matchAll;
    private GuiCheckBox oreDict;
    private GuiCheckBox metaWildcard;
    private GuiCheckBox anyDamage;
    private GuiButtonExt optionsOk;
    private GuiButtonExt nextOreDict;
    private GuiTextField tokenTxt;
    private GuiCheckBox onlyDamaged;
    private GuiCheckBox withDamage;
    private GuiCheckBox onlyDamageAtLeast;
    private GuiCheckBox onlyDamageAtMost;
    private GuiCheckBox onlyDamageBetween;
    private GuiCheckBox reuse;
    private GuiCheckBox noReturn;
    private GuiCheckBox transformDamage;
    private GuiCheckBox transformReplace;
    private GuiCheckBox giveBack;
    private GuiButtonExt returnOk;
    private Map<GuiCheckBox, GuiSlider[]> sliders = new HashMap<>();
    private Map<GuiCheckBox, Pattern> patterns = new HashMap<>();
    private Map<GuiCheckBox, GuiCustomLabel[]> labels = new HashMap<>();

    private GuiCheckBox[] allDamage;
    private GuiCheckBox[] allTransform;
    private GuiCheckBox[][] allGroups;

    private MessageSend messageSend = new MessageSend();
    private int editing = -1;
    private int lastOreId = 0;
    private MessageResponse.Status status;
    private String errorMessage;

    public MTRMGui(MTRMContainer container)
    {
        super(container);
        ySize = 181;
        this.container = container;
    }

    public String getStackToken(boolean nextOreDict, ItemStack stack)
    {
        boolean metaWildcard = this.metaWildcard.isChecked();
        boolean oreDict = this.oreDict.isChecked();
        if (stack.isEmpty()) return "null";
        if (stack.getItem().getRegistryName() == null)
        {
            throw new IllegalStateException("PLEASE REPORT: Item not empty, but getRegistryName null? Debug info: " + stack);
        }
        String stackName = stack.getItem().getRegistryName().toString();
        StringBuilder builder = new StringBuilder("<");
        if (oreDict)
        {
            int[] ids = OreDictionary.getOreIDs(stack);
            if (ids.length != 0)
            {
                stackName = "ore:" + OreDictionary.getOreName(ids[lastOreId]);
                if (nextOreDict) lastOreId++;
                if (lastOreId >= ids.length) lastOreId = 0;
            }
            else oreDict = false;
        }
        builder.append(stackName);
        if (!oreDict && (metaWildcard || stack.getItemDamage() != 0)) builder.append(':').append(metaWildcard || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "*" : stack.getItemDamage());
        builder.append('>');
        if (stack.getCount() > 1) builder.append(" * ").append(stack.getCount());
        if (anyDamage.isChecked()) builder.append(".anyDamage()");
        if (onlyDamaged.isChecked()) builder.append(".onlyDamaged()");
        if (withDamage.isChecked()) builder.append(".withDamage(").append(sliders.get(withDamage)[0].getValueInt()).append(')');
        if (onlyDamageAtLeast.isChecked()) builder.append(".onlyDamageAtLeast(").append(sliders.get(onlyDamageAtLeast)[0].getValueInt()).append(')');
        if (onlyDamageAtMost.isChecked()) builder.append(".onlyDamageAtMost(").append(sliders.get(onlyDamageAtMost)[0].getValueInt()).append(')');
        if (onlyDamageBetween.isChecked()) builder.append(".onlyDamageBetween(").append(sliders.get(onlyDamageBetween)[0].getValueInt()).append(", ").append(sliders.get(onlyDamageBetween)[1].getValueInt()).append(')');
        if (reuse.isChecked()) builder.append(".reuse()");
        if (noReturn.isChecked()) builder.append(".noReturn()");
        if (transformDamage.isChecked()) builder.append(".transformDamage(").append(sliders.get(transformDamage)[0].getValueInt()).append(')');
        if (container.getSlot(RETURN_SLOT_ID).getHasStack() && (giveBack.isChecked() || transformReplace.isChecked()))
        {
            ItemStack returnStack = container.getSlot(RETURN_SLOT_ID).getStack();

            if (giveBack.isChecked()) builder.append(".giveBack(<");
            else if (transformReplace.isChecked()) builder.append(".transformReplace(<");

            builder.append(returnStack.getItem().getRegistryName());
            if (returnStack.getItemDamage() != 0) builder.append(':').append(returnStack.getItemDamage());
            builder.append('>');
            if (returnStack.getCount() > 1) builder.append(" * ").append(returnStack.getCount());
            builder.append(')');
        }
        return builder.toString();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int id = 0;
        this.buttonList.add(new GuiButtonExt(id++, this.width / 2 + 65, this.height / 2 - 50, 10, 10, "*"));
        for (int y = 0; y < 3; ++y) for (int x = 0; x < 3; ++x) this.buttonList.add(new GuiButtonExt(id++, this.width / 2 - 52 + x * 26, this.height / 2 - 78 + y * 26, 10, 10, "*"));

        this.buttonList.add(remove = new GuiCheckBox(ID_REMOVE, this.width / 2 + 90, this.height / 2 - 80, "Remove", false));
        this.buttonList.add(shapeless = new GuiCheckBox(ID_SHAPELESS, this.width / 2 + 90, this.height / 2 - 70, "Shapeless", false));
        this.buttonList.add(mirrored = new GuiCheckBox(ID_MIRRORED, this.width / 2 + 90, this.height / 2 - 60, "Mirrored", true));
        this.buttonList.add(new GuiButtonExt(ID_SEND, this.width / 2 + 90, this.height / 2 - 45, 110, 20, "Ok!"));
        this.buttonList.add(new GuiButtonExt(ID_CLOSE, this.width / 2 + 90, this.height / 2 - 25, 110, 20, "Close"));

        int wOffset = this.width / 2 - 200;
        int hOffset = this.height / 2 - 110;

        tokenTxt = new GuiTextField(0, this.fontRenderer, wOffset, hOffset - 25, 220 + this.xSize, 20);
        tokenTxt.setMaxStringLength(Integer.MAX_VALUE);

        this.buttonList.add(matchAll = new GuiCheckBox(ID_OPTION_MATCHALL, wOffset, hOffset += 10, "Match not empty (*)", false));
        this.buttonList.add(oreDict = new GuiCheckBox(ID_OPTION_OREDICT, wOffset, hOffset += 10, "Use ore dictionary", true));
        this.buttonList.add(metaWildcard = new GuiCheckBox(ID_OPTION_META_WILDCARD, wOffset, hOffset += 10, "Match any metadata", false));
        this.buttonList.add(anyDamage = new GuiCheckBox(ID_OPTION_ANY_DAMAGE, wOffset, hOffset += 10, "Any Damage", false));
        this.buttonList.add(onlyDamaged = new GuiCheckBox(ID_OPTION_ONLY_DAMAGED, wOffset, hOffset += 10, "Only Damaged", false));
        this.buttonList.add(withDamage = new GuiCheckBox(ID_OPTION_WITH_DAMAGE, wOffset, hOffset += 10, "With Damage X", false));
        this.buttonList.add(onlyDamageAtLeast = new GuiCheckBox(ID_OPTION_ONLY_DAMAGE_AT_LEAST, wOffset, hOffset += 10, "With Damage >= X", false));
        this.buttonList.add(onlyDamageAtMost = new GuiCheckBox(ID_OPTION_ONLY_DAMAGE_AT_MOST, wOffset, hOffset += 10, "With Damage < X", false));
        this.buttonList.add(onlyDamageBetween = new GuiCheckBox(ID_OPTION_ONLY_DAMAGE_BETWEEN, wOffset, hOffset += 10, "With X > Damage > Y", false));
        this.buttonList.add(transformDamage = new GuiCheckBox(ID_OPTION_ID_OPTION_TRANSFORM_DAMAGE, wOffset, hOffset += 10, "Transform Damage", false));
        this.buttonList.add(transformReplace = new GuiCheckBox(ID_OPTION_ID_OPTION_TRANSFORM_REPLACE, wOffset, hOffset += 10, "Transform Replace", false));
        this.buttonList.add(reuse = new GuiCheckBox(ID_OPTION_REUSE, wOffset, hOffset += 10, "Reuse", false));
        this.buttonList.add(noReturn = new GuiCheckBox(ID_OPTION_NO_RETURN, wOffset, hOffset += 10, "No Return", false));
        this.buttonList.add(giveBack = new GuiCheckBox(ID_OPTION_GIVE_BACK, wOffset, hOffset += 10, "GiveBack", false));

        this.buttonList.add(nextOreDict = new GuiButtonExt(ID_OPTION_NEXT_OREDICT, wOffset, hOffset += 40, 110, 20, "Next oredict value"));
        this.buttonList.add(optionsOk = new GuiButtonExt(ID_OPTION_OK, wOffset, hOffset += 20, 110, 20, "Save changes!"));

        // Buttongroups

        allDamage = new GuiCheckBox[]{matchAll, metaWildcard, anyDamage, onlyDamaged, withDamage, onlyDamageAtLeast, onlyDamageAtMost, onlyDamageBetween};
        allTransform = new GuiCheckBox[]{transformDamage, transformReplace, reuse, noReturn, giveBack};
        allGroups = new GuiCheckBox[][]{allDamage, allTransform};

        addPatterns(matchAll, MATCH_ALL);
        addPatterns(metaWildcard, META_WILDCARD);
        addPatterns(anyDamage, ANY_DAMAGE);
        addPatterns(onlyDamaged, ONLY_DAMAGE);

        addPatterns(reuse, REUSE);
        addPatterns(noReturn, NO_RETURN);

        id = 100;
        hOffset += 30;
        int hOffsetText = hOffset - this.guiTop - 10;

        GuiCustomLabel returnSlot = new GuiCustomLabel()
        {
            @Override
            public void draw(boolean b)
            {
                if (!b) drawTexturedModalRect(((width - xSize) / 2) - 110, ((height - ySize) / 2) + 142, 21, 20, 18, 18);
            }
        };
        addLabels(transformReplace, returnSlot, new GuiCustomLabelText("Transform Replace:", -110, hOffsetText - 68));
        addPatterns(transformReplace, TRANSFORM_REPLACE);
        returnSlot = new GuiCustomLabel()
        {
            @Override
            public void draw(boolean b)
            {
                if (!b) drawTexturedModalRect(((width - xSize) / 2) - 110, ((height - ySize) / 2) + 142, 21, 20, 18, 18);
            }
        };
        addLabels(giveBack, returnSlot, new GuiCustomLabelText("Give Back:", -110, hOffsetText - 68));
        addPatterns(giveBack, GIVE_BACK);
        this.buttonList.add(returnOk = new GuiButtonExt(ID_OPTION_RETURN_OK, wOffset + 20, hOffset - 68, 20, 18, "OK"));

        addSliders(withDamage, new GuiSlider(id++, wOffset, hOffset, 220 + this.xSize, 20, "X = ", "", 0, 0, 0, false, true, iSlider));
        addLabels(withDamage, new GuiCustomLabelText("With Damage X", -110, hOffsetText));
        addPatterns(withDamage, WITH_DAMAGE);

        addSliders(onlyDamageAtLeast, new GuiSlider(id++, wOffset, hOffset, 220 + this.xSize, 20, "X = ", "", 0, 0, 0, false, true, iSlider));
        addLabels(onlyDamageAtLeast, new GuiCustomLabelText("With Damage >= X", -110, hOffsetText));
        addPatterns(onlyDamageAtLeast, ONLY_DAMAGE_AT_LEAST);

        addSliders(onlyDamageAtMost, new GuiSlider(id++, wOffset, hOffset, 220 + this.xSize, 20, "X = ", "", 0, 0, 0, false, true, iSlider));
        addLabels(onlyDamageAtMost, new GuiCustomLabelText("With Damage < X", -110, hOffsetText));
        addPatterns(onlyDamageAtMost, ONLY_DAMAGE_AT_MOST);

        addSliders(onlyDamageBetween, new GuiSlider(id++, wOffset, hOffset, 220 + this.xSize, 20, "X = ", "", 0, 0, 0, false, true, iSlider), new GuiSlider(id++, wOffset, hOffset + 20, 220 + this.xSize, 20, "Y = ", "", 0, 0, 0, false, true, iSlider));
        addLabels(onlyDamageBetween, new GuiCustomLabelText("With X > Damage > Y", -110, hOffsetText));
        addPatterns(onlyDamageBetween, ONLY_DAMAGE_BETWEEN);

        hOffset += 50;
        hOffsetText += 50;

        addSliders(transformDamage, new GuiSlider(id++, wOffset, hOffset, 220 + this.xSize, 20, "X = ", "", 0, 0, 0, false, true, iSlider));
        addLabels(transformDamage, new GuiCustomLabelText("Transform with X damage", -110, hOffsetText));
        addPatterns(transformDamage, TRANSFORM_DAMAGE);

        setOptionsVisible(false);
    }

    private void addPatterns(GuiCheckBox key, Pattern values)
    {
        patterns.put(key, values);
    }

    private void addSliders(GuiCheckBox key, GuiSlider... values)
    {
        for (GuiSlider value : values) this.buttonList.add(value);
        sliders.put(key, values);
    }

    private void addLabels(GuiCheckBox key, GuiCustomLabel... values)
    {
        labels.put(key, values);
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotNumber, int mouseBtn, ClickType modifier)
    {
        super.handleMouseClick(slot, slotNumber, mouseBtn, modifier);
        if (slot != null && slotNumber >= 0 && slotNumber <= 9)
        {
            showOptionsFor(slotNumber);
            messageSend.data[slotNumber] = getStackToken(false, slot.getStack());
            showOptionsFor(slotNumber);
        }
        if (slotNumber == RETURN_SLOT_ID)
        {
            tokenTxt.setText(getStackToken(true, inventorySlots.getSlot(editing).getStack()));
        }
    }

    protected void actionPerformed(GuiButton btn)
    {
        switch (btn.id)
        {
            case ID_CLOSE:
                this.mc.player.closeScreen();
                break;
            case ID_SEND:
                messageSend.remove = remove.isChecked();
                messageSend.shapeless = shapeless.isChecked();
                messageSend.mirrored = mirrored.isChecked();
                MineTweakerRecipeMaker.getSnw().sendToServer(messageSend);
                break;
            case ID_REMOVE:
            case ID_SHAPELESS:
                mirrored.enabled = !(shapeless.isChecked() || remove.isChecked());
                if (!mirrored.enabled) mirrored.setIsChecked(false);
                break;
            case ID_OPTION_OK:
                saveOptions();
                break;
            case ID_OPTION_MATCHALL:
                radioBoxToggle(matchAll, allDamage);
                oreDict.setIsChecked(!matchAll.isChecked());
                oreDict.enabled = !matchAll.isChecked();
                nextOreDict.enabled = oreDict.isChecked();
                if (matchAll.isChecked()) tokenTxt.setText("<*>");
                else tokenTxt.setText(getStackToken(false, inventorySlots.getSlot(editing).getStack()));
                break;
            case ID_OPTION_META_WILDCARD:
            case ID_OPTION_ONLY_DAMAGED:
            case ID_OPTION_ANY_DAMAGE:
            case ID_OPTION_WITH_DAMAGE:
            case ID_OPTION_ONLY_DAMAGE_AT_LEAST:
            case ID_OPTION_ONLY_DAMAGE_AT_MOST:
            case ID_OPTION_ONLY_DAMAGE_BETWEEN:
                radioBoxToggle((GuiCheckBox) btn, allDamage);
                tokenTxt.setText(getStackToken(false, inventorySlots.getSlot(editing).getStack()));
                break;
            case ID_OPTION_ID_OPTION_TRANSFORM_DAMAGE:
            case ID_OPTION_ID_OPTION_TRANSFORM_REPLACE:
            case ID_OPTION_REUSE:
            case ID_OPTION_NO_RETURN:
            case ID_OPTION_GIVE_BACK:
                radioBoxToggle((GuiCheckBox) btn, allTransform);
                tokenTxt.setText(getStackToken(false, inventorySlots.getSlot(editing).getStack()));
                break;
            case ID_OPTION_OREDICT:
                nextOreDict.enabled = oreDict.isChecked();
            case ID_OPTION_NEXT_OREDICT:
            case ID_OPTION_RETURN_OK:
                tokenTxt.setText(getStackToken(true, inventorySlots.getSlot(editing).getStack()));
                break;
            default:
                if (btn.id >= 0 && btn.id <= 10) showOptionsFor(btn.id);
        }
    }

    private void radioBoxToggle(GuiCheckBox clicked, GuiCheckBox... others)
    {
        for (GuiCheckBox checkBox : others)
        {
            if (sliders.containsKey(checkBox)) for (GuiSlider s : sliders.get(checkBox)) s.visible = checkBox.isChecked();
            if (labels.containsKey(checkBox)) for (GuiCustomLabel s : labels.get(checkBox)) s.draw = checkBox.isChecked();
            if (checkBox == clicked) continue;
            if (clicked.isChecked()) checkBox.setIsChecked(false);
            checkBox.enabled = !clicked.isChecked();
        }
    }

    private void saveOptions()
    {
        messageSend.data[editing] = tokenTxt.getText();
        if (messageSend.data[editing].equalsIgnoreCase("null")) messageSend.data[editing] = null;
        editing = -1;
        setOptionsVisible(false);
    }

    private void showOptionsFor(int id)
    {
        if (editing != id) lastOreId = 0;
        editing = id;
        String token = messageSend.data[id];
        if (token == null)
        {
            tokenTxt.setText("null");

            oreDict.enabled = id != 0;
            oreDict.setIsChecked(id != 0);
            nextOreDict.enabled = id != 0;

            for (GuiCheckBox[] group : allGroups)
            {
                for (GuiCheckBox checkBox : group)
                {
                    checkBox.setIsChecked(false);
                    if (sliders.containsKey(checkBox))
                    {
                        for (GuiSlider slider : sliders.get(checkBox))
                        {
                            slider.minValue = 0;
                            slider.maxValue = 0;
                            slider.setValue(0);
                        }
                    }
                    if (labels.containsKey(checkBox)) for (GuiCustomLabel l : labels.get(checkBox)) l.draw = false;
                }
            }
            container.getSlot(RETURN_SLOT_ID).putStack(ItemStack.EMPTY);
        }
        else
        {
            tokenTxt.setText(token);

            oreDict.enabled = id != 0;
            oreDict.setIsChecked(ORE_DICT.matcher(token).find() && id != 0);
            nextOreDict.enabled = oreDict.isChecked() && id != 0;

            for (GuiCheckBox[] group : allGroups)
            {
                for (GuiCheckBox checkBox : group)
                {
                    if (patterns.containsKey(checkBox))
                    {
                        checkBox.setIsChecked(patterns.get(checkBox).matcher(token).find());
                        radioBoxToggle(checkBox, group);
                    }
                }
            }

            Matcher m = GIVE_BACK.matcher(token);
            if (m.find())
            {
                int meta = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
                int size = m.group(4) != null ? Integer.parseInt(m.group(4)) : 1;
                Item i = Item.REGISTRY.getObject(new ResourceLocation(m.group(1), m.group(2)));
                if (i != null) container.getSlot(RETURN_SLOT_ID).putStack(new ItemStack(i, size, meta));
            }
        }

        for (GuiCheckBox[] group : allGroups)
        {
            for (GuiCheckBox checkBox : group)
            {
                checkBox.enabled = id != 0;
                if (!sliders.containsKey(checkBox)) continue;
                GuiSlider[] sliderA = sliders.get(checkBox);
                for (GuiSlider slider : sliderA)
                {
                    slider.minValue = 0;
                    slider.maxValue = Integer.MAX_VALUE;
                    slider.setValue(0);
                    if (!oreDict.isChecked())
                    {
                        ItemStack stack = container.getSlot(editing).getStack();
                        if (!stack.isEmpty()) slider.maxValue = stack.getMaxDamage();
                    }
                }
                if (token != null && patterns.containsKey(checkBox))
                {
                    Matcher matcher = patterns.get(checkBox).matcher(token);
                    if (matcher.find())
                    {
                        for (int i = 0; i < matcher.groupCount() && i < sliderA.length; i++)
                        {
                            sliderA[i].setValue(Integer.parseInt(matcher.group(i + 1)));
                        }
                    }
                }
            }
        }

        setOptionsVisible(true);
    }

    private void setOptionsVisible(boolean visible)
    {
        matchAll.visible = visible;
        metaWildcard.visible = visible;
        optionsOk.visible = visible;
        oreDict.visible = visible;
        nextOreDict.visible = visible;
        anyDamage.visible = visible;
        withDamage.visible = visible;
        onlyDamaged.visible = visible;
        reuse.visible = visible;
        returnOk.visible = visible;

        for (GuiCheckBox[] group : allGroups)
        {
            for (GuiCheckBox gui : group)
            {
                gui.visible = visible;
                if (sliders.containsKey(gui)) for (GuiSlider s : sliders.get(gui)) s.visible = visible && gui.isChecked();
                if (labels.containsKey(gui)) for (GuiCustomLabel l : labels.get(gui)) l.draw = visible && gui.isChecked();
            }
        }

        tokenTxt.setVisible(visible);

        if (!visible) container.putStackInSlot(RETURN_SLOT_ID, ItemStack.EMPTY);
    }

//    protected void keyTyped(char p_73869_1_, int p_73869_2_)
//    {
//        if (!this.tokenTxt.textboxKeyTyped(p_73869_1_, p_73869_2_))
//        {
//            super.keyTyped(p_73869_1_, p_73869_2_);
//        }
//    }

//    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
//    {
//        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
//        this.tokenTxt.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
//    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        if (editing != -1)
        {
            this.fontRenderer.drawString("Editing slot " + editing, -110, -55, 0xFFFFFF);
            this.fontRenderer.drawString("Slot Options", -100, -20, 0xFFFFFF);
        }
        this.fontRenderer.drawString("Recipe Options", this.xSize + 15, 0, 0xFFFFFF);
        this.fontRenderer.drawString("MineTweaker Recipe Maker", 28, 4, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 5, 4210752);
        if (errorMessage != null)
            this.drawCenteredString(this.fontRenderer, errorMessage, this.xSize / 2, -15, 0xFF0000);
        this.fontRenderer.drawString("0", 144, 53, 0xFFFFFF);
        for (int y = 0; y < 3; ++y) for (int x = 0; x < 3; ++x) this.fontRenderer.drawString(String.valueOf(1 + y * 3 + x), 28 + x * 26, 25 + y * 26, 0xFFFFFF);
        for (GuiCustomLabel[] labela : labels.values()) for (GuiCustomLabel label : labela) if (label.draw) label.draw(true);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        for (GuiCustomLabel[] labela : labels.values()) for (GuiCustomLabel label : labela) if (label.draw) label.draw(false);
        tokenTxt.drawTextBox();
    }

    public void showMessage(String message)
    {
        this.errorMessage = message;
    }

    private abstract class GuiCustomLabel
    {
        boolean draw = false;

        public abstract void draw(boolean b);
    }

    private class GuiCustomLabelText extends GuiCustomLabel
    {
        String text;
        int color = 0xFFFFFF;
        int x, y;

        public GuiCustomLabelText(String s, int x, int y)
        {
            this.x = x;
            this.y = y;
            this.text = s;
        }

        public void draw(boolean b)
        {
            if (b) fontRenderer.drawString(text, x, y, color);
        }
    }
}
