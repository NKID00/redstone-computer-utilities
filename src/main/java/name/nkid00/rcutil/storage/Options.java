package name.nkid00.rcutil.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class Options {
    public static final int requiredPermissionLevel = 2;
    public static final int requiredFileOperationPermissionLevel = 4; // operations on files are dangerous
    public static final Item wandItem = Items.PINK_DYE;
    public static final Text wandItemHoverableText = new ItemStack(Options.wandItem).toHoverableText();
}
