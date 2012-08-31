package com.xiledsystems.AlternateJavaBridgelib.components.OpenGL;

public class GLConstants {
	
	public static final String COLOR_VERTEX_SHADER_CODE = 
			// This matrix member variable provides a hook to manipulate
	        // the coordinates of the objects that use this vertex shader
	        
	        "uniform mat4 uMVPMatrix;	\n" +
	        "attribute vec4 vPosition;  \n" +
	        "void main(){               \n" +
	        
	        // the matrix must be included as a modifier of gl_Position
	        " gl_Position = uMVPMatrix * vPosition; \n" +
	        
	        "}  \n";
	
	public static final String COLOR_FRAG_SHADER_CODE = 
			 "precision mediump float;  \n" +
			 "void main(){              \n" +
			 " gl_FragColor = vec4 (0.63671875, 0.76953125, 0.22265625, 1.0); \n" +
			 "}                         \n";
	
	public static final String TEXTURE_VERTEX_SHADER_CODE = 
			"uniform mat4 uMVPMatrix;	\n" +		
					"uniform mat4 MVMatrix;		\n" +
					"attribute vec4 vPosition;  \n" +
					"attribute vec2 a_TexCoordinate;	\n" +					
					"varying vec2 v_TexCoordinate;	\n" +
					
					"void main() {		\n" +
					
					"v_TexCoordinate = a_TexCoordinate;	\n" +
					"gl_Position = uMVPMatrix * vPosition;	\n" +
					"}	\n";
	
	public static final String TEXTURE_FRAG_SHADER_CODE = 
			"precision mediump float;  \n" +
					"uniform sampler2D u_Texture;	\n" +					
					"varying vec2 v_TexCoordinate;	\n" +
					"void main() {              \n" +
					"gl_FragColor = texture2D(u_Texture, v_TexCoordinate); \n" +
					"}                         \n";
	
	public static String ColorFragShaderCode(float red, float green, float blue) {
		return "precision mediump float;  \n" +
				 "void main(){              \n" +
				 " gl_FragColor = vec4 ("+red+", "+green+", "+blue+", 1.0); \n" +
				 "}                         \n";
	}
	
	private GLConstants() {		
	}
		
}
