package com.rocketnotfound.rnf.util;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.recipe.Ingredient;

import java.util.Iterator;
import java.util.Map;

public class RecipeHelper {
    public interface ReadSymbolType<T> {
        T valueToAdd(JsonElement jsonElement);
    }

    public static <T> Map<String, T> readSymbols(JsonObject jsonObject, ReadSymbolType<T> howToGet, T emptyValue) {
        Map<String, T> map = Maps.newHashMap();
        Iterator var2 = jsonObject.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<String, JsonElement> entry = (Map.Entry)var2.next();
            if (((String)entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put((String)entry.getKey(), howToGet.valueToAdd((JsonElement)entry.getValue()));
        }

        map.put(" ", emptyValue);
        return map;
    }
}
