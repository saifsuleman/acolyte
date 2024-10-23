package net.odinmc.core.common.util;

public class StringUtil {
  public static String limit(String string, int maxLength) {
    if (string.length() > maxLength) {
      return string.substring(0, maxLength);
    } else {
      return string;
    }
  }

  public static String join(String seperator, String... strings) {
    return String.join(seperator, strings);
  }

  public static boolean wildcardCompare(String string, String like) {
    return wildcardCompare(string, like, '%');
  }

  public static boolean wildcardCompare(String string, String like, char wildcard) {
    return string.matches(("\\Q" + like + "\\E").replace(String.valueOf(wildcard), "\\E.*\\Q"));
  }
}
