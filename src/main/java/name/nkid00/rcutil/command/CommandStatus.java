package name.nkid00.rcutil.command;

public enum CommandStatus {
    Idle,
    RcuNewWiresLsb,
    RcuNewWiresSecondLsb,
    RcuNewWiresMsb,
    RcuNewBusLsb,
    RcuNewBusSecondLsb,
    RcuNewBusMsb,
    RcuNewBusClock,
    RcuNewAddrbusAddrLsb,
    RcuNewAddrbusAddrSecondLsb,
    RcuNewAddrbusAddrMsb,
    RcuNewAddrbusDataLsb,
    RcuNewAddrbusDataSecondLsb,
    RcuNewAddrbusDataMsb,
    RcuNewAddrbusClock;

    public boolean isIdle() {
        return this.equals(CommandStatus.Idle);
    }

    public boolean isRunningRcuNew() {
        switch (this) {
            case Idle:
                return false;
            default:
                return true;
        
        }
    }

    public boolean isRunningRcuNewWires() {
        switch (this) {
            case RcuNewWiresLsb:
            case RcuNewWiresSecondLsb:
            case RcuNewWiresMsb:
                return true;
            default:
                return false;
        
        }
    }

    public boolean isRunningRcuNewBus() {
        switch (this) {
            case RcuNewWiresLsb:
            case RcuNewWiresSecondLsb:
            case RcuNewWiresMsb:
                return true;
            default:
                return false;
        
        }
    }

    public boolean isRunningRcuNewAddrbus() {
        switch (this) {
            case RcuNewAddrbusAddrLsb:
            case RcuNewAddrbusAddrSecondLsb:
            case RcuNewAddrbusAddrMsb:
            case RcuNewAddrbusDataLsb:
            case RcuNewAddrbusDataSecondLsb:
            case RcuNewAddrbusDataMsb:
            case RcuNewAddrbusClock:
                return true;
            default:
                return false;
        
        }
    }
}
