package com.github.kancyframework.timewatcher;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.Objects;

/**
 * TimeStatClassAdapter
 *
 * @author huangchengkang
 * @date 2021/12/30 17:49
 */
public class TimeStatClassAdapter extends ClassVisitor implements Opcodes {

    private String className;

    public TimeStatClassAdapter(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    /**
     * Visits the header of the class.
     * */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    /**
     * Visits a method of the class. This method <i>must</i> return a new {@link MethodVisitor}
     * instance (or <tt>null</tt>) each time it is called, i.e., it should not return a previously
     * returned visitor.
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        //构造方法、get和set方法、toString、equals不插桩
        if ("<init>".equals(name) || "<clinit>".equals(name)
                || name.startsWith("get")
                || name.startsWith("set")
                || name.equals("equals")
                || name.equals("toString")) {
            return mv;
        }

        if (Objects.equals(this.className, "com/github/kancyframework/timewatcher/TimeStat")){
            return mv;
        }

        //在return TryCatchMethodAdapter之前插入的代码不会包裹在try-catch内
        //可以在此插入一些代码，原本方法的代码此时还没有添加到方法体内，在return methodVisitor之后由asm调配写入
        return new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {
            //方法进入时获取开始时间
            @Override public void onMethodEnter() {
                //this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/kancyframework/timewatcher/TimeStat", "start", "()V", false);
                Label label0 = new Label();
                this.visitLabel(label0);
                this.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                this.visitVarInsn(LSTORE, 1);
            }

            //方法退出时获取结束时间并计算执行时间
            @Override public void onMethodExit(int opcode) {
                //this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/kancyframework/timewatcher/TimeStat", "end", "()V", false);
                Label label1 = new Label();
                this.visitLabel(label1);
                this.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                this.visitVarInsn(LSTORE, 3);

                Label label2 = new Label();
                this.visitLabel(label2);
                this.visitVarInsn(LLOAD, 3);
                this.visitVarInsn(LLOAD, 1);
                this.visitInsn(LSUB);
                this.visitVarInsn(LSTORE, 5);


                Label label3 = new Label();
                this.visitLabel(label3);
                this.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                this.visitVarInsn(LLOAD, 5);

                this.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
            }
        };
    }
}