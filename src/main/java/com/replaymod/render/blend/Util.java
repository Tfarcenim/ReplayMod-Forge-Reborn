package com.replaymod.render.blend;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.blender.dna.Link;
import org.blender.dna.ListBase;
import org.blender.utils.BlenderFactory;
import org.cakelab.blender.nio.CPointer;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlUtil;

import de.johni0702.minecraft.gui.utils.lwjgl.vector.Matrix3f;
import de.johni0702.minecraft.gui.utils.lwjgl.vector.Matrix4f;
import de.johni0702.minecraft.gui.utils.lwjgl.vector.Quaternion;
import de.johni0702.minecraft.gui.utils.lwjgl.vector.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class Util {
    public static BlenderFactory factory() {
        return BlendState.getState().getFactory();
    }

    public static int align4(int size) {
        return align(size, 4);
    }

    public static int align(int size, int alignment) {
        int misalignment = size % alignment;
        if (misalignment > 0) {
            return size + (alignment - misalignment);
        } else {
            return size;
        }
    }

    private static FloatBuffer floatBuffer = GlUtil.allocateMemory(16).asFloatBuffer();

    public static Matrix4f getGlMatrix(int matrix) {
        floatBuffer.clear();
        GL11.glGetFloatv(matrix, floatBuffer);
        floatBuffer.rewind();
        Matrix4f mat = new Matrix4f();
        mat.load(floatBuffer);
        return mat;
    }

    public static Matrix4f getGlModelViewMatrix() {
        return getGlMatrix(GL11.GL_MODELVIEW_MATRIX);
    }

    public static Matrix4f getGlTextureMatrix() {
        return getGlMatrix(GL11.GL_TEXTURE_MATRIX);
    }

    public static boolean isGlTextureMatrixIdentity() {
        Matrix4f mat = getGlTextureMatrix();
        // Assumes that glLoadIdentity uses positive zero floating point value (seems to be the case, at least on Mesa)
        return mat.m00 == 1 && mat.m01 == 0 && mat.m02 == 0 && mat.m03 == 0 &&
                mat.m10 == 0 && mat.m11 == 1 && mat.m12 == 0 && mat.m13 == 0 &&
                mat.m20 == 0 && mat.m21 == 0 && mat.m22 == 1 && mat.m23 == 0 &&
                mat.m30 == 0 && mat.m31 == 0 && mat.m32 == 0 && mat.m33 == 1;
    }

    public static Vector3f scaleFromMat(Matrix4f mat, Vector3f scale) {
        if (scale == null) scale = new Vector3f();
        scale.set(
                new Vector3f(mat.m00, mat.m01, mat.m02).length(),
                new Vector3f(mat.m10, mat.m11, mat.m12).length(),
                new Vector3f(mat.m20, mat.m21, mat.m22).length()
        );
        Matrix3f m3 = new Matrix3f();
        m3.m00 = mat.m00;
        m3.m01 = mat.m01;
        m3.m02 = mat.m02;
        m3.m10 = mat.m10;
        m3.m11 = mat.m11;
        m3.m12 = mat.m12;
        m3.m20 = mat.m20;
        m3.m21 = mat.m21;
        m3.m22 = mat.m22;
        if (m3.determinant() < 0) {
            scale.x = -scale.x;
        }
        return scale;
    }

    public static void scaleMat3(Matrix4f mat, Vector3f scale) {
        mat.m00 /= scale.x;
        mat.m01 /= scale.x;
        mat.m02 /= scale.x;
        mat.m10 /= scale.y;
        mat.m11 /= scale.y;
        mat.m12 /= scale.y;
        mat.m20 /= scale.z;
        mat.m21 /= scale.z;
        mat.m22 /= scale.z;
    }

    public static Vector3f posFromMat(Matrix4f mat, Vector3f pos) {
        if (pos == null) pos = new Vector3f();
        pos.set(mat.m30, mat.m31, mat.m32);
        return pos;
    }

    public static Quaternion rotFromMat(Matrix4f mat, Quaternion rot) {
        Quaternion result = Quaternion.setFromMatrix(mat, rot);
        result.normalise();
        return result;
    }

    public static void glToBlend(Vector3f vec) {
        float tmp = vec.y;
        vec.y = -vec.z;
        vec.z = tmp;
    }

    public static void glScaleToBlend(Vector3f vec) {
        float tmp = vec.y;
        vec.y = vec.z;
        vec.z = tmp;
    }

    public static void glToBlend(Quaternion q) {
        float tmp = q.y;
        q.y = -q.z;
        q.z = tmp;
        q.w = -q.w;
    }

    public static Vec3 getCameraPos() {
        Minecraft mc = Minecraft.getInstance();
        Vec3 pos = mc.gameRenderer.getMainCamera().getPosition();
        return new Vec3((float) pos.x, (float) pos.y, (float) pos.z);
    }

    public static Vector3f rotate(Quaternion rot, Vector3f vec, Vector3f dest) {
        if (dest == null) dest = new Vector3f();
        Quaternion vecQ = new Quaternion(vec.x, vec.y, vec.z, 0);
        Quaternion.mul(rot, vecQ, vecQ);
        Quaternion.mulInverse(vecQ, rot, vecQ);
        dest.set(vecQ);
        return dest;
    }

    // Original CPointer.plus method is broken, this one works with any non-negative numbers
    public static <T> CPointer<T> plus(CPointer<T> lhs, int rhs) throws IOException {
        while (rhs > 0) {
            lhs = lhs.plus(1); // 1 is the only value that can be passed to the original plus
            rhs--;
        }
        return lhs;
    }

    public static void insert(ListBase list, CPointer<Link> element) throws IOException {
        CPointer<Link> oldFirst = list.getFirst().cast(Link.class);
        if (oldFirst.isValid()) {
            oldFirst.get().setPrev(element);
        }
        element.get().setNext(oldFirst);
        list.setFirst(element.cast(Object.class));
        if (list.getLast().isNull()) {
            list.setLast(element.cast(Object.class));
        }
    }

    public static String getTileEntityId(BlockEntity tileEntity) {
    	CompoundTag nbt = tileEntity.serializeNBT();
        return nbt.getString("id");
    }

    public interface IOCallable<R> {
        R call() throws IOException;
    }

    public interface IOConsumer<T> {
        void accept(T t) throws IOException;
    }

    public interface IOBiConsumer<T, U> {
        void accept(T t, U u) throws IOException;
    }

    public interface IOFunction<T, R> {
        R apply(T t) throws IOException;
    }
}
