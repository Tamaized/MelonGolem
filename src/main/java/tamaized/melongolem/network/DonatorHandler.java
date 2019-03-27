package tamaized.melongolem.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import tamaized.melongolem.MelonMod;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class DonatorHandler {

	public static final Map<UUID, DonatorSettings> settings = Maps.newHashMap();
	private static final String URL_DONATORS = "https://raw.githubusercontent.com/Tamaized/MelonGolem/1.12/donator.properties";
	public static volatile List<UUID> donators = Lists.newArrayList();
	private static boolean started = false;

	public static void start() {
		if (!started) {
			MelonMod.logger.info("Starting Donator Handler");
			started = true;
			new ThreadDonators();
		}
	}

	public static void loadData(Properties props) {
		donators.clear();
		for (String s : props.stringPropertyNames()) {
			donators.add(UUID.fromString(s));
		}
	}

	public static final class DonatorSettings {
		public boolean enabled = true;
		public int color = 0xFFFFFF;
		public DonatorSettings(boolean enabled, int color){
			this.enabled = enabled;
			this.color = color;
		}
	}

	private static class ThreadDonators extends Thread {

		public ThreadDonators() {
			setName("Melon Golem Donator Loader");
			setDaemon(true);
			start();
		}

		@Override
		public void run() {
			try {
				{
					MelonMod.logger.info("Loading Data");
					URL url = new URL(URL_DONATORS);
					Properties props = new Properties();
					InputStreamReader reader = new InputStreamReader(url.openStream());
					props.load(reader);
					loadData(props);
				}
			} catch (IOException e) {
				MelonMod.logger.error("Could not load data");
			}
		}

	}

}