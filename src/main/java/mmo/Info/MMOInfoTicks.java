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

import java.text.DecimalFormat;
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
	private double tps = 0;

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
			private double ticks = 0;

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
			}
		}
	}

	//RGBA to RGBA Float = (255,0,0) = (1,0,0, divide color code by 255 to get float value.

	public class CustomWidget extends GenericContainer {

		private final Gradient left = new GenericGradient(new Color(0, 1f, 0, 0.75f)); //Green
		private final Gradient right = new GenericGradient(new Color(0.69f, 0.09f, 0.12f, 1)); //Red
		private final Gradient background = new GenericGradient(new Color(0, 0, 0, 0.75f)); //Black
		private final Label label = new GenericLabel("20");
		private final Label label2 = new GenericLabel("Server:");
		private final DecimalFormat df = new DecimalFormat("00.0");

		public CustomWidget() {
			super();
			left.setMargin(1).setPriority(RenderPriority.Low).setHeight(8).setWidth(30).shiftXPos(0);
			right.setMargin(1).setPriority(RenderPriority.Normal).setHeight(8).setWidth(30).shiftXPos(0);
			background.setMargin(1).setPriority(RenderPriority.Low).setHeight(8).shiftXPos(0);
			label.setScale(0.8f).setMargin(1).setPriority(RenderPriority.Lowest).setHeight(5).shiftXPos(6).shiftYPos(1);
			label2.setScale(0.8f).setMargin(1).setPriority(RenderPriority.Lowest).setHeight(5).shiftXPos(-35).shiftYPos(1);
			this.setLayout(ContainerType.OVERLAY).setMinWidth(35).setMaxWidth(35);
			this.addChildren(left, right, label, label2);
		}

		private transient int tick = 0;
		@Override
		public void onTick() {
			if (tick++ % 50 == 0) {		
				final double tpsWidth = ((int) tps+10);				 
				label.setText(df.format(tps));			
				if (tps<=18.0) {
					left.setWidth((int) (tpsWidth));
				} else {
					left.setWidth(30);
				}				
			}
		}
	}
}
