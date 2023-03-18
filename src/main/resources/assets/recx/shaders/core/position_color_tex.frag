#version 150 core

in vec4 vertexColor;
in vec2 texCoords0;

out vec4 FragColor;

uniform vec4 ColorModulator;
uniform sampler2D Sampler0;

void main() {
    FragColor = vertexColor * texture(Sampler0, texCoords0) * ColorModulator;
}
