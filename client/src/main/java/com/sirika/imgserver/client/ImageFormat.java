package com.sirika.imgserver.client;

/**
 * Lists all formats supported by Image Server. 
 * This should match the list of formats that 
 * <a href="http://www.pythonware.com/products/pil/">PIL</a> 
 * supports writing to.
 * 
 * @author Sami Dalouche (sami.dalouche@gmail.com)
 *
 */
public enum ImageFormat {
    BMP {
	public String extension() {
	    return "bmp";
	}
    },
    EPS{
	public String extension() {
	    return "eps";
	}
    },
    GIF{
	public String extension() {
	    return "gif";
	}
    },
    IM{
	public String extension() {
	    return "im";
	}
    },
    JPEG{
	public String extension() {
	    return "jpg";
	}
    },
    MSP{
	public String extension() {
	    return "msp";
	}
    },
    PALM{
	public String extension() {
	    return "palm";
	}
    },
    PCX{
	public String extension() {
	    return "pcx";
	}
    },
    PDF{
	public String extension() {
	    return "pdf";
	}
    },
    PNG{
	public String extension() {
	    return "png";
	}
    },
    PPM{
	public String extension() {
	    return "ppm";
	}
    },
    SPIDER{
	public String extension() {
	    return "spider";
	}
    },
    TIFF{
	public String extension() {
	    return "tiff";
	}
    },
    XBM{
	public String extension() {
	    return "xbm";
	}
    },
    XV{
	public String extension() {
	    return "xv";
	}
    };
    
    public abstract String extension();
}
