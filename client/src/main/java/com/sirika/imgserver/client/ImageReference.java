package com.sirika.imgserver.client;

import org.apache.commons.lang.Validate;

public class ImageReference {

    private String id;
    private ImageScale rescaling;
    private ImageFormat imageFormat;
    
    private ImageReference(String id, ImageScale rescaling, ImageFormat imageFormat) {
	Validate.notNull(id);
	this.id = id;
	this.rescaling = rescaling;
	this.imageFormat = imageFormat;
    }
    
    public static ImageReference originalImage(String id) {
	return new ImageReference(id, null, null);
    }
    
    public ImageReference rescaledTo(ImageScale imageScale) {
	return new ImageReference(this.id, imageScale, ImageFormat.JPEG);
    }
    
    public boolean isConverted() {
	return this.imageFormat != null;
    }
    
    public ImageReference convertedTo(ImageFormat imageFormat) {
	Validate.notNull(imageFormat);
	return new ImageReference(this.id, this.rescaling, imageFormat);
    }

    public String getId() {
        return id;
    }

    public ImageScale getRescaling() {
        return rescaling;
    }
    
    public boolean isDerived() {
	return rescaling != null;
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	result = prime * result
		+ ((imageFormat == null) ? 0 : imageFormat.hashCode());
	result = prime * result
		+ ((rescaling == null) ? 0 : rescaling.hashCode());
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
	ImageReference other = (ImageReference) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	if (imageFormat == null) {
	    if (other.imageFormat != null)
		return false;
	} else if (!imageFormat.equals(other.imageFormat))
	    return false;
	if (rescaling == null) {
	    if (other.rescaling != null)
		return false;
	} else if (!rescaling.equals(other.rescaling))
	    return false;
	return true;
    }
}
