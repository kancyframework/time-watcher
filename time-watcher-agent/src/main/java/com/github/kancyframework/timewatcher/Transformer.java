package com.github.kancyframework.timewatcher;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Transformer
 *
 * @author huangchengkang
 * @date 2021/12/30 14:42
 */
@Slf4j
public class Transformer implements ClassFileTransformer {

    private final Set<String> basePackages = new HashSet<>();

    public Transformer(Collection<String> basePackages) {
        this.basePackages.addAll(basePackages);
    }

    /**
     * The implementation of this method may transform the supplied class file and
     * return a new replacement class file.
     *
     * @param loader              the defining loader of the class to be transformed,
     *                            may be <code>null</code> if the bootstrap loader
     * @param className           the name of the class in the internal form of fully
     *                            qualified class and interface names as defined in
     *                            <i>The Java Virtual Machine Specification</i>.
     *                            For example, <code>"java/util/List"</code>.
     * @param classBeingRedefined if this is triggered by a redefine or retransform,
     *                            the class being redefined or retransformed;
     *                            if this is a class load, <code>null</code>
     * @param protectionDomain    the protection domain of the class being defined or redefined
     * @param classfileBuffer     the input byte buffer in class file format - must not be modified
     * @return a well-formed class file buffer (the result of the transform),
     * or <code>null</code> if no transform is performed.
     * @throws IllegalClassFormatException if the input does not represent a well-formed class file
     * @see Instrumentation#redefineClasses
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        for (String basePackage : basePackages) {
            if (className.startsWith(basePackage.replace(".", "/"))){
                log.info("transform class : {}", className);
                return doTransform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            }
        }
        return null;
    }

    private byte[] doTransform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                               ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        //读取类的字节码流
        ClassReader reader = new ClassReader(classfileBuffer);
        //创建操作字节流值对象，ClassWriter.COMPUTE_MAXS:表示自动计算栈大小
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        //接受一个ClassVisitor子类进行字节码修改
        reader.accept(new TimeStatClassAdapter(writer), ClassReader.EXPAND_FRAMES);
        //返回修改后的字节码流
        return writer.toByteArray();
    }
}
