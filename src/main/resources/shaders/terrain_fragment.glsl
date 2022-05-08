#version 400 core

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;
in float visibility;

out vec4 fragColour;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct PointLight {
    vec3 colour;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

struct SpotLight {
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

uniform sampler2D backgroundTexture;
uniform sampler2D redTexture;
uniform sampler2D greenTexture;
uniform sampler2D blueTexture;
uniform sampler2D blendMap;

uniform vec3 ambientLight;
uniform vec3 skyColour;
uniform Material material;
uniform float specularPower;
uniform DirectionalLight directionalLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColours(Material material, vec2 textCoord) {
    if (material.hasTexture == 0) {
        vec4 blendMapColour = texture(blendMap, textCoord);
        float backgroundTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
        vec2 tiledCoords = textCoord / 0.01;
        vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backgroundTextureAmount;
        vec4 redTextureColour = texture(redTexture, tiledCoords) * blendMapColour.r;
        vec4 greenTextureColour = texture(greenTexture, tiledCoords) * blendMapColour.g;
        vec4 blueTextureColour = texture(blueTexture, tiledCoords) * blendMapColour.b;

        ambientC = backgroundTextureColour + redTextureColour + greenTextureColour + blueTextureColour;
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calcLightColour(vec3 lightColour, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal) {
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    //diffuse Light
    float diffuseFactor = max(dot(normal, toLightDir), 0.0);
    diffuseColour = diffuseC * vec4(lightColour, 1.0) * lightIntensity *  diffuseFactor;

    //specular Colour
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDir = -toLightDir;
    vec3 reflectedLight = normalize(reflect(fromLightDir, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColour = specularC * lightIntensity * specularFactor * material.reflectance * vec4(lightColour, 1.0);

    return(diffuseColour + specColour);
}


vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 lightDir = light.position - position;
    vec3 toLightDir = normalize(lightDir);
    vec4 lightColour = calcLightColour(light.colour, light.intensity, position, toLightDir, normal);

    float distance = length(lightDir);
    float attenuationInv = light.constant + light.linear * distance + light.exponent * distance * distance;
    return lightColour / attenuationInv;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 lightDir = light.pl.position - position;
    vec3 toLightDir = normalize(lightDir);
    vec3 fromLightDir = -toLightDir;
    float spotAlfa = dot(fromLightDir, normalize(light.conedir));

    vec4 colour = vec4(0, 0, 0, 0);

    if (spotAlfa > light.cutoff) {
        colour = calcPointLight(light.pl, position, normal);
        colour *= (1.0 - (1.0 - spotAlfa) / (1.0 - light.cutoff));
    }

    return colour;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

void main() {

    setupColours(material, fragTextureCoord);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, fragPos, fragNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) {
            diffuseSpecularComp += calcPointLight(pointLights[i], fragPos, fragNormal);
        }
    }

    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (spotLights[i].pl.intensity > 0) {
            diffuseSpecularComp += calcSpotLight(spotLights[i], fragPos, fragNormal);
        }
    }

    fragColour = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
    fragColour = mix(vec4(skyColour, 1.0), fragColour, visibility);
}