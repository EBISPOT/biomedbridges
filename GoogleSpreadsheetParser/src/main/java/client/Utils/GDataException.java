package client.Utils;

import sun.org.mozilla.javascript.internal.WrappedException;

public class GDataException extends WrappedException {
    public static final GDataException[] EMPTY_ARRAY = {};

    public GDataException(Throwable throwable) {
        super(throwable);
    }
}