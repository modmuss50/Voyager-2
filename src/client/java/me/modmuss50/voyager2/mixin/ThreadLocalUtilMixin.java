package me.modmuss50.voyager2.mixin;

import org.lwjgl.system.ThreadLocalUtil;
import org.lwjgl.system.jni.JNINativeInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(value = ThreadLocalUtil.class, remap = false)
public class ThreadLocalUtilMixin {
	@Unique
	private static final int JNI_VERSION_19 = 0x130000;
	@Unique
	private static final int JNI_VERSION_20 = 0x140000;

	@ModifyVariable(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", shift = At.Shift.BEFORE), ordinal = 2)
	private static int setJniCallCount(int value) {
		final int JNI_VERSION = JNINativeInterface.GetVersion();

		if (JNI_VERSION == JNI_VERSION_19 || JNI_VERSION == JNI_VERSION_20) {
			return 231;
		}

		return value;
	}

	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
	private static void suppressErrorLog(PrintStream instance, String x) {
		final int JNI_VERSION = JNINativeInterface.GetVersion();

		if (JNI_VERSION == JNI_VERSION_19 || JNI_VERSION == JNI_VERSION_20) {
			return;
		}

		// Default path, log error
		instance.println(x);
	}
}

