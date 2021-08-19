package name.nkid00.rcutil.enumeration;

public enum Status {
    Idle,
    FileRamNewStepAddrLsb,
    FileRamNewStepAddr2Lsb,
    FileRamNewStepAddrMsb,
    FileRamNewStepDataLsb,
    FileRamNewStepData2Lsb,
    FileRamNewStepDataMsb,
    FileRamNewStepClock;

    public boolean isIdle() {
        return this == Status.Idle;
    }

    public boolean isRunningFileRamNew() {
        switch (this) {
            case FileRamNewStepAddrLsb:
            case FileRamNewStepAddr2Lsb:
            case FileRamNewStepAddrMsb:
            case FileRamNewStepDataLsb:
            case FileRamNewStepData2Lsb:
            case FileRamNewStepDataMsb:
            case FileRamNewStepClock:
                return true;
            default:
                return false;
        
        }
    }
}
