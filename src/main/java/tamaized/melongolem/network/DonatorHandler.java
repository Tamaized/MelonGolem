package tamaized.melongolem.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
	private static final String URL_DONATORS = "https://raw.githubusercontent.com/Tamaized/MelonGolem/{BRANCH}/donator.properties";
	public static volatile List<UUID> donators = Lists.newArrayList();
	private static boolean started = false;

	public static void start() {
		if (!started) {
			MelonMod.instance.logger.info("Starting Donator Handler");
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
			MelonMod.instance.logger.info("Loading Data");
			try (InputStreamReader json = new InputStreamReader(new URL("https://api.github.com/repos/Tamaized/MelonGolem").openConnection().getInputStream())) {
				String branch = new Gson().fromJson(json, JsonObject.class).get("default_branch").getAsString();
				URL url = new URL(URL_DONATORS.replace("{BRANCH}", branch));
				MelonMod.instance.logger.debug(url);
				Properties props = new Properties();
				InputStreamReader reader = new InputStreamReader(url.openStream());
				props.load(reader);
				loadData(props);
				MelonMod.instance.logger.info("Data Loaded");
			} catch (IOException e) {
				MelonMod.instance.logger.error("Could not load data");
			}
		}

	}

}