package reddragon.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class RedDragonApiMod implements ModInitializer {

	public static final String NAMESPACE = "reddragonapi";

	public static final Logger LOG = LogManager.getLogger(NAMESPACE);

	@Override
	public void onInitialize() {
		LOG.info("RedDragon API initialized");
	}

}
