package org.saga.dependencies.spout;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.getspout.spoutapi.gui.Container;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.saga.Saga;
import org.saga.config.AttributeConfiguration;
import org.saga.player.SagaPlayer;


public class SpoutStatsPopup extends GenericPopup{


	public SpoutStatsPopup(SagaPlayer sagaPlayer) {

		
		DecimalFormat format = new DecimalFormat("00");
		
		Container stats = new GenericContainer();
		Container attrNames = new GenericContainer();
		Container attrVals = new GenericContainer();
		
		stats.setAuto(true);
		attrNames.setAuto(true);
		attrVals.setAuto(true);
		
		// Add attributes:
		ArrayList<String> attributes = AttributeConfiguration.config().getAttributeNames();
		
		for (String attribute : attributes) {
			
			// Attribute names:
			attrNames.addChild(new GenericLabel(attribute));
			
			// Attribute values:
			String score = format.format(sagaPlayer.getAttributeScore(attribute));
			String scoreMax = format.format(AttributeConfiguration.config().maxAttributeScore);
			attrVals.addChild(new GenericLabel(score + "/" + scoreMax));
			
		}
		
		attrNames.setWidth(100);
		attrVals.setWidth(100);
		
		// Combine stats:
		stats.addChild(attrNames);
		stats.addChild(attrVals);
		
		attrVals.setLayout(ContainerType.VERTICAL);
		attrNames.setLayout(ContainerType.VERTICAL);
		stats.setLayout(ContainerType.HORIZONTAL);
		stats.setAnchor(WidgetAnchor.CENTER_CENTER);
		
		stats.deferLayout();
		attrNames.deferLayout();
		attrVals.deferLayout();
		
		
		stats.shiftXPos(-stats.getWidth()/2);
		stats.shiftYPos(-stats.getHeight()/2);
		
		this.attachWidget(Saga.plugin(), stats);

		
	}
	
	
	
	
}
