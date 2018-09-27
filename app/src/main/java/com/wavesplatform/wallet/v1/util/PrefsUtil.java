package com.wavesplatform.wallet.v1.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.wavesplatform.wallet.v1.ui.auth.EnvironmentManager;
import com.wavesplatform.wallet.v2.injection.qualifier.ApplicationContext;

import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;

public class PrefsUtil {

    public static final String GLOBAL_CURRENT_ENVIRONMENT = "global_current_environment";
    @Deprecated
    public static final String GLOBAL_LOGGED_IN_GUID = "global_logged_in_wallet_guid";
    public static final String GLOBAL_LAST_LOGGED_IN_GUID = "global_last_logged_in_wallet_guid"; // ex "global_logged_in_wallet_guid"
    public static final String GLOBAL_SCHEME_URL = "scheme_url";
    public static final String LIST_WALLET_GUIDS = "list_wallet_guid";

    public static final String KEY_WALLET_NAME = "wallet_name";
    public static final String KEY_PUB_KEY = "wallet_public_key";
    public static final String KEY_ENCRYPTED_WALLET = "encrypted_wallet";
    public static final String KEY_SKIP_BACKUP = "skip_backup";
    public static final String KEY_ENCRYPTED_PASSWORD = "encrypted_password";
    public static final String KEY_PIN_FAILS = "pin_fails";
    public static final String KEY_USE_FINGERPRINT = "use_fingerprint";
    public static final String KEY_ENCRYPTED_PIN = "encrypted_pin";

    public static final String KEY_ACCOUNT_FIRST_OPEN = "key_account_first_open";

    public static final String KEY_DEFAULT_ASSETS = "key_default_assets";
    public static final String KEY_DISABLE_SPAM_FILTER = "disable_spam_filter";
    public static final String KEY_SPAM_URL = "spam_url";

    public static final String KEY_AB_NAMES = "address_book_names";
    public static final String KEY_AB_ADDRESSES = "address_book_addresses";

    public static final String KEY_DISABLE_ROOT_WARNING = "disable_root_warning";
    public static final String KEY_BACKUP_DATE_KEY = "backup_date_key";
    public static final String KEY_LAST_BACKUP_PROMPT = "last_backup_prompt";
    public static final String KEY_SECURITY_BACKUP_NEVER = "security_backup_never";
    public static final String KEY_ENCRYPTED_PIN_CODE = "encrypted_pin_code";

    public static final String KEY_FINGERPRINT_ENABLED = "fingerprint_enabled";
    public static final String KEY_SHARED_KEY = "sharedKey";
    public static final String KEY_NEWLY_CREATED_WALLET = "newly_created_wallet";
    public static final String LOGGED_OUT = "logged_out";
    public static final String KEY_RECEIVE_SHORTCUTS_ENABLED = "receive_shortcuts_enabled";
    public static final String KEY_DONT_ASK_AGAIN_ORDER = "dont_ask_again_order";

    private SharedPreferences preferenceManager;

    @Inject
    public PrefsUtil(@ApplicationContext Context context) {
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getValue(String name, String value) {
        return getValueInternal(getGuid() + name, value);
    }

    public String getValue(String guid, String name, String value) {
        return getValueInternal(guid + name, value);
    }

    public String getGlobalValue(String name, String value) {
        return getValueInternal(name, value);
    }

    private String getValueInternal(String name, String value) {
        return preferenceManager.getString(name, TextUtils.isEmpty(value) ? "" : value);
    }

    public void setGlobalValue(String name, String value) {
        setValueInternal(name, value);
    }

    public void setValue(String name, String value) {
        setValueInternal(getGuid() + name, value);
    }

    public void setValue(String guid, String name, String value) {
        setValueInternal(guid + name, value);
    }

    private void setValueInternal(String name, String value) {
        Editor editor = preferenceManager.edit();
        editor.putString(name, (value == null || value.isEmpty()) ? "" : value);
        editor.apply();
    }

    public int getValue(String name, int value) {
        return getValueInternal(getGuid() + name, value);
    }

    private int getValueInternal(String name, int value) {
        return preferenceManager.getInt(name, 0);
    }

    public void setValue(String name, int value) {
        setValueInternal(getGuid() + name, value);
    }

    private void setValueInternal(String name, int value) {
        Editor editor = preferenceManager.edit();
        editor.putInt(name, (value < 0) ? 0 : value);
        editor.apply();
    }

    public long getValue(String name, long value) {
        return getValueInternal(getGuid() + name, value);
    }

    private long getValueInternal(String name, long value) {
        return preferenceManager.getLong(name, 0L);
    }

    public void setValue(String name, long value) {
        setValueInternal(getGuid() + name, value);
    }

    private void setValueInternal(String name, long value) {
        Editor editor = preferenceManager.edit();
        editor.putLong(name, (value < 0L) ? 0L : value);
        editor.apply();
    }

    public boolean getValue(String name, boolean value) {
        return getGuidValue(getGuid(), name, value);
    }

    public boolean getGuidValue(String guid, String name, boolean value) {
        return preferenceManager.getBoolean(guid + name, value);
    }

    private boolean getValueInternal(String name, boolean value) {
        return preferenceManager.getBoolean(name, value);
    }

    public void setValue(String name, boolean value) {
        setValueInternal(getGuid() + name, value);
    }

    private void setValueInternal(String name, boolean value) {
        Editor editor = preferenceManager.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public boolean has(String name) {
        return preferenceManager.contains(name);
    }

    public void removeValue(String name) {
        removeValueInternal(getGuid() + name);
    }

    public void removeValue(String guid, String name) {
        removeValueInternal(guid + name);
    }

    public void removeGlobalValue(String name) {
        removeValueInternal(name);
    }

    private void removeValueInternal(String name) {
        Editor editor = preferenceManager.edit();
        editor.remove(name);
        editor.apply();
    }

    public void clear() {
        Editor editor = preferenceManager.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Clears everything but the GUID for logging back in
     */
    public void logOut() {
        removeGlobalValue(PrefsUtil.GLOBAL_LOGGED_IN_GUID);
    }

    /**
     * Reset value once user logged in
     */
    public void logIn() {
        setValue(PrefsUtil.LOGGED_OUT, false);
    }

    public void addListValue(String name, String value) {
        String prev = getValue(name, "");
        if (prev.isEmpty()) {
            setValue(name, value);
        } else {
            setValue(name, prev + "|" + value.trim());
        }
    }

    public void addGlobalListValue(String name, String value) {
        String prev = getGlobalValue(name, "");
        if (prev.isEmpty()) {
            setGlobalValue(name, value);
        } else {
            setGlobalValue(name, prev + "|" + value.trim());
        }
    }

    public String[] getGlobalValueList(String name) {
        if (getGlobalValue(name, "").isEmpty()) {
            return new String[]{};
        } else {
            return getGlobalValue(name, "").split("\\|");
        }
    }

    public String[] getValueList(String name) {
        if (getValue(name, "").isEmpty()) {
            return new String[]{};
        } else {
            return getValue(name, "").split("\\|");
        }
    }

    public void setValue(String name, String[] value) {
        setValue(name, org.apache.commons.lang3.StringUtils.join(value, "|"));
    }

    public void setGlobalValue(String name, String[] value) {
        setGlobalValue(name, org.apache.commons.lang3.StringUtils.join(value, "|"));
    }

    public String getGuid() {
        return getGlobalValue(PrefsUtil.GLOBAL_LAST_LOGGED_IN_GUID, "");
    }

    public String getEnvironment() {
        return getGlobalValue(PrefsUtil.GLOBAL_CURRENT_ENVIRONMENT, EnvironmentManager.KEY_ENV_PROD);
    }


    public void removeAllGuid(String guid) {
        removeValue(guid + PrefsUtil.KEY_AB_ADDRESSES);
        removeValue(guid + PrefsUtil.KEY_AB_NAMES);

        removeValue(guid + PrefsUtil.KEY_PIN_FAILS);
        removeValue(guid + PrefsUtil.KEY_WALLET_NAME);
        removeValue(guid + PrefsUtil.KEY_PUB_KEY);
        removeValue(guid + PrefsUtil.KEY_ENCRYPTED_WALLET);
        removeValue(guid + PrefsUtil.KEY_ENCRYPTED_PASSWORD);
    }

    public void removeListValue(String name, int index) {
        setValue(name, ArrayUtils.remove(getValueList(name), index));
    }

    public void removeGlobalListValue(String name, int index) {
        setGlobalValue(name, ArrayUtils.remove(getGlobalValueList(name), index));
    }
}
