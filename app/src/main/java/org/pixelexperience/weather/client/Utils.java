package org.pixelexperience.weather.client;

public class Utils {
    public static String getSystemProperty(String key, String defaultValue) {
        String value;

        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
            return (value == null || value.isEmpty()) ? defaultValue : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
    public static Boolean isBuildValid(){
        if (getSystemProperty("org.pixelexperience.version","").isEmpty()) {
            return false;
        }
        if (getSystemProperty("org.pixelexperience.build_date","").isEmpty()) {
            return false;
        }
        if (getSystemProperty("org.pixelexperience.build_type","").isEmpty()) {
            return false;
        }
        if (getSystemProperty("org.pixelexperience.fingerprint","").isEmpty()) {
            return false;
        }
        return true;
    }
}
