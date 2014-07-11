package de.eleon.jswap.spring;

import de.eleon.jswap.Log;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class SpringContextClassfileTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className.contains("AbstractApplicationContext")) {
            Log.LOG("SpringContextClassfileTransformer: transform ", className);

            ClassPool pool = ClassPool.getDefault();
            CtClass currentClass = null;
            CtClass statement = null;
            try {
                currentClass = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
                statement = pool.get("org.springframework.context.support.AbstractApplicationContext");
                if (currentClass.subtypeOf(statement) && !currentClass.isInterface()) {
                    addConstructorRegistration(currentClass);
                }
                classfileBuffer = currentClass.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (currentClass != null) {
                    currentClass.detach();
                }
            }

        }
        return classfileBuffer;
    }

    private void addConstructorRegistration(CtClass currentClass) throws CannotCompileException {
        for (CtConstructor ctConstructor :currentClass.getConstructors()) {
            Log.LOG("SpringContextClassfileTransformer: changed constructor");
            ctConstructor.insertBeforeBody("de.eleon.jswap.spring.SpringContextAware.register(this);");
        }
    }
}
