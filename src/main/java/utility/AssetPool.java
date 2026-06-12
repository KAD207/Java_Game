package utility;

import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();

    public static Shader getShader(String resourceName){
        // check to see if resourceName is in our map already, else add it to the asset pool
        File file = new File(resourceName);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
            return AssetPool.shaders.get(file.getAbsolutePath());
        }
        else {
            Shader shader = new Shader(resourceName);
            shader.compileAndRun();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }

    }

    public static Texture getTexture(String resourceName){
        File file = new File(resourceName);
        if (AssetPool.textures.containsKey(resourceName)){
            return AssetPool.textures.get(file.getAbsolutePath());
        }
        else{
            Texture texture = new Texture(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }

    }

}
