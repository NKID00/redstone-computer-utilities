package name.nkid00.rcutil.exception;

public class ApiException extends RCUtilException {
    private int code;

    public static final ApiException GENERAL_ERROR = new ApiException(-1);
    public static final ApiException ARGUMENT_INVALID = new ApiException(-2);
    public static final ApiException NAME_ILLEGAL = new ApiException(-3);
    public static final ApiException NAME_EXISTS = new ApiException(-4);
    public static final ApiException NAME_NOT_FOUND = new ApiException(-5);
    public static final ApiException INTERNAL_ERROR = new ApiException(-6);
    public static final ApiException CHUNK_UNLOADED = new ApiException(-7);

    public ApiException(int code) {
        this.code = code;
    }

    public static ApiException fromCode(int code) {
        switch (code) {
            default:
            case -1:
                return GENERAL_ERROR;
            case -2:
                return ARGUMENT_INVALID;
            case -3:
                return NAME_ILLEGAL;
            case -4:
                return NAME_EXISTS;
            case -5:
                return NAME_NOT_FOUND;
            case -6:
                return INTERNAL_ERROR;
            case -7:
                return CHUNK_UNLOADED;
        }
    }

    public int code() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ApiException) {
            return ((ApiException) obj).code == code;
        }
        return false;
    }
}
