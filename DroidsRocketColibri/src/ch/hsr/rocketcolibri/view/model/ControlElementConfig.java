package ch.hsr.rocketcolibri.view.model;

public class ControlElementConfig {
	private Class<?> elementClass;
	private int width;
	private int height;
	private int x;
	private int y;
	
	public ControlElementConfig(Class<?> pElementClass, int pWidth, int pHeight, int pX, int pY){
		elementClass = pElementClass;
		width = pWidth;
		height = pHeight;
		x = pX;
		y = pY;
	}

	public Class<?> getElementClass() {
		return elementClass;
	}

	public void setElementClass(Class<?> elementClass) {
		this.elementClass = elementClass;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
