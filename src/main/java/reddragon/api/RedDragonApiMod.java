package reddragon.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class RedDragonApiMod implements ModInitializer {

	public static final String NAMESPACE = "reddragonapi";

	public static final Logger LOG = LogManager.getLogger(NAMESPACE);

	private static final String DRAGON_ASCII_LOGO = "                             ___====-_  _-====___\r\n" +
			"                       _--^^^#####//      \\\\#####^^^--_\r\n" +
			"                    _-^##########// (    ) \\\\##########^-_\r\n" +
			"                   -############//  |\\^^/|  \\\\############-\r\n" +
			"                 _/############//   (@::@)   \\\\############\\_\r\n" +
			"                /#############((     \\\\//     ))#############\\\r\n" +
			"               -###############\\\\    (oo)    //###############-\r\n" +
			"              -#################\\\\  / VV \\  //#################-\r\n" +
			"             -###################\\\\/      \\//###################-\r\n" +
			"            _#/|##########/\\######(   /\\   )######/\\##########|\\#_\r\n" +
			"            |/ |#/\\#/\\#/\\/  \\#/\\##\\  |  |  /##/\\#/  \\/\\#/\\#/\\#| \\|\r\n" +
			"            `  |/  V  V  `   V  \\#\\| |  | |/#/  V   '  V  V  \\|  '\r\n" +
			"               `   `  `      `   / | |  | | \\   '      '  '   '\r\n" +
			"                                (  | |  | |  )\r\n" +
			"                               __\\ | |  | | /__\r\n" +
			"                              (vvv(VVV)(VVV)vvv)";

	@Override
	public void onInitialize() {
		LOG.info("RedDragon API initialized\n" + DRAGON_ASCII_LOGO);
	}

}
