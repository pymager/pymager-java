package com.sirika.imgserver.client;

import org.apache.commons.lang.Validate;

/**
 * An Image ID
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public class ImageId {
    private String id;
    
    private ImageId(String id) {
	Validate.notNull(id);
	this.id = id;
    }
    
    public static ImageId imageId(String id) {
	return new ImageId(id);
    }

    public String toString() {
	return id;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((id == null) ? 0 : id.hashCode());
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
	ImageId other = (ImageId) obj;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }
    
}
