package k.core.util.jar;

/*
 * The contents of this file are subject to the Sapient Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://carbon.sf.net/License.html.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is The Carbon Component Framework.
 *
 * The Initial Developer of the Original Code is Sapient Corporation
 *
 * Copyright (C) 2003 Sapient Corporation. All Rights Reserved.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * An output stream that is used by EnhancedJarFile to write entries to a jar.
 * This implementation uses a ByteArrayOutputStream to buffer the output until
 * the stream is closed. When the stream is closed, the output is written to the
 * jar.
 * 
 * Copyright 2002 Sapient
 * 
 * @since carbon 1.0
 * @author Douglas Voet, April 2002
 * @version $Revision: 1.9 $($Author: dvoet $ / $Date: 2003/05/05 21:21:23 $)
 */
public class JarEntryOutputStream extends ByteArrayOutputStream {

    private EnhancedJarFile jar;
    private String jarEntryName;

    /**
     * Constructor
     * 
     * @param jar
     *            the EnhancedJarFile that this instance will write to
     * @param jarEntryName
     *            the name of the entry to be written
     */
    public JarEntryOutputStream(EnhancedJarFile jar, String jarEntryName) {
        super();

        this.jarEntryName = jarEntryName;
        this.jar = jar;
    }

    /**
     * Closes the stream and writes entry to the jar
     */
    @Override
    public void close() throws IOException {
        writeToJar();
        super.close();
    }

    /**
     * Writes the entry to a the jar file. This is done by creating a temporary
     * jar file, copying the contents of the existing jar to the temp jar,
     * skipping the entry named by this.jarEntryName if it exists. Then, if the
     * stream was written to, then contents are written as a new entry. Last, a
     * callback is made to the EnhancedJarFile to swap the temp jar in for the
     * old jar.
     */
    private void writeToJar() throws IOException {

        File jarDir = new File(this.jar.getName()).getParentFile();
        // create new jar
        File newJarFile = File.createTempFile(
                "temp-jar-for-copy--deleteiffound", ".jar", jarDir);
        newJarFile.deleteOnExit();
        JarOutputStream jarOutputStream = new JarOutputStream(
                new FileOutputStream(newJarFile));

        try {
            Enumeration<JarEntry> entries = this.jar.entries();

            // copy all current entries into the new jar
            while (entries.hasMoreElements()) {
                JarEntry nextEntry = (JarEntry) entries.nextElement();
                // skip the entry named jarEntryName
                if (!this.jarEntryName.equals(nextEntry.getName())) {
                    // the next 3 lines of code are a work around for
                    // bug 4682202 in the java.sun.com bug parade, see:
                    // http://developer.java.sun.com/developer/bugParade/bugs/4682202.html
                    JarEntry entryCopy = new JarEntry(nextEntry);
                    entryCopy.setCompressedSize(-1);
                    jarOutputStream.putNextEntry(entryCopy);

                    InputStream intputStream = this.jar
                            .getInputStream(nextEntry);
                    // write the data
                    for (int data = intputStream.read(); data != -1; data = intputStream
                            .read()) {

                        jarOutputStream.write(data);
                    }
                }
            }

            // write the new or modified entry to the jar
            if (size() > 0) {
                jarOutputStream.putNextEntry(new JarEntry(this.jarEntryName));
                jarOutputStream.write(super.buf, 0, size());
                jarOutputStream.closeEntry();
            }
        } finally {
            // close close everything up
            try {
                if (jarOutputStream != null) {
                    jarOutputStream.close();
                }
            } catch (IOException ioe) {
                // eat it, just wanted to close stream
            }
        }

        // swap the jar
        this.jar.swapJars(newJarFile);
    }

}