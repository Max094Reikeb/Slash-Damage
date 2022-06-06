package net.reikeb.slash_damage;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Map;

public class DamageSourceManager extends SimpleJsonResourceReloadListener {

    public static ArrayList<String> DAMAGES_SOURCES = new ArrayList<>();

    public DamageSourceManager() {
        super(new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(), "damage_sources");
    }

    @Override
    public void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profilerFiller) {
        DAMAGES_SOURCES.clear();
        loadDamageSources(map);
    }

    private static void loadDamageSources(Map<ResourceLocation, JsonElement> map) {
        map.forEach((file, jsonElement) -> {
            try {
                JsonObject config = jsonElement.getAsJsonObject();
                JsonArray array = config.getAsJsonArray("name");
                array.forEach((element) -> DAMAGES_SOURCES.add(element.getAsString()));
            } catch (Exception e) {
                SlashDamage.LOGGER.error("Error when loading {" + file.toString() + "} from data!: " + e.getMessage());
            }
        });
    }
}
