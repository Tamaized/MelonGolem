package tamaized.melongolem.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Avoid adding to this class, this is strictly for {@link ClientListener} initialization from {@link tamaized.melongolem.MelonMod#MelonMod(IEventBus)} via {@link FMLEnvironment#dist} == {@link Dist#CLIENT}
 */
public class ClientInitiator {

	public static void call(IEventBus busMod) {
		ClientListener.init(busMod);
	}

}
