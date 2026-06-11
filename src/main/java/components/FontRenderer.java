package components;

import WinPack.Component;

public class FontRenderer extends Component {



    @Override
    public void start(float dt) {
        if (gameObject.getComponent(SpriteRenderer.class) != null){
            System.out.println("Found Font Renderer");
        }
    }

    @Override
    public void update(float dt) {

    }
}
