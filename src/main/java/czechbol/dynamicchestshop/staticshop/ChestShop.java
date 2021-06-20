package czechbol.dynamicchestshop.staticshop;

import java.util.regex.Pattern;


public class ChestShop {
    static final int NAME_LINE = 0;
    static final int QUANTITY_LINE = 1;
    static final int PRICES_LINE = 2;
    static final int MATERIAL_LINE = 3;

    private static final Pattern price_pattern = Pattern.compile("([BS]\\d+)(:(\\d+[BS]))?");
    private static final Pattern buy_price_pattern = Pattern.compile("B (\\d+)");
    private static final Pattern sell_price_pattern = Pattern.compile("(S (\\d+))|((\\d+) S)");

    public static String formatPrices(String raw) throws Exception {
        var prices = raw.replaceAll("\\s+", "");

        String buy_price = null, sell_price = null;

        var m = price_pattern.matcher(prices);
        if (m.find()) {
            var price = m.group(1);
            if (price.contains("B")) {
                buy_price = price.replace("B", "");
            } else if (price.contains("S")) {
                sell_price = price.replace("S", "");
            }

            price = m.group(3);

            if (price.contains("B")) {
                buy_price = price.replace("B", "");
            } else if (price.contains("S")) {
                sell_price = price.replace("S", "");
            }
        } else {
            throw new Exception("Bad format");
        }

        StringBuilder prices_line = new StringBuilder();

        if (buy_price != null) {
            prices_line.append("B ");
            prices_line.append(buy_price);

            if (sell_price != null) {
                prices_line.append(":");
                prices_line.append(sell_price);
                prices_line.append("S");
            }
        } else if (sell_price != null) {
            prices_line.append("S ");
            prices_line.append(sell_price);
        } else {
            throw new Exception("Bad format");
        }

        return prices_line.toString();
    }

    public static float getBuyPrice(String in) {
        var m = buy_price_pattern.matcher(in);

        if (m.find()) {
            return Float.parseFloat(m.group(1));
        }

        return -1;
    }

    public static float getSellPrice(String in) {
        var m = sell_price_pattern.matcher(in);

        if (m.find()) {
            return Float.parseFloat(m.group(1));
        }

        return -1;
    }
}
