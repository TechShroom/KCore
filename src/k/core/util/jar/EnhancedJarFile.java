package k.core.util.jar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;


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



/**
 * This class enhances functionality of java.util.jar.JarFile.
 * Additional functionality includes jar entry removal, the ability to list
 * the entries within a directory within the jar, and the ability to get
 * an output stream for modifying extisting entries.
 *
 * @see java.util.jar.JarFile
 *
 * Copyright 2002 Sapient
 * @since carbon 1.0
 * @author Doug Voet, April 2002
 * @version $Revision: 1.11 $ ($Author: dvoet $)
 */
public class EnhancedJarFile {
    public static final String JAR_DELIMETER = "/";

 
    private JarFile jar;

    /**
     * @see java.util.jar.JarFile#JarFile(java.lang.String)
     */
    public EnhancedJarFile(String name) throws IOException {

        this.jar = new JarFile(name);
    }

    /**
     * @see java.util.jar.JarFile#JarFile(java.lang.String, boolean)
     */
    public EnhancedJarFile(String name, boolean verify) throws IOException {

        this.jar = new JarFile(name, verify);
    }

    /**
     * @see java.util.jar.JarFile#JarFile(java.io.File)
     */
    public EnhancedJarFile(File file) throws IOException {

        this.jar = new JarFile(file);
    }

    /**
     * @see java.util.jar.JarFile#JarFile(java.io.File, boolean)
     */
    public EnhancedJarFile(File file, boolean verify) throws IOException {

        this.jar = new JarFile(file, verify);
    }

    /**
     * @see java.util.jar.JarFile#JarFile(java.io.File, boolean, int)
     */
    public EnhancedJarFile(File file, boolean verify, int mode)
        throws IOException {

        this.jar = new JarFile(file, verify, mode);
    }

    /**
     * Returns a list of entries that are
     * immediately below the entry named by entryName in the jar's directory
     * structure.
     *
     * @param entryName the name of the directory entry name
     * @return List a list of java.util.jar.JarEntry objects that are
     * immediately below the entry named by entryName in the jar's directory
     * structure.
     */
    public List listSubEntries(String entryName) {
        Enumeration entries = jar.entries();
        List subEntries = new ArrayList();

        while(entries.hasMoreElements()) {
            JarEntry nextEntry = (JarEntry) entries.nextElement();

            if (nextEntry.getName().startsWith(entryName)) {
                // the next entry name starts with the entryName so it
                // is a potential sub entry

                // tokenize the rest of the next entry name to see how
                // many tokens exist
                StringTokenizer tokenizer = new StringTokenizer(
                    nextEntry.getName().substring(entryName.length()),
                    EnhancedJarFile.JAR_DELIMETER);

                if (tokenizer.countTokens() == 1) {
                    // only 1 token exists, so it is a sub-entry
                    subEntries.add(nextEntry);
                }
            }
        }

        return subEntries;
    }

    /**
     * Creates a new output entry stream within the jar.  The entry named
     * will be created if it does not exist within the jar already.
     *
     * @param entryName name of the entry for which to create an output
     * stream.
     * @return JarEntryOutputStream
     */
    public JarEntryOutputStream getEntryOutputStream(String entryName) {
        return new JarEntryOutputStream(this, entryName);
    }

    /**
     * Removes the given entry from the jar.  If the entry does not exist,
     * the method returns without doing anything.
     *
     * @param entry entry to be removed
     * @throws IOException if there is a problem writing the changes
     * to the jar
     */
    public void removeEntry(JarEntry entry) throws IOException {
        // opens an output stream and closes it without writing anything to it
        if (entry != null && getEntry(entry.getName()) != null) {
            JarEntryOutputStream outputStream =
                new JarEntryOutputStream(this, entry.getName());

            outputStream.close();
        }
    }

    /**
     * @see java.util.jar.JarFile#entries()
     */
    public Enumeration entries() {
        return this.jar.entries();
    }

    /**
     * @see java.util.jar.JarFile#getEntry(java.lang.String)
     */
    public ZipEntry getEntry(String arg0) {
        return this.jar.getEntry(arg0);
    }

    /**
     * @see java.util.jar.JarFile#getInputStream(java.util.zip.ZipEntry)
     */
    public InputStream getInputStream(ZipEntry arg0) throws IOException {
        return this.jar.getInputStream(arg0);
    }

    /**
     * @see java.util.jar.JarFile#getJarEntry(java.lang.String)
     */
    public JarEntry getJarEntry(String arg0) {
        return this.jar.getJarEntry(arg0);
    }

    /**
     * @see java.util.jar.JarFile#getManifest()
     */
    public Manifest getManifest() throws IOException {
        return this.jar.getManifest();
    }

    /**
     * @see java.util.zip.ZipFile#close()
     */
    public void close() throws IOException {

        this.jar.close();
    }

    /**
     * @see java.util.zip.ZipFile#getName()
     */
    public String getName() {
        return this.jar.getName();
    }

    /**
     * @see java.util.zip.ZipFile#size()
     */
    public int size() {
        return this.jar.size();
    }

    /**
     * Utility method used to swap the underlying jar file out for the new one.
     * This method closes the old jar file, deletes it, moves the new jar
     * file to the location where the old one used to be and opens it.
     * 
     * This is used when modifying the jar (removal, addition, or changes
     * of entries)
     *
     * @param newJarFile the file object pointing to the new jar file
     */
    void swapJars(File newJarFile) throws IOException {
        File oldJarFile = new File(getName());
        this.jar.close();
        oldJarFile.delete();
        if (newJarFile.renameTo(oldJarFile)) {
            this.jar = new JarFile(oldJarFile);
        } else {
            throw new IOException();
        }
    }
}