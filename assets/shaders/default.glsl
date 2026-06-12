#type vertex
#version 330 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoords;
layout(location = 3) in float aTexId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;

void main(){

    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];

out vec4 color;

void main(){
    // this will make them black and white - take the average of rgb then assign them into vec4
//    float avg = (fColor.r + fColor.g + fColor.b) / 3;
//    color = vec4(avg, avg, avg, 1);

    // noise map
//    float noise = fract(sin(dot(fColor.xy, vec2(12.9898, 78.233))) * 43758.5453);
//    color = fColor * noise;

    // pein noise to make clouds

    if (fTexId > 0){
        int id = int(fTexId);
        color = fColor * texture(uTextures[id], fTexCoords);
        //color = vec4(fTexCoords, 0, 1); //(x, y, 0, 1) = (r, g, b, a)
    }
    else {
        color = fColor;
    }
}