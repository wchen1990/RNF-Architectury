package com.rocketnotfound.rnf.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RitualManager extends JsonDataLoader {
    private static RitualManager instance;
    public static RitualManager getInstance() {
        if (instance == null) {
            instance = new RitualManager();
        }
        return instance;
    }

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes = ImmutableMap.of();
    private Map<Identifier, Recipe<?>> recipesById = ImmutableMap.of();
    private boolean errored;

    public RitualManager() {
        super(GSON, "rituals");
    }

    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        this.errored = false;
        Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2 = Maps.newHashMap();
        ImmutableMap.Builder<Identifier, Recipe<?>> builder = ImmutableMap.builder();
        Iterator var6 = map.entrySet().iterator();

        while(var6.hasNext()) {
            Map.Entry<Identifier, JsonElement> entry = (Map.Entry)var6.next();
            Identifier identifier = (Identifier)entry.getKey();

            try {
                Recipe<?> recipe = deserialize(identifier, JsonHelper.asObject((JsonElement)entry.getValue(), "top element"));
                ((ImmutableMap.Builder)map2.computeIfAbsent(recipe.getType(), (recipeType) -> {
                    return ImmutableMap.builder();
                })).put(identifier, recipe);
                builder.put(identifier, recipe);
            } catch (IllegalArgumentException | JsonParseException var10) {
                LOGGER.error("Parsing error loading ritual {}", identifier, var10);
            }
        }

        this.recipes = (Map)map2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entryx) -> {
            return ((ImmutableMap.Builder)entryx.getValue()).build();
        }));
        this.recipesById = builder.build();
        LOGGER.info("Loaded {} rituals", map2.size());
    }

    public boolean isErrored() {
        return this.errored;
    }

    public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(RecipeType<T> recipeType, C inventory, World world) {
        return this.getAllOfType(recipeType).values().stream().flatMap((recipe) -> {
            return recipeType.match(recipe, world, inventory).stream();
        }).findFirst();
    }

    public <C extends Inventory, T extends Recipe<C>> List<T> listAllOfType(RecipeType<T> recipeType) {
        return (List)this.getAllOfType(recipeType).values().stream().map((recipe) -> {
            return recipe;
        }).collect(Collectors.toList());
    }

    public <C extends Inventory, T extends Recipe<C>> List<T> getAllMatches(RecipeType<T> recipeType, C inventory, World world) {
        return (List)this.getAllOfType(recipeType).values().stream().flatMap((recipe) -> {
            return recipeType.match(recipe, world, inventory).stream();
        }).sorted(Comparator.comparing((recipe) -> {
            return recipe.getOutput().getTranslationKey();
        })).collect(Collectors.toList());
    }

    private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> recipeType) {
        return (Map)this.recipes.getOrDefault(recipeType, Collections.emptyMap());
    }

    public Optional<? extends Recipe<?>> get(Identifier identifier) {
        return Optional.ofNullable((Recipe<?>)this.recipesById.get(identifier));
    }

    public Collection<Recipe<?>> values() {
        return (Collection)this.recipes.values().stream().flatMap((map) -> {
            return map.values().stream();
        }).collect(Collectors.toSet());
    }

    public Stream<Identifier> keys() {
        return this.recipes.values().stream().flatMap((map) -> {
            return map.keySet().stream();
        });
    }

    public static Recipe<?> deserialize(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "type");
        return ((RecipeSerializer) Registry.RECIPE_SERIALIZER.getOrEmpty(new Identifier(string)).orElseThrow(() -> {
            return new JsonSyntaxException("Invalid or unsupported ritual type '" + string + "'");
        })).read(identifier, jsonObject);
    }

    public void setRecipes(Iterable<Recipe<?>> iterable) {
        this.errored = false;
        Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map = Maps.newHashMap();
        ImmutableMap.Builder<Identifier, Recipe<?>> builder = ImmutableMap.builder();
        iterable.forEach((recipe) -> {
            Map<Identifier, Recipe<?>> map2 = (Map)map.computeIfAbsent(recipe.getType(), (recipeType) -> {
                return Maps.newHashMap();
            });
            Identifier identifier = recipe.getId();
            Recipe<?> recipe2 = (Recipe)map2.put(identifier, recipe);
            builder.put(identifier, recipe);
            if (recipe2 != null) {
                throw new IllegalStateException("Duplicate ritual ignored with ID " + identifier);
            }
        });
        this.recipes = ImmutableMap.copyOf(map);
        this.recipesById = builder.build();
    }
}