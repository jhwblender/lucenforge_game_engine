package lucenforge.graphics.primitives.mesh;

import lucenforge.graphics.shaders.Shader;

import java.util.ArrayList;

public class MeshGroup extends Mesh{

    protected final ArrayList<Mesh> meshes = new ArrayList<>();

    public void addMesh(Mesh mesh){
        mesh.setParent(this);
        meshes.add(mesh);
    }

    @Override
    public void render() {
        for(Mesh mesh : meshes){
            mesh.render();
        }
    }

    @Override
    public void cleanup(){
        for(Mesh mesh : meshes){
            mesh.cleanup();
        }
    }

    @Override
    public void setShader(Shader shader){
        for(Mesh mesh : meshes){
            mesh.setShader(shader);
        }
    }

    @Override
    public void setShader(String shaderName){
        for(Mesh mesh : meshes){
            mesh.setShader(shaderName);
        }
    }

    @Override
    public Shader shader(){
        if(!meshes.isEmpty()){
            return meshes.get(0).shader();
        }
        return null;
    }

    @Override
    public void setParam(String name, Object value){
        for(Mesh mesh : meshes){
            mesh.setParam(name, value);
        }
    }

    @Override
    public void init(Usage usage, Shader shader){
        for(Mesh mesh : meshes){
            mesh.init(usage, shader);
        }
    }

}
