package tamaized.melongolem;

import com.google.common.collect.ImmutableSet;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.awt.*;
import java.util.Objects;
import java.util.Set;

@Mod.EventBusSubscriber
@Config(modid = MelonMod.MODID)
public class MelonConfig {

	@Config.Ignore
	public static boolean dirty = true;

	@Config.Name("Donator Settings")
	public static DonatorSettings donatorSettings = new DonatorSettings();

	@Config.Name("Base Golem Health")
	public static double health = 8.0D;

	@Config.Name("Melon Slice Damage")
	public static float damage = 4.0F;

	@Config.Name("Enable Golem Block Heads")
	public static boolean hats = true;

	@Config.Name("Shears Spawn Block")
	public static boolean shear = true;

	@Config.Name("Golem Eats Melons")
	public static boolean eats = true;

	@Config.Name("Melon Heal Amount")
	public static float heal = 1.0F;

	@Config.Name("TehNut Mode")
	public static boolean tehnutMode = false;

	@Config.Name("TTS Signs")
	public static boolean tts = true;

	@Config.Name("Stabby Life Item")
	@Config.Comment("domain:name:meta\ndomain defaults to `minecraft`\nmeta is optional\ndomain is required if meta is specified")
	public static String stabby = Objects.requireNonNull(Items.STICK.getRegistryName()).getPath();

	@Config.Ignore
	public static ItemStackWrapper stabItem = new ItemStackWrapper(Items.STICK);

	public static void setupStabby() {
		String[] split = stabby.split(":");
		String domain = "minecraft";
		String regname = split[0];
		int meta = 0;
		boolean hasmeta = false;
		if (split.length > 1) {
			domain = split[0];
			regname = split[1];
			if (split.length > 2)
				try {
					meta = Integer.parseInt(split[2]);
					hasmeta = true;
				} catch (NumberFormatException e) {
					hasmeta = false;
				}
		}
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(domain, regname));
		if (item == null || item instanceof ItemAir)
			return;
		stabItem = hasmeta ? new ItemStackWrapper(item, meta) : new ItemStackWrapper(item);
	}

	public static boolean compareStabbyItem(ItemStack stack) {
		return ItemStackWrapper.compare(ImmutableSet.of(stabItem), stack);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(MelonMod.MODID)) {
			ConfigManager.sync(MelonMod.MODID, Config.Type.INSTANCE);
			setupStabby();
			try {
				donatorSettings.colorint = Color.decode(donatorSettings.color).getRGB();
			} catch (Throwable e) {
				donatorSettings.color = "0xFFFFFF";
				donatorSettings.colorint = 0xFFFFFF;
			}
			dirty = true;
		}
	}

	public static class DonatorSettings {

		@Config.Name("Enabled")
		public boolean enable = true;

		@Config.Name("Color (0xRRGGBB)")
		public String color = "0xFFFFFF";

		@Config.Ignore
		public int colorint = 0xFFFFFF;
	}

	static class ItemStackWrapper {

		boolean ignoreMeta;
		boolean ignoreNBT;
		ItemStack stack;

		public ItemStackWrapper(Item item) {
			this(item, 0);
			ignoreMeta = true;
		}

		public ItemStackWrapper(Item item, int meta) {
			this(new ItemStack(item, 1, meta), false, true);
		}

		public ItemStackWrapper(NBTTagCompound tag, boolean ignoreMeta, boolean ignoreNBT) {
			this(new ItemStack(tag), ignoreMeta, ignoreNBT);
		}

		public ItemStackWrapper(ItemStack stack, boolean ignoreMeta, boolean ignoreNBT) {
			this.stack = stack;
			this.ignoreMeta = ignoreMeta;
			this.ignoreNBT = ignoreNBT;
		}

		public static boolean compare(Set<ItemStackWrapper> set, ItemStack stack) {
			boolean flag;
			for (ItemStackWrapper wrapper : set) {
				if (wrapper.ignoreMeta && wrapper.ignoreNBT)
					flag = wrapper.stack.getItem() == stack.getItem();
				else if (wrapper.ignoreNBT)
					flag = wrapper.stack.isItemEqual(stack);
				else if (wrapper.ignoreMeta)
					flag = wrapper.stack.getItem() == stack.getItem() && ItemStack.areItemStackTagsEqual(wrapper.stack, stack);
				else
					flag = ItemStack.areItemStacksEqual(wrapper.stack, stack);
				if (flag)
					return true;
			}
			return false;
		}

		public ItemStackWrapper attachNBT(NBTTagCompound tag) {
			ignoreNBT = false;
			stack.setTagCompound(tag);
			return this;
		}

	}

}
