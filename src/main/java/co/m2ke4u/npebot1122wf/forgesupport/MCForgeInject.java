package co.m2ke4u.npebot1122wf.forgesupport;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MCForgeInject {
    private static final Logger logger = LogManager.getLogger();

    public static boolean inject() {
        try {
            if (MCForge.isVersion1710()) {
                injectPluginMessage();
                injectTryCatch("org.spacehq.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket", "read",
                        "{$1.readBytes($1.available());return;}");
                injectTryCatch("org.spacehq.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket",
                        "read", "{$1.readBytes($1.available());return;}");
                injectTryCatch("org.spacehq.packetlib.packet.PacketProtocol", "createIncomingPacket",
                        "{return luohuayu.MCForgeProtocol.MCForgeUtils.createUnknowPacket();}");
            } else {
                ClassPool classPool = ClassPool.getDefault();
                CtClass ctClass = classPool.getOrNull("org.spacehq.mc.protocol.data.MagicValues");
                if (ctClass == null)
                    ctClass = classPool.get("org.spacehq.mc.protocol.data.game.MagicValues");
                CtMethod method1 = ctClass.getDeclaredMethod("key");
                method1.addCatch("{return null;}", classPool.get("java.lang.Exception"));
                CtMethod method2 = ctClass.getDeclaredMethod("value");
                method2.addCatch("{return null;}", classPool.get("java.lang.Exception"));
                ctClass.toClass();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void injectPluginMessage() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket");
        CtMethod method = ctClass.getDeclaredMethod("read");
        method.setBody("{this.channel=$1.readString();\n"
                + "this.data=$1.readBytes(luohuayu.MCForgeProtocol.MCForgeUtils.readVarShort($1));}");
        ctClass.toClass();
    }

    public static void injectTryCatch(String cls, String func, String code) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(cls);
        CtMethod method = ctClass.getDeclaredMethod(func);
        method.addCatch(code, classPool.get("java.lang.Exception"));
        ctClass.toClass();
    }
}
