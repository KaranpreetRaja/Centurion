/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.javax.security.auth.callback;

import java.util.Locale;

/**
 * <p> Underlying security services instantiate and pass a
 * {@code LanguageCallback} to the {@code handle}
 * method of a {@code CallbackHandler} to retrieve the {@code Locale}
 * used for localizing text.
 *
 * @since 1.4
 * @see java.base.share.classes.javax.security.auth.callback.CallbackHandler
 */
public class LanguageCallback implements Callback, java.io.Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 2019050433478903213L;

    /**
     * @serial
     * @since 1.4
     */
    private Locale locale;

    /**
     * Construct a {@code LanguageCallback}.
     */
    public LanguageCallback() { }

    /**
     * Set the retrieved {@code Locale}.
     *
     * @param locale the retrieved {@code Locale}.
     *
     * @see #getLocale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Get the retrieved {@code Locale}.
     *
     * @return the retrieved {@code Locale}, or null
     *          if no {@code Locale} could be retrieved.
     *
     * @see #setLocale
     */
    public Locale getLocale() {
        return locale;
    }
}
