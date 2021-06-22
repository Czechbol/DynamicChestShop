package czechbol.dynamicchestshop.dynamicshop;

import czechbol.dynamicchestshop.DynamicChestShop;

import java.util.regex.Pattern;


public class AdminShop {
    static final int NAME_LINE = 0;
    static final int QUANTITY_LINE = 1;
    static final int PRICES_LINE = 2;
    static final int MATERIAL_LINE = 3;

    static final int NOT_FOR_SALE = -1;
    static final int NOT_FOR_BUY = -1;

    private static final Pattern price_pattern = Pattern.compile("([BS]\\d+)(:(\\d+[BS]))?");
    private static final Pattern buy_price_pattern = Pattern.compile("B (\\d+)");
    private static final Pattern sell_price_pattern = Pattern.compile("(S (\\d+))|((\\d+) S)");

    private static final boolean higherSellThanBuyFlag = DynamicChestShop.getConf().getBoolean("General.BLockHigherSellThanBuy");

    public static String formatPrices(String raw) throws Exception {
        var prices = raw.replaceAll("\\s+", "");

        String buy_price = null, sell_price = null;

        var m = price_pattern.matcher(prices);
        if (m.find()) {
            var price = m.group(1);
            if (price.contains("B")) {
                buy_price = price.replace("B", "");
                price = m.group(3);
            }
            if (price != null)
                if (price.contains("S")) {
                    sell_price = price.replace("S", "");
                }
        } else {
            throw new Exception("Bad format");
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (buy_price != null) {
            stringBuilder.append("B ").append(buy_price);
            if (sell_price != null) {
                if (buy_price.compareTo(sell_price) < 0 && higherSellThanBuyFlag) {
                    throw new Exception("Sell price can not be higher that buy price.");
                }
                stringBuilder.append(":").append(sell_price).append(" S");
                return stringBuilder.toString();
            }
        } else if (sell_price != null) {
            stringBuilder.append("S ").append(sell_price);
        }

        return stringBuilder.toString();
    }

    public static float getBuyPrice(String in) {
        var m = buy_price_pattern.matcher(in);

        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }

        return NOT_FOR_BUY;
    }

    public static int getSellPrice(String in) {
        var m = sell_price_pattern.matcher(in);
        String str = null;

        try {
            if (m.find()) {
                if (m.group(2) != null) {
                    str = m.group(2);
                } else if (m.group(4) != null) {
                    str = m.group(4);
                }

                return Integer.parseInt(str);
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

        return NOT_FOR_SALE;
    }
}
