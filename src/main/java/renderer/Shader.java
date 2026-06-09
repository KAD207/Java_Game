package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    // need a shader id
    private int shaderProgramID;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath){
        this.filepath = filepath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type )([a-zA-Z]+)");

            // find the first pattern after #type 'pattern'
            // get from index 0 + "#type" = 6 to get past the #type, therefore it is + 6
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // same goes + 6 here
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) vertexSource = splitString[1];
            else if (firstPattern.equals("fragment")) fragmentSource = splitString[1];
            else throw new IOException("Unexpected token: '" +  firstPattern + "'" + " in '" + filepath + "'");


            if (secondPattern.equals("vertex")) vertexSource = splitString[2];
            else if (secondPattern.equals("fragment")) fragmentSource = splitString[2];
            else throw new IOException("Unexpected token: '" +  secondPattern + "'" + " in '" + filepath + "'");


        } catch (IOException e){
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }

//        System.out.println(vertexSource);
//        System.out.println(fragmentSource);
    }

    public void compileAndRun(){
        // ====================================
        // COMPILE AND LINK SHADERS
        // ====================================
        int vertexID, fragmentID;

        // First load and compile vertex
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass shader source code to gpu
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // check for errors in compilation process
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tVertex Shader Compilation Failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // First load and compile vertex
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass shader source code to gpu
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // check for errors in compilation process
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tFragment Shader Compilation Failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filepath + "'\n\tLinking of Shaders Failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }

    }

    public void use(){
        // bind shader program
        glUseProgram(shaderProgramID);
    }

    public void detach(){
        glUseProgram(0);
    }

}
