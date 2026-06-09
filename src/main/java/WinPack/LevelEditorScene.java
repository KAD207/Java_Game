package WinPack;

import org.lwjgl.BufferUtils;

import java.awt.event.KeyEvent;
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
        // position             // color
        0.5f, -0.5f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f, // bottom right 0
        -0.5f, 0.5f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f, // top left     1
        0.5f, 0.5f, 0.0f,       0.0f, 0.0f, 0.0f, 1.0f, // top right    2
        -0.5f, -0.5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f, // bottom left  3

    };

    // IMPORTANT: must be in COUNTER-CLOCKWISE order
    private int[] elementArray = {
        0, 1, 2, // Top right triangle
        3, 1, 0, // bottom right triangle
    };

    // vertex array object || vertex buffer object || element buffer object
    private int vaoID, vboID, eboID;

    public LevelEditorScene(){

    }

    @Override
    public void init(){
        // ====================================
        // COMPILE AND LINK SHADERS
        // ====================================

        // First load and compile vertex
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass shader source code to gpu
        glShaderSource(vertexID, vertexShaderSource);
        glCompileShader(vertexID);

        // check for errors in compilation process
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tVertex Shader Compilation Failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // First load and compile vertex
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass shader source code to gpu
        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);

        // check for errors in compilation process
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tFragment Shader Compilation Failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // link shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        // check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE){
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tLinking Shaders Failed.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

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
        int floatSizeBytes = 4;
        int vertexSizeBytes = (posSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, posSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, posSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

    }

    @Override
    public void update(float dt) {
        // bind shader program
        glUseProgram(shaderProgram);
        // bind vertex VAO
        glBindVertexArray(vaoID);

        // enable vertex attributes pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);
    }

}
