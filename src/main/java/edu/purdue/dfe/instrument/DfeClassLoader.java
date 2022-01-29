package edu.purdue.dfe.instrument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.commons.io.IOUtils;

public class DfeClassLoader extends URLClassLoader {
    private ClassFileTransformer transformer = new DfeTransformer();

    public DfeClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public DfeClassLoader(String[] paths, ClassLoader parent) throws MalformedURLException {
        this(stringsToUrls(paths), parent);
    }

    public static URL[] stringsToUrls(String[] paths) throws MalformedURLException {
        URL[] urls = new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            urls[i] = new File(paths[i]).toURI().toURL();
        }
        return urls;
    }


    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] originalBytecode;

        // Try to read the class file in as a resource
        String internalName = name.replace('.', '/');
        String path = internalName.concat(".class");
        try (InputStream in = super.getResourceAsStream(path)) {
            if (in == null) {
                throw new ClassNotFoundException("Cannot find class " + name);
            }
            originalBytecode = IOUtils.toByteArray(in);
            // originalBytecode = in.readAllBytes();
        } catch (IOException e) {
            throw new ClassNotFoundException("I/O exception while loading class.", e);
        }

        assert (originalBytecode != null);

        byte[] bytesToLoad;
        try {
            byte[] instrumented = transformer.transform(this, internalName, null, null, originalBytecode);
            if (instrumented != null) {
                bytesToLoad = instrumented;
            } else {
                bytesToLoad = originalBytecode;
            }
        } catch (IllegalClassFormatException e) {
            bytesToLoad = originalBytecode;
        }
        return defineClass(name, bytesToLoad, 0, bytesToLoad.length);
    }

    public Class<?> testDefineClass(byte[] classBytes){
        return defineClass("edu.purdue.dfe.TestSample", classBytes, 0, classBytes.length);
    }
}
