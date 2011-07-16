package javaheart.Game;

import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javaheart.Game.Global.POSITION;

public class Card {

	private static final int Offset = 30;
	private JLabel jLabel = new JLabel();
	private ImageIcon icon;
	private boolean picked;
	private Point location;
	private String name;

	public String getName() { return name; }
	public JLabel getJLabel() { return this.jLabel; }

	public Card(ImageIcon icon, Point location) {
		this.icon = icon;
		this.name = icon.getDescription();
		this.location = location;
		jLabel.setName(icon.getDescription());
		resetJLabel();
	}

	public void Pick(POSITION position) {
		if (!picked) {
			switch (position) {
				case TOP: location.y += Offset; break;
				case RIGHT: location.x -= Offset; break;
				case BOTTOM: location.y -= Offset; break;
				case LEFT: location.x += Offset; break;
			}
		} else {
			switch (position) {
				case TOP: location.y -= Offset; break;
				case RIGHT: location.x += Offset; break;
				case BOTTOM: location.y += Offset; break;
				case LEFT: location.x -= Offset; break;
			}
		}
		picked = !picked;
		resetJLabel();
	}

	public void Move(Point destination) {
		location = destination;
		picked = false;
		resetJLabel();
	}

	public void Scale(float ratio) {
		int width = (int) (icon.getIconWidth() * ratio);
		int height = (int) (icon.getIconHeight() * ratio);
		Image img = icon.getImage();
		img = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);

		// reset the jLabel
		resetJLabel();
	}

	// Helpers
	public void resetJLabel() {
		jLabel.setIcon(icon);
		jLabel.setBounds((int) location.getX(), (int) location.getY(), icon.getIconWidth(), icon.getIconHeight());
	}

}
