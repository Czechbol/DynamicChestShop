package czechbol.dynamicchestshop.dynamicshop;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Pattern;

public class CalculateInitialPrices {

    private static final int STONE_CUTTER_VALUE = 5;
    private static final int FURNACE_VALUE = 7;

    public static void calc(String inputFile, FileConfiguration config) throws FileNotFoundException {
        HashMap<String, Integer> materialPricesFromFile = new HashMap<>();
        List<Recipe> recipeQueue = new ArrayList<>();
        Pattern pattern = Pattern.compile("(.+)[:] (\\d+)");
        Scanner scanner = new Scanner(new FileReader(inputFile));

        while (scanner.hasNextLine()) {
            var m = pattern.matcher(scanner.nextLine());
            if (m.find()) {
                materialPricesFromFile.put(m.group(1), Integer.parseInt(m.group(2)));
                config.set(m.group(1) + ".Price", Integer.parseInt(m.group(2)));
            }
        }
        scanner.close();

        for (Iterator<Recipe> it = Bukkit.getServer().recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            HashMap<String, Integer> materialsQueue = new HashMap<>();
            ifStatements(materialPricesFromFile, materialsQueue, recipe, recipeQueue, true, config);
        }

        for (int i = 0; i < 100; i++) {
            for (Recipe recipe : recipeQueue) {
                HashMap<String, Integer> materialsQueue = new HashMap<>();
                ifStatements(materialPricesFromFile, materialsQueue, recipe, recipeQueue, false, config);
            }
        }
    }

    private static void ifStatements(HashMap<String, Integer> materialPricesFromFile,
                                     HashMap<String, Integer> materialsQueue,
                                     Recipe recipe,
                                     List<Recipe> recipeQueue,
                                     boolean useRecipeQueue,
                                     FileConfiguration config) {
        if (recipe instanceof FurnaceRecipe r) {
            if (materialPricesFromFile.containsKey(r.getInput().getType().toString())) {
                materialsQueue.put(r.getInput().getType().toString(),
                        materialsQueue.getOrDefault(r.getInput().getType().toString(), FURNACE_VALUE)
                                + materialPricesFromFile.get(r.getInput().getType().toString()));
            }
        } else if (recipe instanceof ShapedRecipe r) {
            for (char c : r.getIngredientMap().keySet()) {
                if (r.getIngredientMap().get(c) == null) continue;
                if (materialPricesFromFile.containsKey(r.getIngredientMap().get(c).getType().toString())) {
                    if (materialsQueue.containsKey(r.getIngredientMap().get(c).getType().toString())) {
                        materialsQueue.put(r.getIngredientMap().get(c).getType().toString(),
                                materialsQueue.get(r.getIngredientMap().get(c).getType().toString())
                                        + materialPricesFromFile.get(r.getIngredientMap().get(c).getType().toString()));
                    } else
                        materialsQueue.put(r.getIngredientMap().get(c).getType().toString(),
                                materialPricesFromFile.get(r.getIngredientMap().get(c).getType().toString()));
                }
            }
        } else if (recipe instanceof ShapelessRecipe r) {
            for (ItemStack itemStack : r.getIngredientList()) {
                if (itemStack == null) continue;
                if (materialPricesFromFile.containsKey(itemStack.getType().toString())) {
                    if (materialsQueue.containsKey(itemStack.getType().toString())) {
                        materialsQueue.put(itemStack.getType().toString(),
                                materialsQueue.get(itemStack.getType().toString())
                                        + materialPricesFromFile.get(itemStack.getType().toString()));
                    } else
                        materialsQueue.put(itemStack.getType().toString(),
                                materialPricesFromFile.get(itemStack.getType().toString()));
                }
            }
        } else if (recipe instanceof StonecuttingRecipe r) {
            if (materialPricesFromFile.containsKey(r.getInput().getType().toString())) {
                materialsQueue.put(r.getInput().getType().toString(),
                        materialsQueue.getOrDefault(r.getInput().getType().toString(), STONE_CUTTER_VALUE)
                                + materialPricesFromFile.get(r.getInput().getType().toString()));
            }
        } else {
            if (useRecipeQueue)
                recipeQueue.add(recipe);
        }

        var price = 0;
        for (String s : materialsQueue.keySet()) {
            price += materialsQueue.get(s);
        }

        if (price != 0 && recipe.getResult().getAmount() != 0) {
            config.set(recipe.getResult().getType().toString() + ".Price", price / recipe.getResult().getAmount());
            materialPricesFromFile.put(recipe.getResult().getType().toString(), price / recipe.getResult().getAmount());
        }
    }
}
