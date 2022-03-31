package com.rocketnotfound.rnf.sound;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RNFSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(RNF.MOD_ID, Registry.SOUND_EVENT_KEY);

    public static final RegistrySupplier<SoundEvent> RITUAL_GENERIC_CHANGE = registerSoundEvent(createIdentifier("ritual.generic.change"));
    public static final RegistrySupplier<SoundEvent> RITUAL_GENERIC_DORMANT = registerSoundEvent(createIdentifier("ritual.generic.dormant"));
    public static final RegistrySupplier<SoundEvent> RITUAL_GENERIC_SET = registerSoundEvent(createIdentifier("ritual.generic.set"));
    public static final RegistrySupplier<SoundEvent> RITUAL_GENERIC_FALTER = registerSoundEvent(createIdentifier("ritual.generic.falter"));
    public static final RegistrySupplier<SoundEvent> RITUAL_GENERIC_BREAK = registerSoundEvent(createIdentifier("ritual.generic.break"));

    public static final RegistrySupplier<SoundEvent> RITUAL_GENERIC_PROGRESS = registerSoundEvent(createIdentifier("ritual.generic.progress"));
    public static final RegistrySupplier<SoundEvent> RITUAL_GENERIC_COMPLETE = registerSoundEvent(createIdentifier("ritual.generic.complete"));
    public static final RegistrySupplier<SoundEvent> RITUAL_GENERIC_INTERRUPT = registerSoundEvent(createIdentifier("ritual.generic.interrupt"));

    public static final RegistrySupplier<SoundEvent> RITUAL_STAFF_CHANGE = registerSoundEvent(createIdentifier("ritual.staff.change"));

    private static final RegistrySupplier<SoundEvent> registerSoundEvent(Identifier id) {
        return SOUND_EVENTS.register(id, () -> new SoundEvent(id));
    }
}
