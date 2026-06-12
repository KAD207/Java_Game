package renderer;

import WinPack.Window;
import components.SpriteRenderer;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL15;
import utility.AssetPool;

import static org.lwjgl.opengl.GL30.*;

public class RenderBatch {

    // Vertex
    // ====================
    // Pos                      Color
    // float, float,            float, float, float, float

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize){
        System.out.println("Creating new render batch");
        shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * VERTEX_SIZE * 4];

        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start(){
        // generate and bind a vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        // this can be changed GL_DYNAMIC_DRAW in future updates
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);


        // create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // enable buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);


    }

    public void addSprite(SpriteRenderer sprite){
        // get index and add renderObject
        int index = this.numSprites;
        // [0, 1, 2, 3, 4, 5]

        this.sprites[index] = sprite;
        this.numSprites++;

        // add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }

    public void render(){
        // for now, we will rebuffer all data every time
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    private void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        // find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        // add vertices with appropriate properties

        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; ++i){
            if (i == 1){
                yAdd = 0.0f;
            }
            else if (i == 2){
                xAdd= 0.0f;
            }
            else if (i == 3){
                yAdd = 1.0f;
            }

            // load position
            vertices[offset] = sprite.gameObject.transform.position.x +
                    (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y +
                    (yAdd * sprite.gameObject.transform.scale.y);

            // load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

//            System.out.println("Vertex " + i + ": pos=(" + vertices[offset] + ", " + vertices[offset+1] +
//                    ") color=(" + vertices[offset+2] + ", " + vertices[offset+3] + ")");

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices(){
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; ++i){
            loadElementIndices(elements, i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        // find offset
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1                 7, 6, 4, 4, 6, 5 (+4)
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;


    }

    public boolean hasRoom(){
        return this.hasRoom;
    }
}
