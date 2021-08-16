package name.nkid00.rcutil.rambus;

import java.io.IOException;

public class WoRamBusBuilder extends RamBusBuilder {
    public boolean setFile(String path) {
        if (!super.setFile(path)) {
            try {
                file.createNewFile();  // create if file does not exist
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public WoRamBus build() {
        WoRamBus ram = new WoRamBus();
        return ram;
    }
}
