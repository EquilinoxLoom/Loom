package loom.fabric;

import equilinoxmodkit.util.EmkLogger;
import net.fabricmc.loader.impl.game.patch.GamePatch;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.function.Consumer;
import java.util.function.Function;

public class LoomPatch extends GamePatch {
    private static final int VAR_INDEX = 3;

    @Override
    public void process(FabricLauncher launcher, Function<String, ClassReader> classSource, Consumer<ClassNode> classEmitter) {
        String entrypoint = launcher.getEntrypoint();
        EmkLogger.log("Entrypoint is " + entrypoint);
        ClassNode entrypointClazz = readClass(classSource.apply(entrypoint));
        if (entrypointClazz == null) {
            throw new LinkageError("Could not load entrypoint class " + entrypoint + "!");
        }
        EmkLogger.log("Entrypoint class is " + entrypointClazz);

        if (entrypoint.equals(EquilinoxGameProvider.ENTRY_POINT)) {
            MethodNode method = findMethod(entrypointClazz, init -> init.name.equals("init") && init.desc.equals("()V"));
            if (method == null) {
                throw new NoSuchMethodError("Could not find init method in " + entrypoint + ".");
            }

            AbstractInsnNode ret = null;

            int returnOpcode = Type.getReturnType(method.desc).getOpcode(Opcodes.IRETURN);
            InsnList list = method.instructions;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof InsnNode && list.get(i).getOpcode() == returnOpcode) ret = list.get(i);
            }

            if (ret == null) {
                throw new RuntimeException("TAIL could not locate a valid RETURN in the target method!");
            }

            method.instructions.insertBefore(ret, new MethodInsnNode(Opcodes.INVOKESTATIC, EquilinoxHooks.class.getName().replace('.', '/'), "init", "()V", false));
        } else throw new IllegalArgumentException("Unknown entrypoint " + entrypoint + ".");

        classEmitter.accept(entrypointClazz);
    }
}
