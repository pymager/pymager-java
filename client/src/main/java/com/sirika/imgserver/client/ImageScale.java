package com.sirika.imgserver.client;


public class ImageScale {
    public static class ImageScaleBuilder {
	private int width;
	
	public ImageScaleBuilder(int width) {
	    super();
	    this.width = width;
	}
	
	public ImageScale by(int height) {
	    return new ImageScale(width, height);
	}
    }
    
    private int width;
    private int height;
    
    private ImageScale(int width, int height) {
	super();
	if(width < 0 || height < 0) {
	    throw new IllegalArgumentException("Width and height must be positive");
	}
	this.width = width;
	this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public static ImageScaleBuilder width(int width) {
	return new ImageScaleBuilder(width);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + height;
	result = prime * result + width;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ImageScale other = (ImageScale) obj;
	if (height != other.height)
	    return false;
	if (width != other.width)
	    return false;
	return true;
    }
    
    
}
