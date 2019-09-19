package hopfiel;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Created by cosijopii on 22/01/17.
 */
public class PaneCell extends Pane {


    private int idd;
    private Color color=Color.WHITE;


    public PaneCell() {
        super();
    }

    public int getIdd() {
        return idd;
    }

    public void setIdd(int idd) {
        this.idd = idd;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
