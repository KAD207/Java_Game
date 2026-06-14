package renderer;

import WinPack.GameObject;
import components.SpriteRenderer;

import java.util.*;

public class Renderer {

    private final int MAX_BATCH_SIZE = 10000;
    private List<RenderBatch> batches;

    public Renderer(){
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null){
            add(spr);
        }
    }

    private void add(SpriteRenderer sprite){
        boolean added = false;
        for (RenderBatch batch : batches){
            if (batch.hasRoom() && batch.getzIndex() == sprite.gameObject.getzIndex()){
                Texture tex = sprite.getTexture();
                if (tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }
        if (!added){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getzIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);

            // this sorting function controls how everything blends together
            Collections.sort(batches);
        }
    }

    public void render(){
        for (RenderBatch batch : batches){
            batch.render();
        }
    }
}
