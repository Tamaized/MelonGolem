package tamaized.melongolem.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import tamaized.melongolem.MelonMod;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class DonatorHandler {

	public static final Map<UUID, DonatorSettings> settings = new HashMap<>();
	private static final String URL_DONATORS = "https://gh.tamaized.com/Tamaized/MelonGolem/donator.properties";
	public static volatile List<UUID> donators = new ArrayList<>();
	private static boolean started = false;

	public static void start() {
		if (!started) {
			MelonMod.LOGGER.info("Starting Donator Handler");
			started = true;
			new ThreadDonators();
		}
	}

	public static void loadData(Properties props) {
		donators.clear();
		for (String s : props.stringPropertyNames()) {
			donators.add(UUID.fromString(s));
		}
		MelonMod.LOGGER.debug(donators);
	}

	public static final class DonatorSettings {
		public boolean enabled = true;
		public int color = 0xFFFFFF;

		public DonatorSettings(boolean enabled, int color) {
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
			MelonMod.LOGGER.info("Loading donor data");
			try (InputStreamReader data = new InputStreamReader(new URL(URL_DONATORS).openConnection().getInputStream())) {
				Properties props = new Properties();
				props.load(data);
				loadData(props);
				MelonMod.LOGGER.info("Donor data loaded");
			} catch (IOException e) {
				MelonMod.LOGGER.error("Could not load donor data");
			}
		}

	}

}