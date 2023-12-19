package net.minecraft.launchwrapper;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Launch {
   private static final String DEFAULT_TWEAK = "net.minecraft.launchwrapper.VanillaTweaker";
   public static File minecraftHome;
   public static File assetsDir;
   public static Map<String, Object> blackboard;
   public static LaunchClassLoader classLoader;

   public static void main(String[] args) {
      (new Launch()).launch(args);
   }

   private Launch() {
      URLClassLoader ucl = (URLClassLoader)this.getClass().getClassLoader();
      classLoader = new LaunchClassLoader(ucl.getURLs());
      blackboard = new HashMap<>();
      Thread.currentThread().setContextClassLoader(classLoader);
   }

   private void launch(String[] args) {
      OptionParser parser = new OptionParser();
      parser.allowsUnrecognizedOptions();
      OptionSpec<String> profileOption = parser.accepts("version", "The version we launched with").withRequiredArg();
      OptionSpec<File> gameDirOption = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().ofType(File.class);
      OptionSpec<File> assetsDirOption = parser.accepts("assetsDir", "Assets directory").withRequiredArg().ofType(File.class);
      OptionSpec<String> tweakClassOption = parser.accepts("tweakClass", "Tweak class(es) to load").withRequiredArg().defaultsTo("net.minecraft.launchwrapper.VanillaTweaker");
      OptionSpec<String> nonOption = parser.nonOptions();
      OptionSet options = parser.parse(args);
      minecraftHome = options.valueOf(gameDirOption);
      assetsDir = options.valueOf(assetsDirOption);
      String profileName = options.valueOf(profileOption);
      List<String> tweakClassNames = new ArrayList<>(options.valuesOf(tweakClassOption));
      List<String> argumentList = new ArrayList<>();
      blackboard.put("TweakClasses", tweakClassNames);
      blackboard.put("ArgumentList", argumentList);
      Set<String> allTweakerNames = new HashSet<>();
      List<ITweaker> allTweakers = new ArrayList<>();

      try {
         List<ITweaker> tweakers = new ArrayList<>(tweakClassNames.size() + 1);
         blackboard.put("Tweaks", tweakers);
         AtomicReference<ITweaker> primaryTweaker = new AtomicReference<>(null);

         ITweaker tweaker;
         do {
            Iterator<String> it0 = tweakClassNames.iterator();

            while(it0.hasNext()) {
               String tweakName = it0.next();
               if (allTweakerNames.contains(tweakName)) {
                  LogWrapper.log(Level.WARN, "Tweak class name %s has already been visited -- skipping", tweakName);
                  it0.remove();
               } else {
                  allTweakerNames.add(tweakName);
                  LogWrapper.log(Level.INFO, "Loading tweak class name %s", tweakName);
                  classLoader.addClassLoaderExclusion(tweakName.substring(0, tweakName.lastIndexOf(46)));
                  ITweaker iTweaker = (ITweaker)Class.forName(tweakName, true, classLoader).newInstance();
                  tweakers.add(iTweaker);
                  it0.remove();
                  if (primaryTweaker.get() == null) {
                     LogWrapper.log(Level.INFO, "Using primary tweak class name %s", tweakName);
                     primaryTweaker.set(iTweaker);
                  }
               }
            }

            Iterator<ITweaker> it1 = tweakers.iterator();

            while(it1.hasNext()) {
               tweaker = it1.next();
               LogWrapper.log(Level.INFO, "Calling tweak class %s", tweaker.getClass().getName());
               tweaker.acceptOptions(options.valuesOf(nonOption), minecraftHome, assetsDir, profileName);
               tweaker.injectIntoClassLoader(classLoader);
               allTweakers.add(tweaker);
               it1.remove();
            }
         } while(!tweakClassNames.isEmpty());

          for (ITweaker allTweaker : allTweakers) {
              tweaker = allTweaker;
              argumentList.addAll(Arrays.asList(tweaker.getLaunchArguments()));
          }

         String launchTarget = primaryTweaker.get().getLaunchTarget();
         Class<?> clazz = Class.forName(launchTarget, false, classLoader);
         Method mainMethod = clazz.getMethod("main", String[].class);
         LogWrapper.info("Launching wrapped minecraft {%s}", launchTarget);
         mainMethod.invoke(null, (Object) argumentList.toArray(new String[0]));
      } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException |
               InvocationTargetException e) {
          throw new RuntimeException(e);
      }
   }
}
