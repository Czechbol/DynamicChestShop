package czechbol.dynamicchestshop.dynamicshop;

import java.util.regex.Pattern;


public class AdminShop {
    static final int NAME_LINE = 0;
    static final int QUANTITY_LINE = 1;
    static final int PRICES_LINE = 2;
    static final int MATERIAL_LINE = 3;

    static final int NOT_FOR_SALE = -1;
    static final int NOT_FOR_BUY = -1;

    private static final Pattern price_pattern = Pattern.compile("(\\d+(\\.\\d+)?)");
    private static final Pattern buy_price_pattern = Pattern.compile("B (\\d+(\\.\\d+)?)");
    private static final Pattern sell_price_pattern = Pattern.compile("(S (\\d+(\\.\\d+)?))|((\\d+(\\.\\d+)?) S)");

    public static int getBuyPrice(String in) {
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
                } else if (m.group(5) != null) {
                    str = m.group(5);
                }

                return Integer.parseInt(str);
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

        return NOT_FOR_SALE;
    }

    //TODO: testing
    public static float getPrice(String in) {
        var m = price_pattern.matcher(in);

        if (m.find()) {
            return Float.parseFloat(m.group(1));
        }

        return 0;
    }
}
