package com.rocketnotfound.rnf.data.managers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.rocketnotfound.rnf.data.spells.ISpell;
import com.rocketnotfound.rnf.data.spells.ISpellType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpellManager extends JsonDataLoader {
    private static SpellManager instance;
    public static SpellManager getInstance() {
        if (instance == null) {
            instance = new SpellManager();
        }
        return instance;
    }

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<ISpellType<?>, Map<Identifier, ISpell>> spells = ImmutableMap.of();
    private Map<Identifier, ISpell> spellsById = ImmutableMap.of();
    private boolean errored;

    private SpellManager() {
        super(GSON, "spells");
    }

    protected void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        this.errored = false;
        Map<RecipeType<?>, ImmutableMap.Builder<Identifier, ISpell>> map2 = Maps.newHashMap();
        ImmutableMap.Builder<Identifier, ISpell> builder = ImmutableMap.builder();
        Iterator var6 = map.entrySet().iterator();

        while(var6.hasNext()) {
            Map.Entry<Identifier, JsonElement> entry = (Map.Entry)var6.next();
            Identifier identifier = (Identifier)entry.getKey();

            try {
                ISpell recipe = deserialize(identifier, JsonHelper.asObject((JsonElement)entry.getValue(), "top element"));
                ((ImmutableMap.Builder)map2.computeIfAbsent(recipe.getType(), (recipeType) -> {
                    return ImmutableMap.builder();
                })).put(identifier, recipe);
                builder.put(identifier, recipe);
            } catch (IllegalArgumentException | JsonParseException var10) {
                LOGGER.error("Parsing error loading spell {}", identifier, var10);
            }
        }

        this.spells = (Map)map2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entryx) -> {
            return ((ImmutableMap.Builder)entryx.getValue()).build();
        }));
        this.spellsById = builder.build();
        LOGGER.info("Loaded {} spells", map2.size());
    }

    public boolean isErrored() {
        return this.errored;
    }

    public Optional<ISpell> getFirstMatch(List<BlockPos> positions, ServerWorld world) {
        return getFirstMatch(positions, world, true);
    }
    public Optional<ISpell> getFirstMatch(List<BlockPos> positions, ServerWorld world, boolean reverseOrder) {
        return this.values().stream().sorted(Comparator.comparing((spell) -> {
            return spell.getLength() * (reverseOrder ? -1 : 1);
        })).filter((spell) -> spell.matches(positions, world)).findFirst();
    }

    public <T extends ISpell> Optional<T> getFirstMatch(ISpellType<T> spellType, List<BlockPos> positions, ServerWorld world) {
        return this.getAllOfType(spellType).values().stream().flatMap((spell) -> {
            return spellType.match((T) spell, world, positions).stream();
        }).findFirst();
    }

    public <T extends ISpell> List<T> listAllOfType(ISpellType<T> spellType) {
        return (List)this.getAllOfType(spellType).values().stream().map((recipe) -> {
            return recipe;
        }).collect(Collectors.toList());
    }

    public <T extends ISpell> List<T> getAllMatches(ISpellType<T> spellType, List<BlockPos> positions, ServerWorld world) {
        return (List)this.getAllOfType(spellType).values().stream().flatMap((spell) -> {
            return spellType.match((T) spell, world, positions).stream();
        }).sorted(Comparator.comparing((spell) -> {
            return spell.getLength();
        })).collect(Collectors.toList());
    }

    private <T extends ISpell> Map<Identifier, T> getAllOfType(ISpellType<T> spellType) {
        return (Map)this.spells.getOrDefault(spellType, Collections.emptyMap());
    }

    public <T extends ISpell> Optional<T> get(Identifier identifier) {
        return Optional.ofNullable((T) this.spellsById.get(identifier));
    }

    public Collection<ISpell> values() {
        return (Collection)this.spells.values().stream().flatMap((map) -> {
            return map.values().stream();
        }).collect(Collectors.toSet());
    }

    public Stream<Identifier> keys() {
        return this.spells.values().stream().flatMap((map) -> {
            return map.keySet().stream();
        });
    }

    public static ISpell deserialize(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "type");
        return (ISpell) ((RecipeSerializer) Registry.RECIPE_SERIALIZER.getOrEmpty(new Identifier(string)).orElseThrow(() -> {
            return new JsonSyntaxException("Invalid or unsupported spell type '" + string + "'");
        })).read(identifier, jsonObject);
    }

    public void setSpells(Iterable<ISpell> iterable) {
        this.errored = false;
        Map<ISpellType<?>, Map<Identifier, ISpell>> map = Maps.newHashMap();
        ImmutableMap.Builder<Identifier, ISpell> builder = ImmutableMap.builder();
        iterable.forEach((spell) -> {
            Map<Identifier, ISpell> map2 = (Map)map.computeIfAbsent((ISpellType) spell.getType(), (recipeType) -> {
                return Maps.newHashMap();
            });
            Identifier identifier = spell.getId();
            ISpell recipe2 = (ISpell)map2.put(identifier, spell);
            builder.put(identifier, spell);
            if (recipe2 != null) {
                throw new IllegalStateException("Duplicate spell ignored with ID " + identifier);
            }
        });
        this.spells = ImmutableMap.copyOf(map);
        this.spellsById = builder.build();
    }
}