package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private String filepath;
    private int texID;

    public Texture(String filepath){

        this.filepath = filepath;

        // generate texture on GPU
        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);

        // set texture parameters
        // repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // when stretching image, we'd wanna pixelate
        // basically instead of blurring many pixels, it chooses the nearest pixel and makes it larger
        // min_filter for stretching, mag_filter for shrinking
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // when shrinking image, we'd also wanna pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // loading textures
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // new
        stbi_set_flip_vertically_on_load(true);

        ByteBuffer image = stbi_load(filepath, width, height, channels, 4);

        if (image != null){
            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }
            else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }
            else{
                assert false : "Error: {Texture} Unknown number of channels: '" + channels.get(0) + "'";
            }
        }
        else {
            assert false : "Error: Texture could not load image: '" + filepath + "'";
        }

        // free the allocating image = memory leak if not freed
        stbi_image_free(image);
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
