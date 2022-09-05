package name.nkid00.rcutil.io;

import com.google.gson.JsonObject;

import io.netty.util.concurrent.Promise;

public record UnblockResult(Reason reason, JsonObject msg, String addr, Promise<UnblockResult> nextPromise) {

    public static UnblockResult Request(JsonObject msg, String addr, Promise<UnblockResult> nextPromise) {
        return new UnblockResult(Reason.Request, msg, addr, nextPromise);
    }

    public static UnblockResult Response(JsonObject msg) {
        return new UnblockResult(Reason.Response, msg, null, null);
    }

    public boolean isRequest() {
        return reason == Reason.Request;
    }

    public boolean isResponse() {
        return reason == Reason.Response;
    }

    public enum Reason {
        Request, Response;
    }
}
