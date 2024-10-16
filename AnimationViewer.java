/*
 * ==========================================================================================
 * AnimationViewer.java : Moves shapes around on the screen according to different paths.
 * It is the main drawing area where shapes are added and manipulated.
 * YOUR UPI: kcha612
 *  Name: Kavi Chand
 * ==========================================================================================
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.xml.ws.Service.Mode;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListDataListener;
import java.lang.reflect.Field;

class AnimationViewer extends JComponent implements Runnable {
	protected NestedShape root;
	private Thread animationThread = null; // the thread for animation
	private static int DELAY = 120; // the current animation speed
	private ShapeType currentShapeType = Shape.DEFAULT_SHAPETYPE; // the current shape type,
	private PathType currentPathType = Shape.DEFAULT_PATHTYPE; // the current path type
	private Color currentColor = Shape.DEFAULT_COLOR; // the current fill colour of a shape
	private Color currentBorderColor = Shape.DEFAULT_BORDER_COLOR;
	private int currentPanelWidth = Shape.DEFAULT_PANEL_WIDTH, currentPanelHeight = Shape.DEFAULT_PANEL_HEIGHT,currentWidth = Shape.DEFAULT_WIDTH, currentHeight = Shape.DEFAULT_HEIGHT;
	private String currentLabel = Shape.DEFAULT_LABEL; 
	MyModel model;

	public AnimationViewer() {
		start();
		addMouseListener(new MyMouseAdapter());
		root = new NestedShape(Shape.DEFAULT_PANEL_WIDTH, Shape.DEFAULT_PANEL_HEIGHT);
		model = new MyModel();
	}
	public void setCurrentLabel(String text) {
		currentLabel = text;
		for (Shape currentShape : root.getAllInnerShapes())
			if (currentShape.isSelected())
				currentShape.setLabel(currentLabel);
	}
	public void setCurrentColor(Color bc) {
	    currentColor = bc;
	    for (Shape currentShape: root.getAllInnerShapes())
	      if ( currentShape.isSelected())
	        currentShape.setColor(currentColor);
	  }
	public void setCurrentBorderColor(Color bc) {
	    currentBorderColor = bc;
	    for (Shape currentShape: root.getAllInnerShapes())
	      if ( currentShape.isSelected())
	        currentShape.setBorderColor(currentBorderColor);
	}
	public void setCurrentHeight(int h) {
	    currentHeight = h;
	    for (Shape currentShape: root.getAllInnerShapes())
	      if ( currentShape.isSelected())
	        currentShape.setHeight(currentHeight);
	}
	public void setCurrentWidth(int w) {
	    currentWidth = w;
	    for (Shape currentShape: root.getAllInnerShapes())
	      if ( currentShape.isSelected())
	        currentShape.setWidth(currentWidth);
	}
	class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			boolean found = false;
			for (Shape currentShape : root.getAllInnerShapes()){
				if (currentShape.contains(e.getPoint())) { // if the mousepoint is within a shape, then set the shape to
					currentShape.setSelected(!currentShape.isSelected());
					found = true;
				} 
			}
			if (found == false){
				model.insertNodeInto(root.createInnerShape(e.getX(), e.getY(), currentWidth, currentHeight, currentColor, currentBorderColor, currentPathType, currentShapeType), root);
			}
		}	
	}
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Shape currentShape : root.getAllInnerShapes()) {
			currentShape.move();
			currentShape.draw(g);
			currentShape.drawHandles(g);
			currentShape.drawString(g);
		}
	}
	public void resetMarginSize() {
		currentPanelWidth = getWidth();
		currentPanelHeight = getHeight();
		for (Shape currentShape : root.getAllInnerShapes())
			currentShape.resetPanelSize(currentPanelWidth, currentPanelHeight);
	}

	class MyModel extends AbstractListModel<Shape> implements TreeModel{
		private ArrayList<Shape> selectedShapes = new ArrayList<Shape> ();
		public MyModel(){
			selectedShapes = root.getAllInnerShapes();
		}
		public int getSize(){
			return selectedShapes.size();
		}
		public Shape getElementAt(int index){
			return selectedShapes.get(index);
		}
		public void reload(NestedShape selected){
			selectedShapes.clear();
			for(Shape shapes: selected.getAllInnerShapes()){
				selectedShapes.add(shapes);
			}
			fireContentsChanged(this, 0, selected.getSize());
		}
		private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
		public NestedShape getRoot(){
			return root;
		}
		public boolean isLeaf(Object node){
			if (node instanceof NestedShape){
				return false;
			} 
			return true;
		}
		public boolean isRoot(Shape selectedNode){
			if (selectedNode == root){
				return true;
			}
			return false;
		}
		public Shape getChild(Object parent, int index){
			try{
				if(parent instanceof NestedShape){
					Shape child = ((NestedShape)parent).getInnerShapeAt(index);
					return child;
				}
			} catch (Exception e){
				return null;
			}
			return null;
		}
		public int getChildCount(Object parent){
			if(parent instanceof NestedShape){
				return ((NestedShape)parent).getSize();
			}
			return 0;
		}
		public int getIndexOfChild(Object parent, Object child){
			if(parent instanceof NestedShape){
				return ((NestedShape)parent).indexOf((Shape)child);
			}
			return -1;
		}
		public void addTreeModelListener(final TreeModelListener tml){
			treeModelListeners.add(tml);
		}
		public void removeTreeModelListener(final TreeModelListener tml){
			treeModelListeners.remove(tml);
		}

		public void valueForPathChanged(TreePath path, Object newValue){}

		public void fireTreeNodesInserted(Object source, Object[] path,int[] childIndices,Object[] children){
			TreeModelEvent object = new TreeModelEvent(source, path);
			for (TreeModelListener nestedShape: treeModelListeners){
				nestedShape.treeNodesInserted(object);
			}
			System.out.printf("Called fireTreeNodesInserted: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));
		}
		public void insertNodeInto(Shape newChild, NestedShape parent){
			Object[] path = parent.getPath();
			int[] childIndices = {parent.indexOf(newChild)};
			Object[] children = {newChild};
			fireTreeNodesInserted(this, path, childIndices, children);
			fireIntervalAdded(children, 0, parent.getSize());
		}
		public void addShapeNode(NestedShape selectedNode){
			Shape s;
			if (selectedNode ==  root){
				s = selectedNode.createInnerShape(0, 0, currentWidth, currentHeight, currentColor, currentBorderColor, currentPathType, currentShapeType);
			} else{
				s = selectedNode.createInnerShape(0, 0, selectedNode.width/2, selectedNode.height/2, selectedNode.color, selectedNode.borderColor, currentPathType, currentShapeType);
			}
			insertNodeInto(s, selectedNode);
		}
		public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,Object[] children) {
			TreeModelEvent object = new TreeModelEvent(source, path);
			for (TreeModelListener nestedShape: treeModelListeners){
				nestedShape.treeNodesRemoved(object);
			}
			System.out.printf("Called fireTr4eeNodesRemoved: path=%s, childIndices=%s, children=%s\n", Arrays.toString(path), Arrays.toString(childIndices), Arrays.toString(children));
		}
		public void removeNodeFromParent(Shape selectedNode){
			NestedShape parent = selectedNode.getParent();
			int[] childIndices = {parent.indexOf(selectedNode)};
			parent.removeInnerShape(selectedNode);
			Object[] path = parent.getPath();
			Object[] children = {selectedNode};
			fireTreeNodesRemoved(this, path, childIndices, children);
			fireIntervalRemoved(children, 0, parent.getSize());
		}
	}

	// you don't need to make any changes after this line ______________
	public String getCurrentLabel() {return currentLabel;}
	public int getCurrentHeight() { return currentHeight; }
	public int getCurrentWidth() { return currentWidth; }
	public Color getCurrentColor() { return currentColor; }
	public Color getCurrentBorderColor() { return currentBorderColor; }
	public void setCurrentShapeType(ShapeType value) {currentShapeType = value;}
	public void setCurrentPathType(PathType value) {currentPathType = value;}
	public ShapeType getCurrentShapeType() {return currentShapeType;}
	public PathType getCurrentPathType() {return currentPathType;}
	public void update(Graphics g) {
		paint(g);
	}
	public void start() {
		animationThread = new Thread(this);
		animationThread.start();
	}
	public void stop() {
		if (animationThread != null) {
			animationThread = null;
		}
	}
	public void run() {
		Thread myThread = Thread.currentThread();
		while (animationThread == myThread) {
			repaint();
			pause(DELAY);
		}
	}
	private void pause(int milliseconds) {
		try {
			Thread.sleep((long) milliseconds);
		} catch (InterruptedException ie) {}
	}
}
