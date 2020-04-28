/*
 * Copyright (c) 2008-2014
 * LANIT
 * All rights reserved.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * LANIT and its licensors, if any.
 *
 * $Id$
 */
package ru.lanit.bpm.jedu.hrjedi.rest;

import java.io.IOException;
import java.io.OutputStream;

public class DownloadableFile implements StreamedResult {
    private String utf8Name;
    private String asciiName;
    private StreamedResult content;
    private String contentType;

    public DownloadableFile(String utf8Name, String asciiName, StreamedResult content, String contentType) {
        this.utf8Name = utf8Name;
        this.asciiName = asciiName;
        this.content = content;
        this.contentType = contentType;
    }

    public DownloadableFile(String utf8Name, String asciiName, StreamedResult content) {
        this(utf8Name, asciiName, content, "application/octet-stream");
    }

    public String getUtf8Name() {
        return utf8Name;
    }

    public String getAsciiName() {
        return asciiName;
    }

    public StreamedResult getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        content.write(outputStream);
    }
}
