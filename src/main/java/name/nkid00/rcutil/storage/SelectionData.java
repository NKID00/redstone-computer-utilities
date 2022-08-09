package name.nkid00.rcutil.storage;

import java.util.LinkedHashMap;

import name.nkid00.rcutil.Selection;
import name.nkid00.rcutil.helper.Log;
import name.nkid00.rcutil.selection.SingleSelection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class SelectionData {
    public int selectionIndex = 0;
    public int selectionIndexMax;
    public LinkedHashMap<String, Selection> selection = new LinkedHashMap<>();
    public String[] selectionKeyArray;

    public SelectionData() {
        selection.put("data", new Selection());
        selection.put("clock", new SingleSelection());
        selection.put("addr", new Selection());
        var keySet = selection.keySet();
        selectionIndexMax = keySet.size();
        selectionKeyArray = keySet.toArray(new String[selectionIndexMax]);
    }

    public void selectMsb(BlockPos pos, DimensionType dimension) {
        selection.get(selectionKeyArray[selectionIndex]).selectMsb(pos, dimension);
        Log.info("{}", selection);
    }

    public void selectLsb(BlockPos pos, DimensionType dimension) {
        selection.get(selectionKeyArray[selectionIndex]).selectLsb(pos, dimension);
        Log.info("{}", selection);
    }

    public void previousSelection() {
        --selectionIndex;
        if (selectionIndex < 0) {
            selectionIndex = selectionIndexMax;
        }
        Log.info("{}", selectionKeyArray[selectionIndex]);
    }

    public void nextSelection() {
        ++selectionIndex;
        if (selectionIndex >= selectionIndexMax) {
            selectionIndex = 0;
        }
        Log.info("{}", selectionKeyArray[selectionIndex]);
    }

    public Selection selection(String key) {
        return selection.get(key);
    }
}
