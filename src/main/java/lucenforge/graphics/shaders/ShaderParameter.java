package lucenforge.graphics.shaders;

import lucenforge.files.Log;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

public class ShaderParameter {

    private final String name; //The name of the parameter
    private final Shader shader; //The shader this parameter belongs to
    private Object value; //The value of the parameter
    private UniformType type; //The type of the parameter
    Integer location = null; //openGL location

    public ShaderParameter(ShaderParameter toCopy) {
        this.name = toCopy.name;
        this.shader = toCopy.shader;
        this.value = null;
        this.type = toCopy.type;
    }

    // Constructor for setting the required parameter
    public ShaderParameter(String name, UniformType type, Shader shader) {
        this.name = name;
        this.shader = shader;
        this.type = type;
    }

    public void set(Object v){
        if(v == null){
            Log.writeln(Log.ERROR, "Cannot set shader parameter " + name + " to null");
            return;
        }
        //Check validity
        boolean isValid = switch (type) {
            case BOOL      -> v instanceof Boolean;
            case INT       -> v instanceof Integer;
            case FLOAT     -> v instanceof Float;
            case VEC2      -> v instanceof Vector2f;
            case VEC3      -> v instanceof Vector3f;
            case VEC4      -> v instanceof Vector4f;
            case MAT4      -> v instanceof Matrix4f;
            case SAMPLER2D -> v instanceof Integer || v instanceof ByteBuffer;
            default -> false;
        };
        if(!isValid){
            Log.writeln(Log.ERROR, "Mismatch or unknown type for shader parameter " + name + ": expected " + type + ", got " + v.getClass().getName());
            return;
        }
        this.value = v;
    }

    public void pushToShader() {
        if (!isSet()) {
            Log.writeln(Log.ERROR, "Cannot push shader parameter " + name + " to the shader " + shader.name() + " because it's not set");
            return;
        }
        shader.setParam(name, this);
    }

    public void pushToOpenGL() {
        if (value == null) {
            Log.writeln(Log.ERROR, "Parameter " + name + " not set!");
            return;
        }

        if(location == null)
            location = glGetUniformLocation(shader.id(), name);
        if (location == -1) {
            Log.writeln(Log.ERROR, "Uniform " + name + " not found in shader " + shader.name());
            location = null;
            return;
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            switch (type) {
                case BOOL -> {
                    boolean b = (Boolean) value;
                    glUniform1i(location, b ? 1 : 0);
                }
                case SAMPLER2D, INT -> {
                    int i = (Integer) value;
                    glUniform1i(location, i);
                }
                case FLOAT -> glUniform1f(location, (Float) value);
                case VEC2 -> {
                    Vector2f v = (Vector2f) value;
                    glUniform2f(location, v.x, v.y);
                }
                case VEC3 -> {
                    Vector3f v = (Vector3f) value;
                    glUniform3f(location, v.x, v.y, v.z);
                }
                case VEC4 -> {
                    Vector4f v = (Vector4f) value;
                    glUniform4f(location, v.x, v.y, v.z, v.w);
                }
                case MAT4 -> {
                    FloatBuffer fb = stack.mallocFloat(16);
                    ((Matrix4f) value).get(fb);
                    glUniformMatrix4fv(location, false, fb);
                }
                default -> Log.writeln(Log.ERROR, "Unknown uniform type: " + type);
            }
        }
    }

    public boolean isSet() {
        return value != null;
    }

    public String name(){
        return name;
    }
}