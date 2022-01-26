package name.nkid00.rcutil.command;

public enum CommandStatus {
    Idle,
    RcuNewStepLsb,
    RcuNewStepSecondLsb,
    RcuNewStepMsb,
    RcuNewStepAddrLsb,
    RcuNewStepAddrSecondLsb,
    RcuNewStepAddrMsb,
    RcuNewStepDataLsb,
    RcuNewStepDataSecondLsb,
    RcuNewStepDataMsb,
    RcuNewStepClock;

    public boolean isIdle() {
        return this.equals(CommandStatus.Idle);
    }

    public boolean isRunningFileRamNew() {
        switch (this) {
            case RcuNewStepLsb:
            case RcuNewStepSecondLsb:
            case RcuNewStepMsb:
            case RcuNewStepAddrLsb:
            case RcuNewStepAddrSecondLsb:
            case RcuNewStepAddrMsb:
            case RcuNewStepDataLsb:
            case RcuNewStepDataSecondLsb:
            case RcuNewStepDataMsb:
            case RcuNewStepClock:
                return true;
            default:
                return false;
        
        }
    }
}
