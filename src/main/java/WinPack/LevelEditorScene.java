package WinPack;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import utility.AssetPool;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    public LevelEditorScene(){

    }

    @Override
    public void init(){
        this.camera = new Camera(new Vector2f(0, 0));

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/potato.png")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/textures/carrot.png")));
        this.addGameObjectToScene(obj2);

        loadResources();
    }

    public void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        System.out.println("FPS: " + (1.0f / dt));

        if (KeyListener.isKeyPressed(GLFW_KEY_D)){
            camera.position.x -= 200 * dt;
        }
        else if (KeyListener.isKeyPressed(GLFW_KEY_A)){
            camera.position.x += 200 * dt;
        }
        else if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            camera.position.y -= 200 * dt;
        }
        else if (KeyListener.isKeyPressed(GLFW_KEY_S)){
            camera.position.y += 200 * dt;
        }

        for (GameObject go : this.gameObjects){
            go.update(dt);
        }

        this.renderer.render();
    }

}
