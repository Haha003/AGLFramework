package com.aglframework.smzh.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.aglframework.smzh.AGLFilter;
import com.aglframework.smzh.OpenGlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;

public class StickerFilter extends AGLFilter {


    private FloatBuffer stickerCubeBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private FloatBuffer stickerTextureBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private Bitmap bitmap;
    private int stickerTexture = -1;


    private final float[] stickerCube = {
            -0.5f, -0.5f,
            0.5f, -0.5f,
            -0.50f, 0.5f,
            0.5f, 0.5f,
    };

    private final float[] stickerCoordnate = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };


    public StickerFilter(Context context) {
        super(context);
    }

    @Override
    protected void onDrawArraysAfter(Frame frame) {
        if (stickerTexture == -1 && bitmap != null) {
            stickerTexture = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
        }

        if (stickerTexture == -1) {
            return;
        }

        GLES20.glEnable(GLES20.GL_BLEND);
        //两个混合方法都可以用
        //GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ZERO, GLES20.GL_ONE);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        bindTexture(stickerTexture);
        GLES20.glUniform1i(glUniformTexture, 1);

        stickerCubeBuffer.clear();
        stickerCubeBuffer.put(stickerCube).position(0);
        GLES20.glVertexAttribPointer(glAttrPosition, 2, GLES20.GL_FLOAT, false, 0, stickerCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttrPosition);

        stickerTextureBuffer.clear();
        stickerTextureBuffer.put(stickerCoordnate).position(0);
        GLES20.glVertexAttribPointer(glAttrTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, stickerTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttrTextureCoordinate);

        GLES20.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(glAttrPosition);
        GLES20.glDisableVertexAttribArray(glAttrTextureCoordinate);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (stickerTexture != -1) {
            GLES20.glDeleteTextures(1, new int[]{stickerTexture}, 0);
            stickerTexture = -1;
        }
    }
}
