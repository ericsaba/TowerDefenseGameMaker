package gae.editorView;

import engine.gameobject.PointSimple;
import gae.gridView.ContainerWrapper;
import javafx.geometry.Point2D;
import javafx.scene.layout.Region;


public class Container extends Region implements ContainerWrapper {

    /**
     * Important method that checks if the object's coordinate is on the grid
     */
    @Override
    public boolean checkBounds (double x, double y) {
        Point2D point = this.screenToLocal(x, y);
        System.out.println("X IS : " + point.getX());
        System.out.println("Y IS : " + y);
        if (point.getX() < 0 || point.getX() > getWidth() || y < 0 ||
                y > getHeight()) {
            return true;
        }
        return false;
    }

    /**
     * Important method that converts other coordinate systems to that relative to the grid
     */
    @Override
    public PointSimple convertCoordinates (double x, double y) {
        Point2D point = this.screenToLocal(x, y);
        return new PointSimple(point.getX(), y);
    }

}
