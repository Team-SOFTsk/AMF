package sk.teamsoft.amf.network;

import android.net.Uri;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Interceptor which enables app to change it's base url on the fly
 *
 * @author Dusan Bartos
 */
public final class HostSelectionInterceptor implements Interceptor {
    private volatile String scheme;
    private volatile String host;
    private volatile int port;

    public boolean setHost(String host) {
        if (TextUtils.isEmpty(host)) return false;
        HttpUrl httpUrl = HttpUrl.parse(host);
        if (httpUrl == null) {
            Timber.w("Illegal URL: %s", host);
            return false;
        }

        try {
            Uri uri = Uri.parse(host);
            if (TextUtils.equals(this.scheme, uri.getScheme()) && TextUtils.equals(this.host, uri.getHost())) {
                return false;
            } else {
                this.scheme = uri.getScheme();
                this.host = uri.getHost();
                this.port = uri.getPort();
                Timber.v("new host scheme: %s, new host: %s:%d", this.scheme, this.host, this.port);
            }
            return true;
        } catch (Exception e) {
            Timber.e(e, "Cannot set a new host");
            return false;
        }
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String scheme = this.scheme;
        String host = this.host;
        int port = this.port;
        if (host != null) {
            //TODO handle exceptions ?
            HttpUrl.Builder builder = request.url().newBuilder();
            if (port != -1) {
                builder.port(port);
            }
            HttpUrl newUrl = builder
                    .scheme(scheme)
                    .host(host)
                    .build();
            request = request.newBuilder()
                    .url(newUrl)
                    .build();
        }
        return chain.proceed(request);
    }
}
