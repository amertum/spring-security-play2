package com.github.amertum.springframework.security.util;

import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public class Urls {

    public static String encodeUtf8(final String content) {
        try {
            return URLEncoder.encode(content, Charsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    /**
     * Returns the complete file part (ie: with ref part if any) of the {@code urlAsString}, without the scheme and
     * domain part.
     */
    public static String extractUrlCompleteFilePart(
            final String urlAsString
    ) {
        String result;

        try {
            final URL context = new URL("http://localhost");
            final URL url = new URL(context, urlAsString);

            if (url.getAuthority().equals(context.getAuthority())) {
                result = url.getFile() + ofNullable(url.getRef()).map(urlHash()).orElse("");
            }
            else {
                final URL _url = new URL(urlAsString);
                result = _url.getFile() + ofNullable(_url.getRef()).map(urlHash()).orElse("");
            }
        }
        catch (MalformedURLException e) {
            LOGGER.error(".extractUrlCompleteFilePart, unable to parse URL path {}", urlAsString);
            result = "/";
        }

        result = result.startsWith("/")
                ? result
                : "/" + result;

        return result;
    }

    private static Function<String, String> urlHash() {
        return input -> "#" + input;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Urls.class);

}
