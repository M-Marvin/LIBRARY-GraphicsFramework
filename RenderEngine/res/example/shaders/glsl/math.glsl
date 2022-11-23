
vec4 lerpVec4(vec4 value1, vec4 value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}

vec3 lerpVec3(vec3 value1, vec3 value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}

vec2 lerpVec2(vec2 value1, vec2 value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}

float lerp(float value1, float value2, float interpolation) {
	return value2 * (1 - interpolation) + value1 * interpolation;
}

vec2 translateVec2(vec2 vector, mat3 matrix) {
	return (matrix * vec3(vector, 1)).xy;
}

vec3 translateVec3(vec3 vector, mat4 matrix) {
	return (matrix * vec4(vector, 1)).xyz;
}
