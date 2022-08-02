package name.nkid00.rcutil.model.connection;

import name.nkid00.rcutil.model.component.Component;

public class Connection {
    Component source;
    Component target;
    
    public boolean isRunning() {
        return this.source.isRunning() && this.target.isRunning();
    }
}
