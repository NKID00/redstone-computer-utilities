package name.nkid00.rcutil.util;

import org.joml.Vector3f;

public class Vec3f extends Vector3f {
    public Vec3f(Vector3f v) {
        super(v);
    }

    public Vec3f(float x, float y, float z) {
        super(x, y, z);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }
}
