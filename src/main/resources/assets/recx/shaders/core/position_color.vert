#version 150 core

in vec3 Position;
in vec4 Color;

out vec4 vertexColor;

uniform mat4 ProjectionViewMatrix;
uniform mat4 ModelMatrix;

void main() {
    gl_Position = ProjectionViewMatrix * ModelMatrix * vec4(Position, 1.0);
    vertexColor = Color;
}
