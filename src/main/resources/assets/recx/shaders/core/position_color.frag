#version 150 core

in vec4 vertexColor;

out vec4 FragColor;

uniform vec4 ColorModulator;

void main() {
    FragColor = vertexColor * ColorModulator;
}
