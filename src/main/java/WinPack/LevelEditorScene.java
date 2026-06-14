package WinPack;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import utility.AssetPool;
import java.awt.*;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    Spritesheet sprites;


    public LevelEditorScene(){

    }

    @Override
    public void init(){
        loadResources();
        this.camera = new Camera(new Vector2f(0, 0));

        sprites = AssetPool.getSpriteSheet("assets/textures/spritesheet.png");
        // THE GREATER THE INDEX, THE MORE "ON-TOP" IT BECOMES

        obj1 = new GameObject("Object 1", new Transform(
                new Vector2f(200, 100), new Vector2f(256, 256)), 4);
        obj1.addComponent(new SpriteRenderer(new Sprite(
                AssetPool.getTexture("assets/textures/blendImage1.png")
        )));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(
                new Vector2f(400, 100), new Vector2f(256, 256)), 2);
        obj2.addComponent(new SpriteRenderer(new Sprite(
                AssetPool.getTexture("assets/textures/blendImage2.png")
        )));
        this.addGameObjectToScene(obj2);
    }

    public void loadResources(){
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/textures/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/textures/spritesheet.png"),
                        16, 16, 26, 0));
    }

    @Override
    public void update(float dt) {
//        System.out.println("FPS: " + (1.0f / dt));
        for (GameObject go : this.gameObjects){
            go.update(dt);
        }

        this.renderer.render();
    }

}
