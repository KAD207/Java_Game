CLASSES YOU WILL NEED 
==========================================================================
# Renderer
 - add(GameObject go)
 - render()
 - List<RenderBatch>

# RenderBatch
 - start() ← create VAO, VBO, EBO here
 - addSprite(SpriteRenderer spr)
 - int[] generateIndices();
 - render()
 - loadVertexProperties(int index) ← Create 4 vertices per sprite here
 - Shader shader
 - SpriteRenderer[] sprites
 - float[] vertices
 - int vaoID, vboID
    