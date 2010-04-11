/**
 * PyMager Java REST Client
 * Copyright (C) 2008 Sami Dalouche
 *
 * This file is part of PyMager Java REST Client.
 *
 * PyMager Java REST Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PyMager Java REST Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PyMager Java REST Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sirika.pymager.client;

/**
 * Lists all formats supported by Image Server. This should match the list of
 * formats that <a href="http://www.pythonware.com/products/pil/">PIL</a>
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
    EPS {
        public String extension() {
            return "eps";
        }

        public String mimeType() {
            return "application/postscript";
        }
    },
    GIF {
        public String extension() {
            return "gif";
        }

        public String mimeType() {
            return "image/gif";
        }
    },
    IM {
        public String extension() {
            return "im";
        }

        public String mimeType() {
            return "binary/octet-stream";
        }
    },
    JPEG {
        public String extension() {
            return "jpg";
        }

        public String mimeType() {
            return "image/jpeg";
        }
    },
    MSP {
        public String extension() {
            return "msp";
        }

        public String mimeType() {
            return "binary/octet-stream";
        }
    },
    PALM {
        public String extension() {
            return "palm";
        }

        public String mimeType() {
            return "binary/octet-stream";
        }
    },
    PCX {
        public String extension() {
            return "pcx";
        }

        public String mimeType() {
            return "image/pcx";
        }
    },
    PDF {
        public String extension() {
            return "pdf";
        }

        public String mimeType() {
            return "application/pdf";
        }
    },
    PNG {
        public String extension() {
            return "png";
        }

        public String mimeType() {
            return "image/png";
        }
    },
    PPM {
        public String extension() {
            return "ppm";
        }

        public String mimeType() {
            return "image/x-portable-pixmap";
        }
    },
    TIFF {
        public String extension() {
            return "tiff";
        }

        public String mimeType() {
            return "image/tiff";
        }
    },
    XBM {
        public String extension() {
            return "xbm";
        }

        public String mimeType() {
            return "image/x-xbitmap";
        }
    },
    XV {
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
