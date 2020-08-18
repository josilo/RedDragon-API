package reddragon.api.utils;

import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvironmentUtils {

	public static void clientOnly(final Supplier<Runnable> runnable) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			runnable.get().run();
		}
	}
}
