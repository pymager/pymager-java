/**
 * Copyright 2009 Sami Dalouche
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iglootools.pymager.api;

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
