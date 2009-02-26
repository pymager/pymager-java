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

	public String mimeType() {
	    return "image/x-ms-bmp";
	}
    },
    EPS{
	public String extension() {
	    return "eps";
	}
	public String mimeType() {
	    return "application/postscript";
	}
    },
    GIF{
	public String extension() {
	    return "gif";
	}
	public String mimeType() {
	    return "image/gif";
	}
    },
    IM{
	public String extension() {
	    return "im";
	}
	public String mimeType() {
	    return "binary/octet-stream";
	}
    },
    JPEG{
	public String extension() {
	    return "jpg";
	}
	public String mimeType() {
	    return "image/jpeg";
	}
    },
    MSP{
	public String extension() {
	    return "msp";
	}
	public String mimeType() {
	    return "binary/octet-stream";
	}
    },
    PALM{
	public String extension() {
	    return "palm";
	}
	public String mimeType() {
	    return "binary/octet-stream";
	}
    },
    PCX{
	public String extension() {
	    return "pcx";
	}
	public String mimeType() {
	    return "image/pcx";
	}
    },
    PDF{
	public String extension() {
	    return "pdf";
	}
	public String mimeType() {
	    return "application/pdf";
	}
    },
    PNG{
	public String extension() {
	    return "png";
	}
	public String mimeType() {
	    return "image/png";
	}
    },
    PPM{
	public String extension() {
	    return "ppm";
	}
	public String mimeType() {
	    return "image/x-portable-pixmap";
	}
    },
    SPIDER{
	public String extension() {
	    return "spider";
	}
	public String mimeType() {
	    return "binary/octet-stream";
	}
    },
    TIFF{
	public String extension() {
	    return "tiff";
	}
	public String mimeType() {
	    return "image/tiff";
	}
    },
    XBM{
	public String extension() {
	    return "xbm";
	}
	public String mimeType() {
	    return "image/x-xbitmap";
	}
    },
    XV{
	public String extension() {
	    return "xv";
	}
	public String mimeType() {
	    return "binary/octet-stream";
	}
    };
    
    public abstract String extension();
    public abstract String mimeType();
}
