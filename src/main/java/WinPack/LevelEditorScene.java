package WinPack;

import components.FontRenderer;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import renderer.Texture;
import utility.Time;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;

public class LevelEditorScene extends Scene {

    private String vertexShaderSource = "#version 330 core\n" +
            " \n" +
            "layout(location = 0) in vec3 aPos;\n" +
            "layout(location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main(){\n" +
            "\n" +
            "fColor = aColor;\n" +
            "gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSource = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main(){\n" +
            "\n" +
            "color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        // position             // color                    // UV coords
        160f,   0f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,     1, 0,  // bottom right 0
          0f, 160f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,     0, 1,  // top left     1
        160f, 160f, 0.0f,      0.0f, 0.0f, 0.0f, 1.0f,     1, 1,  // top right    2
          0f,  0f,  0.0f,      1.0f, 1.0f, 0.0f, 1.0f,    0, 0   // bottom left  3

    };

    // IMPORTANT: must be in COUNTER-CLOCKWISE order
    private int[] elementArray = {
        2, 1, 0, // Top right triangle
        0, 1, 3 // bottom right triangle
    };

    // vertex array object || vertex buffer object || element buffer object
    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture testTexture;

    GameObject testObject;
    private boolean firstTime = false;

    public LevelEditorScene(){

    }

    @Override
    public void init(){
        System.out.println("Creating test object");
        this.testObject = new GameObject("test Object");
        this.testObject.addComponent(new SpriteRenderer());
        this.testObject.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObject);

        this.camera = new Camera(new Vector2f(0, 0));
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndRun();
        this.testTexture = new Texture("assets/textures/potato.png");


        // ==========================================================
        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        // ==========================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // add the vertex attributes POINTERS
        int posSize = 3;
        int colorSize = 4;
        int uvSize = 2;

        int vertexSizeBytes = (posSize + colorSize + uvSize) * Float.BYTES;
        // every pointer must be enabled to use
        // index 0
        glVertexAttribPointer(0, posSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        // index 1
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, posSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        // index 2
        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (posSize + colorSize) * Float.BYTES );
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50.0f;
        camera.position.y -= dt * 20.0f;


//        System.out.println("FPS: " + (1.0f / dt));

        // bind shader program
        defaultShader.use();

        // upload texture to shader
        // "hey we're uploading texture called TEX_SAMPLER into slot 0" - "oh ok ill activate the texture called that at slot 0"
        // "ok good bro remember to bind it"
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

//        System.out.println("ShaderProgramID: " +  defaultShader.getShaderProgramID());
//        int texLocation = glGetUniformLocation(shaderProgram, "TEX_SAMPLER");
//        System.out.println("TEX_SAMPLER location: " + texLocation);

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTimeStarted());

        // bind vertex VAO
        glBindVertexArray(vaoID);

        // enable vertex attributes pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glBindVertexArray(0);

        defaultShader.detach();

        if (!firstTime) {
            GameObject go = new GameObject("Game Test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }

        for (GameObject go : this.gameObjects){
            go.update(dt);
        }
    }

}
