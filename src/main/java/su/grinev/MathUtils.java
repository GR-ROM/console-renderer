package su.grinev;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MathUtils {

    public static Matrix4f updateTransformationMatrix(Matrix4f matrix4f, Vector3f translation, float rx, float ry, float rz, float scale) {
        return matrix4f.identity()
                .translate(translation)
                .rotateX((float) java.lang.Math.toRadians(rx))
                .rotateY((float) java.lang.Math.toRadians(ry))
                .rotateZ((float) java.lang.Math.toRadians(rz))
                .scale(scale);
    }

    public static Matrix4f createProjectionMatrix(int width, int height, float farPlane, float nearPlane, float fov) {
        Matrix4f projectionMatrix = new Matrix4f();
        float aspect = (float) width / (float) height;
        float yScale = (float) ((1.0f / java.lang.Math.tan(java.lang.Math.toRadians(fov * 0.5))) * aspect);
        float xScale = yScale / aspect;
        float frustumLength = farPlane - nearPlane;

        projectionMatrix.m00(xScale);
        projectionMatrix.m11(yScale);
        projectionMatrix.m22(-((farPlane + nearPlane) / frustumLength));
        projectionMatrix.m23(-1.0f);
        projectionMatrix.m32(-((2.0f * nearPlane * farPlane) / frustumLength));
        projectionMatrix.m33(0);
        return projectionMatrix;
    }

}
