#type vertex
#version 330 core
// declaration

// a = attribute
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aColor;

// f = fragment
out vec4 fColor;

void main(){

    fColor = aColor;
    // aPos, aPos, aPos, 1.0;
    gl_Position = vec4(aPos, 1.0);
}


#type fragment
#version 330 core

in vec4 fColor;

out vec4 color;

// basically passing vertex into the fragment shader
void main(){

    color = fColor;
}