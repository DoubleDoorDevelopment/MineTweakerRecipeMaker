package net.doubledoordev.mtrm.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.common.registry.GameData;
import net.doubledoordev.mtrm.MineTweakerRecipeMaker;
import net.doubledoordev.mtrm.network.MessageSend;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.util.regex.Pattern;

/**
 * @author Dries007
 */
public class MTRMGui extends GuiContainer
{
    private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("mtrm:gui/mtrm.png");

    private static final Pattern MATCH_ALL     = Pattern.compile("<\\s*\\*\\s*>");
    private static final Pattern META_WILDCARD = Pattern.compile(":\\s*\\*\\s*>");
    private static final Pattern ORE_DICT      = Pattern.compile("<ore:.*>");

    private static final int ID_SEND                 = 10;
    private static final int ID_REMOVE               = 11;
    private static final int ID_SHAPELESS            = 12;
    private static final int ID_MIRRORED             = 13;
    private static final int ID_OPTION_OK            = 14;
    private static final int ID_OPTION_MATCHALL      = 15;
    private static final int ID_OPTION_OREDICT       = 16;
    private static final int ID_OPTION_META_WILDCARD = 17;
    private static final int ID_OPTION_NEXT_OREDICT  = 18;

    private final MTRMContainer container;
    private       GuiCheckBox   remove;
    private       GuiCheckBox   shapeless;
    private       GuiCheckBox   mirrored;
    private       GuiCheckBox   matchAll;
    private       GuiCheckBox   oreDict;
    private       GuiCheckBox   metaWildcard;
    private       GuiButtonExt  optionsOk;
    private       GuiButtonExt  nextOreDict;
    private       GuiTextField  tokenTxt;
    private MessageSend messageSend = new MessageSend();
    private int         editing     = -1;
    private int         lastOreId   = 0;
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
        if (stack == null) return "null";
        String stackName = GameData.getItemRegistry().getNameForObject(stack.getItem());
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
        }
        builder.append(stackName);
        if (!oreDict && (metaWildcard || stack.getItemDamage() != 0)) builder.append(':').append(metaWildcard || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "*" : stack.getItemDamage());
        builder.append('>');
        if (stack.stackSize > 1) builder.append(" * ").append(stack.stackSize);
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

        this.buttonList.add(matchAll = new GuiCheckBox(ID_OPTION_MATCHALL, this.width / 2 - 200, this.height / 2 - 80, "Match not empty (*)", false));
        this.buttonList.add(oreDict = new GuiCheckBox(ID_OPTION_OREDICT, this.width / 2 - 200, this.height / 2 - 70, "Use ore dictionary", true));
        this.buttonList.add(metaWildcard = new GuiCheckBox(ID_OPTION_META_WILDCARD, this.width / 2 - 200, this.height / 2 - 60, "Match any metadata", false));
        tokenTxt = new GuiTextField(this.fontRendererObj, this.width / 2 - 200, this.height / 2 - 45, 110, 20);
        this.buttonList.add(optionsOk = new GuiButtonExt(ID_OPTION_OK, this.width / 2 - 200, this.height / 2, 110, 20, "Ok!"));
        this.buttonList.add(nextOreDict = new GuiButtonExt(ID_OPTION_NEXT_OREDICT, this.width / 2 - 200, this.height / 2 - 20, 110, 20, "Next oredict value"));
        setOptionsVisible(false);
    }

    protected void handleMouseClick(Slot slot, int slotNumber, int mouseBtn, int modifier)
    {
        super.handleMouseClick(slot, slotNumber, mouseBtn, modifier);
        if (slot != null && slotNumber >= 0 && slotNumber <= 9)
        {
            showOptionsFor(slotNumber);
            messageSend.data[slotNumber] = getStackToken(false, slot.getStack());
            showOptionsFor(slotNumber);
        }
    }

    protected void actionPerformed(GuiButton btn)
    {
        switch (btn.id)
        {
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
                metaWildcard.setIsChecked(matchAll.isChecked());
                metaWildcard.enabled = !matchAll.isChecked();
                oreDict.setIsChecked(!matchAll.isChecked());
                oreDict.enabled = !matchAll.isChecked();
                nextOreDict.enabled = oreDict.isChecked();
                if (matchAll.isChecked()) tokenTxt.setText("<*>");
                else tokenTxt.setText(getStackToken(false, inventorySlots.getSlot(editing).getStack()));
                break;
            case ID_OPTION_META_WILDCARD:
                tokenTxt.setText(getStackToken(false, inventorySlots.getSlot(editing).getStack()));
                break;
            case ID_OPTION_OREDICT:
                nextOreDict.enabled = oreDict.isChecked();
            case ID_OPTION_NEXT_OREDICT:
                tokenTxt.setText(getStackToken(true, inventorySlots.getSlot(editing).getStack()));
                break;
            default:
                showOptionsFor(btn.id);
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
            matchAll.setIsChecked(false);
            metaWildcard.setIsChecked(false);
            oreDict.setIsChecked(id != 0);
            oreDict.enabled = id != 0;
            nextOreDict.enabled = oreDict.isChecked();
        }
        else
        {
            tokenTxt.setText(token);
            matchAll.setIsChecked(MATCH_ALL.matcher(token).find());
            metaWildcard.setIsChecked(matchAll.isChecked() || META_WILDCARD.matcher(token).find());
            oreDict.enabled = id != 0;
            oreDict.setIsChecked(ORE_DICT.matcher(token).find() && id != 0);
            nextOreDict.enabled = oreDict.isChecked() && id != 0;
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
        tokenTxt.setVisible(visible);
    }

    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        if (!this.tokenTxt.textboxKeyTyped(p_73869_1_, p_73869_2_))
        {
            super.keyTyped(p_73869_1_, p_73869_2_);
        }
    }

    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.tokenTxt.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_)
    {
        if (editing != -1) this.fontRendererObj.drawString("Editing slot " + editing, -100, 0, 0xFFFFFF);
        this.fontRendererObj.drawString("MineTweaker Recipe Maker", 28, 4, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 5, 4210752);
        this.fontRendererObj.drawSplitString("You can show the ore dictionary, metadata en wildcard options by clicking on the * button.", 180, 70, 100, 0xFFFFFF);
        if (errorMessage != null) this.fontRendererObj.drawString(errorMessage, 28, -10, 0xFF0000);
        this.fontRendererObj.drawString("0", 144, 53, 0xFFFFFF);
        for (int y = 0; y < 3; ++y) for (int x = 0; x < 3; ++x) this.fontRendererObj.drawString(String.valueOf(1 + y * 3 + x), 28 + x * 26, 25 + y * 26, 0xFFFFFF);
    }

    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(craftingTableGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        tokenTxt.drawTextBox();
    }

    public void showMessage(String message)
    {
        this.errorMessage = message;
    }
}
