/* ==============================================
 *  NestedShape.java : NestedShape superclass represents nested shapes. A
 *  NestedShape contains zero or more inner shapes that bounce around within this shape. NestedShape instance
 *  can be RectangleShape and OvalShape objects, or other NestedShape instances.
 *  YOUR UPI: kcha612
 *  Name: Kavi Chand
 *  ===============================================================================
 */

import java.awt.*;
import java.util.*;


public class NestedShape extends RectangleShape{
    private ArrayList<Shape> innerShapes = new ArrayList<Shape>();

    public NestedShape(){
        super();
        createInnerShape(0, 0, width/2, height/2, color, borderColor, PathType.BOUNCING, ShapeType.RECTANGLE);
    }

    public NestedShape(int x, int y, int width, int height, int panelwidth, int panelheight, Color color, Color bordercolor, PathType pathtype){
        super(x, y, width, height, panelwidth, panelheight, color, bordercolor, pathtype);
        createInnerShape(0, 0, width/2, height/2, color, borderColor, PathType.BOUNCING, ShapeType.RECTANGLE);
    }

    public NestedShape(int width, int height){
       super(0, 0, width, height, DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT, Color.black, Color.black, PathType.BOUNCING);
    }

    public Shape createInnerShape(int x, int y, int w, int h, Color c, Color bc, PathType pt, ShapeType st){
        Shape shape;
        if (st == ShapeType.RECTANGLE){
            shape = new RectangleShape(x, y, w, h, width, height, c, bc, pt);
            innerShapes.add(shape);
            shape.setParent(this);
            return shape;
        }
        if (st == ShapeType.OVAL){
            shape = new OvalShape(x, y, w, h, width, height, c, bc, pt);
            innerShapes.add(shape);
            shape.setParent(this);
            return shape;
        }
        if (st == ShapeType.NESTED){
            shape = new NestedShape(x, y, w, h, width, height, c, bc, pt);
            innerShapes.add(shape);
            shape.setParent(this);
            return shape;
        }
        return null;
    }
    public Shape createInnerShape(PathType pt, ShapeType st){
        Shape shape;
        if (st == ShapeType.RECTANGLE){
            shape = new RectangleShape(0, 0, width/2, height/2, width, height, color, borderColor, pt);
            innerShapes.add(shape);
            shape.setParent(this);
            return shape;
        }
        if (st == ShapeType.OVAL){
            shape = new OvalShape(0, 0, width/2, height/2, width, height, color, borderColor, pt);
            innerShapes.add(shape);
            shape.setParent(this);
            return shape;
        }
        if (st == ShapeType.NESTED){
            shape = new NestedShape(0, 0, width/2, height/2, width, height, color, borderColor, pt);
            innerShapes.add(shape);
            shape.setParent(this);
            return shape;
        }
        return null;
    }
    public Shape getInnerShapeAt(int index){
        return innerShapes.get(index);
    }
    public int getSize(){
        return innerShapes.size();
    }
    
    public void draw(Graphics g){
        g.setColor(Color.black);
        g.drawRect(x, y, width, height);
        g.translate(x, y);
        for (Shape shape: innerShapes){
            shape.draw(g);
            if (shape.isSelected()){
                shape.drawHandles(g);
            }
            shape.drawString(g);
        }
        g.translate(-x, -y);
    }
    @Override
    public void move(){
        path.move();
        for (Shape shape: innerShapes){
            shape.move();
        }
    }
    public int indexOf(Shape s){
        return innerShapes.indexOf(s);
    }
    public void addInnerShape(Shape s){
        innerShapes.add(s);
        s.setParent(this);
    }
    public void removeInnerShape(Shape s){
        innerShapes.remove(s);
        s.setParent(null);
    }
    public void removeInnerShapeAt(int index){
        Shape s = innerShapes.get(index);
        innerShapes.remove(index);
        s.setParent(null);
    }
    public ArrayList<Shape> getAllInnerShapes(){
        return innerShapes;
    }
}
