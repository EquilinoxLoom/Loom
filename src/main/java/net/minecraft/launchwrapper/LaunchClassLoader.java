package net.minecraft.launchwrapper;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class LaunchClassLoader extends URLClassLoader {
   public static final int BUFFER_SIZE = 4096;
   private List<URL> sources;
   private ClassLoader parent = this.getClass().getClassLoader();
   private List<IClassTransformer> transformers = new ArrayList(2);
   private Map<String, Class<?>> cachedClasses = new ConcurrentHashMap();
   private Set<String> invalidClasses = new HashSet(1000);
   private Set<String> classLoaderExceptions = new HashSet();
   private Set<String> transformerExceptions = new HashSet();
   private Map<String, byte[]> resourceCache = new ConcurrentHashMap(1000);
   private Set<String> negativeResourceCache = Collections.newSetFromMap(new ConcurrentHashMap());
   private IClassNameTransformer renameTransformer;
   private final ThreadLocal<byte[]> loadBuffer = new ThreadLocal();
   private static final String[] RESERVED_NAMES = new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
   private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("legacy.debugClassLoading", "false"));
   private static final boolean DEBUG_FINER;
   private static final boolean DEBUG_SAVE;
   private static File tempFolder;

   public LaunchClassLoader(URL[] sources) {
      super(sources, (ClassLoader)null);
      this.sources = new ArrayList(Arrays.asList(sources));
      this.addClassLoaderExclusion("net.minecraft.launchwrapper.");
      this.addTransformerExclusion("javax.");
      this.addTransformerExclusion("argo.");
      this.addTransformerExclusion("org.objectweb.asm.");
      this.addTransformerExclusion("com.google.common.");
      this.addTransformerExclusion("org.bouncycastle.");
      this.addTransformerExclusion("net.minecraft.launchwrapper.injector.");
      if (DEBUG_SAVE) {
         int x = 1;

         for(tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP"); tempFolder.exists() && x <= 10; tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP" + x++)) {
         }

         if (tempFolder.exists()) {
            LogWrapper.info("DEBUG_SAVE enabled, but 10 temp directories already exist, clean them and try again.");
            tempFolder = null;
         } else {
            LogWrapper.info("DEBUG_SAVE Enabled, saving all classes to \"%s\"", tempFolder.getAbsolutePath().replace('\\', '/'));
            tempFolder.mkdirs();
         }
      }

   }

   public void registerTransformer(String transformerClassName) {
      try {
         IClassTransformer transformer = (IClassTransformer)this.loadClass(transformerClassName).newInstance();
         this.transformers.add(transformer);
         if (transformer instanceof IClassNameTransformer && this.renameTransformer == null) {
            this.renameTransformer = (IClassNameTransformer)transformer;
         }
      } catch (Exception var3) {
         LogWrapper.log((Level)Level.ERROR, (Throwable)var3, "A critical problem occurred registering the ASM transformer class %s", transformerClassName);
      }

   }

   public Class<?> findClass(String name) throws ClassNotFoundException {
      if (this.invalidClasses.contains(name)) {
         throw new ClassNotFoundException(name);
      } else {
         Iterator var2 = this.classLoaderExceptions.iterator();

         String untransformedName;
         while(var2.hasNext()) {
            untransformedName = (String)var2.next();
            if (name.startsWith(untransformedName)) {
               return this.parent.loadClass(name);
            }
         }

         if (this.cachedClasses.containsKey(name)) {
            return (Class)this.cachedClasses.get(name);
         } else {
            var2 = this.transformerExceptions.iterator();

            while(var2.hasNext()) {
               untransformedName = (String)var2.next();
               if (name.startsWith(untransformedName)) {
                  try {
                     Class<?> clazz = super.findClass(name);
                     this.cachedClasses.put(name, clazz);
                     return clazz;
                  } catch (ClassNotFoundException var14) {
                     this.invalidClasses.add(name);
                     throw var14;
                  }
               }
            }

            try {
               String transformedName = this.transformName(name);
               if (this.cachedClasses.containsKey(transformedName)) {
                  return (Class)this.cachedClasses.get(transformedName);
               } else {
                  untransformedName = this.untransformName(name);
                  int lastDot = untransformedName.lastIndexOf(46);
                  String packageName = lastDot == -1 ? "" : untransformedName.substring(0, lastDot);
                  String fileName = untransformedName.replace('.', '/').concat(".class");
                  URLConnection urlConnection = this.findCodeSourceConnectionFor(fileName);
                  CodeSigner[] signers = null;
                  if (lastDot > -1 && !untransformedName.startsWith("net.minecraft.")) {
                     if (urlConnection instanceof JarURLConnection) {
                        JarURLConnection jarURLConnection = (JarURLConnection)urlConnection;
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if (jarFile != null && jarFile.getManifest() != null) {
                           Manifest manifest = jarFile.getManifest();
                           JarEntry entry = jarFile.getJarEntry(fileName);
                           Package pkg = this.getPackage(packageName);
                           this.getClassBytes(untransformedName);
                           signers = entry.getCodeSigners();
                           if (pkg == null) {
                              this.definePackage(packageName, manifest, jarURLConnection.getJarFileURL());
                           } else if (pkg.isSealed() && !pkg.isSealed(jarURLConnection.getJarFileURL())) {
                              LogWrapper.severe("The jar file %s is trying to seal already secured path %s", jarFile.getName(), packageName);
                           } else if (this.isSealed(packageName, manifest)) {
                              LogWrapper.severe("The jar file %s has a security seal for path %s, but that path is defined and not secure", jarFile.getName(), packageName);
                           }
                        }
                     } else {
                        Package pkg = this.getPackage(packageName);
                        if (pkg == null) {
                           this.definePackage(packageName, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
                        } else if (pkg.isSealed()) {
                           LogWrapper.severe("The URL %s is defining elements for sealed path %s", urlConnection.getURL(), packageName);
                        }
                     }
                  }

                 byte[] transformedClass = this.runTransformers(untransformedName, transformedName, this.getClassBytes(untransformedName));
                 if (DEBUG_SAVE) {
                    this.saveTransformedClass(transformedClass, transformedName);
                 }

                 CodeSource codeSource = urlConnection == null ? null : new CodeSource(urlConnection.getURL(), signers);

                 Class<?> clazz = this.defineClass(transformedName, transformedClass, 0, transformedClass.length, codeSource);
                  this.cachedClasses.put(transformedName, clazz);
                  return clazz;
               }
            } catch (Throwable var15) {
               this.invalidClasses.add(name);
               if (DEBUG) {
                  LogWrapper.log(Level.TRACE, var15, "Exception encountered attempting classloading of %s", name);
                  LogManager.getLogger("LaunchWrapper").log(Level.ERROR, "Exception encountered attempting classloading of %s", var15);
               }

               throw new ClassNotFoundException(name, var15);
            }
         }
      }
   }

   private void saveTransformedClass(byte[] data, String transformedName) {
      if (tempFolder != null) {
         File outFile = new File(tempFolder, transformedName.replace('.', File.separatorChar) + ".class");
         File outDir = outFile.getParentFile();
         if (!outDir.exists()) {
            outDir.mkdirs();
         }

         if (outFile.exists()) {
            outFile.delete();
         }

         try {
            LogWrapper.fine("Saving transformed class \"%s\" to \"%s\"", transformedName, outFile.getAbsolutePath().replace('\\', '/'));
            OutputStream output = new FileOutputStream(outFile);
            output.write(data);
            output.close();
         } catch (IOException var6) {
            LogWrapper.log((Level)Level.WARN, (Throwable)var6, "Could not save transformed class \"%s\"", transformedName);
         }

      }
   }

   private String untransformName(String name) {
      return this.renameTransformer != null ? this.renameTransformer.unmapClassName(name) : name;
   }

   private String transformName(String name) {
      return this.renameTransformer != null ? this.renameTransformer.remapClassName(name) : name;
   }

   private boolean isSealed(String path, Manifest manifest) {
      Attributes attributes = manifest.getAttributes(path);
      String sealed = null;
      if (attributes != null) {
         sealed = attributes.getValue(Name.SEALED);
      }

      if (sealed == null) {
         attributes = manifest.getMainAttributes();
         if (attributes != null) {
            sealed = attributes.getValue(Name.SEALED);
         }
      }

      return "true".equalsIgnoreCase(sealed);
   }

   private URLConnection findCodeSourceConnectionFor(String name) {
      URL resource = this.findResource(name);
      if (resource != null) {
         try {
            return resource.openConnection();
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }
      } else {
         return null;
      }
   }

   private byte[] runTransformers(String name, String transformedName, byte[] basicClass) {
      Iterator var4;
      IClassTransformer transformer;
      if (DEBUG_FINER) {
         LogWrapper.finest("Beginning transform of {%s (%s)} Start Length: %d", name, transformedName, basicClass == null ? 0 : basicClass.length);
         var4 = this.transformers.iterator();

         while(var4.hasNext()) {
            transformer = (IClassTransformer)var4.next();
            String transName = transformer.getClass().getName();
            LogWrapper.finest("Before Transformer {%s (%s)} %s: %d", name, transformedName, transName, basicClass == null ? 0 : basicClass.length);
            basicClass = transformer.transform(name, transformedName, basicClass);
            LogWrapper.finest("After  Transformer {%s (%s)} %s: %d", name, transformedName, transName, basicClass == null ? 0 : basicClass.length);
         }

         LogWrapper.finest("Ending transform of {%s (%s)} Start Length: %d", name, transformedName, basicClass == null ? 0 : basicClass.length);
      } else {
         for(var4 = this.transformers.iterator(); var4.hasNext(); basicClass = transformer.transform(name, transformedName, basicClass)) {
            transformer = (IClassTransformer)var4.next();
         }
      }

      return basicClass;
   }

   public void addURL(URL url) {
      super.addURL(url);
      this.sources.add(url);
   }

   public List<URL> getSources() {
      return this.sources;
   }

   private byte[] readFully(InputStream stream) {
      try {
         byte[] buffer = this.getOrCreateBuffer();
         int totalLength = 0;

         int read;
         byte[] newBuffer;
         while((read = stream.read(buffer, totalLength, buffer.length - totalLength)) != -1) {
            totalLength += read;
            if (totalLength >= buffer.length - 1) {
               newBuffer = new byte[buffer.length + 4096];
               System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
               buffer = newBuffer;
            }
         }

         newBuffer = new byte[totalLength];
         System.arraycopy(buffer, 0, newBuffer, 0, totalLength);
         return newBuffer;
      } catch (Throwable var6) {
         LogWrapper.log(Level.WARN, var6, "Problem loading class");
         return new byte[0];
      }
   }

   private byte[] getOrCreateBuffer() {
      byte[] buffer = (byte[])this.loadBuffer.get();
      if (buffer == null) {
         this.loadBuffer.set(new byte[4096]);
         buffer = (byte[])this.loadBuffer.get();
      }

      return buffer;
   }

   public List<IClassTransformer> getTransformers() {
      return Collections.unmodifiableList(this.transformers);
   }

   public void addClassLoaderExclusion(String toExclude) {
      this.classLoaderExceptions.add(toExclude);
   }

   public void addTransformerExclusion(String toExclude) {
      this.transformerExceptions.add(toExclude);
   }

   public byte[] getClassBytes(String name) throws IOException {
      if (this.negativeResourceCache.contains(name))
         return null;
      if (this.resourceCache.containsKey(name))
         return this.resourceCache.get(name);
      if (!name.contains("."))
         for (String reservedName : RESERVED_NAMES) {
            if (name.toUpperCase(Locale.ENGLISH).startsWith(reservedName)) {
               byte[] data = getClassBytes("_" + name);
               if (data != null) {
                  this.resourceCache.put(name, data);
                  return data;
               }
            }
         }
      InputStream classStream = null;
      try {
         String resourcePath = name.replace('.', '/').concat(".class");
         URL classResource = findResource(resourcePath);
         if (classResource == null) {
            if (DEBUG)
               LogWrapper.finest("Failed to find class resource %s", new Object[] { resourcePath });
            this.negativeResourceCache.add(name);
            return null;
         }
         classStream = classResource.openStream();
         if (DEBUG)
            LogWrapper.finest("Loading class %s from resource %s", name, classResource.toString());
         byte[] data = readFully(classStream);
         this.resourceCache.put(name, data);
         return data;
      } finally {
         closeSilently(classStream);
      }
   }

   private static void closeSilently(Closeable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
         }
      }

   }

   public void clearNegativeEntries(Set<String> entriesToClear) {
      this.negativeResourceCache.removeAll(entriesToClear);
   }

   static {
      DEBUG_FINER = DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingFiner", "false"));
      DEBUG_SAVE = DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingSave", "false"));
      tempFolder = null;
   }
}
