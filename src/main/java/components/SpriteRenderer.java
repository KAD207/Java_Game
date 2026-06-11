package components;

import WinPack.Component;

public class SpriteRenderer extends Component {

    private boolean firstTime = false;

    @Override
    public void start(float dt){
        System.out.println("I am starting");
    }

    @Override
    public void update(float dt){
        if (!firstTime) {
            System.out.println("I am updating");
            firstTime = true;
        }
    }
}
