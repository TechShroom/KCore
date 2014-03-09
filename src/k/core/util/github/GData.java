package k.core.util.github;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;

public class GData {
    /**
     * Error values for GData.
     * 
     * @author Kenzie Togami
     */
    public static enum GDataError {
        NONE, TIMEOUT, BADURL, IOERRORS;
    }

    private static final class SpecialData {
        private final RateLimit limits;

        private final Map<String, List<String>> headers;

        public SpecialData(int rateremain, int ratelim, long ratereset,
                Map<String, List<String>> headers) {
            limits = new RateLimit(rateremain, ratelim, ratereset);
            this.headers = headers;
        }
    }

    public static final String RATELIMIT_KEY = "X-RateLimit-Limit",
            RATEREMAINING_KEY = "X-RateLimit-Remaining",
            RATERESET_KEY = "X-RateLimit-Reset";

    static final GData TIMEOUT = new GData(GDataError.TIMEOUT),
            BADURL = new GData(GDataError.BADURL), IOERRORS = new GData(
                    GDataError.IOERRORS);

    private static int getRLL(Map<String, List<String>> headers) {
        return Integer.parseInt(headers.get(RATELIMIT_KEY).get(0));
    }

    private static int getRLRemaining(Map<String, List<String>> headers) {
        return Integer.parseInt(headers.get(RATEREMAINING_KEY).get(0));
    }

    /**
     * Note: this converts GitHub's returned epoch seconds into Java's epoch
     * milliseconds.
     * 
     * @return the epoch seconds at which the rate limit will be reset to
     *         {@link #getRLL()}.
     */
    private static long getRLReset(Map<String, List<String>> headers) {
        return Long.parseLong(headers.get(RATERESET_KEY).get(0)) * 1000;
    }

    private final GDataError errstate;

    private SpecialData data;

    private String raw = "";

    public GData() {
        this(GDataError.NONE);
    }

    private GData(GDataError e) {
        if (e != null) {
            errstate = e;
        } else {
            errstate = GDataError.NONE;
        }
    }

    void content(HttpURLConnection urlc, Object content) throws IOException {
        if (content instanceof InputStream) {
            // data is inputstreamed
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    (InputStream) content));
            String data = "";
            try {
                for (String line = reader.readLine(); line != null; line = reader
                        .readLine()) {
                    data += line;
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            reader.close();
            raw = data;
            Map<String, List<String>> headers = urlc.getHeaderFields();
            headers = new HashMap<String, List<String>>(headers); // copy out
            int rlremain = 0;
            int rllim = 0;
            long rlreset = 0;
            try {
                 rlremain = getRLRemaining(headers);
                 rllim = getRLL(headers);
                 rlreset = getRLReset(headers);
                headers.remove(RATELIMIT_KEY);
                headers.remove(RATEREMAINING_KEY);
                headers.remove(RATERESET_KEY);
            } catch (NullPointerException npe) {
                // no ratelimits, errored probably
            }
            headers = Collections.unmodifiableMap(headers); // protect
            this.data = new SpecialData(rlremain, rllim, rlreset, headers);
        } else {
            throw new UnsupportedOperationException("no handler for "
                    + content.getClass());
        }
    }

    public String getData() {
        throwIfErrored();
        return raw;
    }

    /**
     * Returns the error status of the data.
     * 
     * @return the error that occurred while creating the data, or
     *         {@link GDataError#NONE} if none happened.
     * @see GDataError
     */
    public GDataError getErrorState() {
        return errstate;
    }

    public String getFirstHeaderValue(String headerKey) {
        throwIfErrored();
        return data.headers.get(headerKey).get(0);
    }

    /* Some special methods that return headers GitHub always returns */

    public Map<String, List<String>> getHeaders() {
        throwIfErrored();
        return data.headers;
    }

    public List<String> getHeaderValues(String headerKey) {
        throwIfErrored();
        return data.headers.get(headerKey);
    }

    public boolean isErrored() {
        return errstate != GDataError.NONE;
    }

    public RateLimit rate() {
        throwIfErrored();
        return data.limits;
    }

    private void throwIfErrored() {
        if (!isErrored()) {
            return;
        }
        throw new IllegalStateException("errored");
    }

    @Override
    public String toString() {
        return (!isErrored()) ? "RateLimit: " + rate() + " " + getHeaders()
                + "; " + getData() : "Error: " + errstate;
    }

}
