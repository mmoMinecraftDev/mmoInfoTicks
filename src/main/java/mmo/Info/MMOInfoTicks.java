/*
 * This file is part of mmoInfoTicks <http://github.com/mmoMinecraftDev/mmoInfoTicks>,
 * which is part of mmoMinecraft <http://github.com/mmoMinecraftDev>.
 *
 * mmoInfoTicks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmo.Info;

import java.util.HashMap;
import java.util.Map;
import mmo.Core.InfoAPI.MMOInfoEvent;
import mmo.Core.MMOPlugin;
import mmo.Core.util.EnumBitSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MMOInfoTicks extends MMOPlugin implements Listener {

	private static final Map<Player, CustomWidget> WIDGETS = new HashMap<Player, CustomWidget>();
	private int tps = 0;

	@Override
	public EnumBitSet mmoSupport(final EnumBitSet support) {
		support.set(Support.MMO_NO_CONFIG);
		support.set(Support.MMO_AUTO_EXTRACT);
		return support;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		pm.registerEvents(this, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			private long currentSec = 0;
			private int ticks = 0;

			@Override
			public void run() {
				final long sec = (System.currentTimeMillis() / 1000);

				ticks++;
				if (currentSec != sec) {
					tps = Math.min(20, (tps + ticks) / 2); // Will be incorrect to start
					currentSec = sec;
					ticks = 0;
				}
			}
		}, 0, 1);
	}

	@EventHandler
	public void onMMOInfo(final MMOInfoEvent event) {
		if (event.isToken("ticks")) {
			final SpoutPlayer player = event.getPlayer();
			if (player.hasPermission("mmo.info.ticks")) {
				final CustomWidget widget = new CustomWidget();
				WIDGETS.put(player, widget);
				event.setWidget(plugin, widget);
// Can't think of an icon for it...
//				event.setIcon("clock.png");
			}
		}
	}

	public class CustomWidget extends GenericContainer {

		private final Gradient left = new GenericGradient(new Color(0.0f, 1.0f, 0.0f, 0.75f));
		private final Gradient right = new GenericGradient(new Color(1.0f, 0.0f, 0.0f, 0.75f));
		private final Gradient background = new GenericGradient(new Color(0.0f, 0.0f, 0.0f, 0.75f));

		public CustomWidget() {
			super();
			left.setMargin(1, 1, 1, 1).setPriority(RenderPriority.High);
			right.setMargin(1, 1, 1, 41).setPriority(RenderPriority.High);
			this.setLayout(ContainerType.OVERLAY).setFixed(true).setWidth(42).setHeight(10);
			this.addChildren(background, left, right);
		}

		@Override
		public void onTick() {
			final int tpsWidth = (tps * 2) + 1;
			left.setMarginRight(tpsWidth);
			right.setMarginLeft(tpsWidth);
		}
	}
}
