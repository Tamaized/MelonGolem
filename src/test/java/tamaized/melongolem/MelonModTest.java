package tamaized.melongolem;

import net.neoforged.fml.ModList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MelonModTest {

	@Test
	public void modLoads() {
		assertTrue(ModList.get().isLoaded(MelonMod.MODID));
	}

}
