package net.reikeb.slash_damage;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.reikeb.slash_damage.commands.DamageCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SlashDamage.MODID)
@Mod.EventBusSubscriber(modid = SlashDamage.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SlashDamage {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "slash_damage";

    public SlashDamage() {

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        DamageCommand.register(commandDispatcher);
    }
}
