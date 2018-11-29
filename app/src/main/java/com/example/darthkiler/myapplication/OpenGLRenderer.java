package com.example.darthkiler.myapplication;


import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glEnable;

import static android.opengl.GLES20.glLineWidth;

public class OpenGLRenderer implements Renderer{

    private final static int POSITION_COUNT = 3;

    private Context context;

    private FloatBuffer vertexData;
    private int uColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;
    private int programId;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMatrix = new float[16];

    int n=0;
    float eyeX = 3;
    float eyeY = 3;
    float eyeZ = 3;

    // точка направления камеры
    float centerX = 0;
    float centerY = 0;
    float centerZ = 0;

    // up-вектор
    float upX = 0;
    float upY = 1;
    float upZ = 0;


    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_DEPTH_TEST);
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
        createViewMatrix();
        prepareData();
        bindData();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
        bindMatrix();
    }

    private float getY(float x)
    {
        return (float) (Math.sqrt(1-Math.pow(x+1f,2)))+1f;
    }

    private float getY2(float x)
    {
        return -(float) (Math.sqrt(4-Math.pow(x,2)))+1;
    }


    private void prepareData() {

        float s = 5f;
        float d = 5f;
        float l = 5f;

        ArrayList<Float> floats=new ArrayList<>();
        for(float i=0;i>=(-2f);i-=0.1f)
        {
            floats.add(i);
            floats.add(getY(i));
            floats.add(1f);
        }

        for(float i=-2;i<=2f;i+=0.1f)
        {
            floats.add(i);
            floats.add(getY2(i));
            floats.add(1f);
        }

        n=floats.size();

        for(int i=0;i<n;i+=3)
        {
            float x = floats.get(i);
            float y = floats.get(i + 1);
            float z = floats.get(i + 2);
            for(float j=1f;j>=0;j-=0.1f)
            {


                floats.add(j * x);
                floats.add(j * y);
                floats.add(j);
            }
        }

        floats.add(-l);
        floats.add(0f);
        floats.add(0f);
        floats.add(l);
        floats.add(0f);
        floats.add(0f);

        floats.add(0f);
        floats.add(l);
        floats.add(0f);
        floats.add(0f);
        floats.add(-l);
        floats.add(0f);

        floats.add(0f);
        floats.add(0f);
        floats.add(l);
        floats.add(0f);
        floats.add(0f);
        floats.add(-l);

        float[] vertices = new float[floats.size()];
        for(int i=0;i<vertices.length;i++)
            vertices[i]=floats.get(i);



        n=vertices.length;

        vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices);
    }

    private void bindData() {
        // примитивы
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT,
                false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        // цвет
        uColorLocation = glGetUniformLocation(programId, "u_Color");

        // матрица
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
    }

    private void createProjectionMatrix(int width, int height) {
        float ratio = 1;
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 2;
        float far = 8;
        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    private void createViewMatrix() {
        // точка положения камеры


        //Toast.makeText(context,"asd",Toast.LENGTH_SHORT).show();
        //Log.d("My","qwe");
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }


    private void bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        createViewMatrix();
        bindMatrix();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

        glLineWidth(2);

        glUniform4f(uColorLocation, 1.0f, 1f, 0.0f, 1.0f);

        for(int i=0;i<n-24;i++)
        {
            glDrawArrays(GL_POINTS,i/3
                    , 1);
        }

        //glDrawArrays(GL_LINE_STRIP,0 , n/3-6);



        glLineWidth(1);

        glUniform4f(uColorLocation, 1f, 1f, 1f, 1.0f);
        glDrawArrays(GL_LINES, n/3-6, 2);


        glDrawArrays(GL_LINES, n/3-4, 2);


        glDrawArrays(GL_LINES, n/3-2, 2);

        //glUniform4f(uColorLocation, 1.0f, 1f, 0.0f, 1.0f);
        //glDrawArrays(GL_LINES, 8, 2);
        //Log.d("My","asd");

        //Log.d("My",eyeX+"   "+eyeY);

    }

}